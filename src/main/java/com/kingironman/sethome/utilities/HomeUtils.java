package com.kingironman.sethome.utilities;

import com.kingironman.sethome.SetHome;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

/**
 * Utility class for managing player homes (set, get, list, delete) using YAML files.
 * Handles home data storage, retrieval, and validation for the SetHome plugin.
 */
public class HomeUtils {

    private final String homesFilePath;
    private final HashMap<UUID, File> homeFiles;
    private final HashMap<UUID, YamlConfiguration> homeYamls;

    public final String PATH_X = "Homes.main.X";
    public final String PATH_Y = "Homes.main.Y";
    public final String PATH_Z = "Homes.main.Z";
    public final String PATH_YAW = "Homes.main.Yaw";
    public final String PATH_PITCH = "Homes.main.Pitch";
    public final String PATH_WORLD = "Homes.main.World";

    public HomeUtils() {
        homesFilePath = SetHome.getInstance().getDataFolder() + File.separator + "homes";
        homeFiles = new HashMap<>();
        homeYamls = new HashMap<>();
        if (!new File(homesFilePath).exists()) {
            try {
                Files.createDirectories(Paths.get(homesFilePath));
            }
            catch (IOException e) {
                // Do nothing
            }
        }

    }

    private String getXPath(String homeName){
        return "Homes."+ homeName +".X";
    }
    private String getYPath(String homeName){
        return "Homes."+ homeName +".Y";
    }
    private String getZPath(String homeName){
        return "Homes."+ homeName +".Z";
    }
    private String getYawXPath(String homeName){
        return "Homes."+ homeName +".Yaw";
    }
    private String getPitchXPath(String homeName){
        return "Homes."+ homeName +".Pitch";
    }
    private String getWorldXPath(String homeName){
        return "Homes."+ homeName +".World";
    }

    /**
     * Get the map of player UUIDs to their home YAML files.
     * @return HashMap of UUID to File
     */
    public HashMap<UUID, File> getHomeFiles() {
        return homeFiles;
    }

    /**
     * Get the map of player UUIDs to their loaded YAML configurations.
     * @return HashMap of UUID to YamlConfiguration
     */
    public HashMap<UUID, YamlConfiguration> getHomeYamls() {
        return homeYamls;
    }

