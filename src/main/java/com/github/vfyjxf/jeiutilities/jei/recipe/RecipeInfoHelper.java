package com.github.vfyjxf.jeiutilities.jei.recipe;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class RecipeInfoHelper<V extends IRecipeInfo> implements IIngredientHelper<V> {

    public static final RecipeInfoHelper NamedRecipeInfoHelper = new RecipeInfoHelper<NamedRecipeInfo>() {

        @Override
        public @NotNull IIngredientType<NamedRecipeInfo> getIngredientType() {
            return NamedRecipeInfo.NAMED_RECIPE_INFO;
        }
    };

    public static final RecipeInfoHelper UnnamedRecipeInfoHelper = new RecipeInfoHelper<UnnamedRecipeInfo>() {

        @Override
        public @NotNull IIngredientType<UnnamedRecipeInfo> getIngredientType() {
            return UnnamedRecipeInfo.UNNAMED_RECIPE_INFO;
        }
    };

    @Override
    public @NotNull String getDisplayName(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().getDisplayName(ingredient.getOutput());
    }

    @Override
    public @NotNull String getUniqueId(@NotNull V ingredient, @NotNull UidContext context) {
        return ingredient.getUniqueId();
    }

    @SuppressWarnings("removal")
    @Override
    public @NotNull String getModId(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().getModId(ingredient.getOutput());
    }

    @SuppressWarnings("removal")
    @Override
    public @NotNull String getResourceId(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().getResourceId(ingredient.getOutput());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull V copyIngredient(@NotNull V ingredient) {
        return (V) ingredient.copy();
    }

    @Override
    public @NotNull String getErrorInfo(@Nullable V ingredient) {
        if (ingredient == null) {
            return "null";
        }
        return ingredient.getIngredientHelper().getErrorInfo(ingredient.getOutput());
    }
}
