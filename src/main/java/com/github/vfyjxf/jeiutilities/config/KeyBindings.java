package com.github.vfyjxf.jeiutilities.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.List;

public final class KeyBindings {

    public static final KeyMapping displayPreview;
    public static final List<KeyMapping> transferRecipe;
    public static final KeyMapping transferRecipeMax;

    private static final List<KeyMapping> allBindings;

    static {
        KeyMapping transferRecipe1;
//        KeyMapping transferRecipe2;
        allBindings = List.of(
                displayPreview = new KeyMapping("key.jeiutilities.displayPreview", KeyConflictContext.GUI, getKey(GLFW.GLFW_KEY_LEFT_CONTROL), "key.jeiutilities.category"),
                transferRecipe1 = new KeyMapping("key.jeiutilities.transferRecipe", KeyConflictContext.GUI, getKey(GLFW.GLFW_KEY_W), "key.jeiutilities.category"),
//                transferRecipe2 = new KeyMapping("key.jeiutilities.transferRecipe", KeyConflictContext.GUI, InputConstants.Type.MOUSE, InputConstants.MOUSE_BUTTON_LEFT, "key.jeiutilities.category"),
                transferRecipeMax = new KeyMapping("key.jeiutilities.transferRecipeMax", KeyConflictContext.GUI, getKey(GLFW.GLFW_KEY_T), "key.jeiutilities.category")
        );

//        transferRecipe = List.of(transferRecipe1, transferRecipe2);
        transferRecipe = List.of(transferRecipe1);
    }

    public static void init() {
        for (KeyMapping keyMapping : allBindings) {
            ClientRegistry.registerKeyBinding(keyMapping);
        }
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

    public static boolean isKeyDown(Collection<KeyMapping> keys, InputConstants.Key keyCode) {
        return isKeyDown(keys, true, keyCode);
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

    public static boolean isKeyDown(KeyMapping key) {
        return key.isDown();
    }

    public static boolean isKeyDown(Collection<KeyMapping> keys) {
        return keys.stream().anyMatch(keyMapping -> isKeyDown(keyMapping, false));
    }

    public static boolean isKeyDown(InputConstants.Key keycode) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keycode.getValue());
    }

}
