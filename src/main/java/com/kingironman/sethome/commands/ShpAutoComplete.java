package com.kingironman.sethome.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShpAutoComplete implements TabCompleter {
    private static final List<String> SUBCOMMANDS = Arrays.asList("help", "backup", "restore");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String sub : SUBCOMMANDS) {
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
        return Collections.emptyList();
    }
}
