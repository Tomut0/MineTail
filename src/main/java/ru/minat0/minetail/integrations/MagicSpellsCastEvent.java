package ru.minat0.minetail.integrations;

import com.nisovin.magicspells.events.SpellCastEvent;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import ru.minat0.minetail.MineTail;

import java.util.HashMap;
import java.util.Map;

public class MagicSpellsCastEvent implements Listener {

    private final Map<Player, Integer> remainingTimer = new HashMap<>();

    @EventHandler
    public void castEvent(SpellCastEvent event) {
        FileConfiguration config = MineTail.getConfiguration().getConfig();

        Player player = (Player) event.getCaster();
        if (player == null) return;

        int cooldown = (int) event.getSpell().getCooldown(player);
        if (cooldown > 0) return;

        if (!remainingTimer.containsKey(player)) {
            MineTail plugin = MineTail.getInstance();

            BossBar manaBar = plugin.getManaBars().get(player.getUniqueId());
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

        remainingTimer.put(player, config.getInt("disappearTime", 3));
    }

    void decreaseRemainingTimer(Map<Player, Integer> remainingTimer, Player player) {
        int count = remainingTimer.getOrDefault(player, 0);
        remainingTimer.put(player, count - 1);
    }
}
