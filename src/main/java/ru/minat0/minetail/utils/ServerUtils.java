package ru.minat0.minetail.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.bukkit.entity.Player;
import ru.minat0.minetail.MineTail;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerUtils {
    public boolean isOnline(String serverName) {
        ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);

        final boolean[] isOnline = new boolean[1];
        server.ping((result, error) ->
        {
            if (error != null) isOnline[0] = true;
            isOnline[0] = false;
        });

        return isOnline[0];
    }

    public void teleportToServer(Player player, String serverName) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
            player.sendPluginMessage(MineTail.getInstance(), "BungeeCord", b.toByteArray());
        } catch (IOException ex) {
            ErrorsUtil.error("Не могу перенаправить игрока на сервер: " + ex.getMessage());
        }
    }
}
