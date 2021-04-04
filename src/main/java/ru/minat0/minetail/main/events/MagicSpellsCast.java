package ru.minat0.minetail.main.events;

import com.nisovin.magicspells.events.SpellCastEvent;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import ru.minat0.minetail.core.Flags;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.ManaBar;
import ru.minat0.minetail.core.MineTail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MagicSpellsCast implements Listener {

    private final Map<Player, Integer> remainingTimer = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void castEvent(SpellCastEvent event) {
        Player player = (Player) event.getCaster();

        Mage mage = MineTail.getDatabaseManager().getMage(player.getUniqueId());
        if (mage == null) {
            player.sendMessage(ChatColor.DARK_RED + "Вы не инициализованы на сервере как маг. Перезайдите!");
            return;
        }

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        Set<String> spellBlackList = query.queryValue(localPlayer.getLocation(), localPlayer, Flags.SPELL_BLACKLIST);
        Set<String> spellWhiteList = query.queryValue(localPlayer.getLocation(), localPlayer, Flags.SPELL_WHITELIST);

        FileConfiguration config = MineTail.getConfiguration().getConfig();

        int cooldown = (int) event.getSpell().getCooldown(player);
        if (cooldown > 0) return;

        // if Spell doesn't contains in WhiteList
        if (spellWhiteList != null && !spellWhiteList.contains(event.getSpell().getName())) {

            // if Player used a spell and spell_usage is false (deny) or it has in blacklist
            if (!query.testState(localPlayer.getLocation(), localPlayer, Flags.SPELL_USAGE)) {
                player.sendMessage(ChatColor.DARK_RED + "Вы не можете использовать магию здесь!");
                event.setCancelled(true);
                return;
            }

            if (spellBlackList != null && spellBlackList.contains(event.getSpell().getName())) {
                player.sendMessage(ChatColor.DARK_RED + "Вы не можете использовать магию здесь!");
                event.setCancelled(true);
                return;
            }
        }

        if (!remainingTimer.containsKey(player)) {
            MineTail plugin = MineTail.getInstance();
            BossBar manaBar = plugin.getManaBars().get(player.getUniqueId());

            if (manaBar == null) return;

            manaBar.addPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    double remainingMana = (double) plugin.getManaHandler().getMana(player) / plugin.getManaHandler().getMaxMana(player);
                    manaBar.setProgress(remainingMana);
                    decreaseRemainingTimer(remainingTimer, player);

                    if (remainingTimer.get(player) == 0) {
                        manaBar.removePlayer(player);
                        remainingTimer.remove(player);
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 20L);
        }

        remainingTimer.put(player, ManaBar.valueOf(mage.getManaBarAppearTime()).getTime());
    }

    void decreaseRemainingTimer(Map<Player, Integer> remainingTimer, Player player) {
        int count = remainingTimer.getOrDefault(player, 0);
        remainingTimer.put(player, count - 1);

    }
}
