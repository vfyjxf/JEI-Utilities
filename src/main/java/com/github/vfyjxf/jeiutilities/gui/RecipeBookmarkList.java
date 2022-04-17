package com.github.vfyjxf.jeiutilities.gui;

import com.github.vfyjxf.jeiutilities.JEIUtilities;
import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.util.LegacyUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecipeBookmarkList {

    private static final String MARKER_OTHER = "O:";
    private static final String MARKER_STACK = "T:";

    private final List<RecipeInfo<?, ?>> recipeInfoList = new ArrayList<>();


    public void addRecipeInfo(@Nonnull RecipeInfo<?, ?> recipeInfo) {

        for (RecipeInfo<?, ?> info : recipeInfoList) {
            if (info == recipeInfo) {
                return;
            }

            if (info.isRecipeInfoEquals(recipeInfo)) {
                return;
            }
        }

        this.recipeInfoList.add(recipeInfo);
        saveRecipeInfo();
    }

    public <V> void removeRecipeInfo(@Nonnull V ingredient) {
        recipeInfoList.removeIf(info -> info.isResultEquals(ingredient));
    }

    public RecipeInfo<?, ?> getRecipeInfo(Object result) {
        for (RecipeInfo<?, ?> recipeInfo : recipeInfoList) {
            if (recipeInfo.isResultEquals(result)) {
                return recipeInfo;
            }
        }
        return null;
    }

    private static <T> T normalize(T ingredient) {
        IIngredientHelper<T> ingredientHelper = JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(ingredient);
        T copy = LegacyUtil.getIngredientCopy(ingredient, ingredientHelper);
        if (copy instanceof ItemStack) {
            ((ItemStack) copy).setCount(1);
        } else if (copy instanceof FluidStack) {
            ((FluidStack) copy).amount = 1000;
        }
        return copy;
    }

    private void saveRecipeInfo() {
        List<String> strings = new ArrayList<>();
        for (RecipeInfo<?, ?> recipeInfo : recipeInfoList) {
            strings.add(recipeInfo.toString());
        }
        File recipeInfoFile = JeiUtilitiesConfig.getBookmarkRecipeInfoFile();
        if (recipeInfoFile != null) {
            try (FileWriter writer = new FileWriter(recipeInfoFile)) {
                IOUtils.writeLines(strings, "\n", writer);
            } catch (IOException e) {
                JEIUtilities.logger.error("Failed to save recipes info list to file {}", recipeInfoFile, e);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void loadRecipeInfo() {
        File recipeInfoFile = JeiUtilitiesConfig.getBookmarkRecipeInfoFile();
        if (recipeInfoFile == null || !recipeInfoFile.exists()) {
            return;
        }

        List<String> recipesInfoStrings;
        try (FileReader reader = new FileReader(recipeInfoFile)) {
            recipesInfoStrings = IOUtils.readLines(reader);
        } catch (IOException e) {
            JEIUtilities.logger.error("Failed to load bookmarks from file {}", recipeInfoFile, e);
            return;
        }

        Collection<IIngredientType> otherIngredientTypes = new ArrayList<>(JeiUtilitiesPlugin.ingredientRegistry.getRegisteredIngredientTypes());
        otherIngredientTypes.remove(VanillaTypes.ITEM);

        recipeInfoList.clear();

        for (String recipeInfoString : recipesInfoStrings) {
            if (recipeInfoString.isEmpty()) {
                continue;
            }
            RecipeInfo<?, ?> recipeInfo = loadInfoFromJson(recipeInfoString, otherIngredientTypes);
            if (recipeInfo == null) {
                continue;
            }
            recipeInfoList.add(recipeInfo);
        }

    }

    @SuppressWarnings("rawtypes")
    private Object loadIngredientFromJson(String ingredientString, Collection<IIngredientType> otherIngredientTypes) {
        if (ingredientString.startsWith(MARKER_STACK)) {
            String itemStackAsJson = ingredientString.substring(MARKER_STACK.length());
            try {
                NBTTagCompound itemStackAsNbt = JsonToNBT.getTagFromJson(itemStackAsJson);
                ItemStack itemStack = new ItemStack(itemStackAsNbt);
                if (!itemStack.isEmpty()) {
                    return normalize(itemStack);
                } else {
                    JEIUtilities.logger.warn("Failed to load recipe info ItemStack from json string, the item no longer exists:\n{}", itemStackAsJson);
                }
            } catch (NBTException e) {
                JEIUtilities.logger.error("Failed to load bookmarked ItemStack from json string:\n{}", itemStackAsJson, e);
            }
        } else if (ingredientString.startsWith(MARKER_OTHER)) {
            String uid = ingredientString.substring(MARKER_OTHER.length());
            Object ingredient = getUnknownIngredientByUid(otherIngredientTypes, uid);
            if (ingredient != null) {
                return normalize(ingredient);
            }
        } else {
            JEIUtilities.logger.error("Failed to load unknown recipe info:\n{}", ingredientString);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public RecipeInfo<?, ?> loadInfoFromJson(String recipeInfoString, Collection<IIngredientType> otherIngredientTypes) {
        JsonObject jsonObject;
        try {
            jsonObject = new JsonParser().parse(recipeInfoString).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            JEIUtilities.logger.error("Failed to parse recipe info string {}", recipeInfoString, e);
            return null;
        }

        String ingredient = jsonObject.get("ingredient").getAsString();
        String result = jsonObject.get("result").getAsString();
        int recipeCategoryIndex = Integer.parseInt(jsonObject.get("recipeCategoryIndex").getAsString());
        int recipeIndex = Integer.parseInt(jsonObject.get("recipeIndex").getAsString());
        boolean isInputMode = Boolean.parseBoolean(jsonObject.get("isInputMode").getAsString());

        Object ingredientObject = loadIngredientFromJson(ingredient, otherIngredientTypes);
        Object resultObject = loadIngredientFromJson(result, otherIngredientTypes);
        if (ingredientObject == null || resultObject == null) {
            JEIUtilities.logger.error("Failed to load recipe info from json string:\n{}", recipeInfoString);
            return null;
        }
        return new RecipeInfo<>(ingredientObject, resultObject, recipeCategoryIndex, recipeIndex, isInputMode);
    }

    @Nullable
    @SuppressWarnings("rawtypes")
    private Object getUnknownIngredientByUid(Collection<IIngredientType> ingredientTypes, String uid) {
        for (IIngredientType<?> ingredientType : ingredientTypes) {
            Object ingredient = JeiUtilitiesPlugin.ingredientRegistry.getIngredientByUid(ingredientType, uid);
            if (ingredient != null) {
                return ingredient;
            }
        }
        return null;
    }

    public static class RecipeInfo<V, T> {

        private final V ingredient;
        private final T result;
        private final int recipeCategoryIndex;
        private final int recipeIndex;
        private final boolean isInputMode;

        public RecipeInfo(V outputIngredient, T result, int recipeCategoryIndex, int recipeIndex, boolean isInputMode) {
            this.ingredient = outputIngredient;
            this.result = result;
            this.recipeCategoryIndex = recipeCategoryIndex;
            this.recipeIndex = recipeIndex;
            this.isInputMode = isInputMode;
        }

        public V getIngredient() {
            return ingredient;
        }

        public T getResult() {
            return result;
        }

        public int getRecipeCategoryIndex() {
            return recipeCategoryIndex;
        }

        public int getRecipeIndex() {
            return recipeIndex;
        }

        public boolean isInputMode() {
            return isInputMode;
        }

        public boolean isRecipeInfoEquals(@Nonnull RecipeInfo<?, ?> otherInfo) {
            if (this.result.getClass() == otherInfo.result.getClass()) {
                IIngredientHelper<Object> ingredientHelper = JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(result);
                if (ingredientHelper.getUniqueId(this.result).equals(ingredientHelper.getUniqueId(otherInfo.result))) {
                    return this.recipeCategoryIndex == otherInfo.recipeCategoryIndex && this.recipeIndex == otherInfo.recipeIndex;
                }
            }

            return false;
        }

        public boolean isResultEquals(Object result) {
            if (this.result.getClass() == result.getClass()) {
                IIngredientHelper<Object> ingredientHelper = JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(result);
                return ingredientHelper.getUniqueId(this.result).equals(ingredientHelper.getUniqueId(result));
            }
            return false;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"ingredient\":\"" + getIngredientString(ingredient) + "\"," +
                    "\"result\":\"" + getIngredientString(result) + "\"," +
                    "\"recipeCategoryIndex\":" + recipeCategoryIndex + "," +
                    "\"recipeIndex\":" + recipeIndex + "," +
                    "\"isInputMode\":" + isInputMode +
                    "}";

        }

        private <I> String getIngredientString(I ingredient) {
            if (ingredient instanceof ItemStack) {
                //replace " -> \"
                return MARKER_STACK + ((ItemStack) ingredient).writeToNBT(new NBTTagCompound()).toString().replace("\"", "\\\"");
            } else {
                IIngredientHelper<I> ingredientHelper = JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(ingredient);
                return ingredientHelper.getUniqueId(ingredient);
            }
        }

    }

}

