package com.github.vfyjxf.jeiutilities.config;

import net.minecraft.client.resources.I18n;

import java.util.Locale;

/**
 * DISABLE : The ability to completely disable recipe logging
 * ENABLE : The opposite of RESTRICTED mode
 * RESTRICTED : Marking a bookmark while holding down the shift key will record the recipe, and viewing the recipe while holding down the shift key will display the marked recipe
 */
public enum RecordMode {
    DISABLE,
    ENABLE,
    RESTRICTED;

    public String getLocalizedName() {
        return I18n.format("jeiutilities.button.name." + this.name().toLowerCase(Locale.ROOT));
    }
}
