package dev.vfyjxf.jeiutilities.ui.crating;

import dev.vfyjxf.jeiutilities.Constants;
import dev.vfyjxf.jeiutilities.math.Dimension;
import dev.vfyjxf.jeiutilities.ui.overlay.OverlayPlugin;
import dev.vfyjxf.jeiutilities.ui.widgets.WidgetGroup;
import mezz.jei.api.gui.IDrawable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class CraftingHelperPlugin implements OverlayPlugin {

    private final ResourceLocation PLUGIN_UID = new ResourceLocation(Constants.MOD_ID, "crafting_helper");


    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public WidgetGroup<?> init(Dimension size) {
        return null;
    }

    @Override
    public NBTTagCompound saveData() {
        return null;
    }

    @Override
    public void loadData(NBTTagCompound data) {

    }
}
