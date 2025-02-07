package ru.minat0.minetail.core;

import co.aikar.commands.PaperCommandManager;
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
import ru.minat0.minetail.core.worldguard.Flags;
import ru.minat0.minetail.main.RandomKit;

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
        registerDependencies();
        commandManager.registerCommand(new MineTailCommand(instance), true);
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

        databaseManager = new DatabaseManager(this, configManager.getConfig(), DatabaseManager.DBType.valueOf(configManager.getConfig().getString("DataSource.backend")));
        databaseManager.loadDataToMemory();

        RandomKit.loadKits();
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
        }
    }

    private void registerDependencies() {
        commandManager.registerDependency(DatabaseManager.class, databaseManager);
        commandManager.registerDependency(ServerManager.class, serverManager);
        commandManager.registerDependency(ConfigManager.class, configManager);
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
}
