package ru.minat0.minetail.core.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.minat0.minetail.core.MineTail;

import java.util.StringTokenizer;

// FIXME: 14.04.2021
public class Helper {

    public static String getFormattedString(@Nullable Player who, @NotNull String String) {
        return ChatColor.translateAlternateColorCodes('&', who != null ? PlaceholderAPI.setPlaceholders(who, String) : String);
    }

    public static String getFormattedString(@NotNull String String) {
        return getFormattedString(null, String);
    }

    public static boolean isMageRegistered(@NotNull Player player) {
        return MineTail.getDatabaseManager().getMages().stream().anyMatch(mage -> mage.getUniqueId().equals(player.getUniqueId()));
    }

    public static String serialize(@NotNull String[] strs) {
        StringBuilder sizes = new StringBuilder("$");
        StringBuilder result = new StringBuilder();

        for (String str : strs) {
            if (sizes.length() != 1) {
                sizes.append(';');
            }
            sizes.append(str.length());
            result.append(str);
        }

        result.append(sizes.toString());
        return result.toString();
    }

    public static String[] unserialize(String result) {
        if (result != null) {
            int sizesSplitPoint = result.lastIndexOf('$');
            String sizes = result.substring(sizesSplitPoint + 1);
            StringTokenizer st = new StringTokenizer(sizes, ";");
            String[] resultArray = new String[st.countTokens()];

            int i = 0;
            int lastPosition = 0;
            while (st.hasMoreTokens()) {
                String stringLengthStr = st.nextToken();
                int stringLength = Integer.parseInt(stringLengthStr);
                resultArray[i++] = result.substring(lastPosition, lastPosition + stringLength);
                lastPosition += stringLength;
            }
            return resultArray;
        }

        return new String[0];
    }
}
