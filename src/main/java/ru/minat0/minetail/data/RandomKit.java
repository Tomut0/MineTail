package ru.minat0.minetail.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.utils.ErrorsUtil;

import java.util.*;

public class RandomKit {
    public static final Map<Integer, RandomKit> randomKits = new HashMap<>();

    private final String[] spells;
    private final Type rare;
    private final String name;
    private final Mage.MAGIC_CLASS magicClass;

    private RandomKit(String Name, String[] Spells, Type Rare, Mage.MAGIC_CLASS magicClass) {
        this.name = Name;
        this.spells = Spells;
        this.rare = Rare;
        this.magicClass = magicClass;
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
        double percent = 0;

        for (Type type : Type.values()) {
            percent += type.getPercent();
            double spellPercent = type.getPercent() / RandomKit.getCountByRareAndClass(type, magic_class);
            if (random <= percent) {
                ErrorsUtil.debug(random + "/" + percent, false);
                for (int i = 1; i <= RandomKit.getCountByRareAndClass(type, magic_class); i++) {
                    if (random <= spellPercent * i) {
                        return i;
                    }
                }
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

    public String[] getSpells() {
        return spells;
    }

    public Mage.MAGIC_CLASS getMagicClass() {
        return magicClass;
    }

    enum Type {
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

        private static double getSumChances() {
            return Arrays.stream(Type.values()).mapToDouble(Type::getPercent).sum();
        }
    }
}
