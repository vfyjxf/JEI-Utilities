package com.github.vfyjxf.jeiutilities.gui.filter;

import com.github.vfyjxf.jeiutilities.gui.elements.RenderableNineSliceTexture;
import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import com.github.vfyjxf.jeiutilities.search.RecipeFilter;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.recipes.RecipeLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.modIdHelper;
import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.registeredIngredients;

public class RecipesFilterContent implements IFilterLogicListener {

    private final RenderableNineSliceTexture background;
    private final List<RecipeLayout<?>> recipeLayouts = new ArrayList<>();
    private final RecipesFilterScreen filterScreen;
    private final FilterScreenLogic filterLogic;
    private final RecipeFilter recipeFilter;
    private ImmutableRect2i area;

    public RecipesFilterContent(RecipesFilterScreen filterScreen, FilterScreenLogic filterLogic, RecipeFilter recipeFilter) {
        this.filterScreen = filterScreen;
        this.filterLogic = filterLogic;
        this.recipeFilter = recipeFilter;
        this.filterLogic.addFocusListener(this);
        recipeFilter.addRecipeFilterContentListener(this::updateLayouts);
        this.background = JeiUtilitiesTextures.getInstance().getFilterContentBackground();
    }

    public void updateBounds(int x, int y, int width, int height) {
        this.area = new ImmutableRect2i(x, y, width, height);
    }

    @SuppressWarnings("unchecked")
    public <T> void updateLayouts() {
        recipeLayouts.clear();
        IRecipeCategory<T> recipeCategory = (IRecipeCategory<T>) filterLogic.getSelectedRecipeCategory();
        if (recipeCategory != null) {
            Collection<T> recipes = (Collection<T>) recipeFilter.getRecipes();
            int posX = 0;
            int poxY = 0;
            /**
             for (T recipe : recipes) {
             RecipeLayout<T> recipeLayout = RecipeLayout.create(-1, recipeCategory, recipe, filterLogic.getFocus(), registeredIngredients, modIdHelper, posX += 50, poxY += 50);
             recipeLayouts.add(recipeLayout);
             }
             */

        }
    }

    public ImmutableRect2i getArea() {
        return area;
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY) {
        this.background.draw(poseStack, this.area.getX(), this.area.getY(), this.area.getWidth(), this.area.getHeight());
//        for (RecipeLayout<?> recipeLayout : recipeLayouts) {
//            recipeLayout.drawRecipe(poseStack, mouseX, mouseY);
//        }
    }

    public void drawTooltips(PoseStack poseStack, int mouseX, int mouseY) {

    }

    @Override
    public void onFocusChange() {
        updateLayouts();
    }

    @Override
    public void onCategoryChange(IRecipeCategory<?> selectedCategory) {
        //:P
    }

}
