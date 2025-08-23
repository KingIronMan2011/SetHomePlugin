package com.kingironman.sethome.utilities;

import com.kingironman.sethome.SetHome;
import java.util.logging.Level;

public final class LoggingUtils {

    private LoggingUtils() {}

    public static void info(String msg) {
        SetHome.getInstance().getLogger().info(msg);
    }

    public static void warn(String msg) {
        SetHome.getInstance().getLogger().warning(msg);
    }

    public static void error(String msg, Throwable t) {
        SetHome.getInstance().getLogger().log(Level.SEVERE, msg, t);
    }

}
