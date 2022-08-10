package com.github.vfyjxf.jeiutilities.gui.filter;

import mezz.jei.api.recipe.category.IRecipeCategory;

public interface IFilterLogicListener {
    void onFocusChange();
    void onCategoryChange(IRecipeCategory<?> selectedCategory);
}
