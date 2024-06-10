package com.github.vfyjxf.jeiutilities.ui.common;

import com.github.vfyjxf.jeiutilities.config.KeyBindings;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import com.github.vfyjxf.jeiutilities.ui.bookmark.AdvancedBookmarkOverlay;
import com.github.vfyjxf.jeiutilities.ui.recipe.RecipePreviewWidget;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.input.MouseHelper;
import mezz.jei.util.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.ingredientListOverlay;

public class GuiInputHandler {

    private static GuiInputHandler instance;

    public static GuiInputHandler getInstance() {
        if (instance == null) {
            instance = new GuiInputHandler();
        }
        return instance;
    }

    private GuiInputHandler() {

    }

    private final IntSet pressedKeys = new IntArraySet();

    @SubscribeEvent
    public void onKeyPressed(GuiScreenEvent.KeyboardInputEvent.Post event) {
        char typedChar = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();

        if (pressedKeys.contains(eventKey) && !Keyboard.isKeyDown(eventKey)) {
            pressedKeys.remove(eventKey);
        }

        boolean shouldHandleInput = (eventKey == 0 && typedChar >= 32) || Keyboard.getEventKeyState();
        boolean shouldNotHandleKey = !shouldHandleInput ||
                pressedKeys.contains(eventKey) ||
                isContainerTextFieldFocused() ||
                ingredientListOverlay.hasKeyboardFocus();
        if (shouldNotHandleKey) {
            return;
        }

        boolean isTransferRecipe = KeyBindings.isKeyDown(KeyBindings.transferRecipe);
        boolean isTransferRecipeMax = KeyBindings.isKeyDown(KeyBindings.transferRecipeMax);
        if (isTransferRecipe || isTransferRecipeMax) {
            RecipePreviewWidget recipeLayout = getRecipeLayout();
            if (recipeLayout != null) {
                Minecraft mc = event.getGui().mc;
                if (mc == null) {
                    return;
                }
                if (mc.currentScreen instanceof GuiContainer) {
                    Container container = ((GuiContainer) mc.currentScreen).inventorySlots;
                    IRecipeTransferError error = recipeLayout.transferRecipe(container, mc.player, false, false);
                    if (error == null) {
                        recipeLayout.transferRecipe(container, mc.player, isTransferRecipeMax, true);
                        event.setCanceled(true);
                    }
                    pressedKeys.add(eventKey);
                }
            }
        }
    }

    private RecipePreviewWidget getRecipeLayout() {
        if (JeiUtilitiesPlugin.bookmarkOverlay instanceof AdvancedBookmarkOverlay) {
            AdvancedBookmarkOverlay bookmarkOverlay = (AdvancedBookmarkOverlay) JeiUtilitiesPlugin.bookmarkOverlay;
            Object ingredient = bookmarkOverlay.getIngredientUnderMouse();
            if (ingredient instanceof RecipeInfo) {
                RecipeInfo<?, ?> recipeInfo = (RecipeInfo<?, ?>) ingredient;
                if (recipeInfo == bookmarkOverlay.getInfoUnderMouse()) {
                    return bookmarkOverlay.getRecipeLayout();
                } else {
                    RecipePreviewWidget recipeLayout = RecipePreviewWidget.createLayout(recipeInfo, MouseHelper.getX(), MouseHelper.getY());
                    if (recipeLayout != null) {
                        bookmarkOverlay.setRecipeLayout(recipeLayout);
                        bookmarkOverlay.setInfoUnderMouse(recipeInfo);
                        return recipeLayout;
                    }
                }

            }
        }
        return null;
    }

    public static boolean isContainerTextFieldFocused() {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui == null) {
            return false;
        }
        GuiTextField textField = ReflectionUtil.getFieldWithClass(gui, GuiTextField.class);
        return textField != null && textField.getVisible() && textField.isFocused();
    }

}
