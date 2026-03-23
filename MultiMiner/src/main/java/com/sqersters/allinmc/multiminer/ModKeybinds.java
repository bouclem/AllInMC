package com.sqersters.allinmc.multiminer;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Client-side keybind registration and toggle handling.
 */
public class ModKeybinds {

    public static final KeyMapping TOGGLE_MULTIMINER = new KeyMapping(
            "key.allinmc_multiminer.toggle",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_V, // Default: V key
            "key.categories.allinmc_multiminer"
    );

    private static boolean active = false;

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_MULTIMINER);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) return;

        while (TOGGLE_MULTIMINER.consumeClick()) {
            active = !active;
            PacketDistributor.sendToServer(new ActivationPayload(active));
        }
    }

    public static boolean isActive() {
        return active;
    }
}
