package ru.minat0.minetail.events;

import me.RockinChaos.itemjoin.api.APIUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import ru.minat0.minetail.MineTail;

public class PlayerJoin implements Listener {
    private final APIUtils apiUtils = new APIUtils();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        MineTail plugin = MineTail.getInstance();
        Player player = event.getPlayer();
        BarColor barColor = BarColor.valueOf(MineTail.getDatabaseManager().getMage(player.getUniqueId()).getManaBarColor());

        if (!MineTail.getServerManager().isAuthServer()) {
            BossBar manaBar = Bukkit.createBossBar("Мана", barColor, BarStyle.SOLID);
            plugin.getManaBars().put(player.getUniqueId(), manaBar);
            ItemStack item = this.apiUtils.getItemStack(player, "player-profile");
        }
    }
}
