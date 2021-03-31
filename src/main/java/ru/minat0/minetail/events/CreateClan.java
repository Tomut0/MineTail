package ru.minat0.minetail.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.minat0.minetail.data.Ranks;

public class CreateClan implements Listener {

    @EventHandler
    public void onCreate(CreateClanEvent event) {
        Clan clan = event.getClan();

        for (Ranks rankName : Ranks.values()) {
            clan.createRank(rankName.name());
        }

        SimpleClans.getInstance().getStorageManager().updateClan(clan);
    }
}
