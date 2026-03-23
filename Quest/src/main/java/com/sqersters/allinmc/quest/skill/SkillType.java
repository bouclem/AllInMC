package com.sqersters.allinmc.quest.skill;

import net.minecraft.util.StringRepresentable;

/**
 * All available skill types in the Quest mod.
 */
public enum SkillType implements StringRepresentable {
    COMBAT("combat"),
    MINING("mining"),
    WOODCUTTING("woodcutting"),
    FARMING("farming"),
    HEALING("healing");

    private final String name;

    SkillType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    /**
     * Returns the translation key for this skill's display name.
     */
    public String getTranslationKey() {
        return "skill.allinmc_quest." + name;
    }

    /**
     * Look up a SkillType by its serialized name.
     */
    public static SkillType fromName(String name) {
        for (SkillType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown skill type: " + name);
    }
}
