package com.github.vfyjxf.jeiutilities.jei.recipe;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.helper.IngredientHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.recipes.FocusGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.focusFactory;
import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.ingredientManager;
import static com.github.vfyjxf.jeiutilities.jei.bookmark.RecipeBookmarkConfig.MARKER_OTHER;
import static com.github.vfyjxf.jeiutilities.jei.bookmark.RecipeBookmarkConfig.MARKER_STACK;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BasedRecipeInfo<R, T, V> implements IRecipeInfo<R, T, V> {

    protected final IRecipeCategory<R> recipeCategory;
    protected final R recipe;
    protected final T recipeOutput;
    protected final V focusValue;
    protected final boolean isInput;
    protected final int index;

    private final IIngredientHelper<T> ingredientHelper;
    private final IIngredientRenderer<T> ingredientRenderer;

    public static BasedRecipeInfo create(
            IRecipeCategory recipeCategory,
            Object recipe,
            Object recipeOutput,
            Object focusValue,
            boolean isInput,
            int recipeIndex
    ) {
        ResourceLocation registerName = recipeCategory.getRegistryName(recipe);
        if (registerName != null) {
            return new NamedRecipeInfo<>(recipeCategory, recipe, recipeOutput, focusValue, isInput, recipeIndex, registerName);
        } else {
            return new UnnamedRecipeInfo<>(recipeCategory, recipe, recipeOutput, focusValue, isInput, recipeIndex);
        }
    }

    public BasedRecipeInfo(
            IRecipeCategory<R> recipeCategory,
            R recipe,
            T recipeOutput,
            V focusValue,
            boolean isInput,
            int recipeIndex
    ) {
        this.recipeCategory = recipeCategory;
        this.ingredientHelper = ingredientManager.getIngredientHelper(recipeOutput);
        this.ingredientRenderer = ingredientManager.getIngredientRenderer(recipeOutput);
        this.recipeOutput = JeiUtilitiesConfig.getKeepOutputCount() ? ingredientHelper.copyIngredient(recipeOutput) : ingredientHelper.normalizeIngredient(recipeOutput);
        this.focusValue = ingredientManager.getIngredientHelper(focusValue).normalizeIngredient(focusValue);
        this.isInput = isInput;
        this.index = recipeIndex;
        this.recipe = recipe;
    }

    @Override
    public @NotNull IRecipeCategory<R> getRecipeCategory() {
        return this.recipeCategory;
    }

    @Override
    public R getRecipe() {
        return recipe;
    }

    @Override
    public int getRecipeIndex() {
        return this.index;
    }

    @Override
    public boolean isInput() {
        return this.isInput;
    }

    @Override
    public V getFocusValue() {
        return this.focusValue;
    }

    @Override
    public @NotNull List<IFocus<V>> getFocuses() {
        IIngredientType<V> ingredientType = ingredientManager.getIngredientType(this.focusValue);
        List<RecipeIngredientRole> roles = this.isInput ? List.of(RecipeIngredientRole.INPUT, RecipeIngredientRole.CATALYST) : List.of(RecipeIngredientRole.OUTPUT);
        return roles.stream()
                .map(role -> focusFactory.createFocus(role, ingredientType, this.focusValue))
                .collect(Collectors.toList());
    }

    @Override
    public IFocusGroup getFocusGroup() {
        return FocusGroup.create(this.getFocuses());
    }

    @Override
    public @NotNull T getOutput() {
        return this.recipeOutput;
    }

    @Override
    public IIngredientHelper<T> getIngredientHelper() {
        return this.ingredientHelper;
    }

    @Override
    public IIngredientRenderer<T> getIngredientRenderer() {
        return this.ingredientRenderer;
    }

    protected <E> String getIngredientUid(E ingredient, UidContext context) {
        if (ingredient instanceof ItemStack itemStack) {
            return MARKER_STACK + itemStack.save(new CompoundTag()).toString().replace("\"", "\\\"");
        } else {
            return MARKER_OTHER + IngredientHelper.getUniqueId(ingredient, context).replace("\"", "\\\"");
        }
    }

}
