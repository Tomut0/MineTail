package ru.minat0.minetail.main.events;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.MineTail;
import ru.minat0.minetail.core.utils.Logger;

import java.util.Arrays;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (MineTail.getServerManager().isAuthServer()) return;

        MineTail plugin = MineTail.getInstance();
        Player player = event.getPlayer();
        Mage mage = MineTail.getDatabaseManager().getMage(player.getUniqueId());
        Spellbook spellBook = MagicSpells.getSpellbook(player);

        if (mage == null) {
            Logger.debug("Маг не найден! FORCE DB RELOAD!", false);
            MineTail.getDatabaseManager().getMages().clear();
            MineTail.getDatabaseManager().loadDataToMemory();
            return;
        }

        BarColor barColor = BarColor.valueOf(mage.getManaBarColor());
        BossBar manaBar = Bukkit.createBossBar("Мана", barColor, BarStyle.SOLID);
        plugin.getManaBars().put(player.getUniqueId(), manaBar);

        Logger.debug(Arrays.toString(mage.getSpells()), true);
        Logger.debug(MagicSpells.getSpellNames().toString(), true);

        for (String spellName : mage.getSpells()) {
            if (spellBook.getSpellByName(spellName) == null) {
                if (MagicSpells.getSpellNames().get(spellName.toLowerCase()) != null) {
                    spellBook.addSpell(MagicSpells.getSpellNames().get(spellName.toLowerCase()));
                    spellBook.save();
                    player.sendMessage(ChatColor.DARK_GREEN + "Вы выучили заклинание " + spellName);
                } else
                    player.sendMessage(ChatColor.DARK_RED + "Невозможно выдать заклинание, которого не существует: " + spellName);
            }
        }
    }
}
