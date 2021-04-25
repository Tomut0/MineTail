package ru.minat0.minetail.auth;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.MineTail;
import ru.minat0.minetail.core.utils.Logger;

import java.util.*;
import java.util.stream.Collectors;

// FIXME: 14.04.2021
public class RandomKit {
    public static final Map<Integer, RandomKit> randomKits = new HashMap<>();

    private final String[] spells;
    private final Type rare;
    private final String name;
    private final Mage.magicClass magicClass;

    public RandomKit(String Name, String[] Spells, Type Rare, Mage.magicClass magicClass) {
        this.name = Name;
        this.spells = Spells;
        this.rare = Rare;
        this.magicClass = magicClass;
    }

    public static void loadKits() {
        int count = 0;
        FileConfiguration config = MineTail.getConfiguration().getConfig();

        for (Type type : Type.values()) {
            for (Mage.magicClass magicClass : Mage.magicClass.values()) {
                String path = "Kits." + magicClass.name() + "." + type.name();
                ConfigurationSection configurationSection = config.getConfigurationSection(path);

                if (configurationSection != null) {
                    for (String key : configurationSection.getKeys(false)) {
                        randomKits.put(count, new RandomKit(key, config.getStringList(path + "." + key).toArray(String[]::new), type, magicClass));
                        count++;
                    }
                }
            }
        }
    }

    public static int random(Mage.magicClass magicClass) {
        double random = Math.random() * Type.getSumChances();
        int count = 0;

        for (Type type : Type.values()) {
            double spellPercent = type.getPercent() / RandomKit.getCountByRareAndClass(type, magicClass);
            Logger.debug(random + " | " + type.getPercent() + " | " + spellPercent + " | " + RandomKit.getCountByRareAndClass(type, magicClass), true);
            if (random <= type.getPercent()) {
                for (int i = 0; i < RandomKit.getCountByRareAndClass(type, magicClass); i++) {
                    Logger.debug((spellPercent * (i + 1)) + "", true);
                    if (random <= spellPercent * (i + 1)) {
                        return (count + i);
                    }
                }
            } else {
                random -= type.getPercent();
                count += RandomKit.getCountByRareAndClass(type, magicClass);
            }
        }

        return 0;
    }

    public Type getRare() {
        return rare;
    }

    public String getName() {
        return name;
    }

    public static long getCountByRareAndClass(Type type, Mage.magicClass magicClass) {
        return randomKits.values().stream().filter(randomKit -> randomKit.getRare().equals(type) && randomKit.getMagicClass().equals(magicClass)).count();
    }

    public static List<RandomKit> getSorted(Mage.magicClass magicClass) {
        return randomKits.values().stream().sorted(Comparator.comparing(randomKit -> randomKit.getMagicClass().name())).filter(randomKit -> randomKit.getMagicClass().equals(magicClass)).collect(Collectors.toList());
    }

    public List<String> getSpells() {
        return Arrays.asList(spells);
    }

    public Mage.magicClass getMagicClass() {
        return magicClass;
    }

    public enum Type {
        LEGENDARY(1, "§6Легендарный"),
        MYTHICAL(4, "§cМифический"),
        RESTRICTED(15, "§dЗапрещенный"),
        MILITARY(30, "§7Армейский"),
        INDUSTRIAL(50, "§fШирпотрёб");

        private final double percent;
        private final String displayName;

        Type(double percent, String displayName) {
            this.percent = percent;
            this.displayName = displayName;
        }

        public double getPercent() {
            return percent;
        }

        public static double getSumChances() {
            return Arrays.stream(Type.values()).mapToDouble(Type::getPercent).sum();
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
