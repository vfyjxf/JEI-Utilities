package com.github.vfyjxf.jeiutilities.jei.ingredient;

import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("rawtypes")
public class RecipeInfoHelper implements IIngredientHelper<RecipeInfo> {

    @Nullable
    @Override
    public RecipeInfo getMatch(@Nonnull Iterable<RecipeInfo> ingredients, @Nonnull RecipeInfo ingredientToMatch) {
        return null;
    }

    @Override
    @Nonnull
    public String getDisplayName(@Nonnull RecipeInfo ingredient) {
        return getIngredientHelper(ingredient.getResult()).getDisplayName(ingredient.getResult());
    }

    @Override
    @Nonnull
    public String getUniqueId(@Nonnull RecipeInfo ingredient) {
        return ingredient.toString();
    }

    @Override
    @Nonnull
    public String getWildcardId(@Nonnull RecipeInfo ingredient) {
        return getIngredientHelper(ingredient.getResult()).getWildcardId(ingredient.getResult());
    }

    @Override
    @Nonnull
    public String getModId(@Nonnull RecipeInfo ingredient) {
        return getIngredientHelper(ingredient.getResult()).getModId(ingredient.getResult());
    }

    @Override
    @Nonnull
    public String getResourceId(@Nonnull RecipeInfo ingredient) {
        return getIngredientHelper(ingredient.getResult()).getResourceId(ingredient.getResult());
    }

    @Override
    @Nonnull
    public RecipeInfo copyIngredient(@Nonnull RecipeInfo ingredient) {
        return ingredient.copy();
    }

    @Override
    @Nonnull
    public String getErrorInfo(@Nullable RecipeInfo ingredient) {
        if (ingredient == null) {
            return "null";
        }
        return getIngredientHelper(ingredient.getResult()).getErrorInfo(ingredient.getResult());
    }

    private <T> IIngredientHelper<T> getIngredientHelper(T ingredient) {
        return JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(ingredient);
    }

}

