package ru.minat0.minetail.commands;

import org.bukkit.entity.Player;
import ru.minat0.minetail.data.BaseCommand;

public class Profile extends BaseCommand {
    // TODO: Open profile of another player by admin permission
    private static final String commandDescription = "Открыть ваш профиль";
    private static final String commandPermission = "minetail.profile";

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

    }
}
