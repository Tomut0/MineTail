package ru.minat0.minetail.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.UUID;

/**
 * @author Minat0_
 * I'd seen example from RoinujNosde Warrior's class.
 */
public class Mage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID uuid;
    private Integer magicLevel;
    private String rank;
    private String magicClass;
    private String manaBarColor;
    private String manaBarAppearTime;
    private String[] spells;

    public boolean changed = false;

    public Mage(@NotNull UUID uuid, Integer magicLevel, @Nullable String rank, @Nullable String magicClass, String manaBarColor, String manaBarAppearTime, @Nullable String[] Spells) {
        this.uuid = uuid;
        this.rank = rank;
        this.magicLevel = magicLevel;
        this.magicClass = magicClass;
        this.manaBarColor = manaBarColor;
        this.manaBarAppearTime = manaBarAppearTime;
        this.spells = Spells;
    }

    public enum MAGIC_CLASS {
        HOLDING_MAGIC, CASTER_MAGIC
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @Nullable
    public Player getOnlinePlayer() {
        return getOfflinePlayer().getPlayer();
    }

    @NotNull
    public UUID getUniqueId() {
        return getOfflinePlayer().getUniqueId();
    }

    @Override
    public int hashCode() {
        return getOfflinePlayer().getUniqueId().hashCode();
    }

    @NotNull
    public String getName() {
        return getOfflinePlayer().getName() != null ? getOfflinePlayer().getName() : "null";
    }

    public void sendMessage(@Nullable String message) {
        if (message == null) return;
        Player player = getOnlinePlayer();
        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Mage) {
            final UUID uniqueId = getOfflinePlayer().getUniqueId();
            final UUID uniqueId2 = ((Mage) o).getOfflinePlayer().getUniqueId();
            return uniqueId.equals(uniqueId2);
        }
        return false;
    }

    public static byte[] serialize(@NotNull Mage mage) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(mage);
        return bos.toByteArray();
    }

    public static Mage deserialize(@NotNull byte[] bytes) throws IOException {
        ByteArrayInputStream bos = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bos);
        try {
            return (Mage) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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

    public String getManaBarAppearTime() {
        return manaBarAppearTime;
    }

    public void setManaBarAppearTime(String manaBarAppearTime) {
        this.manaBarAppearTime = manaBarAppearTime;
    }

    public String[] getSpells() {
        return spells;
    }

    public void setSpells(String[] spells) {
        this.spells = spells;
    }
}
