package com.github.vfyjxf.jeiutilities.jei.recipe;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

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

    @Override
    public @NotNull String getWildcardId(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().getWildcardId(ingredient.getOutput());
    }

    @Override
    public @NotNull String getDisplayModId(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().getDisplayModId(ingredient.getOutput());
    }

    @Override
    public @NotNull Iterable<Integer> getColors(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().getColors(ingredient.getOutput());
    }

    @Override
    public @NotNull ResourceLocation getResourceLocation(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().getResourceLocation(ingredient.getOutput());
    }

    @Override
    public @NotNull ItemStack getCheatItemStack(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().getCheatItemStack(ingredient.getOutput());
    }

    @Override
    public @NotNull V normalizeIngredient(@NotNull V ingredient) {
        return (V) ingredient.normalizeIngredient();
    }

    @Override
    public boolean isValidIngredient(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().isIngredientOnServer(ingredient.getOutput());
    }

    @Override
    public boolean isIngredientOnServer(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().isIngredientOnServer(ingredient.getOutput());
    }

    @Override
    public @NotNull Collection<ResourceLocation> getTags(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().getTags(ingredient.getOutput());
    }

    @Override
    public @NotNull Collection<String> getCreativeTabNames(@NotNull V ingredient) {
        return ingredient.getIngredientHelper().getCreativeTabNames(ingredient.getOutput());
    }

    @Override
    public @NotNull Optional<ResourceLocation> getTagEquivalent(@NotNull Collection<V> ingredients) {
        Collection<?> realIngredients = ingredients.stream().map(IRecipeInfo::getOutput).toList();
        return ingredients.stream()
                .findAny()
                .map(IRecipeInfo::getIngredientHelper)
                .flatMap(helper -> helper.getTagEquivalent(realIngredients));
    }

}
