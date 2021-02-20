package ru.minat0.minetail.commands;

import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.data.BaseCommand;
import ru.minat0.minetail.data.Mage;

public class Register extends BaseCommand {
    private final MineTail plugin = MineTail.getInstance();

    private static final String commandDescription = "Зарегистрироваться на сервере. Работает только один раз при заходе на сервер.";
    private static final String commandPermission = "minetail.register";

    @Override
    public String getCommandDescription() {
        return commandDescription;
    }

    @Override
    public String getCommandPermission() {
        return commandPermission;
    }

    @Override
    public String getCommandParameters() {
        return null;
    }

    @Override
    public void initialize(String[] args, Player sender) {
        FileConfiguration config = plugin.getConfiguration().getConfig();

        String[] guiSetup = {"    s    "};
        InventoryGui gui = new InventoryGui(plugin, null, "Регистрация | Выбор класса", guiSetup);
        gui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS, 1));
        gui.addElement(new StaticGuiElement('s', new ItemStack(Material.DIAMOND_SWORD), new GuiElement.Action() {
            @Override
            public boolean onClick(GuiElement.Click click) {
                /**
                 * Todo: Async thread + syncing between servers + Remove user permission to register
                 */
                plugin.getDatabaseManager().insert(new Mage(sender, config.getInt("mana"),
                        config.getInt("maxMana"), config.getInt("magicLevel"), null, Mage.MAGIC_CLASS.HOLDING_MAGIC.name()));
                gui.close();
                plugin.getServerManager().teleportToServer(sender, "fairy");
                return true;
            }
        }, "Это магия", "построенная на..."));
        gui.show(sender);
    }
}
