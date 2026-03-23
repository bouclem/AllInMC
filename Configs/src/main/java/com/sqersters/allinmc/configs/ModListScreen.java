package com.sqersters.allinmc.configs;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Main screen listing all loaded "All in MC" mods.
 * Clicking a mod opens NeoForge's built-in ConfigurationScreen for it.
 */
public class ModListScreen extends Screen {

    @Nullable
    private final Screen parent;
    private final List<ModEntry> allModEntries = new ArrayList<>();
    private final List<ModEntry> filteredModEntries = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int ENTRY_HEIGHT = 40;
    private static final int MAX_VISIBLE = 5;
    private EditBox searchBox;
    private String lastFilter = "";

    public ModListScreen(@Nullable Screen parent) {
        super(Component.literal("All In MC: Configs"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        allModEntries.clear();

        // Find all loaded mods with "allinmc_" prefix (excluding this mod itself)
        for (ModContainer container : ModList.get().getSortedMods()) {
            String modId = container.getModId();
            if (modId.startsWith("allinmc_") && !modId.equals(AllInMCConfigs.MODID)) {
                String displayName = container.getModInfo().getDisplayName();
                String description = container.getModInfo().getDescription();
                allModEntries.add(new ModEntry(modId, displayName, description, container));
            }
        }

        // Search box
        int boxWidth = 180;
        searchBox = new EditBox(this.font, this.width / 2 - boxWidth / 2, 30, boxWidth, 16,
                Component.translatable("gui.allinmc_configs.search"));
        searchBox.setHint(Component.translatable("gui.allinmc_configs.search"));
        searchBox.setMaxLength(50);
        searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(searchBox);

        applyFilter();
    }

    private void onSearchChanged(String text) {
        if (!text.equals(lastFilter)) {
            lastFilter = text;
            scrollOffset = 0;
            applyFilter();
        }
    }

    private void applyFilter() {
        filteredModEntries.clear();
        String query = lastFilter.toLowerCase(Locale.ROOT).trim();
        for (ModEntry entry : allModEntries) {
            if (query.isEmpty()
                    || entry.displayName().toLowerCase(Locale.ROOT).contains(query)
                    || entry.modId().toLowerCase(Locale.ROOT).contains(query)) {
                filteredModEntries.add(entry);
            }
        }
        rebuildButtons();
    }

    private void rebuildButtons() {
        this.clearWidgets();

        // Re-add search box (clearWidgets removes it)
        this.addRenderableWidget(searchBox);

        // Done button at the bottom
        this.addRenderableWidget(Button.builder(Component.literal("Done"), btn -> onClose())
                .bounds(this.width / 2 - 75, this.height - 30, 150, 20)
                .build());

        int startY = 55;
        int buttonWidth = 180;
        int presetsWidth = 55;
        int totalWidth = buttonWidth + 5 + presetsWidth;
        int leftX = this.width / 2 - totalWidth / 2;

        int end = Math.min(scrollOffset + MAX_VISIBLE, filteredModEntries.size());
        for (int i = scrollOffset; i < end; i++) {
            ModEntry entry = filteredModEntries.get(i);
            int y = startY + (i - scrollOffset) * ENTRY_HEIGHT;

            // Config button (mod name)
            this.addRenderableWidget(Button.builder(
                            Component.literal(entry.displayName()),
                            btn -> openModConfig(entry))
                    .bounds(leftX, y, buttonWidth, 20)
                    .build());

            // Presets button
            this.addRenderableWidget(Button.builder(
                            Component.literal("Presets"),
                            btn -> openPresets(entry))
                    .bounds(leftX + buttonWidth + 5, y, presetsWidth, 20)
                    .build());
        }

        // Scroll buttons if needed
        int scrollX = leftX + totalWidth + 5;
        if (filteredModEntries.size() > MAX_VISIBLE) {
            if (scrollOffset > 0) {
                this.addRenderableWidget(Button.builder(Component.literal("\u25B2"), btn -> {
                    scrollOffset = Math.max(0, scrollOffset - 1);
                    rebuildButtons();
                }).bounds(scrollX, startY, 20, 20).build());
            }
            if (scrollOffset + MAX_VISIBLE < filteredModEntries.size()) {
                this.addRenderableWidget(Button.builder(Component.literal("\u25BC"), btn -> {
                    scrollOffset = Math.min(filteredModEntries.size() - MAX_VISIBLE, scrollOffset + 1);
                    rebuildButtons();
                }).bounds(scrollX, startY + (MAX_VISIBLE - 1) * ENTRY_HEIGHT, 20, 20).build());
            }
        }
    }

    private void openModConfig(ModEntry entry) {
        // Use NeoForge's built-in ConfigurationScreen which handles editing and saving
        this.minecraft.setScreen(new ConfigurationScreen(entry.container(), this));
    }

    private void openPresets(ModEntry entry) {
        this.minecraft.setScreen(new PresetsScreen(this, entry.modId(), entry.displayName()));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);

        if (filteredModEntries.isEmpty()) {
            String msg = allModEntries.isEmpty() ? "No All In MC mods found." : "No mods match your search.";
            graphics.drawCenteredString(this.font,
                    Component.literal(msg),
                    this.width / 2, this.height / 2, 0xAAAAAA);
        } else {
            // Draw descriptions below each mod entry
            int startY = 55;
            int totalWidth = 180 + 5 + 55;
            int leftX = this.width / 2 - totalWidth / 2;

            int end = Math.min(scrollOffset + MAX_VISIBLE, filteredModEntries.size());
            for (int i = scrollOffset; i < end; i++) {
                ModEntry entry = filteredModEntries.get(i);
                int y = startY + (i - scrollOffset) * ENTRY_HEIGHT;
                // Render first line of description in gray below the button
                String desc = entry.description();
                if (desc != null && !desc.isEmpty()) {
                    // Take only the first line, truncate if too long
                    String firstLine = desc.split("\n")[0].trim();
                    if (firstLine.length() > 45) {
                        firstLine = firstLine.substring(0, 42) + "...";
                    }
                    graphics.drawString(this.font, firstLine, leftX + 2, y + 22, 0x888888);
                }
            }
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    record ModEntry(String modId, String displayName, String description, ModContainer container) {}
}
