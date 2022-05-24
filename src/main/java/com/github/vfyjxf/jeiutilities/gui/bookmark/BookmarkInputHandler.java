package com.github.vfyjxf.jeiutilities.gui.bookmark;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.config.RecordMode;
import com.github.vfyjxf.jeiutilities.helper.IngredientHelper;
import com.github.vfyjxf.jeiutilities.helper.ReflectionUtils;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.config.Config;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.Focus;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.gui.ingredients.IngredientLookupState;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.overlay.bookmarks.LeftAreaDispatcher;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.InputHandler;
import mezz.jei.input.MouseHelper;
import mezz.jei.util.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class BookmarkInputHandler {

    private static BookmarkInputHandler instance;

    private static RecipesGui recipesGui;
    private static RecipeGuiLogic logic;
    private static BookmarkList bookmarkList;
    private static LeftAreaDispatcher leftAreaDispatcher;

    private final IntSet clickHandled = new IntArraySet();

    public static BookmarkInputHandler getInstance() {
        if (instance == null) {
            instance = new BookmarkInputHandler();
        }
        return instance;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBookmarkListAdd(GuiScreenEvent.KeyboardInputEvent.Post event) {

        if (JeiUtilitiesConfig.getRecordMode() == RecordMode.DISABLE) {
            return;
        }

        if (isContainerTextFieldFocused()) {
            return;
        }

        final int eventKey = Keyboard.getEventKey();
        boolean withShift = JeiUtilitiesConfig.getRecordMode() == RecordMode.RESTRICTED;
        if (isAddBookmark(eventKey, withShift)) {
            Pair<RecipeLayout, Object> output = getOutputUnderMouse();
            if (output != null) {
                IngredientLookupState state = ReflectionUtils.getField(RecipeGuiLogic.class, logic, "state");
                if (state != null && state.getFocus() != null) {
                    boolean isInputMode = state.getFocus().getMode() == IFocus.Mode.INPUT;
                    String recipeCategoryUid = state.getRecipeCategories().get(state.getRecipeCategoryIndex()).getUid();

                    IRecipeWrapper recipeWrapper = ReflectionUtils.getField(RecipeLayout.class, output.getLeft(), "recipeWrapper");
                    RecipeInfo<?, ?> recipeInfo = new RecipeInfo<>(
                            IngredientHelper.getNormalize(state.getFocus().getValue()),
                            IngredientHelper.getNormalize(output.getRight()),
                            recipeCategoryUid,
                            state.getRecipeIndex(),
                            isInputMode,
                            recipeWrapper
                    );

                    if (bookmarkList.remove(recipeInfo)) {
                        if (bookmarkList.isEmpty() && Config.isBookmarkOverlayEnabled()) {
                            Config.toggleBookmarkEnabled();
                        }
                        event.setCanceled(true);
                    } else {
                        if (!Config.isBookmarkOverlayEnabled()) {
                            Config.toggleBookmarkEnabled();
                        }
                        if (bookmarkList.add(recipeInfo)) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }

    }

    /**
     * open recorded recipe
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouseClicked(GuiScreenEvent.MouseInputEvent.Pre event) {

        if (JeiUtilitiesConfig.getRecordMode() == RecordMode.DISABLE) {
            return;
        }

        final int eventButton = Mouse.getEventButton();

        if (eventButton > -1) {
            if (Mouse.getEventButtonState()) {
                if (!clickHandled.contains(eventButton)) {
                    if (handleMouseClick(eventButton)) {
                        clickHandled.add(eventButton);
                        event.setCanceled(true);
                    }
                }
            } else if (clickHandled.contains(eventButton)) {
                clickHandled.remove(eventButton);
                event.setCanceled(true);
            }
        }

    }


    /**
     * open recorded recipe
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onKeyPressed(GuiScreenEvent.KeyboardInputEvent.Post event) {

        if (JeiUtilitiesConfig.getRecordMode() == RecordMode.DISABLE) {
            return;
        }

        char typedChar = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();
        boolean shouldHandleKey = (eventKey == 0 && typedChar >= 32) || Keyboard.getEventKeyState();
        if (!shouldHandleKey) {
            return;
        }

        event.setCanceled(handleFocusKeybindings(eventKey));

    }

    private boolean handleMouseClick(int mouseButton) {
        IClickedIngredient<?> clicked = leftAreaDispatcher.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
        if (clicked != null) {
            Object ingredient = clicked.getValue();

            if (ingredient instanceof RecipeInfo) {
                RecipeInfo<?, ?> recipeInfo = (RecipeInfo<?, ?>) ingredient;

                if (mouseButton == 0) {

                    //Use to invert the operation when shift is pressed.
                    if (JeiUtilitiesConfig.getRecordMode() == RecordMode.RESTRICTED) {
                        if (!GuiContainer.isShiftKeyDown()) {
                            recipesGui.show(new Focus<>(IFocus.Mode.OUTPUT, recipeInfo.getResult()));
                            return true;
                        }
                    } else {
                        if (GuiContainer.isShiftKeyDown()) {
                            recipesGui.show(new Focus<>(IFocus.Mode.OUTPUT, recipeInfo.getResult()));
                            return true;
                        }
                    }

                    IFocus.Mode mode = recipeInfo.isInputMode() ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT;
                    showRecipe(new Focus<>(mode, recipeInfo.getIngredient()));
                    JeiUtilitiesPlugin.getGrid().removeElement(0);
                    JeiUtilitiesPlugin.getGrid().addHistoryIngredient(recipeInfo.getResult());
                    IngredientLookupState state = ReflectionUtils.getField(RecipeGuiLogic.class, logic, "state");
                    if (state != null) {
                        state.setRecipeCategoryIndex(getRecipeCategoryIndex(state, recipeInfo.getRecipeCategoryUid()));
                        state.setRecipeIndex(recipeInfo.getRecipeIndex());
                        updateRecipes();
                        recipesGui.onStateChange();
                        clickHandled.add(mouseButton);
                        return true;
                    }
                } else if (mouseButton == 1) {
                    recipesGui.show(new Focus<>(IFocus.Mode.INPUT, recipeInfo.getResult()));
                    return true;
                }

            }

        }
        return false;
    }

    private boolean handleFocusKeybindings(int eventKey) {
        final boolean showRecipe = isShowRecipe(eventKey);
        final boolean showUses = KeyBindings.showUses.isActiveAndMatches(eventKey);

        if (showRecipe || showUses) {
            IClickedIngredient<?> clicked = leftAreaDispatcher.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
            if (clicked != null) {

                Object clickedIngredient = clicked.getValue();
                if (clickedIngredient instanceof RecipeInfo) {
                    RecipeInfo<?, ?> recipeInfo = (RecipeInfo<?, ?>) clickedIngredient;

                    if (showRecipe) {

                        if (JeiUtilitiesConfig.getRecordMode() == RecordMode.RESTRICTED) {
                            if (!GuiContainer.isShiftKeyDown()) {
                                showRecipe(new Focus<>(IFocus.Mode.OUTPUT, recipeInfo.getResult()));
                                return true;
                            }
                        } else {
                            if (GuiContainer.isShiftKeyDown()) {
                                showRecipe(new Focus<>(IFocus.Mode.OUTPUT, recipeInfo.getResult()));
                                return true;
                            }
                        }

                        IFocus.Mode mode = recipeInfo.isInputMode() ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT;
                        showRecipe(new Focus<>(mode, recipeInfo.getIngredient()));
                        JeiUtilitiesPlugin.getGrid().addHistoryIngredient(recipeInfo.getResult());
                        IngredientLookupState state = ReflectionUtils.getField(RecipeGuiLogic.class, logic, "state");
                        if (state != null) {
                            state.setRecipeCategoryIndex(getRecipeCategoryIndex(state, recipeInfo.getRecipeCategoryUid()));
                            state.setRecipeIndex(recipeInfo.getRecipeIndex());
                            updateRecipes();
                            recipesGui.onStateChange();
                        }

                    } else {
                        recipesGui.show(new Focus<>(IFocus.Mode.INPUT, recipeInfo.getResult()));
                    }
                    return true;

                }

            }
        }

        return false;
    }

    private Pair<RecipeLayout, Object> getOutputUnderMouse() {
        List<RecipeLayout> recipeLayouts = ReflectionUtils.getField(RecipesGui.class, recipesGui, "recipeLayouts");
        if (recipeLayouts != null) {
            for (RecipeLayout recipeLayout : recipeLayouts) {
                GuiIngredient<?> clicked = recipeLayout.getGuiIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
                if (clicked != null && !clicked.isInput()) {
                    return Pair.of(recipeLayout, clicked.getDisplayedIngredient());
                }
            }
        }
        return null;
    }

    private void updateRecipes() {
        try {
            ReflectionUtils.getMethod(RecipeGuiLogic.class, "updateRecipes", void.class).invoke(logic);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private int getRecipeCategoryIndex(@Nonnull IngredientLookupState state, @Nonnull String recipeCategoryUid) {
        for (IRecipeCategory<?> recipeCategory : state.getRecipeCategories()) {
            if (recipeCategory.getUid().equals(recipeCategoryUid)) {
                return state.getRecipeCategories().indexOf(recipeCategory);
            }
        }
        return 0;
    }

    private <V> void showRecipe(IFocus<V> focus) {
        focus = Focus.check(focus);

        if (logic.setFocus(focus)) {
            try {
                ReflectionUtils.getMethod(RecipesGui.class, "open", void.class).invoke(recipesGui);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isContainerTextFieldFocused() {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui == null) {
            return false;
        }
        GuiTextField textField = ReflectionUtil.getFieldWithClass(gui, GuiTextField.class);
        return textField != null && textField.getVisible() && textField.isFocused();
    }

    private boolean isAddBookmark(int keycode, boolean withShift) {
        if (withShift) {
            return keycode != 0 &&
                    KeyBindings.bookmark.getKeyCode() == keycode
                    && KeyBindings.bookmark.getKeyConflictContext().isActive() &&
                    GuiContainer.isShiftKeyDown();
        } else {
            return KeyBindings.bookmark.isActiveAndMatches(keycode);
        }
    }

    private boolean isShowRecipe(int keycode) {
        return keycode != 0 &&
                KeyBindings.showRecipe.getKeyCode() == keycode &&
                KeyBindings.showRecipe.getKeyConflictContext().isActive();
    }

    public static void onInputHandlerSet() {
        recipesGui = JeiUtilitiesPlugin.jeiRuntime.getRecipesGui();
        logic = ObfuscationReflectionHelper.getPrivateValue(RecipesGui.class, recipesGui, "logic");
        bookmarkList = ObfuscationReflectionHelper.getPrivateValue(BookmarkOverlay.class, (BookmarkOverlay) JeiUtilitiesPlugin.jeiRuntime.getBookmarkOverlay(), "bookmarkList");
        leftAreaDispatcher = ObfuscationReflectionHelper.getPrivateValue(InputHandler.class, JeiUtilitiesPlugin.inputHandler, "leftAreaDispatcher");
    }

}
