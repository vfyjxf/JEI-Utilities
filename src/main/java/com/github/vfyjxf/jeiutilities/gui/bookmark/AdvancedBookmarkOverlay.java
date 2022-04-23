package com.github.vfyjxf.jeiutilities.gui.bookmark;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.gui.GuiHelper;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Set;

@SuppressWarnings("unused")
public class AdvancedBookmarkOverlay extends BookmarkOverlay {

    private static final int BUTTON_SIZE = 20;

    private final IngredientGridWithNavigation contents;
    private final GuiIconToggleButton recordConfigButton;

    public static BookmarkOverlay create(BookmarkList bookmarkList, GuiHelper guiHelper, GuiScreenHelper guiScreenHelper) {
        if (JeiUtilitiesConfig.getRecordRecipes()) {
            return new AdvancedBookmarkOverlay(bookmarkList, guiHelper, guiScreenHelper);
        } else {
            return new BookmarkOverlay(bookmarkList, guiHelper, guiScreenHelper);
        }
    }

    private AdvancedBookmarkOverlay(BookmarkList bookmarkList, GuiHelper guiHelper, GuiScreenHelper guiScreenHelper) {
        super(bookmarkList, guiHelper, guiScreenHelper);
        this.recordConfigButton = RecordConfigButton.create(this);
        this.contents = ObfuscationReflectionHelper.getPrivateValue(BookmarkOverlay.class, this, "contents");
    }

    @Override
    public void updateBounds(@Nonnull Rectangle area, @Nonnull Set<Rectangle> guiExclusionAreas) {
        super.updateBounds(area, guiExclusionAreas);
        Rectangle rectangle = new Rectangle(area);
        rectangle.x = contents.getArea().x;
        rectangle.width = contents.getArea().width;
        this.recordConfigButton.updateBounds(new Rectangle(
                rectangle.x + BUTTON_SIZE + 2,
                (int) Math.floor(rectangle.getMaxY()) - BUTTON_SIZE - 2,
                BUTTON_SIZE,
                BUTTON_SIZE
        ));
    }

    @Override
    public void drawScreen(@Nonnull Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(minecraft, mouseX, mouseY, partialTicks);
        this.recordConfigButton.draw(minecraft, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawTooltips(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {
        super.drawTooltips(minecraft, mouseX, mouseY);
        this.recordConfigButton.drawTooltips(minecraft, mouseX, mouseY);
    }

    @Override
    public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean result = super.handleMouseClicked(mouseX, mouseY, mouseButton);

        if (recordConfigButton.isMouseOver(mouseX, mouseY)) {
            return recordConfigButton.handleMouseClick(mouseX, mouseY);
        }

        return result;
    }
}
