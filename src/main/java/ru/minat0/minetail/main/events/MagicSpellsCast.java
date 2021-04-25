package ru.minat0.minetail.main.events;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.mana.ManaHandler;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.MineTail;
import ru.minat0.minetail.core.worldguard.Flags;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MagicSpellsCast implements Listener {

    private final Map<Player, Integer> remainingTimer = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void castEvent(SpellCastEvent event) {
        Player player = (Player) event.getCaster();

        Mage mage = MineTail.getMageDao().get(player.getUniqueId()).orElse(null);
        if (mage == null) {
            player.sendMessage(ChatColor.DARK_RED + "Вы не инициализованы на сервере как маг. Перезайдите!");
            return;
        }

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        Set<String> spellBlackList = query.queryValue(localPlayer.getLocation(), localPlayer, Flags.SPELL_BLACKLIST);
        Set<String> spellWhiteList = query.queryValue(localPlayer.getLocation(), localPlayer, Flags.SPELL_WHITELIST);

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
            ManaHandler manaHandler = MagicSpells.getManaHandler();
            BossBar manaBar = MineTail.getInstance().getManaBars().get(player.getUniqueId());

            if (manaBar == null) return;

            manaBar.addPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    double remainingMana = (double) manaHandler.getMana(player) / manaHandler.getMaxMana(player);
                    manaBar.setProgress(remainingMana);
                    decreaseRemainingTimer(remainingTimer, player);

                    if (remainingTimer.get(player) == 0) {
                        manaBar.removePlayer(player);
                        remainingTimer.remove(player);
                        this.cancel();
                    }
                }
            }.runTaskTimer(MineTail.getInstance(), 0, 20L);
        }

        remainingTimer.put(player, Mage.manaBarTime.valueOf(mage.getSettings().get(Mage.SETTINGS.MANABARTIME.name())).getTime());
    }

    @EventHandler
    public void onTarget(SpellTargetEvent event) {
        Optional<Mage> optionalMage = MineTail.getMageDao().get(event.getCaster().getUniqueId());

        if (optionalMage.isPresent()) {
            FileConfiguration config = MineTail.getConfiguration().getConfig();
            Mage mage = optionalMage.get();

            if (mage.getMagicLVL() < config.getInt("maxLevel")) {
                int spellExp = config.getInt("spellsExp." + event.getSpell().getName(), 10);
                mage.setMagicEXP(mage.getMagicEXP() + spellExp);

                if (mage.getMagicEXP() >= MineTail.levelMap.get(mage.getMagicLVL())) {
                    mage.setMagicEXP(0);
                    mage.setMagicLVL(mage.getMagicLVL() + 1);
                }

                Audience.audience(event.getCaster()).sendActionBar(Component.text("Ваш опыт: " + mage.getMagicEXP()));
            }
        }
    }

    void decreaseRemainingTimer(Map<Player, Integer> remainingTimer, Player player) {
        int count = remainingTimer.getOrDefault(player, 0);
        remainingTimer.put(player, count - 1);

    }
}
