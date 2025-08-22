package com.kingironman.sethome.events;

import com.kingironman.sethome.SetHome;
import com.kingironman.sethome.commands.CommandExecutor;
import com.kingironman.sethome.utilities.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!CommandExecutor.getWarmupInEffect().containsKey(event.getPlayer().getUniqueId())) return;
        boolean cancelled = false;
        // SETHOME
        if (CommandExecutor.getWarmupInEffect().get(event.getPlayer().getUniqueId()).getOrDefault(CommandExecutor.COMMAND_TYPE.SETHOME, false)) {
            if (SetHome.getInstance().configUtils.CMD_SETHOME_WARMUP_CANCEL_ON_MOVE) {
                CommandExecutor.getWarmupTask().get(event.getPlayer().getUniqueId()).get(CommandExecutor.COMMAND_TYPE.SETHOME).cancel();
                CommandExecutor.getWarmupInEffect().get(event.getPlayer().getUniqueId()).put(CommandExecutor.COMMAND_TYPE.SETHOME, false);
                cancelled = true;
            }
        }
        // HOME
        if (CommandExecutor.getWarmupInEffect().get(event.getPlayer().getUniqueId()).getOrDefault(CommandExecutor.COMMAND_TYPE.HOME, false)) {
            if (SetHome.getInstance().configUtils.CMD_HOME_WARMUP_CANCEL_ON_MOVE) {
                CommandExecutor.getWarmupTask().get(event.getPlayer().getUniqueId()).get(CommandExecutor.COMMAND_TYPE.HOME).cancel();
                CommandExecutor.getWarmupInEffect().get(event.getPlayer().getUniqueId()).put(CommandExecutor.COMMAND_TYPE.HOME, false);
                cancelled = true;
            }
        }
        // DELETEHOME
        if (CommandExecutor.getWarmupInEffect().get(event.getPlayer().getUniqueId()).getOrDefault(CommandExecutor.COMMAND_TYPE.DELETEHOME, false)) {
            if (SetHome.getInstance().configUtils.CMD_DELETEHOME_WARMUP_CANCEL_ON_MOVE) {
                CommandExecutor.getWarmupTask().get(event.getPlayer().getUniqueId()).get(CommandExecutor.COMMAND_TYPE.DELETEHOME).cancel();
                CommandExecutor.getWarmupInEffect().get(event.getPlayer().getUniqueId()).put(CommandExecutor.COMMAND_TYPE.DELETEHOME, false);
                cancelled = true;
            }
        }
        if (cancelled) {
            SetHome.getInstance().messageUtils.displayMessage(event.getPlayer(), MessageUtils.MESSAGE_TYPE.ON_MOVE, null);
        }
    }

}
