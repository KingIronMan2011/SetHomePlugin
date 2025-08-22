package com.kingironman.sethome.metrics;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class MetricsHandler {
    private final Metrics metrics;

    public MetricsHandler(JavaPlugin plugin, int pluginId) {
        this.metrics = new Metrics(plugin, pluginId);
    }

    public Metrics getMetrics() {
        return metrics;
    }
}
