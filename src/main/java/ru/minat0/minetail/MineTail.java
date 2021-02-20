package ru.minat0.minetail;

import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import ru.minat0.minetail.integrations.AuthMeLoginEvent;
import ru.minat0.minetail.integrations.DiscordSRVListener;
import ru.minat0.minetail.managers.CommandManager;
import ru.minat0.minetail.managers.ConfigManager;
import ru.minat0.minetail.managers.DatabaseManager;
import ru.minat0.minetail.managers.ServerManager;
import ru.minat0.minetail.utils.ErrorsUtil;

import java.util.Set;

public class MineTail extends JavaPlugin {
    private static MineTail instance;

    private ConfigManager configManager;
    private ServerManager serverManager;
    private DatabaseManager databaseManager;

    public static MineTail getInstance() {
        return instance;
    }
    private final DiscordSRVListener discordSRVListener = new DiscordSRVListener(this);

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this, this.getDataFolder(), "config", true, true);
        configManager.reloadConfig();

        serverManager = new ServerManager();
        if (!serverManager.isAuthServer())
            DiscordSRV.api.subscribe(discordSRVListener);

        this.databaseManager = new DatabaseManager();
        this.databaseManager.setup();
        this.databaseManager.loadDataToMemory();

        getServer().getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");
        registerEvents();

        getCommand("minetail").setExecutor(new CommandManager());
    }

    @Override
    public void onDisable() {
        if (!serverManager.isAuthServer())
            DiscordSRV.api.unsubscribe(discordSRVListener);
    }

    private void registerEvents() {
        if (serverManager.isAuthServer()) {
            getServer().getPluginManager().registerEvents(new AuthMeLoginEvent(), this);
        } else {
            Reflections reflections = new Reflections("ru.minat0.minetail.events");
            Set<Class<? extends Listener>> listeners = reflections.getSubTypesOf(Listener.class);
            ErrorsUtil.debug("Listener Reflections: " + listeners.toString(), true);

            for (Class<? extends Listener> c : listeners) {
                try {
                    getServer().getPluginManager().registerEvents(c.getDeclaredConstructor().newInstance(), this);
                } catch (Exception ex) {
                    ErrorsUtil.error("Error registering event: " + ex.getMessage());
                }
            }
        }
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @NotNull
    public ConfigManager getConfiguration() {
        return configManager;
    }
}
