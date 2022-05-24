package com.github.vfyjxf.jeiutilities.jei;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.gui.history.AdvancedIngredientGrid;
import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfoHelper;
import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfoRenderer;
import mezz.jei.Internal;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.ingredients.IngredientListElementFactory;
import mezz.jei.ingredients.IngredientOrderTracker;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.input.InputHandler;
import mezz.jei.runtime.JeiRuntime;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.util.Collections;

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
    public static IGuiHelper guiHelper;
    public static BookmarkOverlay bookmarkOverlay;
    public static BookmarkList bookmarkList;
    /**
     * This field is set by asm.
     */
    public static IRecipeRegistry recipeRegistry;
    private static AdvancedIngredientGrid grid = new AdvancedIngredientGrid();

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        JeiUtilitiesPlugin.jeiRuntime = Internal.getRuntime();
        if (JeiUtilitiesConfig.isEnableHistory()) {
            ObfuscationReflectionHelper.setPrivateValue(
                    IngredientGridWithNavigation.class,
                    ObfuscationReflectionHelper.getPrivateValue(IngredientListOverlay.class, (IngredientListOverlay) jeiRuntime.getIngredientListOverlay(), "contents"),
                    grid,
                    "ingredientGrid"
            );
        }
        ORDER_TRACKER = ObfuscationReflectionHelper.getPrivateValue(IngredientListElementFactory.class, null, "ORDER_TRACKER");
        bookmarkOverlay = (BookmarkOverlay) jeiRuntime.getBookmarkOverlay();
        bookmarkList = ObfuscationReflectionHelper.getPrivateValue(BookmarkOverlay.class, bookmarkOverlay, "bookmarkList");
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        JeiUtilitiesPlugin.modRegistry = registry;
        ingredientRegistry = (IngredientRegistry) registry.getIngredientRegistry();
        JeiUtilitiesPlugin.guiHelper = registry.getJeiHelpers().getGuiHelper();
    }

    @Override
    public void registerIngredients(@Nonnull IModIngredientRegistration registry) {
        registry.register(RecipeInfo.RECIPE_INFO, Collections.emptyList(), new RecipeInfoHelper(), new RecipeInfoRenderer());
    }

    public static AdvancedIngredientGrid getGrid() {
        return grid;
    }

}
