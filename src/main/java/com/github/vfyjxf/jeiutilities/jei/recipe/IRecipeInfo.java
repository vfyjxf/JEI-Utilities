package com.github.vfyjxf.jeiutilities.jei.recipe;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Used to store the location of the recipe in the jei and other information.
 */
public interface IRecipeInfo<R, T, V> {

    String NONE_MARK = "none";

    /**
     * @return The recipe category that this recipe is in.
     */
    @NotNull
    IRecipeCategory<R> getRecipeCategory();

    /**
     * @return The register name of the recipe, which we will normally use to query the recipe.
     */
    @Nullable
    default ResourceLocation getRegistryName() {
        return null;
    }

    /**
     * Get the index of recipe in the RecipeCategory.
     *
     * @return The index of the recipe in RecipesGui, since some recipe storage implementations are unordered, it will get it by query at runtime.
     */
    int getRecipeIndex();

    /**
     *
     * @return The focus value.
     */
    V getFocusValue();

    /**
     *
     * @return The IFocus through which the recipe was queried.
     */
    @NotNull
    List<IFocus<V>> getFocuses();

    boolean isInput();

    /**
     * The recipe's output, which is only the one the player marked when marking the bookmark, and does not represent all the output.
     */
    @NotNull
    T getOutput();

    /**
     *
     * @return The unique id of the RecipeInfo.
     */
    @NotNull
    String getUniqueId();

    /**
     *
     * @return A copy of the RecipeInfo.
     */
    IRecipeInfo copy();

    /**
     *
     * @return The IIngredientHelper for the focus value.
     */
    IIngredientHelper<T> getIngredientHelper();

    /**
     *
     * @return The IIngredientRenderer for the focus value.
     */
    IIngredientRenderer<T> getIngredientRenderer();

}
