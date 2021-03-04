package ru.minat0.minetail.data;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand {
    private CommandSender commandSender;

    public abstract String getCommandDescription();
    public abstract String getCommandPermission();
    public abstract String getCommandParameters();

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public void setCommandSender(CommandSender sender) {
        this.commandSender = sender;
    }

    public abstract void run(Player sender, String[] args);
}
