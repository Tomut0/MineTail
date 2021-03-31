package ru.minat0.minetail.integrations;

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
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.data.Mage;
import ru.minat0.minetail.data.ManaBarAppearTime;
import ru.minat0.minetail.integrations.worldguard.Flags;

import java.util.HashMap;
import java.util.Map;

public class MagicSpellsCastEvent implements Listener {

    private final Map<Player, Integer> remainingTimer = new HashMap<>();

    @EventHandler
    public void castEvent(SpellCastEvent event) {
        Player player = (Player) event.getCaster();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        //Collection<String> greetings = query.queryAllValues(localPlayer.getLocation(), localPlayer, );

//        if (!query.testState(localPlayer.getLocation(), localPlayer, Flags.SPELL_USAGE.getType())) {
//            player.sendMessage(ChatColor.DARK_RED + "Вы не можете использовать магию здесь!");
//            event.setCancelled(true);
//        }

        Mage mage = MineTail.getDatabaseManager().getMage(player.getUniqueId());

        FileConfiguration config = MineTail.getConfiguration().getConfig();

        int cooldown = (int) event.getSpell().getCooldown(player);
        if (cooldown > 0) return;

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

        remainingTimer.put(player, mage != null ? ManaBarAppearTime.valueOf(mage.getManaBarAppearTime()).getTime() : config.getInt("disappearTime", 3));
    }

    void decreaseRemainingTimer(Map<Player, Integer> remainingTimer, Player player) {
        int count = remainingTimer.getOrDefault(player, 0);
        remainingTimer.put(player, count - 1);
    }
}
