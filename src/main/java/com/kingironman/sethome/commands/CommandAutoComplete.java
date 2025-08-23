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
			List<String> subcommands = Arrays.asList("help", "backup", "restore");
			if (args.length == 1) {
				List<String> completions = new ArrayList<>();
				for (String sub : subcommands) {
					if (sub.startsWith(args[0].toLowerCase())) {
						completions.add(sub);
					}
				}
				return completions;
			}
			if (args.length == 2 && args[0].equalsIgnoreCase("restore")) {
				File backupDir = new File("plugins/SetHome/backups");
				if (backupDir.exists() && backupDir.isDirectory()) {
					List<String> files = new ArrayList<>();
					for (File f : backupDir.listFiles()) {
						if (f.isFile() && f.getName().endsWith(".zip")) {
							files.add(f.getName());
						}
					}
					Collections.sort(files);
					return files;
				}
			}
		} else if (cmd.equals("home") || cmd.equals("deletehome")) {
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
		}
		return Collections.emptyList();
	}
}
