package com.github.vfyjxf.jeiutilities.search.filter;

import com.github.vfyjxf.jeiutilities.jei.recipe.RecipeData;

public class ModFilter extends AbstractIngredientFilter<String> {


    public ModFilter(char prefix, IRecipeDataGetter<String> recipeDataGetter) {
        super(prefix, recipeDataGetter);
    }

    @Override
    public boolean matches(String text, RecipeData<?> recipeData) {
        return this.getRecipeDataGetter().getData(recipeData)
                .parallelStream()
                .anyMatch(mod -> mod.contains(text));
    }
}
