package ru.minat0.minetail.main.events;

import com.Zrips.CMI.Modules.tp.Teleportations;
import com.Zrips.CMI.events.CMIAsyncPlayerTeleportEvent;
import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.players.PlayersManagerDB;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerTeleport implements Listener {

    @EventHandler
    public void onSpawnTeleport(CMIAsyncPlayerTeleportEvent event) {
        if (event.getType() == Teleportations.TeleportType.Spawn) {
            if (!PlayersManagerDB.getPlayerAccount(event.getPlayer()).getQuestDatas(BeautyQuests.getInstance().getQuests().get(10)).isFinished()) {
                event.setCancelled(true);
            }
        }
    }
}
