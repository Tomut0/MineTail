package ru.minat0.minetail.core;

import co.aikar.commands.PaperCommandManager;
import com.Zrips.CMI.CMI;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaHandler;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import ru.minat0.minetail.auth.AuthMeLoginEvent;
import ru.minat0.minetail.core.managers.ConfigManager;
import ru.minat0.minetail.core.managers.DatabaseManager;
import ru.minat0.minetail.core.managers.ServerManager;
import ru.minat0.minetail.core.utils.Logger;
import ru.minat0.minetail.main.RandomKit;
import ru.minat0.minetail.main.events.MagicSpellsCast;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class MineTail extends JavaPlugin {
    private static MineTail instance;

    private static ConfigManager configManager;
    private static ServerManager serverManager;
    private static DatabaseManager databaseManager;
    private static PaperCommandManager commandManager;

    public static MineTail getInstance() {
        return instance;
    }

    private final HashMap<UUID, BossBar> manaBars = new HashMap<>();
    private ManaHandler manaHandler;

    @Override
    public void onLoad() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null)
            Flags.registerAll();
    }

    @Override
    public void onEnable() {
        instance = this;

        getServer().getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(getInstance(), "BungeeCord", new PluginMessage());

        registerManagers();
        registerEvents();
        commandManager.registerCommand(new MineTailCommand(instance), true);

        if (!serverManager.isAuthServer()) {
            manaHandler = MagicSpells.getManaHandler();
            setupEconomy();
        }
    }

    @Override
    public void onDisable() {
        MineTail.getDatabaseManager().update(MineTail.getDatabaseManager().getMages());
    }

    private void registerManagers() {
        configManager = new ConfigManager(this, this.getDataFolder(), "config", true, true);
        configManager.reloadConfig();

        commandManager = new PaperCommandManager(instance);
        commandManager.enableUnstableAPI("help");

        serverManager = new ServerManager();

        databaseManager = new DatabaseManager();
        databaseManager.setup();
        databaseManager.loadDataToMemory();

        RandomKit.loadKits();
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("CMI") == null && CMI.getInstance().getEconomyManager().isEnabled()) {
            getServer().getPluginManager().disablePlugin(instance);
            Logger.error("Error when trying to load CMI Economy. Disable plugin.");
        }
    }

    private void registerEvents() {
        if (serverManager.isAuthServer()) {
            getServer().getPluginManager().registerEvents(new AuthMeLoginEvent(), this);
        } else {
            Reflections reflections = new Reflections("ru.minat0.minetail.main.events");
            Set<Class<? extends Listener>> listeners = reflections.getSubTypesOf(Listener.class);
            Logger.debug("Registered events: " + listeners.toString(), true);

            for (Class<? extends Listener> c : listeners) {
                try {
                    getServer().getPluginManager().registerEvents(c.getDeclaredConstructor().newInstance(), this);
                } catch (Exception ex) {
                    Logger.error("Error registering event: " + ex.getMessage());
                }
            }

            getServer().getPluginManager().registerEvents(new MagicSpellsCast(), this);
        }
    }

    public static ServerManager getServerManager() {
        return serverManager;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static ConfigManager getConfiguration() {
        return configManager;
    }

    public HashMap<UUID, BossBar> getManaBars() {
        return manaBars;
    }

    public ManaHandler getManaHandler() {
        return manaHandler;
    }
}
