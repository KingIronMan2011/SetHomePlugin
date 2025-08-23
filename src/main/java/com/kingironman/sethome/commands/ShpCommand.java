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
    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUnknown subcommand. Use /shp help"));
        return true;
    }
}
