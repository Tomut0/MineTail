package ru.minat0.minetail.data.inventories;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.InventoryHolder;
import ru.minat0.minetail.MineTail;

public abstract class Inventory {
    public final MineTail plugin = MineTail.getInstance();
    public final FileConfiguration config = plugin.getConfig();

    private final InventoryGui gui = new InventoryGui(plugin, getInventoryOwner(), getInventoryTitle(), getInventoryFormat());

    public abstract void addGUIElements();

    public abstract String getInventoryTitle();

    public abstract InventoryHolder getInventoryOwner();

    public abstract String[] getInventoryFormat();

    public InventoryGui getGUI() {
        return this.gui;
    }
}

