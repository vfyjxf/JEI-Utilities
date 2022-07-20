package com.github.vfyjxf.jeiutilities.jei.recipe;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("rawtypes")
public class NamedRecipeInfo<R, T, V> extends BasedRecipeInfo<R, T, V> {

    public static final IIngredientType<NamedRecipeInfo> NAMED_RECIPE_INFO = () -> NamedRecipeInfo.class;

    private final ResourceLocation registerName;

    public NamedRecipeInfo(
            IRecipeCategory<R> recipeCategory,
            T recipeOutput,
            V focusValue,
            boolean isInput,
            int recipeIndex,
            ResourceLocation registerName
    ) {
        super(recipeCategory, recipeOutput, focusValue, isInput, recipeIndex);
        this.registerName = registerName;
    }

    @Override
    public @Nullable ResourceLocation getRegistryName() {
        return this.registerName;
    }

    @Override
    public @NotNull String getUniqueId() {
        return toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public IRecipeInfo copy() {
        return new NamedRecipeInfo(
                this.recipeCategory,
                this.recipeOutput,
                this.focusValue,
                this.isInput,
                this.index,
                this.registerName
        );
    }

    public String toString() {
        return "{" +
                "\"category\":\"" + recipeCategory.getRecipeType().getUid() + "\"," +
                "\"output\":\"" + getIngredientUid(recipeOutput, UidContext.Ingredient) + "\"," +
                "\"focus\":\"" + getIngredientUid(focusValue, UidContext.Recipe) + "\"," +
                "\"isInput\":" + isInput + "," +
                "\"registerName\":\"" + registerName + "\"" +
                "}";
    }

}
