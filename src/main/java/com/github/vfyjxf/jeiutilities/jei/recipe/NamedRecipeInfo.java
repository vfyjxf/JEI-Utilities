package com.github.vfyjxf.jeiutilities.jei.recipe;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
public class NamedRecipeInfo<R, T, V> extends BasedRecipeInfo<R, T, V> {

    public static final IIngredientType<NamedRecipeInfo> NAMED_RECIPE_INFO = () -> NamedRecipeInfo.class;

    private final ResourceLocation registerName;

    public NamedRecipeInfo(
            IRecipeCategory<R> recipeCategory,
            R recipe,
            T recipeOutput,
            V focusValue,
            boolean isInput,
            int recipeIndex,
            ResourceLocation registerName
    ) {
        super(recipeCategory, recipe, recipeOutput, focusValue, isInput, recipeIndex);
        this.registerName = registerName;
    }

    @Override
    public @Nullable ResourceLocation getRegistryName() {
        return this.registerName;
    }

    @Override
    public @NotNull String getUniqueId() {
        if (this.uniqueId == null) {
            this.uniqueId = toString();
        }
        return this.uniqueId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IRecipeInfo copy() {
        return new NamedRecipeInfo(
                this.recipeCategory,
                this.recipe,
                this.recipeOutput,
                this.focusValue,
                this.isInput,
                this.index,
                this.registerName
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public IRecipeInfo normalizeIngredient() {
        return new NamedRecipeInfo(
                this.recipeCategory,
                this.recipe,
                this.getIngredientHelper().normalizeIngredient(this.recipeOutput),
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
