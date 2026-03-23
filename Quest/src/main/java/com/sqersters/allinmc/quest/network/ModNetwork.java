package com.sqersters.allinmc.quest.network;

import com.sqersters.allinmc.quest.skill.PlayerSkillData;
import com.sqersters.allinmc.quest.skill.SkillDataAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Registers network payloads and handles incoming skill data on the client.
 */
public class ModNetwork {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                SkillDataPayload.TYPE,
                SkillDataPayload.STREAM_CODEC,
                ModNetwork::handleSkillDataOnClient
        );
    }

    private static void handleSkillDataOnClient(final SkillDataPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                PlayerSkillData incoming = PlayerSkillData.fromTag(payload.skillData());
                player.getData(SkillDataAttachment.PLAYER_SKILLS).copyFrom(incoming);
            }
        });
    }
}
