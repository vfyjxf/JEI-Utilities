package com.github.vfyjxf.jeiutilities.jei;

import com.github.vfyjxf.jeiutilities.gui.HistoryInputHandler;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.ingredients.IngredientListElementFactory;
import mezz.jei.ingredients.IngredientOrderTracker;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;

/**
 * @author vfyjxf
 */
@JEIPlugin
public class JeiUtilitiesPlugin implements IModPlugin {

    public static IJeiRuntime jeiRuntime;
    public static IModRegistry modRegistry;
    public static IIngredientRegistry ingredientRegistry;
    public static IngredientOrderTracker ORDER_TRACKER;

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        JeiUtilitiesPlugin.jeiRuntime = jeiRuntime;
        AdvancedIngredientGrid grid = new AdvancedIngredientGrid();
        ObfuscationReflectionHelper.setPrivateValue(
                IngredientGridWithNavigation.class,
                ObfuscationReflectionHelper.getPrivateValue(IngredientListOverlay.class, (IngredientListOverlay) jeiRuntime.getIngredientListOverlay(), "contents"),
                grid,
                "ingredientGrid"
        );
        HistoryInputHandler.setIngredientGrid(grid);
        ORDER_TRACKER = ObfuscationReflectionHelper.getPrivateValue(IngredientListElementFactory.class, null, "ORDER_TRACKER");
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        JeiUtilitiesPlugin.modRegistry = registry;
        ingredientRegistry = registry.getIngredientRegistry();
    }
}
