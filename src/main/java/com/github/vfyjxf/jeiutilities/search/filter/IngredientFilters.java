package com.github.vfyjxf.jeiutilities.search.filter;

import com.github.vfyjxf.jeiutilities.jei.recipe.RecipeData;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

public class IngredientFilters {

    private final Char2ObjectMap<IIngredientFilter> filters = new Char2ObjectOpenHashMap<>();

    public static final IIngredientFilter NO_PREFIX_FILTER = new IngredientTextFilter('\0', RecipeData::getRecipeIngredientNames);

    public IngredientFilters() {
        addFilter(new ModFilter('@', RecipeData::getAllModIds));
        addFilter(new TooltipFilter('#', RecipeData::getAllTooltipStrings));
        addFilter(new IngredientTagFilter('$', RecipeData::getAllTagStrings));
        addFilter(new CreativeTabFilter('%', RecipeData::getAllCreativeTabStrings));
        addFilter(new ResourceLocationFilter('&', RecipeData::getAllResourceLocations));
    }

    private void addFilter(IIngredientFilter filter) {
        if (filters.containsKey(filter.getPrefix())) {
            throw new IllegalArgumentException("Filter for prefix " + filter.getPrefix() + " already registered");
        } else {
            filters.put(filter.getPrefix(), filter);
        }
    }

    public IIngredientFilter getFilter(char prefix) {
        return filters.get(prefix);
    }

}

