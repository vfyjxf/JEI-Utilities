package com.github.vfyjxf.jeiutilities.gui;


import com.github.vfyjxf.jeiutilities.jei.AdvancedIngredientGrid;
import mezz.jei.config.KeyBindings;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.InputHandler;
import mezz.jei.input.MouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HistoryInputHandler {

    private static InputHandler inputHandler;
    private static AdvancedIngredientGrid ingredientGrid;
    private static Method getFocusUnderMouseForClick;
    private static Method getIngredientUnderMouseForKey;

    private boolean clickHandled = false;

    /**
     * This listener will receive mouse clicks that have been processed by jei to add item history
     */
    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (event.isCanceled()) {

            if (clickHandled) {
                clickHandled = false;
                return;
            }

            GuiScreen guiScreen = event.getGui();
            final int eventButton = Mouse.getEventButton();
            if (eventButton == 0 || eventButton == 1 || isShowRecipeKey(eventButton - 100)) {
                IClickedIngredient<?> clicked = getFocusUnderMouseForClick(guiScreen);
                if (clicked != null) {
                    ingredientGrid.addHistoryIngredient(clicked);
                    clickHandled = true;
                }
            }
        }
    }

    /**
     * This listener will receive key events that have been processed by jei to add item history
     */
    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOW)
    public void onKeyType(GuiScreenEvent.KeyboardInputEvent event) {
        if (event.isCanceled()) {
            final int eventKey = Keyboard.getEventKey();
            if (isShowRecipeKey(eventKey)) {
                IClickedIngredient<?> clicked = getFocusUnderMouseForClick(event.getGui());
                if (clicked != null) {
                    ingredientGrid.addHistoryIngredient(clicked);
                }
            }
        }
    }

    private boolean isShowRecipeKey(int eventKey) {
        return KeyBindings.showRecipe.isActiveAndMatches(eventKey) || KeyBindings.showUses.isActiveAndMatches(eventKey);
    }

    private IClickedIngredient<?> getFocusUnderMouseForClick(GuiScreen guiScreen) {
        try {
            Minecraft minecraft = guiScreen.mc;
            if (minecraft != null) {
                int mouseX = Mouse.getEventX() * guiScreen.width / minecraft.displayWidth;
                int mouseY = guiScreen.height - Mouse.getEventY() * guiScreen.height / minecraft.displayHeight - 1;
                return (IClickedIngredient<?>) getFocusUnderMouseForClick.invoke(inputHandler, mouseX, mouseY);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private IClickedIngredient<?> getIngredientUnderMouseForKey(GuiScreen guiScreen) {
        try {
            Minecraft minecraft = guiScreen.mc;
            if (minecraft != null) {
                return (IClickedIngredient<?>) getIngredientUnderMouseForKey.invoke(inputHandler, MouseHelper.getX(), MouseHelper.getY());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setInputHandler(InputHandler inputHandler) {
        HistoryInputHandler.inputHandler = inputHandler;
        getFocusUnderMouseForClick = ObfuscationReflectionHelper.findMethod(InputHandler.class, "getFocusUnderMouseForClick", IClickedIngredient.class, int.class, int.class);
        getIngredientUnderMouseForKey = ObfuscationReflectionHelper.findMethod(InputHandler.class, "getIngredientUnderMouseForKey", IClickedIngredient.class, int.class, int.class);
    }

    public static void setIngredientGrid(AdvancedIngredientGrid ingredientGrid) {
        HistoryInputHandler.ingredientGrid = ingredientGrid;
    }
}
