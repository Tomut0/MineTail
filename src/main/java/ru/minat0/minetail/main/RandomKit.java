package ru.minat0.minetail.main;

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
    private final Mage.MAGIC_CLASS magicClass;

    public RandomKit(String Name, String[] Spells, Type Rare, Mage.MAGIC_CLASS magic_class) {
        this.name = Name;
        this.spells = Spells;
        this.rare = Rare;
        magicClass = magic_class;
    }

    public static void loadKits() {
        int count = 0;
        FileConfiguration config = MineTail.getConfiguration().getConfig();

        for (Type type : Type.values()) {
            for (Mage.MAGIC_CLASS magicClass : Mage.MAGIC_CLASS.values()) {
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

    public static int random(Mage.MAGIC_CLASS magic_class) {
        double random = Math.random() * Type.getSumChances();
        int count = 0;

        for (Type type : Type.values()) {
            double spellPercent = type.getPercent() / RandomKit.getCountByRareAndClass(type, magic_class);
            Logger.debug(random + " | " + type.getPercent() + " | " + spellPercent + " | " +  RandomKit.getCountByRareAndClass(type, magic_class), true);
            if (random <= type.getPercent()) {
                for (int i = 0; i < RandomKit.getCountByRareAndClass(type, magic_class); i++) {
                    Logger.debug((spellPercent * (i+1)) + "", true);
                    if (random <= spellPercent * (i+1)) {
                        return (count+i);
                    }
                }
            } else {
                random -= type.getPercent();
                count += RandomKit.getCountByRareAndClass(type, magic_class);
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

    public static long getCountByRareAndClass(Type type, Mage.MAGIC_CLASS magic_class) {
        return randomKits.values().stream().filter(randomKit -> randomKit.getRare().equals(type) && randomKit.getMagicClass().equals(magic_class)).count();
    }

    public static List<RandomKit> getSorted(Mage.MAGIC_CLASS magic_class) {
        return randomKits.values().stream().sorted(Comparator.comparing(randomKit -> randomKit.getMagicClass().name())).filter(randomKit -> randomKit.getMagicClass().equals(magic_class)).collect(Collectors.toList());
    }

    public String[] getSpells() {
        return spells;
    }

    public Mage.MAGIC_CLASS getMagicClass() {
        return magicClass;
    }

    public enum Type {
        LEGENDARY(1),
        MYTHICAL(4),
        RESTRICTED(15),
        MILITARY(30),
        INDUSTRIAL(50);

        private final double percent;

        Type(double percent) {
            this.percent = percent;
        }

        public double getPercent() {
            return percent;
        }

        public static double getSumChances() {
            return Arrays.stream(Type.values()).mapToDouble(Type::getPercent).sum();
        }
    }
}
