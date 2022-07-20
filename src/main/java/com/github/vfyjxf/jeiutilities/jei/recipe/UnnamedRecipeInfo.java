package com.github.vfyjxf.jeiutilities.jei.recipe;

import com.github.vfyjxf.jeiutilities.helper.IngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("rawtypes")
public class UnnamedRecipeInfo<R, T, V> extends BasedRecipeInfo<R, T, V> {

    public static final IIngredientType<UnnamedRecipeInfo> UNNAMED_RECIPE_INFO = () -> UnnamedRecipeInfo.class;

    public UnnamedRecipeInfo(
            IRecipeCategory<R> recipeCategory,
            T recipeOutput,
            V focusValue,
            boolean isInput,
            int recipeIndex
    ) {
        super(recipeCategory, recipeOutput, focusValue, isInput, recipeIndex);
    }

    @Override
    public @NotNull String getUniqueId() {
        return toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public IRecipeInfo copy() {
        return new UnnamedRecipeInfo(
                this.recipeCategory,
                this.recipeOutput,
                this.focusValue,
                this.isInput,
                this.index
        );
    }

    @Override
    public String toString() {
        return "{" +
                "\"category\":\"" + recipeCategory.getRecipeType().getUid() + "\"," +
                "\"output\":\"" + getIngredientUid(recipeOutput, UidContext.Ingredient) + "\"," +
                "\"focus\":\"" + getIngredientUid(focusValue, UidContext.Recipe) + "\"," +
                "\"isInput\":" + isInput + "," +
                "}";
    }
}

