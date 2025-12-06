package com.kingironman.sethome.commands;

import com.kingironman.sethome.gui.HomeGui;
import org.bukkit.ChatColor;
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
        
        // No arguments - open player's own homes
        if (args.length == 0) {
            HomeGui.open(player, null);
            return true;
        }
        
        // One argument - could be "admin" or a player name
        if (args.length == 1) {
            // Check for admin menu
            if (args[0].equalsIgnoreCase("admin")) {
                if (!player.isOp() && !player.hasPermission("sethome.admin")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use the admin menu.");
                    return true;
                }
                HomeGui.openAdminMenu(player);
                return true;
            }
            
            // Otherwise, treat as player name (admin viewing specific player's homes)
            if (!player.isOp() && !player.hasPermission("sethome.admin")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to view other players' homes.");
                return true;
            }
            
            Player target = org.bukkit.Bukkit.getPlayerExact(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(ChatColor.RED + "Player not online: " + args[0]);
                return true;
            }
            HomeGui.open(player, target);
            return true;
        }
        
        player.sendMessage(ChatColor.RED + "Usage: /homes [admin|<player>]");
        return true;
    }

}
