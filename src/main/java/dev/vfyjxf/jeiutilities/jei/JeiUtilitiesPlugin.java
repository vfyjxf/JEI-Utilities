package dev.vfyjxf.jeiutilities.jei;

import dev.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import dev.vfyjxf.jeiutilities.jei.ingredient.CraftingRecipeInfo;
import dev.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import dev.vfyjxf.jeiutilities.jei.ingredient.RecipeInfoHelper;
import dev.vfyjxf.jeiutilities.jei.ingredient.RecipeInfoRenderer;
import dev.vfyjxf.jeiutilities.ui.history.AdvancedIngredientGrid;
import mezz.jei.Internal;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.gui.overlay.IngredientGrid;
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
import java.util.Optional;

import static net.minecraftforge.fml.common.ObfuscationReflectionHelper.getPrivateValue;

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
    public static IngredientListOverlay ingredientListOverlay;
    public static IngredientGrid bookmarkIngredientGrid;
    public static IngredientGridWithNavigation bookmarkContents;
    /**
     * This field is set by asm.
     */
    public static IRecipeRegistry recipeRegistry;
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
        if (JeiUtilitiesConfig.getRecordRecipes()) {
            bookmarkOverlay = (BookmarkOverlay) jeiRuntime.getBookmarkOverlay();
            bookmarkList = getPrivateValue(BookmarkOverlay.class, bookmarkOverlay, "bookmarkList");
            bookmarkContents = getPrivateValue(BookmarkOverlay.class, bookmarkOverlay, "contents");
            bookmarkIngredientGrid = getPrivateValue(IngredientGridWithNavigation.class,
                    bookmarkContents,
                    "ingredientGrid");
        }
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        JeiUtilitiesPlugin.modRegistry = registry;
        ingredientRegistry = (IngredientRegistry) registry.getIngredientRegistry();
        JeiUtilitiesPlugin.guiHelper = registry.getJeiHelpers().getGuiHelper();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void registerIngredients(@Nonnull IModIngredientRegistration registry) {
        RecipeInfoHelper helper = new RecipeInfoHelper<>();
        RecipeInfoRenderer renderer = new RecipeInfoRenderer();
        registry.register(RecipeInfo.RECIPE_INFO, Collections.emptyList(), helper, renderer);
        registry.register(CraftingRecipeInfo.CRAFTING_RECIPE_INFO, Collections.emptyList(), helper, renderer);
    }

    public static Optional<AdvancedIngredientGrid> getGrid() {
        return Optional.ofNullable(grid);
    }

}
