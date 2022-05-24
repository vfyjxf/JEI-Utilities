package com.github.vfyjxf.jeiutilities.config;

import net.minecraft.client.resources.I18n;

import java.util.Locale;

/**
 * DISABLE : The ability to completely disable recipe logging.
 * ENABLE : The opposite of RESTRICTED mode.
 * RESTRICTED : You need to hold down Shift to view the recorded recipe or record recipe.
 */
public enum RecordMode {
    DISABLE,
    ENABLE,
    RESTRICTED;

    public String getLocalizedName() {
        return I18n.format("jeiutilities.button.name." + this.name().toLowerCase(Locale.ROOT));
    }
}
