package ru.minat0.minetail.main.events;

import com.Zrips.CMI.Modules.tp.Teleportations;
import com.Zrips.CMI.events.CMIPlayerTeleportEvent;
import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.players.PlayersManagerDB;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerTeleport implements Listener {

    @EventHandler
    public void onSpawnTeleport(CMIPlayerTeleportEvent event) {
        if (event.getType() == Teleportations.TeleportType.Spawn) {
            if (!PlayersManagerDB.getPlayerAccount(event.getPlayer()).getQuestDatas(BeautyQuests.getInstance().getQuests().get(10)).isFinished()) {
                event.getPlayer().sendMessage("§cВам необходимо пройти квест \"§6Начало с Нацу\"§c, чтобы телепортироваться на спавн!");
                event.setCancelled(true);
            }
        }
    }
}
