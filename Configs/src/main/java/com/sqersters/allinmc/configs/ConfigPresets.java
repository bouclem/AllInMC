package com.sqersters.allinmc.configs;

import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Handles saving, loading, listing, and deleting config presets.
 * Presets are stored as TOML copies in config/allinmc_configs_presets/<modId>/.
 */
public class ConfigPresets {

    private static final String PRESETS_DIR = "allinmc_configs_presets";

    /**
     * Returns the presets directory for a given mod.
     */
    private static Path getPresetsDir(String modId) {
        return FMLPaths.CONFIGDIR.get().resolve(PRESETS_DIR).resolve(modId);
    }

    /**
     * Returns the config file path for a given mod (common type).
     */
    private static Path getConfigFile(String modId) {
        return FMLPaths.CONFIGDIR.get().resolve(modId + "-common.toml");
    }

    /**
     * Saves the current config as a named preset.
     * @return true if saved successfully
     */
    public static boolean savePreset(String modId, String presetName) {
        Path configFile = getConfigFile(modId);
        if (!Files.exists(configFile)) return false;

        try {
            Path presetsDir = getPresetsDir(modId);
            Files.createDirectories(presetsDir);
            Path presetFile = presetsDir.resolve(sanitize(presetName) + ".toml");
            Files.copy(configFile, presetFile, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            AllInMCConfigs.LOGGER.error("Failed to save preset '{}' for mod '{}'", presetName, modId, e);
            return false;
        }
    }

    /**
     * Loads a preset by copying it over the current config file.
     * NeoForge's file watcher should pick up the change.
     * @return true if loaded successfully
     */
    public static boolean loadPreset(String modId, String presetName) {
        Path presetFile = getPresetsDir(modId).resolve(sanitize(presetName) + ".toml");
        if (!Files.exists(presetFile)) return false;

        try {
            Path configFile = getConfigFile(modId);
            Files.copy(presetFile, configFile, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            AllInMCConfigs.LOGGER.error("Failed to load preset '{}' for mod '{}'", presetName, modId, e);
            return false;
        }
    }

    /**
     * Lists all saved preset names for a mod.
     */
    public static List<String> listPresets(String modId) {
        Path presetsDir = getPresetsDir(modId);
        if (!Files.isDirectory(presetsDir)) return Collections.emptyList();

        try (Stream<Path> files = Files.list(presetsDir)) {
            return files
                    .filter(p -> p.toString().endsWith(".toml"))
                    .map(p -> {
                        String name = p.getFileName().toString();
                        return name.substring(0, name.length() - 5); // strip .toml
                    })
                    .sorted()
                    .toList();
        } catch (IOException e) {
            AllInMCConfigs.LOGGER.error("Failed to list presets for mod '{}'", modId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Deletes a saved preset.
     * @return true if deleted successfully
     */
    public static boolean deletePreset(String modId, String presetName) {
        Path presetFile = getPresetsDir(modId).resolve(sanitize(presetName) + ".toml");
        try {
            return Files.deleteIfExists(presetFile);
        } catch (IOException e) {
            AllInMCConfigs.LOGGER.error("Failed to delete preset '{}' for mod '{}'", presetName, modId, e);
            return false;
        }
    }

    /**
     * Checks whether a config file exists for the given mod.
     */
    public static boolean hasConfigFile(String modId) {
        return Files.exists(getConfigFile(modId));
    }

    /**
     * Sanitizes a preset name for use as a filename.
     */
    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
