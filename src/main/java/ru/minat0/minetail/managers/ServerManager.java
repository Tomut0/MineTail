package ru.minat0.minetail.managers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.utils.ErrorsUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@SuppressWarnings("UnstableApiUsage")
public class ServerManager {

    /**
     * Used on Spigot/Bukkit
     *
     * @param host of server
     * @param port of server
     * @return True if server is online
     */
    public boolean isOnline(String host, int port) {
        try {
            Socket s = new Socket(host, port);
            s.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Used on BungeeCord
     *
     * @param serverName - server to player teleport
     * @return True if server online
     */
    public boolean isOnlineBungee(String serverName) {
        ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);

        final boolean[] isOnline = new boolean[1];
        server.ping((result, error) ->
        {
            if (error != null) isOnline[0] = true;
            isOnline[0] = false;
        });

        return isOnline[0];
    }

    /**
     * Teleports player to a server trough BungeeCord
     *
     * @param player
     * @param serverName
     */
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

    public void sendPluginMessage(Player player, String subchannel, boolean argument) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subchannel);
        out.writeBoolean(argument);
        player.sendPluginMessage(MineTail.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void sendPluginMessage(Player player, String subchannel, String argument) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subchannel);
        out.writeUTF(argument);
        player.sendPluginMessage(MineTail.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void sendForwardMessage(Player player, String serverName, String channel, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(serverName);
        out.writeUTF(channel);

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);

        try {
            msgout.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        player.sendPluginMessage(MineTail.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void getPlayerCount(String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("PlayerCount");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAuthServer() {
        return Bukkit.getServer().getPluginManager().getPlugin("AuthMe") != null;
    }
}
