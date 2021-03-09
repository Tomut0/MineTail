package ru.minat0.minetail;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaHandler;
import github.scarsz.discordsrv.DiscordSRV;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.boss.BossBar;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import ru.minat0.minetail.integrations.AuthMeLoginEvent;
import ru.minat0.minetail.integrations.DiscordSRVListener;
import ru.minat0.minetail.integrations.MagicSpellsCastEvent;
import ru.minat0.minetail.listeners.PluginMessage;
import ru.minat0.minetail.managers.CommandManager;
import ru.minat0.minetail.managers.ConfigManager;
import ru.minat0.minetail.managers.DatabaseManager;
import ru.minat0.minetail.managers.ServerManager;
import ru.minat0.minetail.utils.ErrorsUtil;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class MineTail extends JavaPlugin {
    private static MineTail instance;

    private static ConfigManager configManager;
    private static ServerManager serverManager;
    private static DatabaseManager databaseManager;

    public static MineTail getInstance() {
        return instance;
    }

    private final DiscordSRVListener discordSRVListener = new DiscordSRVListener(this);

    private final HashMap<UUID, BossBar> manaBars = new HashMap<>();
    private ManaHandler manaHandler;

    private static Economy econ = null;

    public static Economy getEcon() {
        return econ;
    }

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this, this.getDataFolder(), "config", true, true);
        configManager.reloadConfig();

        serverManager = new ServerManager();
        if (!serverManager.isAuthServer()) {
            DiscordSRV.api.subscribe(discordSRVListener);
            manaHandler = MagicSpells.getManaHandler();

            if (!setupEconomy()) {
                ErrorsUtil.error(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        databaseManager = new DatabaseManager();
        databaseManager.setup();
        databaseManager.loadDataToMemory();

        getServer().getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(getInstance(), "BungeeCord", new PluginMessage());
        registerEvents();

        getCommand("minetail").setExecutor(new CommandManager());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
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

            getServer().getPluginManager().registerEvents(new MagicSpellsCastEvent(), this);
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
