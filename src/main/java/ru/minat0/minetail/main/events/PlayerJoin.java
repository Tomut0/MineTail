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
import org.bukkit.scheduler.BukkitRunnable;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.MineTail;
import ru.minat0.minetail.core.utils.Logger;

public class PlayerJoin implements Listener {

    int count = 3;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (MineTail.getServerManager().isAuthServer()) return;

        MineTail plugin = MineTail.getInstance();
        Player player = event.getPlayer();
        Spellbook spellBook = MagicSpells.getSpellbook(player);

        new BukkitRunnable() {

            @Override
            public void run() {
                Mage mage = MineTail.getMageDao().get(player.getUniqueId()).orElse(null);

                if (mage == null && count >= 0) {
                    Logger.debug("Попытка получить мага: " + count, true);
                    count--;
                } else if (mage == null) {
                    Logger.debug("Неуспешно! Force DB updated!", true);
                    MineTail.getMageDao().getAll().clear();
                    MineTail.getMageDao().loadMages();
                    count = 3;
                } else {
                    Logger.debug("Успешно!", true);

                    BarColor barColor = BarColor.valueOf(mage.getSettings().get(Mage.SETTINGS.MANABARCOLOR.name()));
                    BossBar manaBar = Bukkit.createBossBar("Мана", barColor, BarStyle.SOLID);
                    plugin.getManaBars().put(player.getUniqueId(), manaBar);

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

                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }
}
