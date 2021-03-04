package ru.minat0.minetail.data.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.minat0.minetail.commands.Profile;

public class ProfileInventory extends Inventory {
    private final Player sender;

    @Override
    public void addGUIElements() {
        String path = "GUI.profile.items.";
        // TODO: this
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String getInventoryTitle() {
        return PlaceholderAPI.setPlaceholders(sender, config.getString("GUI.profile.title", "null"));
    }

    @Override
    public InventoryHolder getInventoryOwner() {
        return null;
    }

    @Override
    public String[] getInventoryFormat() {
        return config.getStringList("GUI.profile.format").toArray(new String[0]);
    }

    public ProfileInventory(Profile profile) {
        this.sender = (Player) profile.getCommandSender();
        getGUI().setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        addGUIElements();
    }
}