    /**
     * Check if a home exists for a player and if the world is valid.
     * @param player The player
     * @param homeName The home name
     * @param verbose If true, send error messages to the player
     * @return true if the home exists and world is valid, false otherwise
     */
    public boolean homeExists(Player player, String homeName, boolean verbose) {
        if (getHomeYaml(player).getString(getWorldXPath(homeName)) == null) {
            if (verbose)
                SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.MISSING_HOME, null);
            return false;
        }
        if (Bukkit.getWorld(getHomeYaml(player).getString(getWorldXPath(homeName))) == null) {
            if (verbose)
                SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.MISSING_WORLD, null);
            return false;
        }
        return true;
    }

    /**
     * Set or update a player's home location. Enforces max homes per player.
     * @param player The player
     * @param homeName The home name
     */
    public void setPlayerHome(Player player, String homeName) {
        int maxHomes = SetHome.getInstance().configUtils.MAX_HOMES_PER_PLAYER;
        List<String> homeNames = getHomeNames(player);
        if (maxHomes > 0 && !homeNames.contains(homeName) && homeNames.size() >= maxHomes) {
            player.sendMessage(ChatColor.RED + "You have reached the maximum number of homes (" + maxHomes + "). Delete a home before setting a new one.");
            return;
        }
        getHomeYaml(player).set(getXPath(homeName), player.getLocation().getX());
        getHomeYaml(player).set(getYPath(homeName), player.getLocation().getY());
        getHomeYaml(player).set(getZPath(homeName), player.getLocation().getZ());
        getHomeYaml(player).set(getYawXPath(homeName), player.getLocation().getYaw());
        getHomeYaml(player).set(getPitchXPath(homeName), player.getLocation().getPitch());
        getHomeYaml(player).set(getWorldXPath(homeName), player.getLocation().getWorld().getName());
        saveHomesFile(player);
        if (SetHome.getInstance().configUtils.CMD_SETHOME_MESSAGE_SHOW)
            SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.CMD_SETHOME, null);
    }

    /**
     * Get the Location object for a player's home.
     * @param player The player
     * @param homeName The home name
     * @return Location of the home
     */
    public Location getPlayerHome(Player player, String homeName) {
        return new Location(
                Bukkit.getWorld(getHomeYaml(player).getString(getWorldXPath(homeName))),
                getHomeYaml(player).getDouble(getXPath(homeName)),
                getHomeYaml(player).getDouble(getYPath(homeName)),
                getHomeYaml(player).getDouble(getZPath(homeName)),
                getHomeYaml(player).getLong(getYawXPath(homeName)),
                getHomeYaml(player).getLong(getPitchXPath(homeName))
        );
    }

    /**
     * Teleport a player to their home, with optional message and sound.
     * @param player The player
     * @param homeName The home name
     */
    public void sendPlayerHome(Player player, String homeName) {
        if (!homeExists(player, homeName, true)) return;
        Location home = getPlayerHome(player, homeName);
        player.teleport(home);
        if (SetHome.getInstance().configUtils.CMD_HOME_MESSAGE_SHOW)
            SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.CMD_HOME, null);
        if (SetHome.getInstance().configUtils.EXTRA_PLAY_WARP_SOUND)
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }
    /**
     * Get a list of all home names for a player.
     * @param player The player
     * @return List of home names
     */
    public List<String> getHomeNames(Player player) {
        YamlConfiguration config = getHomeYaml(player); // Assuming this loads the YAML for the player
        List<String> homeNames = new ArrayList<>();

        // Debugging output to ensure YAML is loaded correctly
        if (config == null) {
            getLogger().warning("Configuration is null for player: " + player.getName());
            return Collections.emptyList();
        }

        if (config.contains("Homes")) {
            // Debugging output to see if the "Homes" section is accessible
            getLogger().info("Homes section exists for player: " + player.getName());
            homeNames.addAll(config.getConfigurationSection("Homes").getKeys(false));
        } else {
            getLogger().warning("No homes found in the configuration for player: " + player.getName());
        }

        return homeNames.isEmpty() ? Collections.emptyList() : homeNames;
    }

    /**
     * Send a message to the player listing all their homes.
     * @param player The player
     */
    public void listHome(Player player){
        List<String> homeNames = getHomeNames(player);
        if (homeNames.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You have no homes set.");
        } else {
            player.sendMessage(ChatColor.GREEN + "Your homes: " + ChatColor.YELLOW + String.join(", ", homeNames));
        }
    }

    /**
     * Delete a player's home by name.
     * @param player The player
     * @param homeName The home name
     */
    public void deletePlayerHome(Player player, String homeName) {
        if (!homeExists(player, homeName, true)) return;
        getHomeYaml(player).set(getXPath(homeName), null);
        getHomeYaml(player).set(getYPath(homeName), null);
        getHomeYaml(player).set(getZPath(homeName), null);
        getHomeYaml(player).set(getYawXPath(homeName), null);
        getHomeYaml(player).set(getPitchXPath(homeName), null);
        getHomeYaml(player).set(getWorldXPath(homeName), null);
        getHomeYaml(player).set("Homes."+homeName, null);
        saveHomesFile(player);
        if (SetHome.getInstance().configUtils.CMD_DELETEHOME_MESSAGE_SHOW)
            SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.CMD_DELETEHOME, null);
    }

    private void saveHomesFile(Player player) {
        try {
            getHomeYaml(player).save(getHomeFile(player));
        } catch (Exception e) {
            SetHome.getInstance().getLogger().log(Level.SEVERE, "Error saving home for " + player.getName() + "!");
            e.printStackTrace();
        }
    }

    private File getHomeFile(Player player) {
        if (!homeFiles.containsKey(player.getUniqueId())) {
            homeFiles.put(player.getUniqueId(), new File(homesFilePath, player.getUniqueId() + ".yml"));
        }
        if (!homeFiles.get(player.getUniqueId()).exists()) {
            try {
                homeFiles.get(player.getUniqueId()).createNewFile();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return homeFiles.get(player.getUniqueId());
    }

    private YamlConfiguration getHomeYaml(Player player) {
        if (!homeYamls.containsKey(player.getUniqueId())) {
            homeYamls.put(player.getUniqueId(), YamlConfiguration.loadConfiguration(getHomeFile(player)));
        }
        return homeYamls.get(player.getUniqueId());
    }

}