package com.kingironman.sethome.utilities;

import com.kingironman.sethome.SetHome;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    public final int resourceId;

    public UpdateChecker(int resourceId) {
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        if (!SetHome.getInstance().configUtils.EXTRA_CHECK_UPDATES) return;
        Bukkit.getScheduler().runTaskAsynchronously(SetHome.getInstance(), () -> {
            try {
                java.net.URI uri = new java.net.URI("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId);
                try (InputStream is = uri.toURL().openStream(); Scanner scanner = new Scanner(is)) {
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                }
            } catch (IOException | java.net.URISyntaxException e) {
                SetHome.getInstance().getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }

}