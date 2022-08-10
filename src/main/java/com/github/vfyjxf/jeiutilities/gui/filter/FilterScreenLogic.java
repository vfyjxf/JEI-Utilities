package com.github.vfyjxf.jeiutilities.gui.filter;

import com.github.vfyjxf.jeiutilities.helper.IngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.recipes.FocusedRecipes;
import mezz.jei.recipes.FocusGroup;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FilterScreenLogic {

    private final FilterConfig filterConfig;
    private final IRecipeManager recipeManager;

    private final List<IFilterLogicListener> listeners = new ArrayList<>();
    /**
     * Raw data for matching
     */
    private final Map<ResourceLocation, FocusedRecipes<?>> recipeDataMap = new HashMap<>();
    private final List<IRecipeCategory<?>> recipeCategories = new ArrayList<>();
    @Nullable
    private IRecipeCategory<?> selectedCategory;
    @Nullable
    private ITypedIngredient<?> focusValue;

    public FilterScreenLogic(IRecipeManager recipeManager, FilterConfig filterConfig) {
        this.recipeManager = recipeManager;
        this.filterConfig = filterConfig;
    }

    public boolean setFocus(@NotNull ITypedIngredient<?> typedIngredient) {
        this.focusValue = typedIngredient;
        IFocusGroup focusGroup = IngredientHelper.createFocusGroup(typedIngredient, filterConfig.isFocusInput());
        return this.setFocus(focusGroup);
    }

    public boolean setFocus(@NotNull List<IFocus<?>> focuses) {
        if (!focuses.isEmpty()) {
            this.focusValue = focuses.get(0).getTypedValue();
        }
        return this.setFocus(FocusGroup.create(focuses));
    }

    public boolean setFocus(@NotNull IFocusGroup focuses) {
        this.recipeCategories.clear();
        this.recipeDataMap.clear();
        List<IRecipeCategory<?>> recipeCategories = recipeManager.createRecipeCategoryLookup()
                .limitFocus(focuses.getAllFocuses())
                .get()
                .toList();
        this.recipeCategories.addAll(recipeCategories);
        if (recipeCategories.isEmpty()) {
            this.notifyListenersOfChange();
            return false;
        }

        for (IRecipeCategory<?> recipeCategory : recipeCategories) {
            this.recipeDataMap.put(recipeCategory.getRecipeType().getUid(), FocusedRecipes.create(focuses, recipeManager, recipeCategory));
        }
        this.notifyListenersOfChange();
        return true;
    }

    public IFocusGroup getFocus() {
        return focusValue == null ? FocusGroup.EMPTY : IngredientHelper.createFocusGroup(focusValue, filterConfig.isFocusInput());
    }

    public void cleanFocus() {
        this.focusValue = null;
        this.setFocus(FocusGroup.EMPTY);
        this.notifyListenersOfChange();
    }

    public void onFocusModeChange() {
        if (this.focusValue != null) {
            this.setFocus(this.focusValue);
        }
    }

    public void onFilterModeChange() {
        if (this.focusValue != null) {
            notifyListenersOfChange();
        }
    }

    public void setCategory(@Nullable IRecipeCategory<?> selectedCategory) {
        if (!isCategoryEquals(selectedCategory)) {
            this.selectedCategory = selectedCategory;
            for (IFilterLogicListener listener : this.listeners) {
                listener.onCategoryChange(selectedCategory);
            }
        }
    }

    @Nullable
    public IRecipeCategory<?> getSelectedRecipeCategory() {
        return selectedCategory;
    }

    public List<IRecipeCategory<?>> getRecipeCategories() {
        return recipeCategories;
    }

    public <T> Pair<IRecipeCategory<T>, List<T>> getSelectedRecipes() {
        if (this.selectedCategory == null) {
            return Pair.of(null, new ArrayList<>());
        }
        //noinspection unchecked
        IRecipeCategory<T> recipeCategory = (IRecipeCategory<T>) this.selectedCategory;

        return Pair.of(recipeCategory, getRecipes(recipeCategory));
    }

    public <T> List<T> getRecipes(@NotNull IRecipeCategory<T> recipeCategory) {
        return getRecipes(recipeCategory.getRecipeType().getUid());
    }

    public <T> List<T> getRecipes(@NotNull ResourceLocation recipeTypeUid) {
        //noinspection unchecked
        FocusedRecipes<T> focusedRecipes = (FocusedRecipes<T>) this.recipeDataMap.get(recipeTypeUid);
        return focusedRecipes == null ? new ArrayList<>() : focusedRecipes.getRecipes();
    }


    public List<?> getAllRecipes() {
        return this.recipeDataMap.values().stream()
                .map(FocusedRecipes::getRecipes)
                .collect(Collectors.toList());
    }

    public void addFocusListener(@NotNull IFilterLogicListener lister) {
        this.listeners.add(lister);
    }

    private boolean isCategoryEquals(@Nullable IRecipeCategory<?> recipeCategory) {

        if (this.selectedCategory == recipeCategory) {
            return true;
        }

        if (this.selectedCategory == null || recipeCategory == null) {
            return false;
        }

        ResourceLocation categoryUid = recipeCategory.getRecipeType().getUid();
        ResourceLocation selectedCategoryUid = selectedCategory.getRecipeType().getUid();
        return categoryUid.equals(selectedCategoryUid);
    }

    private void notifyListenersOfChange() {
        for (IFilterLogicListener listener : this.listeners) {
            listener.onFocusChange();
        }
    }

}
