package com.github.vfyjxf.jeiutilities.jei;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.gui.history.AdvancedIngredientGrid;
import mezz.jei.Internal;
import mezz.jei.api.*;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.ingredients.IngredientListElementFactory;
import mezz.jei.ingredients.IngredientOrderTracker;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.runtime.JeiRuntime;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.util.Optional;

import static net.minecraftforge.fml.common.ObfuscationReflectionHelper.getPrivateValue;

/**
 * @author vfyjxf
 */
@JEIPlugin
public class JeiUtilitiesPlugin implements IModPlugin {

    public static JeiRuntime jeiRuntime;
    public static IModRegistry modRegistry;
    public static IngredientRegistry ingredientRegistry;
    public static IngredientOrderTracker ORDER_TRACKER;
    public static IGuiHelper guiHelper;
    public static IngredientListOverlay ingredientListOverlay;
    private static AdvancedIngredientGrid grid;

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        JeiUtilitiesPlugin.jeiRuntime = Internal.getRuntime();
        ingredientListOverlay = (IngredientListOverlay) jeiRuntime.getIngredientListOverlay();
        if (JeiUtilitiesConfig.isEnableHistory()) {
            ObfuscationReflectionHelper.setPrivateValue(
                    IngredientGridWithNavigation.class,
                    getPrivateValue(IngredientListOverlay.class, (IngredientListOverlay) jeiRuntime.getIngredientListOverlay(), "contents"),
                    grid = new AdvancedIngredientGrid(),
                    "ingredientGrid"
            );
            ORDER_TRACKER = getPrivateValue(IngredientListElementFactory.class, null, "ORDER_TRACKER");
        }
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        JeiUtilitiesPlugin.modRegistry = registry;
        ingredientRegistry = (IngredientRegistry) registry.getIngredientRegistry();
        JeiUtilitiesPlugin.guiHelper = registry.getJeiHelpers().getGuiHelper();
    }

    public static Optional<AdvancedIngredientGrid> getGrid() {
        return Optional.ofNullable(grid);
    }

}
