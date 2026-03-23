package com.sqersters.allinmc.multiminer;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Network payload sent from client to server when the player toggles the keybind.
 */
public record ActivationPayload(boolean active) implements CustomPacketPayload {

    public static final Type<ActivationPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AllInMCMultiminer.MODID, "activation")
    );

    public static final StreamCodec<ByteBuf, ActivationPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ActivationPayload::active,
                    ActivationPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
