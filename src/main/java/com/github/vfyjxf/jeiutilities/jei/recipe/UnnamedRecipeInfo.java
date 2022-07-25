package com.github.vfyjxf.jeiutilities.jei.recipe;

import com.github.vfyjxf.jeiutilities.helper.IngredientHelper;
import com.github.vfyjxf.jeiutilities.helper.RecipeHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.registeredIngredients;

/**
 * Used to record recipes that do not have a RegisterName, such as Brewing Recipe.
 */
@SuppressWarnings("rawtypes")
public class UnnamedRecipeInfo<R, T, V> extends BasedRecipeInfo<R, T, V> {

    public static final IIngredientType<UnnamedRecipeInfo> UNNAMED_RECIPE_INFO = () -> UnnamedRecipeInfo.class;

    public UnnamedRecipeInfo(
            IRecipeCategory<R> recipeCategory,
            R recipe,
            T recipeOutput,
            V focusValue,
            boolean isInput,
            int recipeIndex
    ) {
        super(recipeCategory, recipe, recipeOutput, focusValue, isInput, recipeIndex);
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
        return new UnnamedRecipeInfo(
                this.recipeCategory,
                this.recipe,
                this.recipeOutput,
                this.focusValue,
                this.isInput,
                this.index
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public IRecipeInfo normalizeIngredient() {
        return new UnnamedRecipeInfo(
                this.recipeCategory,
                this.recipe,
                this.getIngredientHelper().normalizeIngredient(this.recipeOutput),
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
                "\"inputs\":" + getInputsAsString() +
                "}";
    }

    private String getInputsAsString() {
        List<List<String>> recipeIngredientUidList = new ArrayList<>(RecipeHelper.getRecipeUidMap(this.recipe, this.recipeCategory, registeredIngredients).values());
        if (!recipeIngredientUidList.isEmpty()) {

            recipeIngredientUidList.sort((o1, o2) -> Integer.compare(o2.size(), o1.size()));

            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (List<String> ingredientUidList : recipeIngredientUidList) {
                builder.append("[");
                for (String ingredientUid : ingredientUidList) {
                    builder.append("\"")
                            .append(ingredientUid.replace("\"", "\\\""))
                            .append("\",");
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append("],");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("]");

            return builder.toString();
        }
        return "[]";
    }

    private static <E> String getIngredientUid(E ingredient) {
        if (ingredient instanceof ItemStack itemStack) {
            return itemStack.save(new CompoundTag()).toString();
        } else {
            return IngredientHelper.getUniqueId(ingredient, UidContext.Ingredient);
        }
    }

}

