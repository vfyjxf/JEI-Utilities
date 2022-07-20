package com.github.vfyjxf.jeiutilities.gui.input;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.jei.recipe.IRecipeInfo;
import com.github.vfyjxf.jeiutilities.mixin.accessor.BookmarkOverlayAccessor;
import com.github.vfyjxf.jeiutilities.mixin.accessor.RecipeGuiLogicAccessor;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.Focus;
import mezz.jei.gui.ingredients.IngredientLookupState;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.input.CombinedRecipeFocusSource;
import mezz.jei.input.UserInput;
import mezz.jei.input.mouse.IUserInputHandler;
import mezz.jei.input.mouse.handlers.FocusInputHandler;
import mezz.jei.input.mouse.handlers.LimitedAreaInputHandler;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.vfyjxf.jeiutilities.config.KeyBindings.isKeyDown;
import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.focusFactory;
import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.ingredientManager;

/**
 * Based on {@link FocusInputHandler}, but with some modifications to facilitate reading recipes from {@link IRecipeInfo}.
 */
@SuppressWarnings("unused")
public class ExtendedFocusInputHandler implements IUserInputHandler {

    private final CombinedRecipeFocusSource focusSource;
    private final RecipesGui recipesGui;
    private final IngredientGridWithNavigation bookmarkContents;

    public static IUserInputHandler create(CombinedRecipeFocusSource focusSource, RecipesGui recipesGui) {
        if (JeiUtilitiesConfig.getRecordRecipes() || JeiUtilitiesConfig.getEnableHistory()) {
            return new ExtendedFocusInputHandler(focusSource, recipesGui);
        } else {
            return new FocusInputHandler(focusSource, recipesGui);
        }
    }

    public ExtendedFocusInputHandler(CombinedRecipeFocusSource focusSource, RecipesGui recipesGui) {
        this.focusSource = focusSource;
        this.recipesGui = recipesGui;
        this.bookmarkContents = ((BookmarkOverlayAccessor) JeiUtilitiesPlugin.bookmarkOverlay).getContents();
    }

    @Override
    public @NotNull Optional<IUserInputHandler> handleUserInput(@NotNull Screen screen, @NotNull UserInput input) {

        Optional<IUserInputHandler> result;

        if (bookmarkContents.isMouseOver(input.getMouseX(), input.getMouseY())) {
            result = handleBookmarkShow(input);
        } else {
            result = handleOriginalShow(input);
        }

        if (!input.isSimulate() && result.isPresent()) {
            focusSource.getIngredientUnderMouse(input)
                    .findFirst()
                    .ifPresent(clicked -> {
                        if (JeiUtilitiesConfig.getEnableHistory()) {
                            JeiUtilitiesPlugin.historyGrid.addHistory(clicked.getTypedIngredient());
                        }
                    });
        }

        return result;
    }

    private Optional<IUserInputHandler> handleBookmarkShow(UserInput input) {
        if (isKeyDown(KeyBindings.showRecipe, false, input.getKey())) {
            return handleShow(input, List.of(RecipeIngredientRole.OUTPUT), false);
        }

        if (input.is(KeyBindings.showUses)) {
            return handleShow(input, List.of(RecipeIngredientRole.INPUT, RecipeIngredientRole.CATALYST), true);
        }

        return Optional.empty();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Optional<IUserInputHandler> handleShow(UserInput input, List<RecipeIngredientRole> roles, boolean showUses) {
        return focusSource.getIngredientUnderMouse(input)
                .findFirst()
                .map(clicked -> {
                    if (!input.isSimulate()) {
                        ITypedIngredient<?> ingredientUnderMouse = clicked.getTypedIngredient();
                        IRecipeInfo recipeInfo = null;
                        boolean showRecord = false;
                        List<IFocus<?>> focuses;
                        if (ingredientUnderMouse.getIngredient() instanceof IRecipeInfo) {
                            recipeInfo = (IRecipeInfo) ingredientUnderMouse.getIngredient();

                            if (showUses) {
                                focuses = getFocuses(recipeInfo, roles);
                            } else {
                                if (Screen.hasShiftDown()) {
                                    if (JeiUtilitiesConfig.isEnableMode()) {
                                        focuses = getFocuses(recipeInfo, roles);
                                    } else {
                                        focuses = recipeInfo.getFocuses();
                                    }
                                } else {
                                    focuses = JeiUtilitiesConfig.isEnableMode() ? recipeInfo.getFocuses() : getFocuses(recipeInfo, roles);
                                }
                                showRecord = true;
                            }

                        } else {
                            focuses = roles.stream()
                                    .<IFocus<?>>map(role -> new Focus<>(role, clicked.getTypedIngredient()))
                                    .toList();
                        }

                        recipesGui.show(focuses);
                        boolean handleInversion = (!Screen.hasShiftDown() && JeiUtilitiesConfig.isEnableMode()) ||
                                (Screen.hasShiftDown() && JeiUtilitiesConfig.isRestrictedMode());
                        if (recipeInfo != null && showRecord && handleInversion) {
                            setRecipe(recipeInfo);
                        }

                    }
                    return LimitedAreaInputHandler.create(this, clicked.getArea());
                });
    }

    private Optional<IUserInputHandler> handleOriginalShow(UserInput input) {
        if (input.is(KeyBindings.showRecipe)) {
            return handleShow(input, List.of(RecipeIngredientRole.OUTPUT));
        }

        if (input.is(KeyBindings.showUses)) {
            return handleShow(input, List.of(RecipeIngredientRole.INPUT, RecipeIngredientRole.CATALYST));
        }

        return Optional.empty();
    }

    private Optional<IUserInputHandler> handleShow(UserInput input, List<RecipeIngredientRole> roles) {
        return focusSource.getIngredientUnderMouse(input)
                .findFirst()
                .map(clicked -> {
                    if (!input.isSimulate()) {
                        List<IFocus<?>> focuses = roles.stream()
                                .<IFocus<?>>map(role -> new Focus<>(role, clicked.getTypedIngredient()))
                                .toList();
                        recipesGui.show(focuses);
                    }
                    return LimitedAreaInputHandler.create(this, clicked.getArea());
                });
    }

    @SuppressWarnings("rawtypes")
    private void setRecipe(@NotNull IRecipeInfo recipeInfo) {
        IngredientLookupState state = ((RecipeGuiLogicAccessor) (JeiUtilitiesPlugin.logic)).getState();
        state.setRecipeCategory(recipeInfo.getRecipeCategory());
        state.setRecipeIndex(recipeInfo.getRecipeIndex());
        recipesGui.onStateChange();
    }

    @SuppressWarnings("rawtypes")
    private List<IFocus<?>> getFocuses(@NotNull IRecipeInfo recipeInfo, List<RecipeIngredientRole> roles) {
        Object outputValue = recipeInfo.getOutput();
        IIngredientType<Object> ingredientType = ingredientManager.getIngredientType(outputValue);
        return roles.stream()
                .map(role -> focusFactory.createFocus(role, ingredientType, outputValue))
                .collect(Collectors.toList());
    }

}
