package ru.minat0.minetail.data;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.minat0.minetail.MineTail;

public abstract class BaseCommand {
    public final MineTail plugin = MineTail.getInstance();
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
