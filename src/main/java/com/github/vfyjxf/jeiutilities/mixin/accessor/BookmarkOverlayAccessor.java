package com.github.vfyjxf.jeiutilities.mixin.accessor;


import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.input.mouse.handlers.CheatInputHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BookmarkOverlay.class, remap = false)
public interface BookmarkOverlayAccessor {

    @Accessor("hasRoom")
    boolean getHasRoom();

    @Accessor("contents")
    IngredientGridWithNavigation getContents();

    @Accessor("cheatInputHandler")
    CheatInputHandler getCheatInputHandler();

    @Accessor("bookmarkButton")
    GuiIconToggleButton getBookmarkButton();

    @Accessor("parentArea")
    ImmutableRect2i getParentArea();

    @Accessor("hasRoom")
    void setHasRoom(boolean hasRoom);

    @Accessor("parentArea")
    void setParentArea(ImmutableRect2i parentArea);

}
