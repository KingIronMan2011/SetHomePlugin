package com.kingironman.sethome.commands;

import com.kingironman.sethome.SetHome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class CommandAutoComplete implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return Collections.emptyList();
		}
		Player player = (Player) sender;
		String cmd = command.getName().toLowerCase();

		if (cmd.equals("shp")) {
			List<String> subcommands = Arrays.asList("help", "backup", "restore", "home");
			if (args.length == 1) {
				List<String> completions = new ArrayList<>();
				String prefix = args[0].toLowerCase();
				for (String sub : subcommands) {
					if (sub.startsWith(prefix)) {
						completions.add(sub);
					}
				}
				return completions;
			}

			// /shp restore <backup.zip>
			if (args.length == 2 && args[0].equalsIgnoreCase("restore")) {
				File backupDir = new File("plugins/SetHome/backups");
				List<String> files = new ArrayList<>();
				if (backupDir.exists() && backupDir.isDirectory()) {
					for (File f : backupDir.listFiles()) {
						if (f.isFile() && f.getName().endsWith(".zip")) {
							files.add(f.getName());
						}
					}
				}
				String prefix = args[1].toLowerCase();
				List<String> completions = new ArrayList<>();
				for (String f : files) if (f.toLowerCase().startsWith(prefix)) completions.add(f);
				Collections.sort(completions);
				return completions;
			}

			// /shp home <set|tp|delete|list> <player> [homeName]
			if (args.length >= 2 && args[0].equalsIgnoreCase("home")) {
				List<String> homeSub = Arrays.asList("set", "tp", "delete", "list");
				if (args.length == 2) {
					List<String> completions = new ArrayList<>();
					String prefix = args[1].toLowerCase();
					for (String s : homeSub) if (s.startsWith(prefix)) completions.add(s);
					return completions;
				}
				if (args.length == 3) {
					// suggest online players for third arg
					List<String> matches = new ArrayList<>();
					String prefix = args[2].toLowerCase();
					for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
						if (p.getName().toLowerCase().startsWith(prefix)) matches.add(p.getName());
					}
					Collections.sort(matches);
					return matches;
				}
				if (args.length == 4) {
					String sub = args[1].toLowerCase();
					if (sub.equals("set") || sub.equals("tp") || sub.equals("delete")) {
						Player target = org.bukkit.Bukkit.getPlayerExact(args[2]);
						if (target != null && target.isOnline()) {
							List<String> homeNames = SetHome.getInstance().homeUtils.getHomeNames(target);
							String prefix = args[3].toLowerCase();
							List<String> suggestions = new ArrayList<>();
							for (String home : homeNames) if (home.toLowerCase().startsWith(prefix)) suggestions.add(home);
							return suggestions;
						}
					}
				}
			}
	} else if (cmd.equals("home") || cmd.equals("deletehome") || cmd.equals("sethome") || cmd.equals("listhome")) {
			// Only suggest home names for the first argument
			if (args.length == 1) {
				List<String> homeNames = SetHome.getInstance().homeUtils.getHomeNames(player);
				String prefix = args[0].toLowerCase();
				List<String> suggestions = new ArrayList<>();
				for (String home : homeNames) {
					if (home.toLowerCase().startsWith(prefix)) {
						suggestions.add(home);
					}
				}
				return suggestions;
			}
		} else if (cmd.equals("invitehome")) {
			// /invitehome <player> [homeName]
			if (args.length == 1) {
				List<String> matches = new ArrayList<>();
				String prefix = args[0].toLowerCase();
				for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
					if (p.getName().toLowerCase().startsWith(prefix) && !p.getUniqueId().equals(player.getUniqueId())) {
						matches.add(p.getName());
					}
				}
				Collections.sort(matches);
				return matches;
			} else if (args.length == 2) {
				List<String> homeNames = SetHome.getInstance().homeUtils.getHomeNames(player);
				String prefix = args[1].toLowerCase();
				List<String> suggestions = new ArrayList<>();
				for (String home : homeNames) {
					if (home.toLowerCase().startsWith(prefix)) suggestions.add(home);
				}
				return suggestions;
			}
		} else if (cmd.equals("acceptinvite")) {
			// no suggestions
			return Collections.emptyList();
		}
		else if (cmd.equals("homes")) {
			// /homes [player] - suggest online player names for OPs
			if (args.length == 1) {
				if (!player.isOp()) return Collections.emptyList();
				List<String> matches = new ArrayList<>();
				String prefix = args[0].toLowerCase();
				for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
					if (p.getName().toLowerCase().startsWith(prefix)) matches.add(p.getName());
				}
				Collections.sort(matches);
				return matches;
			}
		}
		return Collections.emptyList();
	}
}
