package com.kingironman.sethome.commands;

import com.kingironman.sethome.SetHome;
import com.kingironman.sethome.utilities.BackupUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class ShpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/sethome &7- Set your home"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/home &7- Teleport to your home"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/deletehome &7- Delete your home"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/listhome &7- List all your homes"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/shp backup &7- Backup all home data"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/shp restore <file> &7- Restore home data from backup"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/invitehome <player> [homeName] &7- Invite a player to your home"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/acceptinvite &7- Accept a home invite"));
            return true;
        }
        if (args[0].equalsIgnoreCase("backup")) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must be OP to use this command."));
                return true;
            }
            String storageType = SetHome.getInstance().configUtils.getStorageType();
            try {
                String backupFile = BackupUtils.createBackup(storageType);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aBackup complete! File: &e" + backupFile));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBackup failed: " + e.getMessage()));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("restore")) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must be OP to use this command."));
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /shp restore <file>"));
                return true;
            }
            String storageType = SetHome.getInstance().configUtils.getStorageType();
            try {
                boolean ok = BackupUtils.restoreBackup(storageType, args[1]);
                if (ok) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aRestore started from file: &e" + args[1]));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cRestore failed: File not found."));
                }
            } catch (Exception e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cRestore failed: " + e.getMessage()));
            }
            return true;
        }
        // Admin home management: /shp home <set|tp|delete|list> <player> [homeName]
        if (args[0].equalsIgnoreCase("home")) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must be OP to use this command."));
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /shp home <set|tp|delete|list> <player> [homeName]"));
                return true;
            }
            String sub = args[1].toLowerCase();
            String targetName = args[2];
            org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayerExact(targetName);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTarget player not online: &e" + targetName));
                return true;
            }
            String homeName = "main";
            if (args.length >= 4) homeName = args[3];

            if (sub.equals("set")) {
                // set target's home at admin's location if sender is player
                if (sender instanceof org.bukkit.entity.Player) {
                    org.bukkit.entity.Player admin = (org.bukkit.entity.Player) sender;
                    SetHome.getInstance().homeUtils.setPlayerHomeAtLocation(target, homeName, admin.getLocation());
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSet home &e" + homeName + " &afor &e" + target.getName()));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cConsole cannot set a player's home at its location."));
                }
                return true;
            }

            if (sub.equals("tp")) {
                // teleport admin to target's home
                if (!(sender instanceof org.bukkit.entity.Player)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cConsole cannot teleport to a player's home."));
                    return true;
                }
                org.bukkit.entity.Player admin = (org.bukkit.entity.Player) sender;
                if (!SetHome.getInstance().homeUtils.homeExists(target, homeName, true)) return true;
                org.bukkit.Location loc = SetHome.getInstance().homeUtils.getPlayerHome(target, homeName);
                if (loc == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cHome not found."));
                    return true;
                }
                admin.teleport(loc);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTeleported to &e" + target.getName() + "&a's home &e" + homeName));
                return true;
            }

            if (sub.equals("delete")) {
                if (!SetHome.getInstance().homeUtils.homeExists(target, homeName, true)) return true;
                SetHome.getInstance().homeUtils.deletePlayerHome(target, homeName);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDeleted home &e" + homeName + " &afor &e" + target.getName()));
                return true;
            }

            if (sub.equals("list")) {
                java.util.List<String> names = SetHome.getInstance().homeUtils.getHomeNames(target);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHomes for &f" + target.getName() + ": &a" + String.join(", ", names)));
                return true;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUnknown subcommand. Use set,tp,delete,list."));
            return true;
        }
    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUnknown subcommand. Use /shp help"));
        return true;
    }
}
