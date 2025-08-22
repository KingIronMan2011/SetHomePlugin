package com.kingironman.sethome.commands;

import com.kingironman.sethome.SetHome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandAutoComplete implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return Collections.emptyList();
		}
		Player player = (Player) sender;
		String cmd = command.getName().toLowerCase();

		// Only suggest home names for the first argument
		if (args.length == 1) {
			List<String> homeNames = SetHome.getInstance().homeUtils.getHomeNames(player);
			if (cmd.equals("home") || cmd.equals("deletehome")) {
				// Filter by what the user has started typing
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
