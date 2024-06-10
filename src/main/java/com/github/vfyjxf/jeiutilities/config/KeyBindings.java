package com.github.vfyjxf.jeiutilities.config;

import com.github.vfyjxf.jeiutilities.JeiUtilities;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = JeiUtilities.MODID, value = Side.CLIENT)
public final class KeyBindings {

    public static final KeyBinding displayRecipe;
    public static final KeyBinding pickBookmark;
    public static final KeyBinding transferRecipe;
    public static final KeyBinding transferRecipeMax;

    static {
        displayRecipe = new KeyBinding("key.jeiutilities.displayRecipe", KeyConflictContext.GUI, Keyboard.KEY_LCONTROL, JeiUtilities.NAME);
        pickBookmark = new KeyBinding("key.jeiutilities.pickBookmark", KeyConflictContext.GUI, -98, JeiUtilities.NAME);
        transferRecipe = new KeyBinding("key.jeiutilities.transferRecipe", KeyConflictContext.GUI, KeyModifier.CONTROL, Keyboard.KEY_W, JeiUtilities.NAME);
        transferRecipeMax = new KeyBinding("key.jeiutilities.transferRecipeMax", KeyConflictContext.GUI, KeyModifier.CONTROL, Keyboard.KEY_T, JeiUtilities.NAME);
    }

    public static void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(displayRecipe);
        ClientRegistry.registerKeyBinding(pickBookmark);
        ClientRegistry.registerKeyBinding(transferRecipe);
        ClientRegistry.registerKeyBinding(transferRecipeMax);
    }


    /**
     * @param keyBinding    The key binding to check.
     * @param checkModifier Whether to check the modifier(Usually when the KeyModifier of your KeyBinding is set to {@link net.minecraftforge.client.settings.KeyModifier#NONE}, but you want to allow it to be pressed at the same time as several other KeyModifiers is set to false.).
     * @return true if the key is down.
     */
    public static boolean isKeyDown(@Nonnull KeyBinding keyBinding, boolean checkModifier) {
        if (checkModifier) {
            return Keyboard.isKeyDown(keyBinding.getKeyCode()) &&
                    keyBinding.getKeyConflictContext().isActive() &&
                    keyBinding.getKeyModifier().isActive(keyBinding.getKeyConflictContext());
        } else {
            return Keyboard.isKeyDown(keyBinding.getKeyCode()) &&
                    keyBinding.getKeyConflictContext().isActive();
        }
    }

    public static boolean isKeyDown(@Nonnull KeyBinding keyBinding) {
        return isKeyDown(keyBinding, true);
    }

}
