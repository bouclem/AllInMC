package com.sqersters.allinmc.quest;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import com.sqersters.allinmc.quest.client.SkillMenuScreen;
import com.sqersters.allinmc.quest.network.ModNetwork;
import com.sqersters.allinmc.quest.skill.SkillDataAttachment;
import com.sqersters.allinmc.quest.skill.SkillEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod(AllInMCQuest.MODID)
public class AllInMCQuest {
    public static final String MODID = "allinmc_quest";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final KeyMapping SKILLS_KEY = new KeyMapping(
            "key.allinmc_quest.open_skills",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "key.categories.allinmc"
    );

    public AllInMCQuest(IEventBus modEventBus, ModContainer modContainer) {
        // Register data attachment types
        SkillDataAttachment.ATTACHMENT_TYPES.register(modEventBus);

        // Register network payloads on the mod event bus
        modEventBus.addListener(ModNetwork::register);

        // Register keybinding on the mod event bus
        modEventBus.addListener(this::registerKeyMappings);

        // Register game event handlers on the NeoForge event bus
        NeoForge.EVENT_BUS.register(new SkillEvents());
        NeoForge.EVENT_BUS.register(this);

        LOGGER.info("All In MC: Quest loaded!");
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SKILLS_KEY);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (SKILLS_KEY.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen == null) {
                mc.setScreen(new SkillMenuScreen());
            }
        }
    }
}
