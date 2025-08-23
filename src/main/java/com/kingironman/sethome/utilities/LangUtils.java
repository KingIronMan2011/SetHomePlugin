package com.kingironman.sethome.utilities;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LangUtils {
    private static final String LANG_DIR = "languages/";
    private static final String DEFAULT_LANG = "en";
    private static Map<String, String> messages = new HashMap<>();
    private static String currentLang = DEFAULT_LANG;

    public static void loadLanguage(JavaPlugin plugin, String lang) {
        currentLang = lang;
        messages.clear();
        try {
            String fileName = LANG_DIR + lang + ".json";
            Reader reader = new InputStreamReader(
                Objects.requireNonNull(plugin.getResource(fileName)), StandardCharsets.UTF_8);
            JSONObject obj = (JSONObject) new JSONParser().parse(reader);
            for (Object key : obj.keySet()) {
                messages.put(key.toString(), obj.get(key).toString());
            }
        } catch (Exception e) {
            if (!lang.equals(DEFAULT_LANG)) {
                loadLanguage(plugin, DEFAULT_LANG);
            }
        }
    }

    public static String get(String key) {
        return messages.getOrDefault(key, key);
    }

    public static String get(String key, Map<String, String> params) {
        String msg = get(key);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return msg;
    }

    public static String getCurrentLang() {
        return currentLang;
    }
}
