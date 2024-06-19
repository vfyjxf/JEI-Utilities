package dev.vfyjxf.jeiutilities.ui;

import net.minecraft.client.Minecraft;

public interface IRenderableTexture {

    int getWidth();

    int getHeight();

    default void render(Minecraft mc) {
        render(mc, 0, 0);
    }

    void render(Minecraft mc, int xOffset, int yOffset);

    default void render(Minecraft mc, int xOffset, int yOffset, int width, int height) {
        render(mc, xOffset, yOffset);
    }

    void render(Minecraft mc, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight);

}
