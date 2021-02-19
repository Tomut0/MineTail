package ru.minat0.minetail;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import ru.minat0.minetail.integrations.AuthMeLoginEvent;
import ru.minat0.minetail.integrations.DiscordSRVListener;
import ru.minat0.minetail.managers.ConfigManager;
import ru.minat0.minetail.utils.ErrorsUtil;

import java.util.Set;

public class MineTail extends JavaPlugin {
    private static MineTail instance;

    private ConfigManager configManager;

    public static MineTail getInstance() {
        return instance;
    }
    private DiscordSRVListener discordSRVListener = new DiscordSRVListener(this);

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this, this.getDataFolder(), "config", true, true);

        DiscordSRV.api.subscribe(discordSRVListener);
        registerEvents();
    }

    @Override
    public void onDisable() {
        DiscordSRV.api.unsubscribe(discordSRVListener);
    }
    private void registerEvents() {
        if (getServer().getPluginManager().getPlugin("AuthMe") != null) {
            getServer().getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");
            getServer().getPluginManager().registerEvents(new AuthMeLoginEvent(), this);
        } else {
            Reflections reflections = new Reflections("ru.minat0.minetail.events");
            Set<Class<? extends Listener>> listeners = reflections.getSubTypesOf(Listener.class);
            for (Class<? extends Listener> c : listeners) {
                try {
                    getServer().getPluginManager().registerEvents(c.getDeclaredConstructor().newInstance(), this);
                } catch (Exception ex) {
                    ErrorsUtil.error("Error registering event: " + ex.getMessage());
                }
            }
        }
    }

    public ConfigManager getConfiguration() {
        return configManager;
    }
}
