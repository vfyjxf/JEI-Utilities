package com.github.vfyjxf.jeiutilities.gui.bookmark;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.config.RecordMode;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.util.Translator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.util.List;

public class RecordConfigButton extends GuiIconToggleButton {

    private RecordMode currentMode;
    private final BookmarkOverlay bookmarkOverlay;

    public static RecordConfigButton create(BookmarkOverlay bookmarkOverlay) {
        IDrawable offIcon = JeiUtilitiesPlugin.guiHelper.drawableBuilder(new ResourceLocation("jeiutilities:textures/gui/icon/bookmark_button.config.disable.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        IDrawable onIcon = JeiUtilitiesPlugin.guiHelper.drawableBuilder(new ResourceLocation("jeiutilities:textures/gui/icon/bookmark_button.config.enable.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        return new RecordConfigButton(offIcon, onIcon, bookmarkOverlay);
    }

    private RecordConfigButton(IDrawable offIcon, IDrawable onIcon, BookmarkOverlay bookmarkOverlay) {
        super(offIcon, onIcon);
        this.currentMode = JeiUtilitiesConfig.getRecordMode();
        this.bookmarkOverlay = bookmarkOverlay;
    }

    public void setRecordMode(RecordMode mode) {
        this.currentMode = mode;
    }

    @Override
    public void getTooltips(@Nonnull List<String> tooltip) {
        tooltip.add(Translator.translateToLocal("jeiutilities.tooltip.recording"));
        tooltip.add(TextFormatting.GRAY + Translator.translateToLocalFormatted("jeiutilities.tooltip.recording.mode", this.currentMode.getLocalizedName()));
        tooltip.add(TextFormatting.GRAY + Translator.translateToLocal("jeiutilities.tooltip.recording.mode.description"));
    }

    @Override
    public boolean isIconToggledOn() {
        return this.currentMode != RecordMode.DISABLE;
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY) {
        if (this.bookmarkOverlay.hasRoom()) {
            int ordinal = Mouse.getEventButton() != 2 ? this.currentMode.ordinal() + 1 : this.currentMode.ordinal() - 1;
            if (ordinal >= RecordMode.values().length) {
                ordinal = 0;
            }
            if (ordinal < 0) {
                ordinal = RecordMode.values().length - 1;
            }
            this.setRecordMode(RecordMode.values()[ordinal]);
            JeiUtilitiesConfig.setRecordMode(this.currentMode);
            return true;
        }
        return false;
    }
}
