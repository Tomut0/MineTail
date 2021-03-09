package ru.minat0.minetail.commands;

import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import ru.minat0.minetail.conversation.TailConversation;
import ru.minat0.minetail.conversation.prompts.ConfirmClassChange;
import ru.minat0.minetail.data.BaseCommand;

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
        TailConversation conversation = new TailConversation(plugin, ((Conversable) sender), new ConfirmClassChange());
        conversation.begin();
    }
}
