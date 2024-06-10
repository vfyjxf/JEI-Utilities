package com.github.vfyjxf.jeiutilities.ui.bookmark;

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
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.gui.ingredients.IngredientLookupState;
import mezz.jei.gui.overlay.IngredientGrid;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.overlay.bookmarks.LeftAreaDispatcher;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.InputHandler;
import mezz.jei.input.MouseHelper;
import mezz.jei.render.IngredientListBatchRenderer;
import mezz.jei.render.IngredientListSlot;
import mezz.jei.util.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class BookmarkInputHandler {

    private static BookmarkInputHandler instance;

    //recipe

    private RecipesGui recipesGui;
    private RecipeGuiLogic logic;

    //bookmark

    private BookmarkList bookmarkList;
    private LeftAreaDispatcher leftAreaDispatcher;
    private IngredientGrid bookmarkIngredientGrid;
    private IngredientGridWithNavigation bookmarkContents;
    private IngredientListBatchRenderer bookmarkIngredientSlots;

    private final IntSet clickHandled = new IntArraySet();
    private IIngredientListElement<?> draggedElement;

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

        if (isAddBookmark(eventKey)) {
            if (JeiUtilitiesConfig.getRecordMode() == RecordMode.ENABLE && GuiContainer.isShiftKeyDown()) {
                //handle shift + add bookmarks in ENABLE mode.
                IClickedIngredient<?> clicked = recipesGui.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
                if (clicked != null) {
                    if (addOrRemoveBookmark(clicked.getValue())) {
                        event.setCanceled(true);
                    }
                }
            } else {
                //In RESTRICTED mode, player needs to press shift in order to mark the recipe.
                boolean withShift = JeiUtilitiesConfig.getRecordMode() == RecordMode.RESTRICTED;
                if (withShift && !GuiContainer.isShiftKeyDown()) {
                    return;
                }

                Pair<RecipeLayout, Object> output = getOutputUnderMouse();
                if (output != null) {
                    IngredientLookupState state = ReflectionUtils.getFieldValue(RecipeGuiLogic.class, logic, "state");
                    if (state != null && state.getFocus() != null) {
                        boolean isInputMode = state.getFocus().getMode() == IFocus.Mode.INPUT;
                        String recipeCategoryUid = state.getRecipeCategories().get(state.getRecipeCategoryIndex()).getUid();

                        IRecipeWrapper recipeWrapper = ReflectionUtils.getFieldValue(RecipeLayout.class, output.getLeft(), "recipeWrapper");
                        RecipeInfo<?, ?> recipeInfo = RecipeInfo.create(
                                IngredientHelper.getNormalize(state.getFocus().getValue()),
                                IngredientHelper.getNormalize(output.getRight()),
                                recipeCategoryUid,
                                state.getRecipeIndex(),
                                isInputMode,
                                recipeWrapper
                        );

                        if (addOrRemoveBookmark(recipeInfo)) {
                            event.setCanceled(true);
                        }

                    }
                }
            }
        }

    }

    /**
     * open recorded recipe and handle bookmarks movement(Replace jei InputHandler).
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouseClicked(GuiScreenEvent.MouseInputEvent.Pre event) {

        final int eventButton = Mouse.getEventButton();

        if (eventButton > -1) {
            if (Mouse.getEventButtonState()) {
                if (!clickHandled.contains(eventButton)) {

                    if (handleBookmarkMove(eventButton)) {
                        clickHandled.add(eventButton);
                        event.setCanceled(true);
                        return;
                    }

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
     * open recorded recipe(Replace jei InputHandler)
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onKeyPressed(GuiScreenEvent.KeyboardInputEvent.Post event) {

        char typedChar = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();
        boolean shouldHandleKey = (eventKey == 0 && typedChar >= 32) || Keyboard.getEventKeyState();
        if (!shouldHandleKey) {
            return;
        }

        event.setCanceled(handleFocusKeybindings(eventKey));

    }

    /**
     * Restore bookmarks to their initial state when a gui is closed/opened.
     */
    @SubscribeEvent
    public void onGuiClosed(GuiOpenEvent event) {
        this.draggedElement = null;
        this.notifyListenersOfChange();
    }

    public IIngredientListElement<?> getDraggedElement() {
        return draggedElement;
    }

    private boolean handleMouseClick(int mouseButton) {

        if (this.draggedElement != null) {
            return false;
        }

        IClickedIngredient<?> clicked = leftAreaDispatcher.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
        if (clicked != null) {
            Object ingredient = clicked.getValue();

            if (ingredient instanceof RecipeInfo) {
                RecipeInfo<?, ?> recipeInfo = (RecipeInfo<?, ?>) ingredient;

                if (mouseButton == 0) {

                    if (JeiUtilitiesConfig.getRecordMode() == RecordMode.DISABLE) {
                        recipesGui.show(new Focus<>(IFocus.Mode.OUTPUT, recipeInfo.getResult()));
                        return true;
                    }

                    //Use to invert the operation when shift is pressed.
                    if (handleInvert(recipeInfo)) {
                        return true;
                    }

                    showRecipe(new Focus<>(recipeInfo.getMode(), recipeInfo.getIngredient()));
                    JeiUtilitiesPlugin.getGrid().ifPresent(grid -> grid.addHistoryIngredient(recipeInfo.getResult()));
                    IngredientLookupState state = ReflectionUtils.getFieldValue(RecipeGuiLogic.class, logic, "state");
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

                        if (JeiUtilitiesConfig.getRecordMode() == RecordMode.DISABLE) {
                            recipesGui.show(new Focus<>(IFocus.Mode.OUTPUT, recipeInfo.getResult()));
                            return true;
                        }

                        if (handleInvert(recipeInfo)) {
                            return true;
                        }

                        showRecipe(new Focus<>(recipeInfo.getMode(), recipeInfo.getIngredient()));
                        JeiUtilitiesPlugin.getGrid().ifPresent(grid -> grid.addHistoryIngredient(recipeInfo.getResult()));
                        IngredientLookupState state = ReflectionUtils.getFieldValue(RecipeGuiLogic.class, logic, "state");
                        if (state != null) {
                            state.setRecipeCategoryIndex(getRecipeCategoryIndex(state, recipeInfo.getRecipeCategoryUid()));
                            state.setRecipeIndex(recipeInfo.getRecipeIndex());
                            updateRecipes();
                            recipesGui.onStateChange();
                            return true;
                        }

                    } else {
                        recipesGui.show(new Focus<>(IFocus.Mode.INPUT, recipeInfo.getResult()));
                        return true;
                    }

                }

            }
        }

        return false;
    }

    private boolean handleBookmarkMove(int eventButton) {
        //Pick up the bookmark to the mouse.
        if (eventButton == 2 && draggedElement == null) {
            IIngredientListElement<?> elementUnderMouse = bookmarkIngredientGrid.getElementUnderMouse();
            if (elementUnderMouse != null) {
                pickUpElement(elementUnderMouse);
                return true;
            }
        } else if (eventButton == 0 || eventButton == 1 || eventButton == 2) {

            if (draggedElement == null) {
                return false;
            }

            IIngredientListElement<?> replaceElement = null;
            if (eventButton == 2) {
                replaceElement = bookmarkIngredientGrid.getElementUnderMouse();
            }

            int insertIndex = getInsertIndex();

            if (insertIndex > -1) {
                if (bookmarkList.remove(draggedElement.getIngredient())) {
                    addElement(insertIndex, draggedElement);
                }
                draggedElement = null;
                notifyListenersOfChange();
                if (replaceElement != null) {
                    pickUpElement(replaceElement);
                }
                return true;
            }

        }

        return false;
    }

    private Pair<RecipeLayout, Object> getOutputUnderMouse() {
        List<RecipeLayout> recipeLayouts = ReflectionUtils.getFieldValue(RecipesGui.class, recipesGui, "recipeLayouts");
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

    private boolean addOrRemoveBookmark(Object value) {
        if (bookmarkList.remove(value)) {
            if (bookmarkList.isEmpty() && Config.isBookmarkOverlayEnabled()) {
                Config.toggleBookmarkEnabled();
            }

            return true;
        } else {
            if (!Config.isBookmarkOverlayEnabled()) {
                Config.toggleBookmarkEnabled();
            }
            return bookmarkList.add(value);
        }
    }

    private boolean handleInvert(RecipeInfo<?, ?> recipeInfo) {
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
        return false;
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

    private boolean isAddBookmark(int keycode) {
        return keycode != 0 &&
                KeyBindings.bookmark.getKeyCode() == keycode
                && KeyBindings.bookmark.getKeyConflictContext().isActive();
    }

    private boolean isShowRecipe(int keycode) {
        return keycode != 0 &&
                KeyBindings.showRecipe.getKeyCode() == keycode &&
                KeyBindings.showRecipe.getKeyConflictContext().isActive();
    }

    @SuppressWarnings("rawtypes")
    private int getFirstItemIndex(List<IIngredientListElement> ingredientList) {
        int firstItemIndex = ReflectionUtils.getFieldValue(
                IngredientGridWithNavigation.class,
                bookmarkContents,
                "firstItemIndex"
        );
        if (firstItemIndex >= ingredientList.size()) {
            firstItemIndex = 0;
        }
        return firstItemIndex;
    }

    @SuppressWarnings("rawtypes")
    private void pickUpElement(@Nonnull IIngredientListElement<?> element) {
        List<IIngredientListElement> ingredientList = new LinkedList<>(bookmarkList.getIngredientList());
        ingredientList.remove(element);
        int firstItemIndex = getFirstItemIndex(ingredientList);
        bookmarkIngredientSlots.set(firstItemIndex, ingredientList);
        this.draggedElement = element;
    }

    /**
     * @return The index of the slot into which the bookmark will be inserted.
     */
    private int getInsertIndex() {
        int mouseX = MouseHelper.getX();
        int mouseY = MouseHelper.getY();
        IngredientListSlot slotUnderMouse = null;
        List<IngredientListSlot> allGuiIngredientSlots = bookmarkIngredientSlots.getAllGuiIngredientSlots();
        for (IngredientListSlot slot : allGuiIngredientSlots) {
            if (slot.getArea().contains(mouseX, mouseY)) {
                if (slot.getIngredientRenderer() == null) {
                    List<Object> list = ReflectionUtils.getFieldValue(BookmarkList.class, bookmarkList, "list");
                    return list.size() - 1;
                } else {
                    slotUnderMouse = slot;
                    break;
                }

            }
        }

        if (slotUnderMouse != null) {
            int halfX = slotUnderMouse.getArea().x + slotUnderMouse.getArea().width / 2;
            int slotIndex = bookmarkIngredientSlots.getAllGuiIngredientSlots().indexOf(slotUnderMouse);
            return mouseX <= halfX ? slotIndex : slotIndex + 1;
        }

        return -1;
    }

    private void addElement(int index, @Nonnull IIngredientListElement<?> element) {

        for (IIngredientListElement<?> existing : bookmarkList.getIngredientList()) {
            if (IngredientHelper.ingredientEquals(existing.getIngredient(), element.getIngredient())) {
                return;
            }
        }

        List<Object> list = ReflectionUtils.getFieldValue(BookmarkList.class, bookmarkList, "list");
        list.add(index, element.getIngredient());
        bookmarkList.getIngredientList().add(index, element);
        bookmarkList.saveBookmarks();
    }

    private void notifyListenersOfChange() {
        try {
            ReflectionUtils.getMethod(BookmarkList.class, "notifyListenersOfChange", void.class).invoke(bookmarkList);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void onInputHandlerSet() {
        recipesGui = JeiUtilitiesPlugin.jeiRuntime.getRecipesGui();
        logic = ObfuscationReflectionHelper.getPrivateValue(RecipesGui.class, recipesGui, "logic");
        bookmarkList = ObfuscationReflectionHelper.getPrivateValue(BookmarkOverlay.class, (BookmarkOverlay) JeiUtilitiesPlugin.jeiRuntime.getBookmarkOverlay(), "bookmarkList");
        leftAreaDispatcher = ObfuscationReflectionHelper.getPrivateValue(InputHandler.class, JeiUtilitiesPlugin.inputHandler, "leftAreaDispatcher");
        bookmarkIngredientGrid = JeiUtilitiesPlugin.bookmarkIngredientGrid;
        bookmarkContents = JeiUtilitiesPlugin.bookmarkContents;
        bookmarkIngredientSlots = ObfuscationReflectionHelper.getPrivateValue(IngredientGrid.class, bookmarkIngredientGrid, "guiIngredientSlots");
    }

}
