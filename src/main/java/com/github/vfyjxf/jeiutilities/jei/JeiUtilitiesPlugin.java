package com.github.vfyjxf.jeiutilities.jei;

import com.github.vfyjxf.jeiutilities.JeiUtilities;
import com.github.vfyjxf.jeiutilities.gui.filter.FilterGhostIngredientHandler;
import com.github.vfyjxf.jeiutilities.gui.filter.FilterTextContent;
import com.github.vfyjxf.jeiutilities.gui.filter.GuiFilterProperties;
import com.github.vfyjxf.jeiutilities.gui.filter.RecipesFilterScreen;
import com.github.vfyjxf.jeiutilities.gui.history.AdvancedIngredientListGrid;
import com.github.vfyjxf.jeiutilities.jei.recipe.NamedRecipeInfo;
import com.github.vfyjxf.jeiutilities.jei.recipe.RecipeInfoHelper;
import com.github.vfyjxf.jeiutilities.jei.recipe.RecipeInfoRenderer;
import com.github.vfyjxf.jeiutilities.jei.recipe.UnnamedRecipeInfo;
import com.github.vfyjxf.jeiutilities.mixin.accessor.IngredientListOverlayAccessor;
import com.github.vfyjxf.jeiutilities.mixin.accessor.RecipeGuiLogicAccessor;
import com.github.vfyjxf.jeiutilities.mixin.accessor.RecipeManagerAccessor;
import com.github.vfyjxf.jeiutilities.mixin.accessor.RecipesGuiAccessor;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.runtime.IBookmarkOverlay;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.ghost.GhostIngredientDragManager;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.ingredients.RegisteredIngredients;
import mezz.jei.recipes.RecipeManagerInternal;
import mezz.jei.recipes.RecipeTransferManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

@JeiPlugin
public class JeiUtilitiesPlugin implements IModPlugin {

    public static IRecipeManager recipeManager;
    public static RecipeManagerInternal managerInternal;
    public static IIngredientManager ingredientManager;
    public static IJeiHelpers jeiHelpers;
    public static IGuiHelper guiHelper;
    public static IModIdHelper modIdHelper;
    public static IFocusFactory focusFactory;
    public static IRecipeGuiLogic logic;
    public static RegisteredIngredients registeredIngredients;
    public static IIngredientListOverlay ingredientListOverlay;
    public static IBookmarkOverlay bookmarkOverlay;
    public static RecipesGui recipesGui;
    public static RecipeTransferManager recipeTransferManager;
    public static GhostIngredientDragManager ghostIngredientDragManager;
    public static AdvancedIngredientListGrid historyGrid;
    public static RecipesFilterScreen recipesFilterScreen;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(JeiUtilities.MOD_ID, "jei");
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        ingredientListOverlay = jeiRuntime.getIngredientListOverlay();
        bookmarkOverlay = jeiRuntime.getBookmarkOverlay();
        recipesGui = (RecipesGui) jeiRuntime.getRecipesGui();
        logic = ((RecipesGuiAccessor) recipesGui).getLogic();
        registeredIngredients = ((RecipeGuiLogicAccessor) logic).getRegisteredIngredients();
        recipeTransferManager = ((RecipesGuiAccessor) recipesGui).getRecipeTransferManager();
        ghostIngredientDragManager = ((IngredientListOverlayAccessor)ingredientListOverlay).getGhostIngredientDragManager();
        recipesFilterScreen = new RecipesFilterScreen(new FilterTextContent(), recipeManager);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerIngredients(@NotNull IModIngredientRegistration registration) {
        registration.register(NamedRecipeInfo.NAMED_RECIPE_INFO, Collections.emptyList(), RecipeInfoHelper.NamedRecipeInfoHelper, RecipeInfoRenderer.RECIPE_INFO_RENDERER);
        registration.register(UnnamedRecipeInfo.UNNAMED_RECIPE_INFO, Collections.emptyList(), RecipeInfoHelper.UnnamedRecipeInfoHelper, RecipeInfoRenderer.RECIPE_INFO_RENDERER);
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(RecipesFilterScreen.class, GuiFilterProperties::create);
        registration.addGhostIngredientHandler(RecipesFilterScreen.class, new FilterGhostIngredientHandler());
    }

    /**
     * Called by ASM.
     */
    @SuppressWarnings("unused")
    public static void setEarlyValue(IRecipeManager recipeManager, IJeiHelpers jeiHelpers, IIngredientManager ingredientManager) {
        JeiUtilitiesPlugin.recipeManager = recipeManager;
        JeiUtilitiesPlugin.managerInternal = ((RecipeManagerAccessor) recipeManager).getInternal();
        JeiUtilitiesPlugin.jeiHelpers = jeiHelpers;
        JeiUtilitiesPlugin.focusFactory = jeiHelpers.getFocusFactory();
        JeiUtilitiesPlugin.ingredientManager = ingredientManager;
        JeiUtilitiesPlugin.guiHelper = jeiHelpers.getGuiHelper();
        JeiUtilitiesPlugin.modIdHelper = jeiHelpers.getModIdHelper();
    }

}
