package com.github.vfyjxf.jeiutilities;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.config.KeyBindings;
import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import com.github.vfyjxf.jeiutilities.gui.textures.SpritesManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(JeiUtilities.MOD_ID)
public class JeiUtilities {

    public static final String MOD_ID = "jeiutilities";
    public static final Logger logger = LogManager.getLogger("JeiUtilities");

    public JeiUtilities() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, JeiUtilitiesConfig.CLIENT_SPEC);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> KeyBindings::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterClientReloadListenersEvent);
    }
    @SubscribeEvent
    public void onRegisterClientReloadListenersEvent(RegisterClientReloadListenersEvent event) {
        SpritesManager spritesManager = new SpritesManager(Minecraft.getInstance().textureManager);
        JeiUtilitiesTextures textures = new JeiUtilitiesTextures(spritesManager);
        event.registerReloadListener(spritesManager);
    }


}
