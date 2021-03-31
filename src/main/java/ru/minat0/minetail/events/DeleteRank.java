package ru.minat0.minetail.events;

import net.sacredlabyrinth.phaed.simpleclans.Rank;
import net.sacredlabyrinth.phaed.simpleclans.events.DeleteRankEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.minat0.minetail.data.Ranks;

public class DeleteRank implements Listener {

    @EventHandler
    public void onDelete(DeleteRankEvent event) {
        Rank rank = event.getRank();
        Player sender = event.getPlayer();

        for (Ranks rankName : Ranks.values()) {
            if (rankName.name().equals(rank.getName())) {
                sender.sendMessage(ChatColor.DARK_RED + "Вы не можете удалить этот ранг!");
                event.setCancelled(true);
            }
        }
    }
}
