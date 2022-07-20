package com.github.vfyjxf.jeiutilities.gui.bookmark;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.config.KeyBindings;
import com.github.vfyjxf.jeiutilities.config.RecordMode;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.input.UserInput;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecordConfigButton extends GuiIconToggleButton {

    private RecordMode currentMode;
    private final BookmarkOverlay bookmarkOverlay;

    public static RecordConfigButton create(BookmarkOverlay bookmarkOverlay) {
        IDrawable offIcon = JeiUtilitiesPlugin.guiHelper.drawableBuilder(new ResourceLocation("jeiutilities:textures/gui/icon/bookmark_button_config_disable.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        IDrawable onIcon = JeiUtilitiesPlugin.guiHelper.drawableBuilder(new ResourceLocation("jeiutilities:textures/gui/icon/bookmark_button_config_enable.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        return new RecordConfigButton(offIcon, onIcon, bookmarkOverlay);
    }

    public RecordConfigButton(IDrawable offIcon, IDrawable onIcon, BookmarkOverlay bookmarkOverlay) {
        super(offIcon, onIcon);
        this.currentMode = JeiUtilitiesConfig.getRecordMode();
        this.bookmarkOverlay = bookmarkOverlay;
    }

    public void setRecordMode(RecordMode mode) {
        this.currentMode = mode;
        JeiUtilitiesConfig.setRecordMode(mode);
    }

    @Override
    protected void getTooltips(@NotNull List<Component> tooltip) {
        tooltip.add(new TranslatableComponent("jeiutilities.tooltip.recording"));
        tooltip.add(new TranslatableComponent("jeiutilities.tooltip.recording.mode", this.currentMode.getLocalizedName()).withStyle(ChatFormatting.GRAY));
        if (currentMode == RecordMode.RESTRICTED) {
            tooltip.add(new TranslatableComponent("jeiutilities.tooltip.recording.description.restricted").withStyle(ChatFormatting.GRAY));
        }
        if (currentMode == RecordMode.ENABLE) {
            tooltip.add(new TranslatableComponent("jeiutilities.tooltip.recording.description.enable").withStyle(ChatFormatting.GRAY));
        }
        if (currentMode != RecordMode.DISABLE) {
            tooltip.add(new TranslatableComponent("jeiutilities.tooltip.recording.information_1", KeyBindings.displayPreview.getTranslatedKeyMessage()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    protected boolean isIconToggledOn() {
        return this.currentMode != RecordMode.DISABLE;
    }

    @Override
    protected boolean onMouseClicked(@NotNull UserInput input) {
        if (this.bookmarkOverlay.hasRoom()) {
            if (!input.isSimulate()) {
                int ordinal = this.currentMode.ordinal() + 1;
                if (ordinal >= RecordMode.values().length) {
                    ordinal = 0;
                }
                this.setRecordMode(RecordMode.values()[ordinal]);
            }
            return true;
        }
        return false;
    }

}
