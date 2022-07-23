package com.github.vfyjxf.jeiutilities.gui.input;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.config.RecordMode;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.jei.recipe.BasedRecipeInfo;
import com.github.vfyjxf.jeiutilities.jei.recipe.IRecipeInfo;
import com.github.vfyjxf.jeiutilities.mixin.accessor.RecipeGuiLogicAccessor;
import com.github.vfyjxf.jeiutilities.mixin.accessor.RecipesGuiAccessor;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.ingredients.IngredientLookupState;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.input.CombinedRecipeFocusSource;
import mezz.jei.input.UserInput;
import mezz.jei.input.mouse.IUserInputHandler;
import mezz.jei.input.mouse.handlers.BookmarkInputHandler;
import mezz.jei.input.mouse.handlers.LimitedAreaInputHandler;
import net.minecraft.client.gui.screens.Screen;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.vfyjxf.jeiutilities.config.KeyBindings.isKeyDown;
import static com.github.vfyjxf.jeiutilities.helper.IngredientHelper.*;

@SuppressWarnings("unused")
public class RecipeBookmarkInputHandler implements IUserInputHandler {

    private final CombinedRecipeFocusSource focusSource;
    private final BookmarkList bookmarkList;

    public static IUserInputHandler create(CombinedRecipeFocusSource focusSource, BookmarkList bookmarkList) {
        if (JeiUtilitiesConfig.getRecordRecipes()) {
            return new RecipeBookmarkInputHandler(focusSource, bookmarkList);
        } else {
            return new BookmarkInputHandler(focusSource, bookmarkList);
        }
    }

    public RecipeBookmarkInputHandler(CombinedRecipeFocusSource focusSource, BookmarkList bookmarkList) {
        this.focusSource = focusSource;
        this.bookmarkList = bookmarkList;
    }

    @Override
    public @NotNull Optional<IUserInputHandler> handleUserInput(@NotNull Screen screen, @NotNull UserInput input) {
        if (isKeyDown(KeyBindings.bookmark, false, input.getKey())) {
            boolean handleEnableMode = JeiUtilitiesConfig.getRecordMode() == RecordMode.ENABLE && !Screen.hasShiftDown();
            boolean handleRestrictedMode = JeiUtilitiesConfig.getRecordMode() == RecordMode.RESTRICTED && Screen.hasShiftDown();
            boolean handleOriginLogic = JeiUtilitiesConfig.getRecordMode() == RecordMode.DISABLE || (!handleEnableMode && !handleRestrictedMode);

            if (handleOriginLogic) {
                return handlerOriginalInput(input);
            } else {
                ITypedIngredient<?> typedInfo = getTypedInfo(input.getMouseX(), input.getMouseY());
                if (typedInfo == null) {
                    return handlerOriginalInput(input);
                } else {
                    return handlerRecipeBookmarkInput(input, typedInfo, handleEnableMode);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<IUserInputHandler> handlerRecipeBookmarkInput(UserInput input, ITypedIngredient<?> typedInfo, boolean handleEnableMode) {
        boolean shouldAddRecipe = JeiUtilitiesConfig.getRecordMode() == RecordMode.DISABLE;
        return focusSource.getIngredientUnderMouse(input)
                .findFirst()
                .flatMap(clicked -> {

                    if (input.isSimulate() ||
                            bookmarkList.remove(typedInfo) ||
                            bookmarkList.add(typedInfo)
                    ) {
                        return Optional.of(LimitedAreaInputHandler.create(this, clicked.getArea()));
                    }

                    if (input.isSimulate() ||
                            bookmarkList.remove(clicked.getTypedIngredient()) ||
                            bookmarkList.add(clicked.getTypedIngredient())
                    ) {
                        return Optional.of(LimitedAreaInputHandler.create(this, clicked.getArea()));
                    }

                    return Optional.empty();

                });
    }

    private Optional<IUserInputHandler> handlerOriginalInput(UserInput input) {
        return focusSource.getIngredientUnderMouse(input)
                .findFirst()
                .flatMap(clicked -> {
                    if (input.isSimulate() ||
                            bookmarkList.remove(clicked.getTypedIngredient()) ||
                            bookmarkList.add(clicked.getTypedIngredient())
                    ) {
                        return Optional.of(LimitedAreaInputHandler.create(this, clicked.getArea()));
                    }
                    return Optional.empty();
                });
    }

    private ITypedIngredient<?> getTypedInfo(double mouseX, double mouseY) {
        Pair<? extends RecipeLayout<?>, ?> output = getOutputUnderMouse(mouseX, mouseY);
        if (output != null) {
            IngredientLookupState state = ((RecipeGuiLogicAccessor) (JeiUtilitiesPlugin.logic)).getState();
            if (!state.getFocuses().isEmpty()) {
                IRecipeCategory<?> recipeCategory = state.getRecipeCategories().get(state.getRecipeCategoryIndex());
                Object ingredient = state.getFocuses().getAllFocuses().get(0).getTypedValue().getIngredient();
                @SuppressWarnings("rawtypes")
                IRecipeInfo recipeInfo = BasedRecipeInfo.create(
                        recipeCategory,
                        output.getLeft().getRecipe(),
                        (JeiUtilitiesConfig.getKeepOutputCount() ? createTypedIngredient(output.getRight()) : normalizeIngredient(output.getRight())),
                        (JeiUtilitiesConfig.getKeepOutputCount() ? copyIngredient(ingredient) : normalizeIngredient(ingredient)),
                        isInput(state.getFocuses()),
                        state.getRecipeIndex()
                );
                return createTypedIngredient(recipeInfo);
            }

        }
        return null;
    }

    private Pair<? extends RecipeLayout<?>, ?> getOutputUnderMouse(double mouseX, double mouseY) {
        List<RecipeLayout<?>> recipeLayouts = ((RecipesGuiAccessor) JeiUtilitiesPlugin.recipesGui).getRecipeLayouts();
        for (RecipeLayout<?> recipeLayout : recipeLayouts) {
            Pair<? extends RecipeLayout<?>, ?> outputPair = recipeLayout.getRecipeSlotUnderMouse(mouseX, mouseY)
                    .filter(recipeSlot -> recipeSlot.getRole() == RecipeIngredientRole.OUTPUT && recipeSlot.getDisplayedIngredient().isPresent())
                    .map(recipeSlot -> Pair.of(recipeLayout, recipeSlot.getDisplayedIngredient().get().getIngredient()))
                    .orElse(null);
            if (outputPair != null) {
                return outputPair;
            }
        }
        return null;
    }

    private boolean isInput(IFocusGroup focusGroup) {
        List<RecipeIngredientRole> roles = getRoles(focusGroup);
        return !roles.contains(RecipeIngredientRole.OUTPUT) && (roles.contains(RecipeIngredientRole.INPUT) && roles.contains(RecipeIngredientRole.CATALYST));
    }

    private List<RecipeIngredientRole> getRoles(IFocusGroup focusGroup) {
        return focusGroup.getAllFocuses()
                .stream()
                .map(IFocus::getRole)
                .collect(Collectors.toList());
    }

}
