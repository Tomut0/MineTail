package ru.minat0.minetail.core;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import ru.minat0.minetail.core.utils.Logger;

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
                switch (argument) {
                    case "Reload":
                        MineTail.getDatabaseManager().getMages().clear();
                        MineTail.getDatabaseManager().loadDataToMemory();
                        break;
                    case "MageSetInsert":
                        Mage deserializedMage = Mage.deserialize(msgin.readAllBytes());
                        Mage mage = MineTail.getDatabaseManager().getMage(player.getUniqueId());

                        if (deserializedMage != null) {
                            if (mage != null) {
                                boolean remove = MineTail.getDatabaseManager().getMages().remove(mage);
                                Logger.debug("Remove set: " + remove, false);
                            }
                            boolean add = MineTail.getDatabaseManager().getMages().add(deserializedMage);
                            Logger.debug("Add set: " + add, false);
                        } else Logger.debug("There is no deserialized mage: " + player.getName(), false);
                        break;
                    case "MageSetDelete":
                        if (MineTail.getDatabaseManager().getMage(player.getUniqueId()) != null) {
                            MineTail.getDatabaseManager().getMages().remove(Mage.deserialize(msgin.readAllBytes()));
                        }
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}