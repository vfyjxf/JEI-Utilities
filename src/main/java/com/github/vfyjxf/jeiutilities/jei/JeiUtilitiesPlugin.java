package com.github.vfyjxf.jeiutilities.jei;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import mezz.jei.Internal;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.ingredients.IngredientListElementFactory;
import mezz.jei.ingredients.IngredientOrderTracker;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.input.InputHandler;
import mezz.jei.runtime.JeiRuntime;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;

/**
 * @author vfyjxf
 */
@JEIPlugin
public class JeiUtilitiesPlugin implements IModPlugin {

    public static JeiRuntime jeiRuntime;
    public static InputHandler inputHandler;
    public static IModRegistry modRegistry;
    public static IngredientRegistry ingredientRegistry;
    public static IngredientOrderTracker ORDER_TRACKER;
    private static AdvancedIngredientGrid grid = new AdvancedIngredientGrid();

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        JeiUtilitiesPlugin.jeiRuntime = Internal.getRuntime();
        if (JeiUtilitiesConfig.enableHistory) {
            ObfuscationReflectionHelper.setPrivateValue(
                    IngredientGridWithNavigation.class,
                    ObfuscationReflectionHelper.getPrivateValue(IngredientListOverlay.class, (IngredientListOverlay) jeiRuntime.getIngredientListOverlay(), "contents"),
                    grid,
                    "ingredientGrid"
            );
        }
        ORDER_TRACKER = ObfuscationReflectionHelper.getPrivateValue(IngredientListElementFactory.class, null, "ORDER_TRACKER");
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        JeiUtilitiesPlugin.modRegistry = registry;
        ingredientRegistry = (IngredientRegistry) registry.getIngredientRegistry();
    }

    public static AdvancedIngredientGrid getGrid() {
        return grid;
    }

}
