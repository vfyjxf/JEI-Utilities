package com.github.vfyjxf.jeiutilities.gui;

import com.github.vfyjxf.jeiutilities.gui.RecipeBookmarkList.RecipeInfo;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.bookmarks.BookmarkList;
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
import mezz.jei.util.LegacyUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class BookmarkInputHandler {

    private static BookmarkInputHandler instance;

    private static RecipesGui recipesGui;
    private static RecipeGuiLogic logic;
    private static BookmarkList bookmarkList;
    private static LeftAreaDispatcher leftAreaDispatcher;
    private static Field recipeLayoutsField;
    private static Field stateField;
    private static Method containsMethod;
    private static Method getIngredientUnderMouseForKeyMethod;
    private static Method updateRecipesMethod;

    private final RecipeBookmarkList recipeBookmarkList = new RecipeBookmarkList();
    private Object ingredientUnderMouse;

    public static BookmarkInputHandler getInstance() {
        if (instance == null) {
            instance = new BookmarkInputHandler();
        }
        return instance;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBookmarkRemove(GuiScreenEvent.KeyboardInputEvent event) {
        if (ingredientUnderMouse == null) {
            int eventKey = Keyboard.getEventKey();
            if (KeyBindings.bookmark.isActiveAndMatches(eventKey)) {
                IClickedIngredient<?> clicked = getIngredientUnderMouseForKey();
                if (clicked != null) {
                    ingredientUnderMouse = clicked.getValue();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public void onBookmarkListAddOrRemove(GuiScreenEvent.KeyboardInputEvent event) {
        if (event.isCanceled()) {
            final int eventKey = Keyboard.getEventKey();

            if (KeyBindings.bookmark.isActiveAndMatches(eventKey)) {
                Object ingredient = getOutputUnderMouse();
                //If the bookmark contains this ingredient, the current recipe is recorded
                if (ingredient != null) {
                    if (isBookmarkContains(ingredient)) {
                        IngredientLookupState state = getState();
                        if (state != null && state.getFocus() != null) {
                            boolean isInputMode = state.getFocus().getMode() == IFocus.Mode.INPUT;
                            RecipeInfo<?, ?> recipeInfo = new RecipeInfo<>(
                                    normalize(state.getFocus().getValue()),
                                    normalize(ingredient),
                                    state.getRecipeCategoryIndex(),
                                    state.getRecipeIndex(),
                                    isInputMode
                            );
                            recipeBookmarkList.addRecipeInfo(recipeInfo);
                        }
                    }
                } else if (!isBookmarkContains(ingredientUnderMouse)) {
                    recipeBookmarkList.removeRecipeInfo(ingredientUnderMouse);
                    ingredientUnderMouse = null;
                }

            }
        }
    }

    /**
     * open recorded recipe
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouseClicked(GuiScreenEvent.MouseInputEvent event) {
        IClickedIngredient<?> clicked = leftAreaDispatcher.getIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
        final int eventButton = Mouse.getEventButton();

        if (clicked != null && eventButton == 0) {
            RecipeInfo<?, ?> recipeInfo = recipeBookmarkList.getRecipeInfo(clicked.getValue());
            if (recipeInfo != null) {
                IFocus.Mode mode = recipeInfo.isInputMode() ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT;
                recipesGui.show(new Focus<Object>(mode, recipeInfo.getIngredient()));
                JeiUtilitiesPlugin.getGrid().removeElement(0);
                JeiUtilitiesPlugin.getGrid().addHistoryIngredient(recipeInfo.getResult());
                IngredientLookupState state = getState();
                if (state != null) {
                    state.setRecipeCategoryIndex(recipeInfo.getRecipeCategoryIndex());
                    state.setRecipeIndex(recipeInfo.getRecipeIndex());
                    updateRecipes();
                    recipesGui.onStateChange();
                    event.setCanceled(true);
                }
            }
        }
    }


    /**
     * open recorded recipe
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onKeyPressed(GuiScreenEvent.KeyboardInputEvent event) {
        int eventKey = Keyboard.getEventKey();

        if (KeyBindings.showRecipe.isActiveAndMatches(eventKey)) {
            IClickedIngredient<?> clicked = getIngredientUnderMouseForKey();
            if (clicked != null) {

                RecipeInfo<?, ?> recipeInfo = recipeBookmarkList.getRecipeInfo(clicked.getValue());
                if (recipeInfo != null) {
                    IFocus.Mode mode = recipeInfo.isInputMode() ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT;
                    recipesGui.show(new Focus<Object>(mode, recipeInfo.getIngredient()));
                    JeiUtilitiesPlugin.getGrid().removeElement(0);
                    JeiUtilitiesPlugin.getGrid().addHistoryIngredient(recipeInfo.getResult());
                    IngredientLookupState state = getState();
                    if (state != null) {
                        state.setRecipeCategoryIndex(recipeInfo.getRecipeCategoryIndex());
                        state.setRecipeIndex(recipeInfo.getRecipeIndex());
                        updateRecipes();
                        recipesGui.onStateChange();
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    private <T> T normalize(T ingredient) {
        IIngredientHelper<T> ingredientHelper = JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(ingredient);
        T copy = LegacyUtil.getIngredientCopy(ingredient, ingredientHelper);
        if (copy instanceof ItemStack) {
            ((ItemStack) copy).setCount(1);
        } else if (copy instanceof FluidStack) {
            ((FluidStack) copy).amount = 1000;
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    private Object getOutputUnderMouse() {
        List<RecipeLayout> recipeLayouts = null;
        try {
            recipeLayouts = (List<RecipeLayout>) recipeLayoutsField.get(recipesGui);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (recipeLayouts != null) {
            for (RecipeLayout recipeLayout : recipeLayouts) {
                GuiIngredient<?> clicked = recipeLayout.getGuiIngredientUnderMouse(MouseHelper.getX(), MouseHelper.getY());
                if (clicked != null && !clicked.isInput()) {
                    return clicked.getDisplayedIngredient();
                }
            }
        }
        return null;
    }

    private boolean isBookmarkContains(Object ingredient) {
        try {
            return (boolean) containsMethod.invoke(bookmarkList, ingredient);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private IClickedIngredient<?> getIngredientUnderMouseForKey() {
        try {
            return (IClickedIngredient<?>) getIngredientUnderMouseForKeyMethod.invoke(JeiUtilitiesPlugin.inputHandler, MouseHelper.getX(), MouseHelper.getY());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private IngredientLookupState getState() {
        try {
            return (IngredientLookupState) stateField.get(logic);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateRecipes() {
        try {
            updateRecipesMethod.invoke(logic);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void onInputHandlerSet() {
        recipesGui = JeiUtilitiesPlugin.jeiRuntime.getRecipesGui();
        logic = ObfuscationReflectionHelper.getPrivateValue(RecipesGui.class, recipesGui, "logic");
        bookmarkList = ObfuscationReflectionHelper.getPrivateValue(BookmarkOverlay.class, (BookmarkOverlay) JeiUtilitiesPlugin.jeiRuntime.getBookmarkOverlay(), "bookmarkList");
        leftAreaDispatcher = ObfuscationReflectionHelper.getPrivateValue(InputHandler.class, JeiUtilitiesPlugin.inputHandler, "leftAreaDispatcher");
        recipeLayoutsField = ObfuscationReflectionHelper.findField(RecipesGui.class, "recipeLayouts");
        stateField = ObfuscationReflectionHelper.findField(RecipeGuiLogic.class, "state");
        containsMethod = ObfuscationReflectionHelper.findMethod(BookmarkList.class, "contains", boolean.class, Object.class);
        getIngredientUnderMouseForKeyMethod = ObfuscationReflectionHelper.findMethod(InputHandler.class, "getIngredientUnderMouseForKey", IClickedIngredient.class, int.class, int.class);
        updateRecipesMethod = ObfuscationReflectionHelper.findMethod(RecipeGuiLogic.class, "updateRecipes", void.class);
    }

    public RecipeBookmarkList getRecipeBookmarkList() {
        return recipeBookmarkList;
    }
}
