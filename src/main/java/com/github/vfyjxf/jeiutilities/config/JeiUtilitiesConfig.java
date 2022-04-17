package com.github.vfyjxf.jeiutilities.config;

import com.github.vfyjxf.jeiutilities.JEIUtilities;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class JeiUtilitiesConfig {

    private static final String CATEGORY_GENERAL = "general";

    private static Configuration config;
    private static File modConfigFile;
    private static File bookmarkRecipeInfoFile;

    public static boolean enableHistory = true;

    public static boolean recordRecipes = true;

    public static int backgroundColour = 0xee555555;


    public static void preInit(FMLPreInitializationEvent event) {
        File configDir = new File(event.getModConfigurationDirectory(), JEIUtilities.MODID);

        modConfigFile = new File(configDir, JEIUtilities.MODID + ".cfg");
        bookmarkRecipeInfoFile = new File(configDir, "recipes.ini");
        config = new Configuration(modConfigFile);

        loadConfig();

    }

    private static void loadConfig() {

        if (config == null) {
            return;
        }

        config.load();

        enableHistory = config.getBoolean("enableHistory", CATEGORY_GENERAL, enableHistory, "Enable browsing history function");
        recordRecipes = config.getBoolean("recordRecipes", CATEGORY_GENERAL, recordRecipes, "Record current recipe when add ingredient to bookmark in recipe screen");
        backgroundColour = config.getInt("backgroundColour", CATEGORY_GENERAL, backgroundColour, Integer.MIN_VALUE, Integer.MAX_VALUE, "Color of the history area display");

        if (config.hasChanged()){
            config.save();
        }
    }

    public static Configuration getConfig() {
        return config;
    }

    public static File getModConfigFile() {
        return modConfigFile;
    }

    public static File getBookmarkRecipeInfoFile() {
        return bookmarkRecipeInfoFile;
    }

}
