package com.kingironman.sethome.commands;

import com.kingironman.sethome.gui.HomeGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can open the homes GUI.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            HomeGui.open(player, null);
            return true;
        }
        // /homes <player> (admin)
        if (!player.isOp()) {
            player.sendMessage("You don't have permission to open other players' homes.");
            return true;
        }
        Player target = org.bukkit.Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("Player not online: " + args[0]);
            return true;
        }
        HomeGui.open(player, target);
        return true;
    }

}
