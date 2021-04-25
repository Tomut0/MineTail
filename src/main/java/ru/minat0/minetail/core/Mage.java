package ru.minat0.minetail.core;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Minat0_
 * I'd seen example from RoinujNosde Warrior's class.
 */
public class Mage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID uuid;
    private Integer magicLVL;
    private Integer magicEXP;
    private String magicRank;
    private String magicClass;
    private List<String> spells;

    private Map<String, String> settings;
    public Mage(@NotNull UUID uuid, @NotNull String magicClass, @Nullable String kitName, @Nullable String kitRare, @Nullable List<String> spells) {
        this.uuid = uuid;
        this.magicClass = magicClass;
        this.spells = spells;
        this.magicLVL = MineTail.getConfiguration().getConfig().getInt("magicLevel", 1);
        this.magicEXP = 0;
        this.magicRank = null;
        this.settings = new HashMap<>();
        settings.put(SETTINGS.KITNAME.name(), kitName);
        settings.put(SETTINGS.KITRARE.name(), kitRare);
        settings.put(SETTINGS.MANABARTIME.name(), SETTINGS.MANABARTIME.getDefaultValue());
        settings.put(SETTINGS.MANABARCOLOR.name(), SETTINGS.MANABARCOLOR.getDefaultValue());
    }

    public Mage(@NotNull UUID uuid, @NotNull String magicClass, Integer magicLVL, Integer magicEXP,
                String magicRank, Map<String, String> settings, @Nullable List<String> spells) {
        this.uuid = uuid;
        this.magicClass = magicClass;
        this.magicRank = magicRank;
        this.magicLVL = magicLVL;
        this.magicEXP = magicEXP;
        this.settings = settings;
        this.spells = spells;
    }

    public enum magicClass {
        HOLDING_MAGIC, CASTER_MAGIC
    }

    public enum manaBarTime {
        FOREVER(-1),
        SHORT(3),
        MEDIUM(5),
        LONG(10);

        private final int appearTime;

        manaBarTime(int seconds) {
            this.appearTime = seconds;
        }

        public int getTime() {
            return appearTime;
        }
    }

    public enum ranks {
        SS(99999999, 99999999),
        S(100, 15),
        A(75, 10),
        B(50, 5),
        C(25, 3),
        D(10, 1);

        private final int questCompleted;
        private final int magicLevel;

        ranks(int questCompleted, int magicLevel) {
            this.questCompleted = questCompleted;
            this.magicLevel = magicLevel;
        }

        public int getQuestCompleted() {
            return questCompleted;
        }

        public int getMagicLevel() {
            return magicLevel;
        }
    }

    public enum SETTINGS {
        KITNAME("UNDEFINED"),
        KITRARE("UNDEFINED"),
        MANABARCOLOR("PINK"),
        MANABARTIME("MEDIUM");

        private final String defaultValue;

        SETTINGS(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }

    /*  __  ___     __  __              __
       /  |/  /__  / /_/ /_  ____  ____/ /____
      / /|_/ / _ \/ __/ __ \/ __ \/ __  / ___/
     / /  / /  __/ /_/ / / / /_/ / /_/ (__  )
    /_/  /_/\___/\__/_/ /_/\____/\__,_/____/
    */
    public void sendMessage(@Nullable String message) {
        if (message == null) return;
        Player player = getOnlinePlayer();
        if (player != null) {
            player.sendMessage(message);
        }
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @NotNull
    public UUID getUniqueId() {
        return getOfflinePlayer().getUniqueId();
    }

    @Nullable
    public Player getOnlinePlayer() {
        return getOfflinePlayer().getPlayer();
    }

    public String getName() {
        return getOfflinePlayer().getName() != null ? getOfflinePlayer().getName() : "null";
    }

    public static byte[] serialize(Mage mage) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(mage);
        return bos.toByteArray();
    }

    public static Mage deserialize(byte[] bytes) throws IOException {
        ByteArrayInputStream bos = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bos);
        try {
            return (Mage) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*  ______     __  __
      / ____/__  / /_/ /____  __________
     / / __/ _ \/ __/ __/ _ \/ ___/ ___/
    / /_/ /  __/ /_/ /_/  __/ /  (__  )
    \____/\___/\__/\__/\___/_/  /____/
    */
    public Integer getMagicLVL() {
        return magicLVL;
    }

    public Integer getMagicEXP() {
        return magicEXP;
    }

    public String getMagicRank() {
        return magicRank;
    }

    public String getMagicClass() {
        return magicClass;
    }

    public List<String> getSpells() {
        return spells;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    /*
       _____      __  __
      / ___/___  / /_/ /____  __________
      \__ \/ _ \/ __/ __/ _ \/ ___/ ___/
     ___/ /  __/ /_/ /_/  __/ /  (__  )
    /____/\___/\__/\__/\___/_/  /____/
    */
    public void setMagicLVL(Integer magicLVL) {
        this.magicLVL = magicLVL;
    }

    public void setMagicEXP(Integer magicEXP) {
        this.magicEXP = magicEXP;
    }

    public void setMagicRank(String magicRank) {
        this.magicRank = magicRank;
    }

    public void setMagicClass(String magicClass) {
        this.magicClass = magicClass;
    }

    public void setSpells(List<String> spells) {
        this.spells = spells;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}
