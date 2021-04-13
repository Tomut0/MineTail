package ru.minat0.minetail.core.inventories;

import de.themoep.inventorygui.StaticGuiElement;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.minat0.minetail.core.Inventory;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.ManaBar;
import ru.minat0.minetail.core.MineTail;
import ru.minat0.minetail.core.utils.Logger;
import ru.minat0.minetail.main.RandomKit;

public class RegisterInventory extends Inventory {
    private final Player sender;

    public RegisterInventory(Player player) {
        this.sender = player;

        getGUI().setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        addGUIElements();
    }

    @Override
    public void addGUIElements() {
        for (Mage.MAGIC_CLASS magic_class : Mage.MAGIC_CLASS.values()) {
            String path = "GUI.register.items." + magic_class.name();

            ConfigurationSection configurationSection = config.getConfigurationSection(path);

            if (configurationSection != null) {
                String materialName = configurationSection.getString("material");

                if (materialName != null) {
                    Material material = Material.getMaterial(materialName);
                    String letter = configurationSection.getString("key");

                    if (material != null && letter != null) {
                        getGUI().addElement(new StaticGuiElement(letter.charAt(0),
                                new ItemStack(material), click -> registerAndTeleport(sender, magic_class),
                                configurationSection.getStringList("lore").stream().map(String ->
                                        ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(sender, String))).toArray(String[]::new)));
                    }
                }
            } else Logger.error("Error occurred when trying to find configuration section: " + path);
        }
    }

    boolean registerAndTeleport(Player player, Mage.MAGIC_CLASS magicClass) {
        boolean mageIsRegistered = MineTail.getDatabaseManager().getMages().stream().anyMatch(mage -> mage.getUniqueId().equals(player.getUniqueId()));

        if (MineTail.getServerManager().isMaintenance()) {
            player.sendMessage("Сервер находиться на техническом обслуживании, вход временно недоступен!");
            return false;
        }

        if (mageIsRegistered) {
            MineTail.getServerManager().teleportToServer(player, "fairy");
            getGUI().close();
        } else {
            RandomKit randomKit = RandomKit.getSorted(magicClass).get(RandomKit.random(magicClass));
            Logger.debug(randomKit.getName() + " | " + randomKit.getMagicClass() + " | " + randomKit.getRare(), false);
            Mage mage = new Mage(player.getUniqueId(), config.getInt("magicLevel"), null, magicClass.name(),
                    config.getString("bossBarDefaultColor", "PINK"), ManaBar.MEDIUM.name(), randomKit.getSpells());
            MineTail.getDatabaseManager().insert(mage);
            getGUI().close();
            MineTail.getServerManager().sendForwardMage(player, "fairy", "DatabaseChannel", "MageSetInsert", mage);
            MineTail.getServerManager().teleportToServer(player, "fairy");
            return true;
        }

        return false;
    }

    @Override
    public String getInventoryTitle() {
        String title = config.getString("GUI.register.title");
        if (title != null) {
            return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(sender, title));
        }
        return "Регистрация";
    }

    @Override
    public InventoryHolder getInventoryOwner() {
        return null;
    }

    @Override
    public String[] getInventoryFormat() {
        return config.getStringList("GUI.register.format").toArray(new String[0]);
    }
}
