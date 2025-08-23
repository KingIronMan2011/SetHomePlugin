package com.kingironman.sethome.utilities;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.Map;

public class MessageUtils {

    public enum MESSAGE_TYPE {
        CMD_SETHOME("sethome.success"),
        CMD_HOME("home.success"),
        CMD_DELETEHOME("deletehome.success"),
        MISSING_HOME("error.notfound"),
        MISSING_WORLD("error.missingworld"),
        COOLDOWN("error.cooldown"),
        WARMUP("error.warmup"),
        ON_MOVE("error.onmove"),
        DENY_CONSOLE("error.denyconsole");

        private final String key;
        MESSAGE_TYPE(String key) { this.key = key; }
        public String getKey() { return key; }
    }

    public void displayMessage(CommandSender sender, MESSAGE_TYPE messageType, Integer seconds) {
        String playerName = sender.getName();
        Map<String, String> params = new HashMap<>();
        params.put("player", playerName);
        if (seconds != null) {
            params.put("seconds", String.valueOf(seconds));
        }
        String msg = LangUtils.get(messageType.getKey(), params);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

}
