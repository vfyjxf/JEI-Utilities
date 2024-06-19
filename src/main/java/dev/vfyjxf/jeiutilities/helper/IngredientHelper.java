package dev.vfyjxf.jeiutilities.helper;

import dev.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import dev.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.util.LegacyUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

public class IngredientHelper {
    public static <V> String getUniqueId(V ingredient) {

        if (ingredient == null) return RecipeInfo.NONE_MARK;

        if (ingredient instanceof ItemStack) {
            return ((ItemStack) ingredient).writeToNBT(new NBTTagCompound()).toString();
        }

        IIngredientHelper<V> ingredientHelper = JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(ingredient);
        return ingredientHelper.getUniqueId(ingredient);
    }

    public static IIngredientType<?> getIngredientType(List<String> ingredientUidList) {

        for (String ingredientUid : ingredientUidList) {
            //First check if the type corresponding to the uid is ItemStack.
            if (!getItemStackFromUid(ingredientUid).isEmpty()) {
                return VanillaTypes.ITEM;
            }

            for (IIngredientType<?> ingredientType : JeiUtilitiesPlugin.ingredientRegistry.getRegisteredIngredientTypes()) {
                if (JeiUtilitiesPlugin.ingredientRegistry.getIngredientByUid(ingredientType, ingredientUid) != null) {
                    return ingredientType;
                }
            }
        }

        return null;
    }

    public static <T> T getNormalize(@Nonnull T ingredient) {
        IIngredientHelper<T> ingredientHelper = JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(ingredient);
        T copy = LegacyUtil.getIngredientCopy(ingredient, ingredientHelper);
        if (copy instanceof ItemStack) {
            ((ItemStack) copy).setCount(1);
        } else if (copy instanceof FluidStack) {
            ((FluidStack) copy).amount = 1000;
        }
        return copy;
    }

    public static boolean ingredientEquals(Object ingredient1, Object ingredient2) {

        if (ingredient1 == ingredient2){
            return true;
        }

        if (ingredient1 == null || ingredient2 == null){
            return false;
        }

        if (ingredient1.getClass() == ingredient2.getClass()){
            IIngredientHelper<Object> ingredientHelper = JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(ingredient1);
            return ingredientHelper.getUniqueId(ingredient1).equals(ingredientHelper.getUniqueId(ingredient2));
        }

        return false;
    }

    private static ItemStack getItemStackFromUid(String uid) {
        try {
            NBTTagCompound itemStackAsNbt = JsonToNBT.getTagFromJson(uid);
            return new ItemStack(itemStackAsNbt);
        } catch (NBTException ignored) {
            //:p
        }
        return ItemStack.EMPTY;
    }

}


