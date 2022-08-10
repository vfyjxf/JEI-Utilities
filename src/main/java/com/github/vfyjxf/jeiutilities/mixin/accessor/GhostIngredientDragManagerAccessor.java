package com.github.vfyjxf.jeiutilities.mixin.accessor;

import mezz.jei.gui.ghost.GhostIngredientDrag;
import mezz.jei.gui.ghost.GhostIngredientDragManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GhostIngredientDragManager.class)
public interface GhostIngredientDragManagerAccessor {
    @Accessor
    GhostIngredientDrag<?> getGhostIngredientDrag();
}
