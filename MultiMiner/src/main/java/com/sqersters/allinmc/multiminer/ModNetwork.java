package com.sqersters.allinmc.multiminer;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Registers network payloads for client-server keybind sync.
 */
public class ModNetwork {

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        registrar.playToServer(
                ActivationPayload.TYPE,
                ActivationPayload.STREAM_CODEC,
                ModNetwork::handleActivation
        );
    }

    private static void handleActivation(ActivationPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            ActivationManager.setActive(player.getUUID(), payload.active());
        }
    }
}
