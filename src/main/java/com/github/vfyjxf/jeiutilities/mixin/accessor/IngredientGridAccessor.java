package com.github.vfyjxf.jeiutilities.mixin.accessor;

import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.config.IIngredientGridConfig;
import mezz.jei.gui.overlay.IngredientGrid;
import mezz.jei.gui.overlay.IngredientGridTooltipHelper;
import mezz.jei.render.IngredientListRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = IngredientGrid.class,remap = false)
public interface IngredientGridAccessor {

    @Accessor
    IngredientListRenderer getIngredientListRenderer();

    @Accessor
    IIngredientGridConfig getGridConfig();

    @Accessor
    IngredientGridTooltipHelper getTooltipHelper();

    @Accessor
    void setArea(ImmutableRect2i area);

}
