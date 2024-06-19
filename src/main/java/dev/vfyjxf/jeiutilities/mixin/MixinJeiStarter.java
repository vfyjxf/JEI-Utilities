package dev.vfyjxf.jeiutilities.mixin;

import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.gui.GuiHelper;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.startup.JeiStarter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = JeiStarter.class, remap = false)
@SideOnly(Side.CLIENT)
public class MixinJeiStarter {

    @Redirect(method = "start", at = @At(
            value = "NEW",
            target = "(Lmezz/jei/bookmarks/BookmarkList;Lmezz/jei/gui/GuiHelper;Lmezz/jei/gui/GuiScreenHelper;)Lmezz/jei/gui/overlay/bookmarks/BookmarkOverlay;"
    ))
    private BookmarkOverlay redirectBookmarkOverlay(BookmarkList bookmarkList, GuiHelper guiHelper, GuiScreenHelper guiScreenHelper) {
        return null;
    }

}
