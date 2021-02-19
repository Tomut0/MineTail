package ru.minat0.minetail.integrations;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.minat0.minetail.utils.ServerUtils;

public class AuthMeLoginEvent implements Listener {

    @EventHandler
    public void TeleportOnLogin(LoginEvent event) {
        Player p = event.getPlayer();
        ServerUtils serverUtils = new ServerUtils();
        if (serverUtils.isOnline("fairy")) {
            if (p.hasPlayedBefore()) {
                serverUtils.teleportToServer(p, "fairy");
            }
        } else {
            p.kickPlayer(ChatColor.DARK_RED + "Сервер, на который вы пытаетесь зайти – недоступен в данный момент. \nПовторите попытку позже.");
        }
    }
}
