package com.sqersters.allinmc.quest.skill;

import com.sqersters.allinmc.quest.AllInMCQuest;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Registers the player skill data attachment type.
 */
public class SkillDataAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AllInMCQuest.MODID);

    public static final Supplier<AttachmentType<PlayerSkillData>> PLAYER_SKILLS = ATTACHMENT_TYPES.register(
            "player_skills",
            () -> AttachmentType.builder(PlayerSkillData::new)
                    .serialize(PlayerSkillData.CODEC)
                    .copyOnDeath()
                    .build()
    );
}
