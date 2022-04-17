package com.github.vfyjxf.jeiutilities;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.gui.BookmarkInputHandler;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.Internal;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = JEIUtilities.MODID,
        name = JEIUtilities.NAME,
        version = JEIUtilities.VERSION,
        dependencies = JEIUtilities.DEPENDENCIES,
        clientSideOnly = true
)
public class JEIUtilities {
    public static final String MODID = "jeiutilities";
    public static final String NAME = "JEI Utilities";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDENCIES = "required-after:jei";

    public static final Logger logger = LogManager.getLogger(JEIUtilities.NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        JeiUtilitiesConfig.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (JeiUtilitiesConfig.recordRecipes) {
            MinecraftForge.EVENT_BUS.register(BookmarkInputHandler.getInstance());
        }
    }

    @EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        if (JeiUtilitiesConfig.recordRecipes || JeiUtilitiesConfig.enableHistory) {
            JeiUtilitiesPlugin.inputHandler = ObfuscationReflectionHelper.getPrivateValue(Internal.class, null, "inputHandler");
            if (JeiUtilitiesConfig.recordRecipes) {
                BookmarkInputHandler.onInputHandlerSet();
                BookmarkInputHandler.getInstance().getRecipeBookmarkList().loadRecipeInfo();
            }
        }
    }


}