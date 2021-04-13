package ru.minat0.minetail.core.inventories;

import de.themoep.inventorygui.InventoryGui;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.minat0.minetail.core.MineTail;

import java.util.Objects;

public abstract class Inventory {
    public final MineTail plugin = MineTail.getInstance();
    public final FileConfiguration config = MineTail.getConfiguration().getConfig();

    private final InventoryGui gui;
    protected final Player sender;

    public Inventory(@NotNull Player sender, @NotNull String path, boolean hasFilter) {
        this.sender = sender;
        gui = new InventoryGui(plugin, getOwner(), getTitle(path), getInventoryFormat(path));
        addGUIElements();
        if (hasFilter) {
            setFilter();
        }
    }

    protected abstract void addGUIElements();

    @NotNull
    public String getTitle(String path) {
        String title = config.getString(path + ".title");
        return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(sender,
                Objects.requireNonNull(title, this.getClass().getSimpleName() + " | Couldn't found title!")));
    }

    @Nullable
    public InventoryHolder getOwner() {
        return null;
    }

    @NotNull
    public String[] getInventoryFormat(String path) {
        return config.getStringList(path + ".format").toArray(new String[0]);
    }

    private void setFilter() {
        gui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
    }

    @NotNull
    public InventoryGui getGUI() {
        return gui;
    }
}

