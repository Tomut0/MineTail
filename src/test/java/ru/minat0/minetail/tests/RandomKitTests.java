package ru.minat0.minetail.tests;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.MineTail;
import ru.minat0.minetail.main.RandomKit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PowerMockIgnore("ru.minat0.minetail.core.*")
@PrepareForTest({RandomKit.class, JavaPlugin.class, MineTail.class})
@RunWith(PowerMockRunner.class)
public class RandomKitTests {

    @Test
    public void loadKitTest() {
        Set<RandomKit> actualSet = new HashSet();
        List<String> expected = new ArrayList<>();

        for (RandomKit.Type type : RandomKit.Type.values()) {
            for (Mage.MAGIC_CLASS magicClass : Mage.MAGIC_CLASS.values()) {
                actualSet.add(new RandomKit(null, null, type, magicClass));
                expected.add(magicClass.name());

                expected.sort(String::lastIndexOf);
            }
        }

        Assert.assertEquals(expected, actualSet.stream().map(randomKit -> randomKit.getMagicClass().name()).sorted().collect(Collectors.toList()));
    }
}
