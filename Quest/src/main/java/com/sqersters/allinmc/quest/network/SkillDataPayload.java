package com.sqersters.allinmc.quest.network;

import com.sqersters.allinmc.quest.AllInMCQuest;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Payload sent from server to client to sync player skill data.
 */
public record SkillDataPayload(CompoundTag skillData) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SkillDataPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AllInMCQuest.MODID, "skill_data"));

    public static final StreamCodec<ByteBuf, SkillDataPayload> STREAM_CODEC =
            ByteBufCodecs.COMPOUND_TAG.map(SkillDataPayload::new, SkillDataPayload::skillData);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
