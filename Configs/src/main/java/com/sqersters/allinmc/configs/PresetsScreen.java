package com.sqersters.allinmc.configs;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Screen for managing config presets for a specific mod.
 * Allows saving the current config, loading, and deleting presets.
 */
public class PresetsScreen extends Screen {

    @Nullable
    private final Screen parent;
    private final String modId;
    private final String modDisplayName;

    private EditBox nameField;
    private int scrollOffset = 0;
    private static final int ENTRY_HEIGHT = 24;
    private static final int MAX_VISIBLE = 6;

    public PresetsScreen(@Nullable Screen parent, String modId, String modDisplayName) {
        super(Component.literal("Presets: " + modDisplayName));
        this.parent = parent;
        this.modId = modId;
        this.modDisplayName = modDisplayName;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;

        // Name input field
        nameField = new EditBox(this.font, centerX - 110, 40, 170, 20, Component.literal("Preset Name"));
        nameField.setMaxLength(32);
        nameField.setHint(Component.literal("Preset name..."));
        this.addRenderableWidget(nameField);

        // Save button next to the name field
        this.addRenderableWidget(Button.builder(Component.literal("Save"), btn -> {
            String name = nameField.getValue().trim();
            if (!name.isEmpty()) {
                ConfigPresets.savePreset(modId, name);
                nameField.setValue("");
                rebuildPresetList();
            }
        }).bounds(centerX + 65, 40, 45, 20).build());

        rebuildPresetList();
    }

    private void rebuildPresetList() {
        // Remove only the preset list widgets (keep nameField and save button)
        // Rebuild by clearing all and re-adding
        this.clearWidgets();

        // Re-add name field and save button
        this.addRenderableWidget(nameField);

        int centerX = this.width / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Save"), btn -> {
            String name = nameField.getValue().trim();
            if (!name.isEmpty()) {
                ConfigPresets.savePreset(modId, name);
                nameField.setValue("");
                rebuildPresetList();
            }
        }).bounds(centerX + 65, 40, 45, 20).build());

        // Done button
        this.addRenderableWidget(Button.builder(Component.literal("Done"), btn -> onClose())
                .bounds(centerX - 75, this.height - 30, 150, 20)
                .build());

        // Preset list
        List<String> presets = ConfigPresets.listPresets(modId);
        int startY = 70;
        int listWidth = 220;
        int listX = centerX - listWidth / 2;

        int end = Math.min(scrollOffset + MAX_VISIBLE, presets.size());
        for (int i = scrollOffset; i < end; i++) {
            String preset = presets.get(i);
            int y = startY + (i - scrollOffset) * ENTRY_HEIGHT;

            // Load button
            this.addRenderableWidget(Button.builder(Component.literal("Load"), btn -> {
                ConfigPresets.loadPreset(modId, preset);
            }).bounds(listX + listWidth - 90, y, 40, 20).build());

            // Delete button
            this.addRenderableWidget(Button.builder(Component.literal("\u2716"), btn -> {
                ConfigPresets.deletePreset(modId, preset);
                scrollOffset = Math.max(0, Math.min(scrollOffset, presets.size() - 2 - MAX_VISIBLE));
                rebuildPresetList();
            }).bounds(listX + listWidth - 45, y, 20, 20).build());
        }

        // Scroll buttons
        if (presets.size() > MAX_VISIBLE) {
            if (scrollOffset > 0) {
                this.addRenderableWidget(Button.builder(Component.literal("\u25B2"), btn -> {
                    scrollOffset = Math.max(0, scrollOffset - 1);
                    rebuildPresetList();
                }).bounds(centerX + listWidth / 2 + 5, startY, 20, 20).build());
            }
            if (scrollOffset + MAX_VISIBLE < presets.size()) {
                this.addRenderableWidget(Button.builder(Component.literal("\u25BC"), btn -> {
                    scrollOffset = Math.min(presets.size() - MAX_VISIBLE, scrollOffset + 1);
                    rebuildPresetList();
                }).bounds(centerX + listWidth / 2 + 5, startY + (MAX_VISIBLE - 1) * ENTRY_HEIGHT, 20, 20).build());
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);

        // Draw preset names in the list
        List<String> presets = ConfigPresets.listPresets(modId);
        int startY = 70;
        int listWidth = 220;
        int listX = this.width / 2 - listWidth / 2;

        int end = Math.min(scrollOffset + MAX_VISIBLE, presets.size());
        for (int i = scrollOffset; i < end; i++) {
            int y = startY + (i - scrollOffset) * ENTRY_HEIGHT;
            graphics.drawString(this.font, presets.get(i), listX + 4, y + 6, 0xFFFFFF);
        }

        if (presets.isEmpty()) {
            graphics.drawCenteredString(this.font,
                    Component.literal("No presets saved yet."),
                    this.width / 2, startY + 10, 0xAAAAAA);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
