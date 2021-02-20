package ru.minat0.minetail.integrations;

import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.RegisterEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.data.Mage;

public class AuthMeLoginEvent implements Listener {
    private final MineTail plugin = MineTail.getInstance();

    @EventHandler
    public void TeleportOnLogin(LoginEvent event) {
        Player p = event.getPlayer();
        //if (plugin.getServerManager().isOnline("fairy")) {
        if (plugin.getDatabaseManager().getMage(p.getUniqueId()) != null) {
            plugin.getServerManager().teleportToServer(p, "fairy");
        }
        //} else {
        //    p.kickPlayer(ChatColor.DARK_RED + "Сервер, на который вы пытаетесь зайти – недоступен в данный момент. \nПовторите попытку позже.");
        //}
    }
}
