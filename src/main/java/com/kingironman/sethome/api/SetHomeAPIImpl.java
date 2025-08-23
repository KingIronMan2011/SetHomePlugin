package com.kingironman.sethome.api;

import com.kingironman.sethome.SetHome;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetHomeAPIImpl implements SetHomeAPI {

    private final SetHome plugin = SetHome.getInstance();

    @Override
    public Location getHomeLocation(OfflinePlayer player, String homeName) {
        if (player == null || homeName == null) return null;
        if (player.isOnline()) {
            return plugin.homeUtils.getPlayerHome(player.getPlayer(), homeName);
        }
        // For offline players, we currently do not support reading YAML without a Player instance.
        // Return null as a best-effort; future work: load YamlConfiguration by UUID.
        return null;
    }

    @Override
    public boolean setPlayerHome(OfflinePlayer player, String homeName, Location location) {
        if (player == null || homeName == null || location == null) return false;
        if (!player.isOnline()) return false;
        try {
            plugin.homeUtils.setPlayerHomeAtLocation(player.getPlayer(), homeName, location);
            return true;
        } catch (Exception e) {
            com.kingironman.sethome.utilities.LoggingUtils.error("API setPlayerHome failed", e);
            return false;
        }
    }

    @Override
    public boolean deletePlayerHome(OfflinePlayer player, String homeName) {
        if (player == null || homeName == null) return false;
        if (!player.isOnline()) return false;
        try {
            plugin.homeUtils.deletePlayerHome(player.getPlayer(), homeName);
            return true;
        } catch (Exception e) {
            com.kingironman.sethome.utilities.LoggingUtils.error("API deletePlayerHome failed", e);
            return false;
        }
    }

    @Override
    public List<String> listHomes(OfflinePlayer player) {
        if (player == null) return Collections.emptyList();
        if (!player.isOnline()) return Collections.emptyList();
        try {
            return plugin.homeUtils.getHomeNames(player.getPlayer());
        } catch (Exception e) {
            com.kingironman.sethome.utilities.LoggingUtils.error("API listHomes failed", e);
            return Collections.emptyList();
        }
    }

    @Override
    public int getMaxHomesFor(OfflinePlayer player) {
        if (player == null) return plugin.configUtils.MAX_HOMES_PER_PLAYER;
        if (!player.isOnline()) return plugin.configUtils.MAX_HOMES_PER_PLAYER;
        return plugin.configUtils.getMaxHomesFor(player.getPlayer());
    }

    @Override
    public boolean sendHomeInvite(Player from, String targetName, String homeName) {
        try {
            plugin.homeUtils.sendHomeInvite(from, targetName, homeName);
            return true;
        } catch (Exception e) {
            com.kingironman.sethome.utilities.LoggingUtils.error("API sendHomeInvite failed", e);
            return false;
        }
    }

    @Override
    public boolean acceptHomeInvite(Player acceptor) {
        try {
            plugin.homeUtils.acceptHomeInvite(acceptor);
            return true;
        } catch (Exception e) {
            com.kingironman.sethome.utilities.LoggingUtils.error("API acceptHomeInvite failed", e);
            return false;
        }
    }
}
