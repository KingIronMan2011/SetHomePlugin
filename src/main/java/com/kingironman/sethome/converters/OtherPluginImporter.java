package com.kingironman.sethome.converters;

import com.kingironman.sethome.SetHome;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Importer for third-party home formats (starting with Essentials-like YAML exports).
 */
public class OtherPluginImporter {

    /**
     * Import homes from an Essentials-like YAML file. The file is expected to have top-level player names
     * with a nested 'homes' section or direct home entries.
     * Returns the number of homes imported.
     */
    public static int importEssentialsYaml(File file) throws IOException {
        if (file == null || !file.exists()) throw new IOException("File not found: " + file);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        int imported = 0;

        Set<String> players = yaml.getKeys(false);
        for (String playerKey : players) {
            Object obj = yaml.get(playerKey);
            if (!(obj instanceof ConfigurationSection) && !(obj instanceof java.util.Map)) continue;

            UUID uuid = null;
            try {
                uuid = UUID.fromString(playerKey);
            } catch (IllegalArgumentException e) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(java.util.UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerKey).getBytes())); // fallback for legacy names
                uuid = op.getUniqueId();
            }
            if (uuid == null) continue;

            // Determine where homes are stored for this player
            ConfigurationSection homesSection = null;
            if (yaml.isConfigurationSection(playerKey + ".homes")) {
                homesSection = yaml.getConfigurationSection(playerKey + ".homes");
            } else if (yaml.isConfigurationSection(playerKey)) {
                ConfigurationSection sec = yaml.getConfigurationSection(playerKey);
                if (sec.isConfigurationSection("homes")) homesSection = sec.getConfigurationSection("homes");
                else homesSection = sec;
            }

            if (homesSection == null) continue;

            for (String homeName : homesSection.getKeys(false)) {
                ConfigurationSection h = homesSection.getConfigurationSection(homeName);
                if (h == null) continue;
                double x = h.contains("x") ? h.getDouble("x") : h.getDouble("X", Double.NaN);
                double y = h.contains("y") ? h.getDouble("y") : h.getDouble("Y", Double.NaN);
                double z = h.contains("z") ? h.getDouble("z") : h.getDouble("Z", Double.NaN);
                String world = h.contains("world") ? h.getString("world") : h.getString("World");
                double yaw = h.contains("yaw") ? h.getDouble("yaw") : h.getDouble("Yaw", 0.0);
                double pitch = h.contains("pitch") ? h.getDouble("pitch") : h.getDouble("Pitch", 0.0);

                if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z) || world == null) continue;

                // Write into SetHome per-player file
                File homeFile = new File(SetHome.getInstance().getDataFolder() + File.separator + "homes", uuid.toString() + ".yml");
                YamlConfiguration target = YamlConfiguration.loadConfiguration(homeFile);
                target.set("Homes." + homeName + ".World", world);
                target.set("Homes." + homeName + ".X", x);
                target.set("Homes." + homeName + ".Y", y);
                target.set("Homes." + homeName + ".Z", z);
                target.set("Homes." + homeName + ".Yaw", yaw);
                target.set("Homes." + homeName + ".Pitch", pitch);
                try {
                    target.save(homeFile);
                    imported++;
                } catch (IOException e) {
                    throw new IOException("Failed to save home file for " + uuid + ": " + e.getMessage(), e);
                }
            }
        }

        SetHome.getInstance().getLogger().info("Imported " + imported + " homes from " + file.getName());
        return imported;
    }

}
