package com.github.vfyjxf.jeiutilities.mixin.accessor;

import mezz.jei.gui.recipes.builder.IRecipeLayoutSlotSource;
import mezz.jei.gui.recipes.builder.RecipeLayoutBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

@Mixin(value = RecipeLayoutBuilder.class, remap = false)
public interface RecipeLayoutBuilderAccessor {

    @Invoker("slotStream")
    Stream<IRecipeLayoutSlotSource> invokerSlotStream();

}
