package com.github.vfyjxf.jeiutilities.jei.ingredient;

import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RecipeInfoHelper<T extends RecipeInfo> implements IIngredientHelper<T> {

    @Nullable
    @Override
    public T getMatch(@Nonnull Iterable<T> ingredients, @Nonnull T ingredientToMatch) {
        return null;
    }

    @Override
    @Nonnull
    public String getDisplayName(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getDisplayName(ingredient.getResult());
    }

    @Override
    @Nonnull
    public String getUniqueId(@Nonnull T ingredient) {
        return ingredient.toString();
    }

    @Override
    @Nonnull
    public String getWildcardId(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getWildcardId(ingredient.getResult());
    }

    @Override
    @Nonnull
    public String getModId(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getModId(ingredient.getResult());
    }

    @Override
    @Nonnull
    public String getResourceId(@Nonnull T ingredient) {
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

    private <E> IIngredientHelper<E> getIngredientHelper(E ingredient) {
        return JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(ingredient);
    }

}

