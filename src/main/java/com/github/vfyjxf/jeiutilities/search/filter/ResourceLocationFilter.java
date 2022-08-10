package com.github.vfyjxf.jeiutilities.search.filter;

import com.github.vfyjxf.jeiutilities.jei.recipe.RecipeData;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationFilter extends AbstractIngredientFilter<ResourceLocation> {


    public ResourceLocationFilter(char prefix, IRecipeDataGetter<ResourceLocation> recipeDataGetter) {
        super(prefix, recipeDataGetter);
    }

    @Override
    public boolean matches(String text, RecipeData<?> recipeData) {
        return this.getRecipeDataGetter().getData(recipeData)
                .parallelStream()
                .anyMatch(resourceLocation -> resourceLocation.getPath().contains(text));
    }
}
