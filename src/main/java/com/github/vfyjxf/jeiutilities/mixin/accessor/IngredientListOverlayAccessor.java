package com.github.vfyjxf.jeiutilities.mixin.accessor;

import mezz.jei.gui.ghost.GhostIngredientDragManager;
import mezz.jei.gui.overlay.IngredientListOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IngredientListOverlay.class)
public interface IngredientListOverlayAccessor {
    @Accessor
    GhostIngredientDragManager getGhostIngredientDragManager();
}
