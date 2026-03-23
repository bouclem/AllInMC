package com.sqersters.allinmc.configs;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod(AllInMCConfigs.MODID)
public class AllInMCConfigs {
    public static final String MODID = "allinmc_configs";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Keybind to open the config menu (default: K)
    public static final KeyMapping CONFIG_KEY = new KeyMapping(
            "key.allinmc_configs.open",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.allinmc"
    );

    public AllInMCConfigs(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerKeyMappings);
        NeoForge.EVENT_BUS.register(this);

        // Register config screen factory so this mod shows a config button in the Mods menu
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (container, parent) -> new ModListScreen(parent));

        LOGGER.info("All In MC: Configs loaded!");
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(CONFIG_KEY);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (CONFIG_KEY.consumeClick()) {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.screen == null) {
                mc.setScreen(new ModListScreen(null));
            }
        }
    }
}
