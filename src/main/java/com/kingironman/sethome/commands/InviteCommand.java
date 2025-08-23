package com.kingironman.sethome.commands;

import com.kingironman.sethome.SetHome;
import com.kingironman.sethome.utilities.LoggingUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                SetHome.getInstance().messageUtils.displayMessage(sender, com.kingironman.sethome.utilities.MessageUtils.MESSAGE_TYPE.DENY_CONSOLE, null);
                return true;
            }
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("invitehome")) {
                if (args.length < 1) {
                    player.sendMessage("\u00a7cUsage: /invitehome <player> [homeName]");
                    return true;
                }
                String target = args[0];
                String homeName = "main";
                if (args.length >= 2) homeName = args[1];
                SetHome.getInstance().homeUtils.sendHomeInvite(player, target, homeName);
                return true;
            }

            if (command.getName().equalsIgnoreCase("acceptinvite")) {
                SetHome.getInstance().homeUtils.acceptHomeInvite(player);
                return true;
            }

            return false;
        } catch (Exception e) {
            LoggingUtils.error("Unexpected error in InviteCommand", e);
            if (sender != null) sender.sendMessage("\u00a7cAn internal error occurred. See server logs.");
            return true;
        }
    }

}
