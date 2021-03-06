package ru.minat0.minetail.commands;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import ru.minat0.minetail.data.BaseCommand;
import ru.minat0.minetail.prompts.ConfirmClassChange;

public class ChangeClass extends BaseCommand {
    private static final String commandDescription = "Сменить свой класс.";
    private static final String commandPermission = "minetail.changeclass";

    @Override
    public String getCommandDescription() {
        return commandDescription;
    }

    @Override
    public String getCommandPermission() {
        return commandPermission;
    }

    @Override
    public String getCommandParameters() {
        return null;
    }

    @Override
    public void run(Player sender, String[] args) {
        ConversationFactory cf = new ConversationFactory(plugin).withTimeout(60);
        Conversation conversation = cf.withFirstPrompt(new ConfirmClassChange()).withLocalEcho(true).buildConversation(sender);
        conversation.begin();
    }
}
