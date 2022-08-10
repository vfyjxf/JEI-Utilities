package com.github.vfyjxf.jeiutilities.search.filter;

import com.github.vfyjxf.jeiutilities.jei.recipe.RecipeData;

import java.util.Set;

public abstract class AbstractIngredientFilter<T> implements IIngredientFilter {

    private final char prefix;
    private final IRecipeDataGetter<T> recipeDataGetter;

    public AbstractIngredientFilter(char prefix, IRecipeDataGetter<T> recipeDataGetter) {
        this.prefix = prefix;
        this.recipeDataGetter = recipeDataGetter;
    }

    public char getPrefix() {
        return this.prefix;
    }

    public IRecipeDataGetter<T> getRecipeDataGetter() {
        return recipeDataGetter;
    }

    @FunctionalInterface
    public interface IRecipeDataGetter<T> {

        Set<T> getData(RecipeData<?> recipeData);

    }

}
