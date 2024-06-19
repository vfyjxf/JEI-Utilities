package dev.vfyjxf.jeiutilities.jei.ingredient;


import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * A special version of {@link RecipeInfo}, used only to describe CraftingRecipe,
 * because CraftingRecipe has the recipeName feature,
 * we can simplify its storage and improve the speed of loading it.
 */
public class CraftingRecipeInfo extends RecipeInfo<ItemStack, ItemStack> {

    public static final IIngredientType<CraftingRecipeInfo> CRAFTING_RECIPE_INFO = () -> CraftingRecipeInfo.class;
    @Nonnull
    private final ResourceLocation registryName;

    public CraftingRecipeInfo(@Nonnull ItemStack ingredient,
                              @Nonnull ItemStack result,
                              String recipeCategoryUid,
                              int recipeIndex,
                              boolean isInputMode,
                              @Nonnull ICraftingRecipeWrapper recipeWrapper,
                              @Nonnull ResourceLocation registryName
    ) {
        super(ingredient, result, recipeCategoryUid, recipeIndex, isInputMode, recipeWrapper);
        this.registryName = registryName;
    }

    @Override
    public RecipeInfo<ItemStack, ItemStack> copy() {
        return new CraftingRecipeInfo(
                getIngredient(),
                getResult(),
                getRecipeCategoryUid(),
                getRecipeIndex(),
                isInputMode(),
                (ICraftingRecipeWrapper) getRecipeWrapper(),
                registryName
        );
    }

    @Nonnull
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public String toString() {
        return "{" +
                "\"ingredient\":\"" + getIngredientString(getIngredient()) + "\"," +
                "\"result\":\"" + getIngredientString(getResult()) + "\"," +
                "\"recipeCategoryUid\":\"" + getRecipeCategoryUid() + "\"," +
                "\"isInputMode\":" + isInputMode() + "," +
                "\"registryName\":\"" + getRegistryName() + "\"" +
                "}";
    }

}
