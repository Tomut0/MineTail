package ru.minat0.minetail.core.inventories;

import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.ManaBar;
import ru.minat0.minetail.core.MineTail;
import ru.minat0.minetail.core.utils.Helper;
import ru.minat0.minetail.core.utils.Logger;
import ru.minat0.minetail.main.RandomKit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterInventory extends Inventory {

    public RegisterInventory(Player player) {
        super(player, "GUI.register", true);
    }

    @Override
    public void addGUIElements() {
        ArrayList<GuiElement> guiElements = new ArrayList<>();

        if (Helper.isMageRegistered(player)) {
            guiElements.add(new StaticGuiElement('l', new ItemStack(Material.ENDER_PEARL), click -> {
                MineTail.getServerManager().teleportToServer(player, "fairy");
                getGUI().close();
                return true;
            }, "Телепортироваться на Земной Край"));
        } else {
            for (Mage.MAGIC_CLASS magic_class : Mage.MAGIC_CLASS.values()) {
                String path = "GUI.register.items." + magic_class.name();
                String materialName = config.getString(path + ".material");
                List<String> lore = config.getStringList(path + ".lore");
                String keyString = config.getString(path + ".key");

                if (materialName != null && keyString != null) {
                    Material material = Objects.requireNonNull(Material.getMaterial(materialName));
                    char key = keyString.charAt(0);

                    guiElements.add(new StaticGuiElement(key, new ItemStack(material), click -> {
                        register(player, magic_class);
                        return true;
                    }, lore.stream().map(String -> Helper.getFormattedString(player, String)).toArray(String[]::new)));
                }
            }
        }

        getGUI().addElements(guiElements);
    }

    void register(Player player, Mage.MAGIC_CLASS magicClass) {
        RandomKit randomKit = RandomKit.getSorted(magicClass).get(RandomKit.random(magicClass));
        Logger.debug(randomKit.getName() + " | " + randomKit.getMagicClass() + " | " + randomKit.getRare(), false);
        Mage mage = new Mage(player.getUniqueId(), config.getInt("magicLevel"), null, magicClass.name(),
                config.getString("bossBarDefaultColor", "PINK"), ManaBar.MEDIUM.name(), randomKit.getSpells());
        MineTail.getDatabaseManager().insert(mage);
        MineTail.getServerManager().sendForwardMage(player, "fairy", "DatabaseChannel", "MageSetInsert", mage);
        MineTail.getServerManager().teleportToServer(player, "fairy");
    }
}
