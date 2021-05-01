package ru.minat0.minetail.core;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import ru.minat0.minetail.auth.AuthMeLoginEvent;
import ru.minat0.minetail.auth.RandomKit;
import ru.minat0.minetail.core.managers.ConfigManager;
import ru.minat0.minetail.core.managers.DatabaseManager;
import ru.minat0.minetail.core.managers.ServerManager;
import ru.minat0.minetail.core.storage.MageDao;
import ru.minat0.minetail.core.utils.Logger;
import ru.minat0.minetail.core.worldguard.Flags;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MineTail extends JavaPlugin {
    private static MineTail instance;
    private static MageDao mageDao;

    private static ConfigManager configManager;
    private static ServerManager serverManager;
    private static DatabaseManager databaseManager;
    private static PaperCommandManager commandManager;
    public static final Map<Integer, Integer> levelMap = new HashMap<>();

    public static MineTail getInstance() {
        return instance;
    }

    public static MageDao getMageDao() {
        return mageDao;
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

        mageDao = new MageDao(databaseManager.getDataSource());
        mageDao.loadMages();

        RandomKit.loadKits();
        levelSetup();
    }

    @Override
    public void onDisable() {
        if (!serverManager.isAuthServer())
            mageDao.updateAll();
    }

    private void registerManagers() {
        configManager = new ConfigManager(this, this.getDataFolder(), "config", true, true);
        configManager.reloadConfig();

        commandManager = new PaperCommandManager(instance);
        commandManager.enableUnstableAPI("help");

        serverManager = new ServerManager();
        databaseManager = new DatabaseManager(this, configManager.getConfig(), DatabaseManager.DBType.valueOf(configManager.getConfig().getString("DataSource.backend")));
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

    public void levelSetup() {
        for (int lvl = 1; lvl <= getConfiguration().getConfig().getInt("maxLevel"); lvl++) {
            int exp = (int) (10 * Math.pow(lvl + 10, 2) + 1000);
            levelMap.put(lvl, exp);
            Logger.debug(String.valueOf(levelMap.get(lvl)), true);
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
