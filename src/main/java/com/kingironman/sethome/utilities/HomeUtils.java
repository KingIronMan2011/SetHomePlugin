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
    private final String storageType;
    private final SQLiteUtils sqliteUtils;
    private final MySQLUtils mysqlUtils;

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
        storageType = SetHome.getInstance().configUtils.getStorageType();
        homesFilePath = SetHome.getInstance().getDataFolder() + File.separator + "homes";
        homeFiles = new HashMap<>();
        homeYamls = new HashMap<>();
        if (!new File(homesFilePath).exists()) {
            try {
                Files.createDirectories(Paths.get(homesFilePath));
            } catch (IOException e) {
                // Do nothing
            }
        }
        if (storageType.equalsIgnoreCase("sqlite")) {
            sqliteUtils = new SQLiteUtils();
            try {
                sqliteUtils.setupTables();
            } catch (Exception e) {
                getLogger().severe("Failed to setup SQLite tables: " + e.getMessage());
            }
            mysqlUtils = null;
        } else if (storageType.equalsIgnoreCase("mysql")) {
            sqliteUtils = null;
            String host = SetHome.getInstance().getConfig().getString("extra.mysql.host", "localhost");
            int port = SetHome.getInstance().getConfig().getInt("extra.mysql.port", 3306);
            String database = SetHome.getInstance().getConfig().getString("extra.mysql.database", "sethome");
            String username = SetHome.getInstance().getConfig().getString("extra.mysql.username", "root");
            String password = SetHome.getInstance().getConfig().getString("extra.mysql.password", "password");
            mysqlUtils = new MySQLUtils(host, port, database, username, password);
            try {
                mysqlUtils.setupTables();
            } catch (Exception e) {
                getLogger().severe("Failed to setup MySQL tables: " + e.getMessage());
            }
        } else {
            sqliteUtils = null;
            mysqlUtils = null;
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
        if (storageType.equalsIgnoreCase("sqlite")) {
            try {
                sqliteUtils.connect();
                java.sql.PreparedStatement ps = sqliteUtils.getConnection().prepareStatement("SELECT world FROM homes WHERE uuid=? AND home_name=?");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, homeName);
                java.sql.ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    if (verbose)
                        SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.MISSING_HOME, null);
                    rs.close(); ps.close();
                    return false;
                }
                String world = rs.getString("world");
                rs.close(); ps.close();
                if (Bukkit.getWorld(world) == null) {
                    if (verbose)
                        SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.MISSING_WORLD, null);
                    return false;
                }
                return true;
            } catch (Exception e) {
                getLogger().severe("SQLite homeExists error: " + e.getMessage());
                return false;
            }
        } else if (storageType.equalsIgnoreCase("mysql")) {
            try {
                mysqlUtils.connect();
                java.sql.PreparedStatement ps = mysqlUtils.getConnection().prepareStatement("SELECT world FROM homes WHERE uuid=? AND home_name=?");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, homeName);
                java.sql.ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    if (verbose)
                        SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.MISSING_HOME, null);
                    rs.close(); ps.close();
                    return false;
                }
                String world = rs.getString("world");
                rs.close(); ps.close();
                if (Bukkit.getWorld(world) == null) {
                    if (verbose)
                        SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.MISSING_WORLD, null);
                    return false;
                }
                return true;
            } catch (Exception e) {
                getLogger().severe("MySQL homeExists error: " + e.getMessage());
                return false;
            }
        } else if (storageType.equalsIgnoreCase("yaml")) {
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
        } else {
            String msg = "[SetHome] Invalid storage-type in config: '" + storageType + "'. Supported: yaml, sqlite, mysql.";
            getLogger().severe(msg);
            if (verbose && player != null) {
                player.sendMessage(ChatColor.RED + msg);
            }
            return false;
        }
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
            Map<String, String> params = new HashMap<>();
            params.put("max", String.valueOf(maxHomes));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', com.kingironman.sethome.utilities.LangUtils.get("error.maxhomes", params)));
            return;
        }
        if (storageType.equalsIgnoreCase("sqlite")) {
            try {
                sqliteUtils.connect();
                java.sql.PreparedStatement ps = sqliteUtils.getConnection().prepareStatement(
                        "INSERT OR REPLACE INTO homes (uuid, home_name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, homeName);
                ps.setString(3, player.getLocation().getWorld().getName());
                ps.setDouble(4, player.getLocation().getX());
                ps.setDouble(5, player.getLocation().getY());
                ps.setDouble(6, player.getLocation().getZ());
                ps.setFloat(7, player.getLocation().getYaw());
                ps.setFloat(8, player.getLocation().getPitch());
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                getLogger().severe("SQLite setPlayerHome error: " + e.getMessage());
            }
            if (SetHome.getInstance().configUtils.CMD_SETHOME_MESSAGE_SHOW)
                SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.CMD_SETHOME, null);
        } else if (storageType.equalsIgnoreCase("mysql")) {
            try {
                mysqlUtils.connect();
                java.sql.PreparedStatement ps = mysqlUtils.getConnection().prepareStatement(
                        "REPLACE INTO homes (uuid, home_name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, homeName);
                ps.setString(3, player.getLocation().getWorld().getName());
                ps.setDouble(4, player.getLocation().getX());
                ps.setDouble(5, player.getLocation().getY());
                ps.setDouble(6, player.getLocation().getZ());
                ps.setFloat(7, player.getLocation().getYaw());
                ps.setFloat(8, player.getLocation().getPitch());
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                getLogger().severe("MySQL setPlayerHome error: " + e.getMessage());
            }
            if (SetHome.getInstance().configUtils.CMD_SETHOME_MESSAGE_SHOW)
                SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.CMD_SETHOME, null);
        } else if (storageType.equalsIgnoreCase("yaml")) {
            getHomeYaml(player).set(getXPath(homeName), player.getLocation().getX());
            getHomeYaml(player).set(getYPath(homeName), player.getLocation().getY());
            getHomeYaml(player).set(getZPath(homeName), player.getLocation().getZ());
            getHomeYaml(player).set(getYawXPath(homeName), player.getLocation().getYaw());
            getHomeYaml(player).set(getPitchXPath(homeName), player.getLocation().getPitch());
            getHomeYaml(player).set(getWorldXPath(homeName), player.getLocation().getWorld().getName());
            saveHomesFile(player);
            if (SetHome.getInstance().configUtils.CMD_SETHOME_MESSAGE_SHOW)
                SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.CMD_SETHOME, null);
        } else {
            String msg = "[SetHome] Invalid storage-type in config: '" + storageType + "'. Supported: yaml, sqlite, mysql.";
            getLogger().severe(msg);
            if (player != null) {
                player.sendMessage(ChatColor.RED + msg);
            }
        }
    }

    /**
     * Get the Location object for a player's home.
     * @param player The player
     * @param homeName The home name
     * @return Location of the home
     */
    public Location getPlayerHome(Player player, String homeName) {
        if (storageType.equalsIgnoreCase("sqlite")) {
            try {
                sqliteUtils.connect();
                java.sql.PreparedStatement ps = sqliteUtils.getConnection().prepareStatement(
                        "SELECT world, x, y, z, yaw, pitch FROM homes WHERE uuid=? AND home_name=?");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, homeName);
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    Location loc = new Location(
                            Bukkit.getWorld(rs.getString("world")),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z"),
                            rs.getFloat("yaw"),
                            rs.getFloat("pitch")
                    );
                    rs.close(); ps.close();
                    return loc;
                }
                rs.close(); ps.close();
            } catch (Exception e) {
                getLogger().severe("SQLite getPlayerHome error: " + e.getMessage());
            }
            return null;
        } else if (storageType.equalsIgnoreCase("mysql")) {
            try {
                mysqlUtils.connect();
                java.sql.PreparedStatement ps = mysqlUtils.getConnection().prepareStatement(
                        "SELECT world, x, y, z, yaw, pitch FROM homes WHERE uuid=? AND home_name=?");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, homeName);
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    Location loc = new Location(
                            Bukkit.getWorld(rs.getString("world")),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z"),
                            rs.getFloat("yaw"),
                            rs.getFloat("pitch")
                    );
                    rs.close(); ps.close();
                    return loc;
                }
                rs.close(); ps.close();
            } catch (Exception e) {
                getLogger().severe("MySQL getPlayerHome error: " + e.getMessage());
            }
            return null;
        } else if (storageType.equalsIgnoreCase("yaml")) {
            return new Location(
                    Bukkit.getWorld(getHomeYaml(player).getString(getWorldXPath(homeName))),
                    getHomeYaml(player).getDouble(getXPath(homeName)),
                    getHomeYaml(player).getDouble(getYPath(homeName)),
                    getHomeYaml(player).getDouble(getZPath(homeName)),
                    getHomeYaml(player).getLong(getYawXPath(homeName)),
                    getHomeYaml(player).getLong(getPitchXPath(homeName))
            );
        } else {
            String msg = "[SetHome] Invalid storage-type in config: '" + storageType + "'. Supported: yaml, sqlite, mysql.";
            getLogger().severe(msg);
            if (player != null) {
                player.sendMessage(ChatColor.RED + msg);
            }
            return null;
        }
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
        if (storageType.equalsIgnoreCase("sqlite")) {
            List<String> homeNames = new ArrayList<>();
            try {
                sqliteUtils.connect();
                java.sql.PreparedStatement ps = sqliteUtils.getConnection().prepareStatement(
                        "SELECT home_name FROM homes WHERE uuid=?");
                ps.setString(1, player.getUniqueId().toString());
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    homeNames.add(rs.getString("home_name"));
                }
                rs.close(); ps.close();
            } catch (Exception e) {
                getLogger().severe("SQLite getHomeNames error: " + e.getMessage());
            }
            return homeNames;
        } else if (storageType.equalsIgnoreCase("mysql")) {
            List<String> homeNames = new ArrayList<>();
            try {
                mysqlUtils.connect();
                java.sql.PreparedStatement ps = mysqlUtils.getConnection().prepareStatement(
                        "SELECT home_name FROM homes WHERE uuid=?");
                ps.setString(1, player.getUniqueId().toString());
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    homeNames.add(rs.getString("home_name"));
                }
                rs.close(); ps.close();
            } catch (Exception e) {
                getLogger().severe("MySQL getHomeNames error: " + e.getMessage());
            }
            return homeNames;
        } else if (storageType.equalsIgnoreCase("yaml")) {
            YamlConfiguration config = getHomeYaml(player);
            List<String> homeNames = new ArrayList<>();
            if (config == null) {
                getLogger().warning("Configuration is null for player: " + player.getName());
                return Collections.emptyList();
            }
            if (config.contains("Homes")) {
                homeNames.addAll(config.getConfigurationSection("Homes").getKeys(false));
            }
            return homeNames.isEmpty() ? Collections.emptyList() : homeNames;
        } else {
            String msg = "[SetHome] Invalid storage-type in config: '" + storageType + "'. Supported: yaml, sqlite, mysql.";
            getLogger().severe(msg);
            if (player != null) {
                player.sendMessage(ChatColor.RED + msg);
            }
            return Collections.emptyList();
        }
    }

    /**
     * Send a message to the player listing all their homes.
     * @param player The player
     */
    public void listHome(Player player){
        List<String> homeNames = getHomeNames(player);
        Map<String, String> params = new HashMap<>();
        params.put("homes", String.join(", ", homeNames));
        if (homeNames.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', com.kingironman.sethome.utilities.LangUtils.get("error.nohomes", params)));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', com.kingironman.sethome.utilities.LangUtils.get("listhome.success", params)));
        }
    }

    /**
     * Delete a player's home by name.
     * @param player The player
     * @param homeName The home name
     */
    public void deletePlayerHome(Player player, String homeName) {
        if (!homeExists(player, homeName, true)) return;
        if (storageType.equalsIgnoreCase("sqlite")) {
            try {
                sqliteUtils.connect();
                java.sql.PreparedStatement ps = sqliteUtils.getConnection().prepareStatement(
                        "DELETE FROM homes WHERE uuid=? AND home_name=?");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, homeName);
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                getLogger().severe("SQLite deletePlayerHome error: " + e.getMessage());
            }
            if (SetHome.getInstance().configUtils.CMD_DELETEHOME_MESSAGE_SHOW)
                SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.CMD_DELETEHOME, null);
        } else if (storageType.equalsIgnoreCase("mysql")) {
            try {
                mysqlUtils.connect();
                java.sql.PreparedStatement ps = mysqlUtils.getConnection().prepareStatement(
                        "DELETE FROM homes WHERE uuid=? AND home_name=?");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, homeName);
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                getLogger().severe("MySQL deletePlayerHome error: " + e.getMessage());
            }
            if (SetHome.getInstance().configUtils.CMD_DELETEHOME_MESSAGE_SHOW)
                SetHome.getInstance().messageUtils.displayMessage(player, MessageUtils.MESSAGE_TYPE.CMD_DELETEHOME, null);
        } else if (storageType.equalsIgnoreCase("yaml")) {
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
        } else {
            String msg = "[SetHome] Invalid storage-type in config: '" + storageType + "'. Supported: yaml, sqlite, mysql.";
            getLogger().severe(msg);
            if (player != null) {
                player.sendMessage(ChatColor.RED + msg);
            }
        }
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