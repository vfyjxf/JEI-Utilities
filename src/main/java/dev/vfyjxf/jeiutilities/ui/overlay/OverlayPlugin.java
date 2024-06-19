package dev.vfyjxf.jeiutilities.ui.overlay;

import dev.vfyjxf.jeiutilities.math.Dimension;
import dev.vfyjxf.jeiutilities.ui.widgets.WidgetGroup;
import mezz.jei.api.gui.IDrawable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public interface OverlayPlugin {

    ResourceLocation getPluginUid();

    IDrawable getIcon();

    WidgetGroup<?> init(Dimension size);

    NBTTagCompound saveData();

    void loadData(NBTTagCompound data);

}
