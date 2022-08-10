package com.github.vfyjxf.jeiutilities.search.filter;

import com.github.vfyjxf.jeiutilities.jei.recipe.RecipeData;

public interface IIngredientFilter {

    char getPrefix();

    boolean matches(String text,RecipeData<?> recipeData);

}
