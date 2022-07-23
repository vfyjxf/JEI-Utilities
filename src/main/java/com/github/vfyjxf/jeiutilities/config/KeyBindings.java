package com.github.vfyjxf.jeiutilities.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;

public final class KeyBindings {

    public static final KeyMapping displayPreview;
    public static final KeyMapping transferRecipe;
    public static final KeyMapping transferRecipeMax;

    static {
        displayPreview = new KeyMapping("key.jeiutilities.displayPreview", KeyConflictContext.GUI, getKey(GLFW.GLFW_KEY_LEFT_CONTROL), "key.jeiutilities.category");
        transferRecipe = new KeyMapping("key.jeiutilities.transferRecipe", KeyConflictContext.GUI, KeyModifier.CONTROL, getKey(GLFW.GLFW_KEY_W), "key.jeiutilities.category");
        transferRecipeMax = new KeyMapping("key.jeiutilities.transferRecipeMax", KeyConflictContext.GUI, KeyModifier.CONTROL, getKey(GLFW.GLFW_KEY_T), "key.jeiutilities.category");
    }

    public static void init() {
        ClientRegistry.registerKeyBinding(displayPreview);
        ClientRegistry.registerKeyBinding(transferRecipe);
        ClientRegistry.registerKeyBinding(transferRecipeMax);
    }

    static InputConstants.Key getKey(int key) {
        return InputConstants.Type.KEYSYM.getOrCreate(key);
    }

    /**
     * @param key           The key binding to check.
     * @param checkModifier Whether to check the modifier(Usually when the KeyModifier of your KeyBinding is set to {@link net.minecraftforge.client.settings.KeyModifier#NONE}, but you want to allow it to be pressed at the same time as several other KeyModifiers is set to false.).
     * @param keyCode       The current keycode.
     * @return true if the key is down.
     */
    public static boolean isKeyDown(KeyMapping key, boolean checkModifier, InputConstants.Key keyCode) {
        if (checkModifier) {
            return key.isActiveAndMatches(keyCode);
        } else {
            return keyCode != InputConstants.UNKNOWN &&
                    keyCode.equals(key.getKey()) &&
                    key.getKeyConflictContext().isActive();
        }
    }

    public static boolean isKeyDown(Collection<KeyMapping> keys, boolean checkModifier, InputConstants.Key keyCode) {
        return keys.stream().anyMatch(key -> isKeyDown(key, checkModifier, keyCode));
    }

    public static boolean isKeyDown(KeyMapping key, InputConstants.Key keycode) {
        return isKeyDown(key, true, keycode);
    }

    public static boolean isKeyDown(KeyMapping key, boolean checkModifier) {
        if (checkModifier) {
            return key.isDown();
        } else {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue());
        }
    }

}
