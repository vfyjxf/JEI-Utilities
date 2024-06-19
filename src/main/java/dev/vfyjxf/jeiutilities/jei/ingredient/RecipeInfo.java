package dev.vfyjxf.jeiutilities.jei.ingredient;

import dev.vfyjxf.jeiutilities.helper.IngredientHelper;
import dev.vfyjxf.jeiutilities.helper.ReflectionUtils;
import dev.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.ingredients.Ingredients;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.vfyjxf.jeiutilities.jei.bookmark.RecipeBookmarkList.MARKER_OTHER;
import static dev.vfyjxf.jeiutilities.jei.bookmark.RecipeBookmarkList.MARKER_STACK;

/**
 * Wrap the recipe inside this class to allow jei bookmarks to accept the same ingredients as the elements in the bookmark.
 */
@SuppressWarnings("rawtypes")
public class RecipeInfo<R, I> {

    public static final IIngredientType<RecipeInfo> RECIPE_INFO = () -> RecipeInfo.class;
    public static final String NONE_MARK = "none";

    private final I ingredient;
    private final R result;
    private final String recipeCategoryUid;
    private final int recipeIndex;
    private final boolean isInputMode;
    /**
     * The runtime points to the saved recipe, and this object is used at runtime to compare recipes for equality.
     */
    private final IRecipeWrapper recipeWrapper;

    public static RecipeInfo create(
            @Nonnull Object ingredient,
            @Nonnull Object result,
            String recipeCategoryUid,
            int recipeIndex,
            boolean isInputMode,
            @Nonnull IRecipeWrapper recipeWrapper
    ) {
        if (recipeWrapper instanceof ICraftingRecipeWrapper) {
            ICraftingRecipeWrapper craftingWrapper = (ICraftingRecipeWrapper) recipeWrapper;
            if (craftingWrapper.getRegistryName() != null && ingredient instanceof ItemStack && result instanceof ItemStack) {
                return new CraftingRecipeInfo(
                        (ItemStack) ingredient,
                        (ItemStack) result,
                        recipeCategoryUid,
                        recipeIndex,
                        isInputMode,
                        craftingWrapper,
                        craftingWrapper.getRegistryName()
                );
            }
        }

        return new RecipeInfo<>(ingredient, result, recipeCategoryUid, recipeIndex, isInputMode, recipeWrapper);
    }

    public RecipeInfo(@Nonnull I ingredient,
                      @Nonnull R result,
                      String recipeCategoryUid,
                      int recipeIndex,
                      boolean isInputMode,
                      @Nonnull IRecipeWrapper recipeWrapper
    ) {
        this.ingredient = ingredient;
        this.result = result;
        this.recipeCategoryUid = recipeCategoryUid;
        this.recipeIndex = recipeIndex;
        this.isInputMode = isInputMode;
        this.recipeWrapper = recipeWrapper;
    }

    public I getIngredient() {
        return ingredient;
    }

    @Nonnull
    public R getResult() {
        return result;
    }

    public String getRecipeCategoryUid() {
        return recipeCategoryUid;
    }

    public int getRecipeIndex() {
        return recipeIndex;
    }

    public IFocus.Mode getMode() {
        return isInputMode ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT;
    }

    public boolean isInputMode() {
        return isInputMode;
    }

    public IIngredientRenderer<?> getResultIngredientRenderer() {
        return JeiUtilitiesPlugin.ingredientRegistry.getIngredientRenderer(this.result);
    }

    public IRecipeWrapper getRecipeWrapper() {
        return recipeWrapper;
    }

    public RecipeInfo<R, I> copy() {
        return new RecipeInfo<>(
                this.ingredient,
                this.result,
                this.recipeCategoryUid,
                this.recipeIndex,
                this.isInputMode,
                this.recipeWrapper
        );
    }

    @Override
    public String toString() {
        return "{" +
                "\"ingredient\":\"" + getIngredientString(ingredient) + "\"," +
                "\"result\":\"" + getIngredientString(result) + "\"," +
                "\"recipeCategoryUid\":\"" + recipeCategoryUid + "\"," +
                "\"isInputMode\":" + isInputMode + "," +
                "\"inputs\":" + getInputsString() +
                "}";
    }

    protected <T> String getIngredientString(T ingredient) {
        if (ingredient instanceof ItemStack) {
            //replace " -> \"
            return MARKER_STACK + ((ItemStack) ingredient).writeToNBT(new NBTTagCompound()).toString().replace("\"", "\\\"");
        } else {
            return MARKER_OTHER + IngredientHelper.getUniqueId(ingredient).replace("\"", "\\\"");
        }
    }

    protected String getInputsString() {
        IIngredients recipeIngredients = new Ingredients();
        recipeWrapper.getIngredients(recipeIngredients);
        Map<IIngredientType, List<List>> allInputs = ReflectionUtils.getFieldValue(
                Ingredients.class,
                recipeIngredients,
                "inputs"
        );
        List<List<String>> recipeIngredientUidList = new ArrayList<>();

        for (List<List> inputsPerType : allInputs.values()) {
            List<String> ingredientUidList = new ArrayList<>();
            for (List inputPerSlot : inputsPerType) {
                if (inputPerSlot.isEmpty()) {
                    ingredientUidList.add(RecipeInfo.NONE_MARK);
                } else {
                    ingredientUidList.add(IngredientHelper.getUniqueId(inputPerSlot.get(0)));
                }
            }
            recipeIngredientUidList.add(ingredientUidList);
        }

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

}
