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

    private static boolean enableHistory = true;

    private static boolean recordRecipes = true;

    private static RecordMode recordMode = RecordMode.ENABLE;

    private static int backgroundColour = 0xee555555;


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
        recordMode = RecordMode.valueOf(config.getString(
                "recordMode", CATEGORY_GENERAL, recordMode.name(),
                "Current mode of recording recipes." + "\n"
                        + "Enable: The opposite of RESTRICTED mode" + "\n"
                        + "Disable: Don't record any recipes" + "\n"
                        + "RESTRICTED: Marking a bookmark while holding down the shift key will record the recipe, and viewing the recipe while holding down the shift key will display the marked recipe"));
        backgroundColour = config.getInt("backgroundColour", CATEGORY_GENERAL, backgroundColour, Integer.MIN_VALUE, Integer.MAX_VALUE, "Color of the history area display");

        if (config.hasChanged()) {
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

    public static boolean getEnableHistory() {
        return enableHistory;
    }

    public static boolean getRecordRecipes() {
        return recordRecipes;
    }

    public static RecordMode getRecordMode() {
        return recordMode;
    }

    public static int getBackgroundColour() {
        return backgroundColour;
    }

    public static void setRecordMode(RecordMode mode) {
        JeiUtilitiesConfig.recordMode = mode;
        config.get(CATEGORY_GENERAL, "recordMode", recordMode.name(), "Current mode of recording recipes").set(mode.name());
        config.save();
    }

}
