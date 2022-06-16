package com.github.vfyjxf.jeiutilities.gui.common;

import com.github.vfyjxf.jeiutilities.config.KeyBindings;
import com.github.vfyjxf.jeiutilities.gui.bookmark.AdvancedBookmarkOverlay;
import com.github.vfyjxf.jeiutilities.gui.recipe.RecipeLayoutLite;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

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

        boolean shouldHandleKey = (eventKey == 0 && typedChar >= 32) || Keyboard.getEventKeyState();
        if (!shouldHandleKey || pressedKeys.contains(eventKey)) {
            return;
        }

        boolean isTransferRecipe = KeyBindings.isKeyDown(KeyBindings.transferRecipe);
        boolean isTransferRecipeMax = KeyBindings.isKeyDown(KeyBindings.transferRecipeMax);
        if (isTransferRecipe || isTransferRecipeMax) {
            RecipeLayoutLite recipeLayout = getRecipeLayout();
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

    private RecipeLayoutLite getRecipeLayout() {
        if (JeiUtilitiesPlugin.bookmarkOverlay instanceof AdvancedBookmarkOverlay) {
            return ((AdvancedBookmarkOverlay) JeiUtilitiesPlugin.bookmarkOverlay).getRecipeLayout();
        }
        return null;
    }

}
