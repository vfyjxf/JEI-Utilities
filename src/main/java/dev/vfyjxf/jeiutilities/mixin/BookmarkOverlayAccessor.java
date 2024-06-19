package dev.vfyjxf.jeiutilities.mixin;

import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.awt.Rectangle;

@Mixin(value = BookmarkOverlay.class, remap = false)
@SideOnly(Side.CLIENT)
public interface BookmarkOverlayAccessor {
    @Accessor
    Rectangle getParentArea();

    @Accessor
    Rectangle getDisplayArea();

    @Accessor
    IngredientGridWithNavigation getContents();

    @Accessor
    GuiIconToggleButton getBookmarkButton();

    @Accessor
    boolean isHasRoom();

    @Accessor
    BookmarkList getBookmarkList();

    @Invoker
    static int callGetMinWidth() {
        throw new UnsupportedOperationException();
    }
}
