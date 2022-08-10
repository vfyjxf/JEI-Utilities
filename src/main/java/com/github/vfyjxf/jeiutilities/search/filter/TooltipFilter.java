package com.github.vfyjxf.jeiutilities.search.filter;

import com.github.vfyjxf.jeiutilities.jei.recipe.RecipeData;

import java.util.List;

public class TooltipFilter extends AbstractIngredientFilter<List<String>> {


    public TooltipFilter(char prefix, IRecipeDataGetter<List<String>> recipeDataGetter) {
        super(prefix, recipeDataGetter);
    }

    @Override
    public boolean matches(String text, RecipeData<?> recipeData) {
        return this.getRecipeDataGetter().getData(recipeData)
                .parallelStream()
                .anyMatch(tooltips -> tooltips.parallelStream().anyMatch(tooltip -> tooltip.contains(text)));
    }
}
