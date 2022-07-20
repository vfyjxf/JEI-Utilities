package com.github.vfyjxf.jeiutilities.config;

import net.minecraft.network.chat.TranslatableComponent;

import java.util.Locale;

public enum RecordMode {
    DISABLE,
    ENABLE,
    RESTRICTED;

    public TranslatableComponent getLocalizedName() {
        return new TranslatableComponent("jeiutilities.button.name." + this.name().toLowerCase(Locale.ROOT));
    }
}
