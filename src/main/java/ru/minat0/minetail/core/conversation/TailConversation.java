package ru.minat0.minetail.core.conversation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TailConversation extends Conversation {
    public TailConversation(@Nullable Plugin plugin, @NotNull Conversable forWhom, @Nullable Prompt firstPrompt) {
        super(plugin, forWhom, firstPrompt);
    }

    @Override
    public void outputNextPrompt() {
        if (this.currentPrompt == null) {
            abandon(new ConversationAbandonedEvent(this));
        } else {
            CommandSender sender = ((CommandSender) this.context.getForWhom());
            String output = this.currentPrompt.getPromptText(this.context);
            Component component = GsonComponentSerializer.gson().deserialize(output);
            sender.sendMessage(component);

            if (!this.currentPrompt.blocksForInput(this.context)) {
                this.currentPrompt = this.currentPrompt.acceptInput(this.context, null);
                outputNextPrompt();
            }
        }
    }
}
