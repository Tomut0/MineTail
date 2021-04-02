package ru.minat0.minetail.main.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ru.minat0.minetail.core.MineTail;

public class AsyncChat implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().startsWith("!") && event.getMessage().length() > 1) {
            String newMessage = event.getMessage().replaceFirst("!", "");
            event.setMessage(newMessage);
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().chat("/g " + newMessage);
                }
            }.runTask(MineTail.getInstance());
            event.setCancelled(true);
        }
    }
}
