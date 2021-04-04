package ru.minat0.minetail.main.events;

import fr.skytasul.quests.players.PlayerQuestDatas;
import fr.skytasul.quests.players.PlayersManagerDB;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.MineTail;
import ru.minat0.minetail.core.Ranks;

public class CreateClan implements Listener {

    @EventHandler
    public void onCreate(CreateClanEvent event) {
        Clan clan = event.getClan();
        Player player = clan.getLeaders().get(0).toPlayer();

        if (player == null) {
            return;
        }
        Mage mage = MineTail.getDatabaseManager().getMage(player.getUniqueId());

        if (mage == null) return;

        for (Ranks rankName : Ranks.values()) {
            clan.createRank(rankName.name());
        }

        long completedQuests = PlayersManagerDB.getPlayerAccount(player).getQuestsDatas().stream().filter(PlayerQuestDatas::isFinished).count();
        player.sendMessage(completedQuests + "");
        for (Ranks rank : Ranks.values()) {
            if (completedQuests >= rank.getQuestCompleted() && mage.getMagicLevel() >= rank.getMagicLevel()) {
                clan.getLeaders().get(0).setRank(rank.name());
            }
        }

        SimpleClans.getInstance().getStorageManager().updateClan(clan);
    }
}
