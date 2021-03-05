package ru.minat0.minetail.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import ru.minat0.minetail.MineTail;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class PluginMessage implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);

        if (subchannel.equals("DatabaseChannel")) {
            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            try {
                String argument = msgin.readUTF();
                if (argument.equals("Reload")) {
                    MineTail.getDatabaseManager().getMages().clear();
                    MineTail.getDatabaseManager().loadDataToMemory();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
