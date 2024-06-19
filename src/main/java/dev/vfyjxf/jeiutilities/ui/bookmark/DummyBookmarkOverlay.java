package dev.vfyjxf.jeiutilities.ui.bookmark;

import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.gui.GuiHelper;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;

public class DummyBookmarkOverlay extends BookmarkOverlay {
    public DummyBookmarkOverlay(BookmarkList bookmarkList, GuiHelper guiHelper, GuiScreenHelper guiScreenHelper) {
        super(bookmarkList, guiHelper, guiScreenHelper);
    }
}
