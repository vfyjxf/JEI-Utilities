package com.github.vfyjxf.jeiutilities.helper;

import com.github.vfyjxf.jeiutilities.mixin.accessor.GhostIngredientDragManagerAccessor;

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.ghostIngredientDragManager;

public class DragHelper {

    public static boolean isDragging() {
        return ((GhostIngredientDragManagerAccessor) ghostIngredientDragManager).getGhostIngredientDrag() != null;
    }

}
