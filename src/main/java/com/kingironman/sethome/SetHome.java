package com.kingironman.sethome;

import com.kingironman.sethome.commands.CommandExecutor;
import com.kingironman.sethome.commands.CommandAutoComplete;
import com.kingironman.sethome.commands.Commands;
import com.kingironman.sethome.commands.ShpCommand;
import com.kingironman.sethome.converters.ConfigManipulation;
import com.kingironman.sethome.converters.ConfigV5ToV6;
import com.kingironman.sethome.converters.HomesV5ToV6;
import com.kingironman.sethome.converters.HomesV61ToV62;
import com.kingironman.sethome.events.EventMove;
import com.kingironman.sethome.events.EventQuit;
import com.kingironman.sethome.events.EventRespawn;
import com.kingironman.sethome.metrics.MetricsHandler;
import com.kingironman.sethome.utilities.ConfigUtils;
import com.kingironman.sethome.utilities.HomeUtils;
import com.kingironman.sethome.utilities.MessageUtils;
import com.kingironman.sethome.utilities.UpdateChecker;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SetHome extends JavaPlugin {
    @SuppressWarnings("unused")
	private MetricsHandler metricsHandler;

    private static SetHome instance;

    public SetHome() {
        SetHome.instance = this;
    }

    public static SetHome getInstance() {
        return SetHome.instance;
    }

    public ConfigUtils configUtils;
    public MessageUtils messageUtils;
    public HomeUtils homeUtils;
    public Commands commands;
    public ConfigManipulation configManipulation;
    public ConfigV5ToV6 configV5ToV6;
    public HomesV5ToV6 homesV5ToV6;
    public HomesV61ToV62 homesV61ToV62;

    @Override

    public void onEnable() {
        // Copy default config if missing
        saveDefaultConfig();
        // Update config with new options (preserve user settings)
        new ConfigManipulation().updateConfigWithDefaults();

        // Load language from config or default to 'en'
        String lang = getConfig().getString("extra.language", "en");
        com.kingironman.sethome.utilities.LangUtils.loadLanguage(this, lang);

        // Initialize objects
        configUtils = new ConfigUtils();
        messageUtils = new MessageUtils();
        homeUtils = new HomeUtils();
        commands = new Commands();
        configManipulation = new ConfigManipulation();
        configV5ToV6 = new ConfigV5ToV6();
        homesV5ToV6 = new HomesV5ToV6();
        homesV61ToV62 = new HomesV61ToV62();

        metricsHandler = new MetricsHandler(this, 27015);

        // Register commands and tab completers
        CommandExecutor executor = new CommandExecutor();
        CommandAutoComplete autoComplete = new CommandAutoComplete();
        getCommand("sethome").setExecutor(executor);
        getCommand("sethome").setTabCompleter(autoComplete);
        getCommand("home").setExecutor(executor);
        getCommand("home").setTabCompleter(autoComplete);
        getCommand("deletehome").setExecutor(executor);
        getCommand("deletehome").setTabCompleter(autoComplete);
        getCommand("listhome").setExecutor(executor);
        getCommand("listhome").setTabCompleter(autoComplete);
        getCommand("shp").setExecutor(new ShpCommand());
        getCommand("shp").setTabCompleter(autoComplete);

        // Register events
        getServer().getPluginManager().registerEvents(new EventMove(), this);
        getServer().getPluginManager().registerEvents(new EventQuit(), this);
        getServer().getPluginManager().registerEvents(new EventRespawn(), this);

        // Check for updates
        new UpdateChecker(128270).getVersion(version -> {
            if (!getDescription().getVersion().equals(version)) {
                getLogger().info("SetHome Remote version: " + version);
                getLogger().info("SetHome Local version: " + getDescription().getVersion());
            }
        });
    }

    @Override
    public void onDisable() {
        // Unregister commands
        getCommand("sethome").setExecutor(null);
        getCommand("home").setExecutor(null);
        getCommand("deletehome").setExecutor(null);
        getCommand("listhome").setExecutor(null);

        // Unregister events
        PlayerMoveEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerRespawnEvent.getHandlerList().unregister(this);

        // De-initialize objects
        configUtils = null;
        messageUtils = null;
        homeUtils = null;
        commands = null;
        configManipulation = null;
        configV5ToV6 = null;
        homesV5ToV6 = null;
        homesV61ToV62 = null;
    }

}