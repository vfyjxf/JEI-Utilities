package com.github.vfyjxf.jeiutilities;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.config.KeyBindings;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.ui.bookmark.BookmarkInputHandler;
import com.github.vfyjxf.jeiutilities.ui.common.GuiInputHandler;
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

@Mod(modid = JeiUtilities.MODID,
        name = JeiUtilities.NAME,
        version = JeiUtilities.VERSION,
        dependencies = JeiUtilities.DEPENDENCIES,
        guiFactory = JeiUtilities.GUI_FACTORY,
        clientSideOnly = true
)
public class JeiUtilities {
    public static final String MODID = "jeiutilities";
    public static final String NAME = "JEI Utilities";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDENCIES = "required-after:jei";
    public static final String GUI_FACTORY = "com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfigGuiFactory";

    public static final Logger logger = LogManager.getLogger(JeiUtilities.NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        JeiUtilitiesConfig.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("JEI Utilities Initializing...");
        if (JeiUtilitiesConfig.getRecordRecipes()) {
            MinecraftForge.EVENT_BUS.register(BookmarkInputHandler.getInstance());
        }
        MinecraftForge.EVENT_BUS.register(GuiInputHandler.getInstance());
        KeyBindings.registerKeyBindings();
    }

    @EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        if (JeiUtilitiesConfig.getRecordRecipes() || JeiUtilitiesConfig.isEnableHistory()) {
            JeiUtilitiesPlugin.inputHandler = ObfuscationReflectionHelper.getPrivateValue(Internal.class, null, "inputHandler");
            if (JeiUtilitiesConfig.getRecordRecipes()) {
                BookmarkInputHandler.getInstance().onInputHandlerSet();
            }
        }
        logger.info("JEI Utilities Loading Complete...");
    }


}
