package ru.minat0.minetail.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.data.BaseCommand;

public class Reload extends BaseCommand {
    private static final String commandDescription = "Перезагрузить конфигруацию плагина";
    private static final String commandPermission = "minetail.reload";


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
    public void initialize(String[] args, Player sender) {
        MineTail.getInstance().getConfiguration().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "[MineTail] Конфигурация была успешно перезагружена!");
    }
}
