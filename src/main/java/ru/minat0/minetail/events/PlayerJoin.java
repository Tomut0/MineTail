package ru.minat0.minetail.events;

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
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.data.Mage;
import ru.minat0.minetail.utils.ErrorsUtil;

import java.util.Arrays;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (MineTail.getServerManager().isAuthServer()) return;

        MineTail plugin = MineTail.getInstance();
        Player player = event.getPlayer();
        Mage mage = MineTail.getDatabaseManager().getMage(player.getUniqueId());
        Spellbook spellBook = MagicSpells.getSpellbook(player);

        if (mage == null) return;

        BarColor barColor = BarColor.valueOf(mage.getManaBarColor());
        BossBar manaBar = Bukkit.createBossBar("Мана", barColor, BarStyle.SOLID);
        plugin.getManaBars().put(player.getUniqueId(), manaBar);

        ErrorsUtil.debug(Arrays.toString(mage.getSpells()), false);
        ErrorsUtil.debug(MagicSpells.getSpellNames().toString(), false);
        for (String spellName : mage.getSpells()) {
            if (spellBook.getSpellByName(spellName) == null) {
                if (MagicSpells.getSpellNames().get(spellName) != null) {
                    spellBook.addSpell(MagicSpells.getSpellNames().get(spellName));
                    spellBook.save();
                    player.sendMessage(ChatColor.DARK_GREEN + "Вы выучили заклинание " + spellName);
                } else
                    player.sendMessage(ChatColor.DARK_RED + "Невозможно выдать заклинание, которого не существует: " + spellName);
            }
        }
    }
}
