package ru.minat0.minetail.integrations;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.UUID;

public class DiscordSRVListener implements Listener {
    private final Plugin plugin;

    public DiscordSRVListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void discordMessageProcessed(DiscordGuildMessagePostProcessEvent event) {
        AccountLinkManager am = DiscordSRV.getPlugin().getAccountLinkManager();
        UUID id = am.getUuid(event.getAuthor().getId());

        if (id != null) {
            Player p = Objects.requireNonNull(Bukkit.getPlayer(id));
            event.setProcessedMessage(removeDiscordEmojis(event.getProcessedMessage()).replace(event.getAuthor().getName(), p.getName()));
        }
    }

    String removeDiscordEmojis(String string) {
        return string.replaceFirst(":[a-zA-Z]+:", "");
    }
}
