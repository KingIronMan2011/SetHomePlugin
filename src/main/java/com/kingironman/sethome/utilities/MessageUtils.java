package com.kingironman.sethome.utilities;

import com.kingironman.sethome.SetHome;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class MessageUtils {

    public enum MESSAGE_TYPE {
        CMD_SETHOME,
        CMD_HOME,
        CMD_DELETEHOME,
        MISSING_HOME,
        MISSING_WORLD,
        COOLDOWN,
        WARMUP,
        ON_MOVE,
        DENY_CONSOLE
    }

    public void displayMessage(CommandSender sender, MESSAGE_TYPE messageType, Integer seconds) {
        String playerName = sender.getName();
        Optional<Integer> secOpt = Optional.ofNullable(seconds);
        String msg = null;
        switch (messageType) {
            case CMD_SETHOME:
                msg = SetHome.getInstance().configUtils.MESSAGE_CMD_SETHOME;
                break;
            case CMD_HOME:
                msg = SetHome.getInstance().configUtils.MESSAGE_CMD_HOME;
                break;
            case CMD_DELETEHOME:
                msg = SetHome.getInstance().configUtils.MESSAGE_CMD_DELETEHOME;
                break;
            case MISSING_HOME:
                msg = SetHome.getInstance().configUtils.MESSAGE_MISSING_HOME;
                break;
            case MISSING_WORLD:
                msg = SetHome.getInstance().configUtils.MESSAGE_MISSING_WORLD;
                break;
            case COOLDOWN:
                msg = SetHome.getInstance().configUtils.MESSAGE_COOLDOWN;
                break;
            case WARMUP:
                msg = SetHome.getInstance().configUtils.MESSAGE_WARMUP;
                break;
            case ON_MOVE:
                msg = SetHome.getInstance().configUtils.MESSAGE_ON_MOVE;
                break;
            case DENY_CONSOLE:
                msg = SetHome.getInstance().configUtils.MESSAGE_DENY_CONSOLE;
                break;
        }
        if (msg != null) {
            sender.sendMessage(formatMessage(msg, playerName, secOpt));
        } else {
            sender.sendMessage(ChatColor.RED + "[SetHome] An unknown error occurred. Please contact an admin.");
        }
    }

    public static String formatMessage(String message, String playerName, Optional<Integer> seconds) {
        String formatted;

        if (seconds.isPresent()) {
            formatted = ChatColor.translateAlternateColorCodes('&', message)
                    .replace("%player%", playerName)
                    .replace("%seconds%", String.valueOf(seconds.get()));
        }
        else {
            formatted = ChatColor.translateAlternateColorCodes('&', message)
                    .replace("%player%", playerName);
        }

        return formatted;
    }

}
