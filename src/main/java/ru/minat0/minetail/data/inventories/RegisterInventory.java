package ru.minat0.minetail.data.inventories;

import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.commands.Register;
import ru.minat0.minetail.data.Mage;
import ru.minat0.minetail.utils.ErrorsUtil;

public class RegisterInventory {
    private final MineTail plugin = MineTail.getInstance();
    private final FileConfiguration config = plugin.getConfig();

    public InventoryGui gui = new InventoryGui(plugin, null, this.getInventoryTitle(), this.getInventoryFormat());

    private final Player sender;

    public RegisterInventory(Register register) {
        this.sender = (Player) register.getCommandSender();

        gui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        addGUIElements();
    }

    private void addGUIElements() {
        String path = "GUI.register.items.";

        for (Mage.MAGIC_CLASS magic_class : Mage.MAGIC_CLASS.values()) {
            ConfigurationSection configurationSection = config.getConfigurationSection(path + magic_class.name());

            if (configurationSection != null) {
                String materialName = configurationSection.getString("material");

                if (materialName != null) {
                    Material material = Material.getMaterial(materialName);
                    String letter = configurationSection.getString("key");

                    if (material != null && letter != null) {
                        gui.addElement(new StaticGuiElement(letter.charAt(0),
                                new ItemStack(material), click -> insertAndTeleport(sender, magic_class.name()),
                                configurationSection.getStringList("lore").stream().map(String ->
                                        ChatColor.translateAlternateColorCodes('&', String)).toArray(String[]::new)));
                    }
                }
            } else ErrorsUtil.error("Error occurred when trying to find configuration section: " + path);
        }
    }

    boolean insertAndTeleport(Player player, String magicClass) {
        MineTail.getDatabaseManager().insert(new Mage(player, config.getInt("magicLevel"), null, magicClass, "PINK"));
        gui.close();
        MineTail.getServerManager().teleportToServer(player, "fairy");
        return true;
    }

    String getInventoryTitle() {
        return config.getString("GUI.register.title", "null");
    }

    String[] getInventoryFormat() {
        return config.getStringList("GUI.register.format").toArray(new String[0]);
    }
}
