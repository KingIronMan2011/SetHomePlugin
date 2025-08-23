package com.kingironman.sethome.commands;

import com.kingironman.sethome.SetHome;
import org.bukkit.entity.Player;

public class Commands {
    public void cmdSetHome(Player player, String home) {
        SetHome.getInstance().homeUtils.setPlayerHome(player, home);
    }

    public void cmdHome(Player player, String homeName) {
        SetHome.getInstance().homeUtils.sendPlayerHome(player, homeName);
    }

    public void cmdDeleteHome(Player player, String homeName) {
        if (SetHome.getInstance().homeUtils.homeExists(player, homeName, true))
            SetHome.getInstance().homeUtils.deletePlayerHome(player, homeName);
    }

    public void cmdListHome(Player player) {
        SetHome.getInstance().homeUtils.listHome(player);
    }

}