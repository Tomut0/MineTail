package ru.minat0.minetail.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.data.BaseCommand;

public class Reload extends BaseCommand {
    private static final String commandDescription = "Перезагрузить конфигруацию плагина";
    private static final String commandPermission = "minetail.reload";
    private static final String commandParameters = "§7[§fconfig/database§7]";

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
        return commandParameters;
    }

    @Override
    public void run(Player sender, String[] args) {
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("config")) {
                MineTail.getConfiguration().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "[MineTail] Конфигурация была успешно перезагружена!");
            } else if (args[1].equalsIgnoreCase("database")) {
                MineTail.getDatabaseManager().getMages().clear();
                MineTail.getDatabaseManager().loadDataToMemory();
                sender.sendMessage(ChatColor.GREEN + "[MineTail] База данных была успешно перезагружена!");
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "[MineTail] Аргумент не найден. Используйте: " + commandParameters);
            }
        }
    }
}
