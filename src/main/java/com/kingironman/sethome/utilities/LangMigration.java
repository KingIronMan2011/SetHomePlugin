package com.kingironman.sethome.utilities;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LangMigration {
    // Add all required keys and their default English values here
    private static final Map<String, String> REQUIRED_KEYS = new LinkedHashMap<>();
    static {
        REQUIRED_KEYS.put("sethome.success", "&2[&a*&2] &aYou now have a new home, &6{player}&a!");
        REQUIRED_KEYS.put("home.success", "&2[&a*&2] &aYou have been teleported to your home, &6{player}&a!");
        REQUIRED_KEYS.put("deletehome.success", "&2[&a*&2] &aYour home has been deleted, &6{player}&a!");
        REQUIRED_KEYS.put("listhome.success", "Your homes: {homes}");
        REQUIRED_KEYS.put("error.nohomes", "&cYou have no homes set.");
        REQUIRED_KEYS.put("error.maxhomes", "&cYou have reached the maximum number of homes ({max}). Delete a home before setting a new one.");
        REQUIRED_KEYS.put("error.invalidstoragetype", "&4[&cError&4] &cInvalid storage-type in config: '{type}'. Supported: yaml, sqlite, mysql.");
        REQUIRED_KEYS.put("error.unknown", "&4[&cError&4] &cAn unknown error occurred.");
        REQUIRED_KEYS.put("error.notfound", "&4[&cError&4] &cNo home found for &6{player}&c. Use &a/sethome &cto create one!");
        REQUIRED_KEYS.put("error.missingworld", "&4[&cError&4] &cThe world for your home no longer exists. Please contact an admin or set a new home.");
        REQUIRED_KEYS.put("error.cooldown", "&e[Cooldown] &cYou must wait &6{seconds} &cseconds before using this command again, &6{player}&c.");
        REQUIRED_KEYS.put("error.warmup", "&b[Warmup] &7Command will execute in &3{seconds}&7 seconds. Please wait...");
        REQUIRED_KEYS.put("error.onmove", "&6[Notice] &cYou moved! Command cancelled. Stand still to complete the action.");
        REQUIRED_KEYS.put("error.denyconsole", "&4[&cError&4] &cThis command can only be used by in-game players.");
        REQUIRED_KEYS.put("error.nopermission", "You do not have permission to do that.");
        REQUIRED_KEYS.put("backup.success", "Backup complete! File: {file}");
        REQUIRED_KEYS.put("backup.fail", "Backup failed: {error}");
        REQUIRED_KEYS.put("restore.success", "Restore started from file: {file}");
        REQUIRED_KEYS.put("restore.fail", "Restore failed: {error}");
    }

    public static void migrateLanguageFile(File langFile) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(langFile);
            boolean changed = false;
            for (Map.Entry<String, String> entry : REQUIRED_KEYS.entrySet()) {
                if (!config.contains(entry.getKey())) {
                    config.set(entry.getKey(), entry.getValue());
                    changed = true;
                }
            }
            if (changed) {
                // Write as JSON
                try (FileWriter writer = new FileWriter(langFile, StandardCharsets.UTF_8)) {
                    writer.write("{\n");
                    Set<String> keys = config.getKeys(false);
                    int i = 0;
                    for (String key : keys) {
                        String value = config.getString(key).replace("\"", "\\\"");
                        writer.write("  \"" + key + "\": \"" + value + "\"");
                        if (++i < keys.size()) writer.write(",\n");
                    }
                    writer.write("\n}\n");
                }
                Bukkit.getLogger().info("[SetHome] Migrated language file: " + langFile.getName());
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("[SetHome] Failed to migrate language file: " + langFile.getName());
        }
    }

    public static void migrateAll() {
        File langDir = new File("plugins/SetHome/languages");
        if (!langDir.exists()) return;
        File[] files = langDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;
        for (File file : files) {
            migrateLanguageFile(file);
        }
    }
}
