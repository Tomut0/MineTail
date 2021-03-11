package ru.minat0.minetail.data;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.conversation.TailConversation;
import ru.minat0.minetail.conversation.prompts.ConfirmClassChange;
import ru.minat0.minetail.data.inventories.RegisterInventory;

@SuppressWarnings("ALL")
@CommandAlias("mt|minetail")
public class MineTailCommand extends BaseCommand {
    private final MineTail plugin;

    public MineTailCommand(Plugin instance) {
        this.plugin = ((MineTail) instance);
    }

    @Subcommand("reload|rl")
    @CommandPermission("minetail.admin.config")
    public class Reload extends BaseCommand {

        @Description("Перезагрузить конфигурацию плагина.")
        @Subcommand("config|cfg")
        public void onConfig(CommandSender sender) {
            MineTail.getConfiguration().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "[MineTail] Конфигурация была успешно перезагружена!");
        }

        @Description("Перезагрузить базу данных плагина.")
        @Subcommand("database|db")
        public void onDatabase(CommandSender sender) {
            MineTail.getDatabaseManager().getMages().clear();
            MineTail.getDatabaseManager().loadDataToMemory();
            sender.sendMessage(ChatColor.GREEN + "[MineTail] База данных была успешно перезагружена!");
        }
    }


    @Subcommand("change|c")
    public class Change extends BaseCommand {

        @Description("Сбросить ваш магический класс.")
        @Subcommand("class")
        public void onPlayerClassChange(Player sender) {
            TailConversation conversation = new TailConversation(plugin, sender, new ConfirmClassChange(sender.getUniqueId()));
            conversation.begin();
        }

        @Subcommand("manabar")
        @CommandCompletion("white|red|blue|pink|yellow|purple|green")
        @Syntax("<color> - Изменить цвет отображаемой маны.")
        public void onManaBar(Player sender, String[] args) {
            Mage mage = MineTail.getDatabaseManager().getMage(sender.getUniqueId());
            if (mage == null) return;

            for (BarColor value : BarColor.values()) {
                if (args[2].equalsIgnoreCase(value.name())) {
                    mage.setManaBarColor(value.name());
                    plugin.getManaBars().get(sender.getUniqueId()).setColor(BarColor.valueOf(mage.getManaBarColor()));
                    sender.sendMessage(ChatColor.GREEN + "[MineTail] Вы успешно сменили себе цвет маны!");
                }
            }
        }
    }

    @Private
    @Subcommand("register|reg")
    public class Register extends BaseCommand {

        @Default
        @CommandPermission("minetail.player.register")
        public void onRegister(Player player) {
            RegisterInventory regInv = new RegisterInventory(player);
            regInv.getGUI().show(player);
        }
    }

    @HelpCommand
    @Syntax("<страница/команда> - Получить справку по плагину.")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
