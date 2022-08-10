package com.github.vfyjxf.jeiutilities.search;

import com.github.vfyjxf.jeiutilities.gui.filter.FilterConfig;
import com.github.vfyjxf.jeiutilities.gui.filter.FilterScreenLogic;
import com.github.vfyjxf.jeiutilities.gui.filter.IFilterLogicListener;
import com.github.vfyjxf.jeiutilities.jei.recipe.RecipeData;
import com.github.vfyjxf.jeiutilities.search.filter.IIngredientFilter;
import com.github.vfyjxf.jeiutilities.search.filter.IngredientFilters;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.overlay.IFilterTextSource;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.modIdHelper;
import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.registeredIngredients;

public class RecipeFilter implements IFilterLogicListener {

    private final FilterTextParser textParser;
    private final IFilterTextSource filterTextSource;
    private final FilterConfig filterConfig;
    private final FilterScreenLogic filterLogic;
    private final Map<ResourceLocation, Set<?>> recipesCachedMap = new HashMap<>();
    /**
     * The Data of the recipes which we focus on.
     */
    private final Set<RecipeData<?>> recipeData = new HashSet<>();
    private final List<IRecipeFilterContentListener> listeners = new ArrayList<>();

    public RecipeFilter(IFilterTextSource filterTextSource, FilterConfig filterConfig, FilterScreenLogic filterLogic) {
        this.filterTextSource = filterTextSource;
        this.filterConfig = filterConfig;
        this.filterLogic = filterLogic;
        this.textParser = new FilterTextParser(new IngredientFilters());
        this.filterTextSource.addListener(filterText -> {
            recipesCachedMap.clear();
            notifyListenersOfChange();
        });
        this.filterLogic.addFocusListener(this);
    }

    public void addRecipeData(RecipeData<?> recipeData) {
        this.recipeData.add(recipeData);
    }

    public Set<?> getRecipes() {
        if (filterLogic.getSelectedRecipeCategory() == null) {
            return Collections.emptySet();
        }
        return getRecipes(filterLogic.getSelectedRecipeCategory().getRecipeType().getUid());
    }

    public Set<?> getRecipes(ResourceLocation recipeTypeUid) {
        String filterText = this.filterTextSource.getFilterText().toLowerCase();
        Set<?> cachedRecipes = this.recipesCachedMap.get(recipeTypeUid);
        if (cachedRecipes == null || cachedRecipes.isEmpty()) {
            cachedRecipes = searchRecipeUnCached(recipeTypeUid, filterText);
            this.recipesCachedMap.put(recipeTypeUid, cachedRecipes);
        }
        return cachedRecipes;
    }

    private Set<?> searchRecipeUnCached(ResourceLocation recipeTypeUid, String filterText) {
        List<FilterTextParser.MatchTokens> matchTokens = this.textParser.parseFilterText(filterText);
        Stream<?> recipesStream;
        if (matchTokens.isEmpty()) {
            recipesStream = filterLogic.getRecipes(recipeTypeUid).parallelStream();
        } else {
            recipesStream = matchTokens.stream()
                    .flatMap(this::getSearchResults)
                    .distinct();
        }
        return recipesStream.collect(Collectors.toSet());
    }

    private Stream<?> getSearchResults(FilterTextParser.MatchTokens matchTokens) {
        return matchTokens.toMatch().stream()
                .map(token -> {
                    String text = token.token();
                    if (text.isEmpty()) {
                        return Set.of();
                    }
                    IIngredientFilter filter = token.ingredientFilter();
                    return recipeData.parallelStream()
                            .filter(recipe -> filter.matches(text, recipe))
                            .map(RecipeData::getRecipe)
                            .collect(Collectors.toSet());
                })
                .flatMap(Set::stream);
    }

    @Override
    public void onFocusChange() {
        onFilterConfigChange();
    }

    @Override
    public void onCategoryChange(IRecipeCategory<?> selectedCategory) {
        onFilterConfigChange();
    }

    private <R> void onFilterConfigChange() {
        if (this.filterLogic.getSelectedRecipeCategory() != null) {
            this.recipeData.clear();
            this.recipesCachedMap.clear();
            Pair<IRecipeCategory<R>, List<R>> recipes = this.filterLogic.getSelectedRecipes();
            recipes.getRight().stream()
                    .map(recipe -> new RecipeData<>(recipe, recipes.getLeft(), registeredIngredients, modIdHelper, filterConfig))
                    .forEach(this::addRecipeData);
            notifyListenersOfChange();
        }
    }

    @FunctionalInterface
    public interface IRecipeFilterContentListener {
        void onFilterContentChanged();
    }

    public void addRecipeFilterContentListener(IRecipeFilterContentListener listener) {
        this.listeners.add(listener);
    }

    private void notifyListenersOfChange() {
        for (IRecipeFilterContentListener listener : listeners) {
            listener.onFilterContentChanged();
        }
    }

}
