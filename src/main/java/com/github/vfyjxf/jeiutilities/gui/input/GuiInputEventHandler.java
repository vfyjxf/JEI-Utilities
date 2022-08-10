package com.github.vfyjxf.jeiutilities.gui.input;

import com.github.vfyjxf.jeiutilities.JeiUtilities;
import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.config.KeyBindings;
import com.github.vfyjxf.jeiutilities.gui.bookmark.AdvancedBookmarkOverlay;
import com.github.vfyjxf.jeiutilities.gui.recipe.RecipeLayoutLite;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.jei.recipe.IRecipeInfo;
import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.core.util.ReflectionUtil;
import mezz.jei.input.MouseUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.ingredientListOverlay;

@Mod.EventBusSubscriber(modid = JeiUtilities.MOD_ID, value = Dist.CLIENT)
public class GuiInputEventHandler {

    private static final ReflectionUtil reflectionUtil = new ReflectionUtil();
    private static final Set<InputConstants.Key> pressedKeys = new HashSet<>();

    @SubscribeEvent
    public static void onKeyboardKeyPressedEvent(ScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        InputConstants.Key input = InputConstants.getKey(event.getKeyCode(), event.getScanCode());
        boolean shouldNotHandleKey = pressedKeys.contains(input) ||
                isContainerTextFieldFocused(event.getScreen()) ||
                (ingredientListOverlay != null && ingredientListOverlay.hasKeyboardFocus());
        if (shouldNotHandleKey) {
            return;
        }

        if (JeiUtilitiesConfig.getRecordRecipes()) {
            boolean isCtrlDown = Screen.hasControlDown();
            boolean isTransferRecipe = KeyBindings.isKeyDown(KeyBindings.transferRecipe, false, input);
            boolean isTransferRecipeMax = KeyBindings.isKeyDown(KeyBindings.transferRecipeMax, false, input);
            if (isCtrlDown && (isTransferRecipe || isTransferRecipeMax)) {
                RecipeLayoutLite<?> recipeLayout = getRecipeLayout();
                if (recipeLayout != null) {
                    Minecraft minecraft = event.getScreen().getMinecraft();
                    Player player = minecraft.player;
                    if (player == null) {
                        return;
                    }
                    if (minecraft.screen instanceof AbstractContainerScreen<?> screen) {
                        AbstractContainerMenu container = screen.getMenu();
                        IRecipeTransferError error = recipeLayout.getTransferRecipeError(container, recipeLayout, player, true);
                        recipeLayout.setShowError(error != null);
                        if (error == null) {
                            recipeLayout.transferRecipe(container, recipeLayout, player, isTransferRecipeMax);
                        }
                        event.setCanceled(true);
                        pressedKeys.add(input);
                    }
                }
            }

        }

        if (KeyBindings.openFilterGui.isActiveAndMatches(input) && JeiUtilitiesPlugin.recipesFilterScreen != null) {
            Minecraft.getInstance().setScreen(JeiUtilitiesPlugin.recipesFilterScreen);
        }

    }

    @SubscribeEvent
    public static void onKeyboardKeyReleasedEvent(ScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
        InputConstants.Key input = InputConstants.getKey(event.getKeyCode(), event.getScanCode());
        pressedKeys.remove(input);
        if (input.equals(KeyBindings.displayPreview.getKey())) {
            RecipeLayoutLite<?> recipeLayout = getRecipeLayout();
            if (recipeLayout != null) {
                recipeLayout.setShowError(false);
            }
        }
    }

    private static RecipeLayoutLite<?> getRecipeLayout() {
        if (!JeiUtilitiesConfig.getRecordRecipes()) {
            return null;
        }
        if (JeiUtilitiesPlugin.bookmarkOverlay instanceof AdvancedBookmarkOverlay bookmarkOverlay) {
            Optional<ITypedIngredient<?>> ingredient = bookmarkOverlay.getIngredientUnderMouse();
            if (ingredient.isPresent()) {
                if (ingredient.get().getIngredient() instanceof IRecipeInfo recipeInfo) {
                    if (recipeInfo == bookmarkOverlay.getInfoUnderMouse()) {
                        return bookmarkOverlay.getRecipeLayout();
                    } else {
                        RecipeLayoutLite<?> recipeLayout = RecipeLayoutLite.create(recipeInfo, (int) MouseUtil.getX(), (int) MouseUtil.getY());
                        if (recipeLayout != null) {
                            bookmarkOverlay.setRecipeLayout(recipeLayout);
                            bookmarkOverlay.setInfoUnderMouse(recipeInfo);
                            return recipeLayout;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isContainerTextFieldFocused(Screen screen) {
        return reflectionUtil.getFieldWithClass(screen, EditBox.class)
                .anyMatch(textField -> textField.isActive() && textField.isFocused());
    }

}
