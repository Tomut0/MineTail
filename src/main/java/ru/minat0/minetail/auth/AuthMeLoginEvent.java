package ru.minat0.minetail.auth;

import fr.xephi.authme.events.LoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import ru.minat0.minetail.core.MineTail;

public class AuthMeLoginEvent implements Listener {

    @EventHandler
    public void TeleportOnLogin(LoginEvent event) {
        Player p = event.getPlayer();
        FileConfiguration config = MineTail.getConfiguration().getConfig();

        if (MineTail.getServerManager().isOnline(config.getString("host"), config.getInt("port"))) {
            if (MineTail.getDatabaseManager().getMage(p.getUniqueId()) != null) {
                p.sendMessage(ChatColor.YELLOW + "Подключаемся к серверу FairyTail");
                MineTail.getServerManager().teleportToServer(p, "fairy");
            }
        } else {
            final @NonNull TextComponent textComponent = Component.text("Сервер, на который вы пытаетесь зайти – ")
                    .append(Component.text("недоступен в данный момент.", NamedTextColor.DARK_RED)
                            .append(Component.text("\nПовторите попытку позже.", NamedTextColor.WHITE)));
            p.kick(textComponent);
        }
    }
}
