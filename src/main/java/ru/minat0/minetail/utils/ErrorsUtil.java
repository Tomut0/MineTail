package ru.minat0.minetail.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import ru.minat0.minetail.MineTail;

import java.util.logging.Level;

public class ErrorsUtil {
    public static void error(String message) {
        Bukkit.getLogger().log(Level.SEVERE, "[MineTail] " + message);
    }

    public static void warning(String message) {
        Bukkit.getLogger().log(Level.WARNING, "[MineTail] " + message);
    }

    /**
     * Sends a message to the console
     *
     * @author RoinujNosde
     * @param message message to send
     * @param respectUserDecision should the message be sent if debug is false?
     */
    public static void debug(String message, boolean respectUserDecision) {
        if (respectUserDecision) {
            if (!MineTail.getInstance().getConfiguration().getConfig().getBoolean("debug")) {
                return;
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[MineTail] " + message);
    }
}
