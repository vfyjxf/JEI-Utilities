package com.github.vfyjxf.jeiutilities.jei;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import mezz.jei.api.recipe.IFocus;

@SuppressWarnings("unused")
public final class JeiHooks {

    private JeiHooks() {
    }

    /**
     * Since using events to implement add history requires too much processing, we decided to use asm.
     */
    public static <V> void onSetFocus(IFocus<V> focus) {
        if (JeiUtilitiesConfig.isEnableHistory()) {
            JeiUtilitiesPlugin.getGrid().ifPresent(grid -> grid.addHistoryIngredient(focus.getValue()));
        }
    }

}
