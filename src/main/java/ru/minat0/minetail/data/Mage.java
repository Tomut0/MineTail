package ru.minat0.minetail.data;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Minat0_
 * I'd seen example from RoinujNosde Warrior's class.
 */
public class Mage {

    private final OfflinePlayer player;
    private Integer magicLevel;
    private String rank;
    private String magicClass;
    private String manaBarColor;

    public Mage(@NotNull OfflinePlayer player, Integer magicLevel, @Nullable String rank, @Nullable String magicClass, String manaBarColor) {
        this.player = player;
        this.rank = rank;
        this.magicLevel = magicLevel;
        this.magicClass = magicClass;
        this.manaBarColor = manaBarColor;
    }

    public enum MAGIC_CLASS {
        HOLDING_MAGIC, CASTER_MAGIC
    }

    @Nullable
    public Player toOnlinePlayer() {
        return player.getPlayer();
    }

    @NotNull
    public OfflinePlayer toPlayer() {
        return player;
    }

    @NotNull
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public int hashCode() {
        return toPlayer().getUniqueId().hashCode();
    }

    @NotNull
    public String getName() {
        return player.getName() != null ? player.getName() : "null";
    }

    public void sendMessage(@Nullable String message) {
        if (message == null) return;
        Player player = toOnlinePlayer();
        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Mage) {
            final UUID uniqueId = toPlayer().getUniqueId();
            final UUID uniqueId2 = ((Mage) o).toPlayer().getUniqueId();
            return uniqueId.equals(uniqueId2);
        }
        return false;
    }

    public Integer getMagicLevel() {
        return magicLevel;
    }

    public void setMagicLevel(Integer magicLevel) {
        this.magicLevel = magicLevel;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getMagicClass() {
        return magicClass;
    }

    public void setMagicClass(String magicClass) {
        this.magicClass = magicClass;
    }

    public String getManaBarColor() {
        return manaBarColor;
    }

    public void setManaBarColor(String manaBarColor) {
        this.manaBarColor = manaBarColor;
    }
}
