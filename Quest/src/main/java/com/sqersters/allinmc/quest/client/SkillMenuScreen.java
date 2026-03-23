package com.sqersters.allinmc.quest.client;

import com.sqersters.allinmc.quest.skill.PlayerSkillData;
import com.sqersters.allinmc.quest.skill.SkillBonuses;
import com.sqersters.allinmc.quest.skill.SkillDataAttachment;
import com.sqersters.allinmc.quest.skill.SkillType;
import com.sqersters.allinmc.quest.skill.SkillUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * GUI screen that displays all skill levels, XP progress, and bonus percentages.
 */
public class SkillMenuScreen extends Screen {

    private static final int BAR_WIDTH = 150;
    private static final int BAR_HEIGHT = 10;
    private static final int ROW_HEIGHT = 50;

    public SkillMenuScreen() {
        super(Component.translatable("screen.allinmc_quest.skills_title"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (Minecraft.getInstance().player == null) return;

        PlayerSkillData data = Minecraft.getInstance().player.getData(SkillDataAttachment.PLAYER_SKILLS);

        int centerX = this.width / 2;
        int startY = 40;

        // Title
        graphics.drawCenteredString(this.font, this.title, centerX, 15, 0xFFFFFF);

        SkillType[] skills = SkillType.values();
        for (int i = 0; i < skills.length; i++) {
            SkillType type = skills[i];
            int y = startY + (i * ROW_HEIGHT);

            int level = data.getLevel(type);
            int currentXp = data.getXp(type);
            int requiredXp = SkillUtils.xpRequiredForLevel(level);

            // Skill name
            Component skillName = Component.translatable(type.getTranslationKey());
            graphics.drawString(this.font, skillName, centerX - BAR_WIDTH / 2, y, 0xFFFFFF);

            // Level text
            String levelText = String.format("Level %d", level);
            int levelTextWidth = this.font.width(levelText);
            graphics.drawString(this.font, levelText, centerX + BAR_WIDTH / 2 - levelTextWidth, y, 0xAAFFAA);

            // XP bar background
            int barY = y + 14;
            int barX = centerX - BAR_WIDTH / 2;
            graphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, 0xFF333333);

            // XP bar fill
            float progress = (level >= SkillUtils.MAX_LEVEL) ? 1.0f : (float) currentXp / requiredXp;
            int fillWidth = (int) (BAR_WIDTH * progress);
            graphics.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT, 0xFF44AA44);

            // XP bar border
            graphics.renderOutline(barX, barY, BAR_WIDTH, BAR_HEIGHT, 0xFF888888);

            // XP text centered on bar
            String xpText;
            if (level >= SkillUtils.MAX_LEVEL) {
                xpText = "MAX";
            } else {
                xpText = currentXp + " / " + requiredXp + " XP";
            }
            int xpTextWidth = this.font.width(xpText);
            graphics.drawString(this.font, xpText,
                    centerX - xpTextWidth / 2, barY + 1, 0xFFFFFF);

            // Bonus text below the bar
            int bonusPercent = SkillBonuses.getBonusPercent(type, level);
            Component bonusText = Component.translatable(
                    SkillBonuses.getBonusDescription(type), bonusPercent
            );
            graphics.drawString(this.font, bonusText, barX, barY + BAR_HEIGHT + 2, 0xFFDD88);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
