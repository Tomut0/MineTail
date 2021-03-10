package ru.minat0.minetail.commands;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.conversation.TailConversation;
import ru.minat0.minetail.conversation.prompts.ConfirmClassChange;
import ru.minat0.minetail.data.BaseCommand;
import ru.minat0.minetail.data.Mage;

public class Change extends BaseCommand {
    private static final String commandDescription = "Сменить класс, цвет маны и многое другое.";
    private static final String commandPermission = "minetail.change";
    private static final String commandParameters = "§7[§fclass/manabar (§cцвет§f)§7]";

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
        Mage mage = MineTail.getDatabaseManager().getMage(sender.getUniqueId());
        if (mage == null) return;

        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("class")) {
                TailConversation conversation = new TailConversation(plugin, sender, new ConfirmClassChange(sender.getUniqueId()));
                conversation.begin();
            } else if (args[1].equalsIgnoreCase("manabar")) {
                if (args.length == 3) {
                    for (BarColor value : BarColor.values()) {
                        if (args[2].equalsIgnoreCase(value.name())) {
                            mage.setManaBarColor(value.name());
                            plugin.getManaBars().get(sender.getUniqueId()).setColor(BarColor.valueOf(mage.getManaBarColor()));
                            sender.sendMessage(ChatColor.GREEN + "[MineTail] Вы успешно сменили себе цвет маны!");
                        }
                    }
                } else sender.sendMessage("§cПравильное использование: §7/minetail change manabar (§bцвет§7)");
            }
        }
    }
}
