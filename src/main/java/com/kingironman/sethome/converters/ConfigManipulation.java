package com.kingironman.sethome.converters;

import com.kingironman.sethome.SetHome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class ConfigManipulation {
    private final Path configPath;

    public ConfigManipulation() {
        configPath = Path.of(SetHome.getInstance().getDataFolder() + "/config.yml");
    }

    public boolean oldConfigExists(String oldSetting) {
        if (oldSetting != null) {
            return true;
        } else {
            return false;
        }
    }

    public void backupOldConfig(Path destination) {
        if (Files.exists(destination)) return;
        SetHome.getInstance().getLogger().log(Level.INFO, "Old configuration found! Backing up to: " + destination);
        try {
            Files.copy(configPath, destination);
            SetHome.getInstance().getLogger().log(Level.INFO, "Old configuration successfully backed up to: " + destination);
        }
        catch (IOException e) {
            SetHome.getInstance().getLogger().log(Level.SEVERE, "An error occurred while trying to backup " + configPath + " - Please manually backup this file.");
            throw new RuntimeException(e);
        }
    }

    public void createNewConfig() {
        SetHome.getInstance().saveResource(configPath.getFileName().toString(), true);
        SetHome.getInstance().reloadConfig();
    }

    /**
     * Merge missing keys from the default config into the user's config, preserving user values.
     */
    public void updateConfigWithDefaults() {
        File configFile = configPath.toFile();
        FileConfiguration userConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defStream = SetHome.getInstance().getResource("config.yml");
        if (defStream == null) return;
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defStream));

        boolean changed = false;
        // Iterate only top-level sections (sethome, home, deletehome, extra, messages)
        for (String section : defaultConfig.getKeys(false)) {
            if (!userConfig.contains(section)) {
                // If the whole section is missing, add it
                userConfig.set(section, defaultConfig.get(section));
                changed = true;
                continue;
            }
            // Section exists, check for missing keys inside
            Object defSectionObj = defaultConfig.get(section);
            Object userSectionObj = userConfig.get(section);
            if (defSectionObj instanceof org.bukkit.configuration.ConfigurationSection && userSectionObj instanceof org.bukkit.configuration.ConfigurationSection) {
                org.bukkit.configuration.ConfigurationSection defSection = (org.bukkit.configuration.ConfigurationSection) defSectionObj;
                org.bukkit.configuration.ConfigurationSection userSection = (org.bukkit.configuration.ConfigurationSection) userSectionObj;
                for (String key : defSection.getKeys(false)) {
                    if (!userSection.contains(key)) {
                        userSection.set(key, defSection.get(key));
                        changed = true;
                    }
                }
            }
        }
        if (changed) {
            try {
                userConfig.save(configFile);
                SetHome.getInstance().getLogger().info("Config updated with new options in correct sections. Your settings are preserved.");
            } catch (Exception e) {
                SetHome.getInstance().getLogger().warning("Failed to update config: " + e.getMessage());
            }
        }
    }
}
