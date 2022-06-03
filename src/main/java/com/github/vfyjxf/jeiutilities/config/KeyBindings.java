package com.github.vfyjxf.jeiutilities.config;

import com.github.vfyjxf.jeiutilities.JEIUtilities;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public final class KeyBindings {

    public static final KeyBinding displayRecipe = new KeyBinding("key.jeiutilities.displayRecipe", KeyConflictContext.GUI, Keyboard.KEY_LCONTROL, JEIUtilities.NAME);

    public static void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(displayRecipe);
    }

}
