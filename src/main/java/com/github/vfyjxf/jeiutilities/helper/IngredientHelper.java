package com.github.vfyjxf.jeiutilities.helper;

import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
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

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.ingredientRegistry;

public class IngredientHelper {
    public static <V> String getUniqueId(V ingredient) {

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

            for (IIngredientType<?> ingredientType : ingredientRegistry.getRegisteredIngredientTypes()) {
                if (ingredientRegistry.getIngredientByUid(ingredientType, ingredientUid) != null) {
                    return ingredientType;
                }
            }
        }

        return null;
    }

    public static <T> T getNormalize(@Nonnull T ingredient) {
        IIngredientHelper<T> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredient);
        T copy = LegacyUtil.getIngredientCopy(ingredient, ingredientHelper);
        if (copy instanceof ItemStack) {
            ((ItemStack) copy).setCount(1);
        } else if (copy instanceof FluidStack) {
            ((FluidStack) copy).amount = 1000;
        }
        return copy;
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


