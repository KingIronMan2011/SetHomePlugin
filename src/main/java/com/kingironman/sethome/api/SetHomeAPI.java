package com.kingironman.sethome.api;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Public API for other plugins to interact with SetHome functionality.
 * Implementations are provided by the SetHome plugin and registered via Bukkit's ServicesManager.
 */
public interface SetHomeAPI {

    /**
     * Get the Location for a player's named home, or null if not found.
     */
    Location getHomeLocation(OfflinePlayer player, String homeName);

    /**
     * Set a player's home at the specified location. Returns true on success.
     */
    boolean setPlayerHome(OfflinePlayer player, String homeName, Location location);

    /**
     * Delete a player's named home. Returns true if deleted.
     */
    boolean deletePlayerHome(OfflinePlayer player, String homeName);

    /**
     * List all home names for the player.
     */
    List<String> listHomes(OfflinePlayer player);

    /**
     * Return the maximum number of homes allowed for the player based on plugin config and permissions.
     * If the player is offline, returns the global default configured limit.
     */
    int getMaxHomesFor(OfflinePlayer player);

    /**
     * Send a home invite from an online player to a target player name. Returns true if the invite was sent.
     */
    boolean sendHomeInvite(Player from, String targetName, String homeName);

    /**
     * Accept a pending invite for an online player. Returns true if accepted.
     */
    boolean acceptHomeInvite(Player acceptor);

}
