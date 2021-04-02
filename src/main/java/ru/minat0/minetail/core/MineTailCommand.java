package ru.minat0.minetail.core;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.minat0.minetail.core.conversation.TailConversation;
import ru.minat0.minetail.core.inventories.RegisterInventory;
import ru.minat0.minetail.main.RandomKit;
import ru.minat0.minetail.main.prompts.ConfirmClassChange;

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

            RandomKit.randomKits.clear();
            RandomKit.loadKits();

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

        @Description("Сбросить магический класс игрока.")
        @CommandCompletion("@players holding_magic|caster_magic")
        @CommandPermission("minetail.admin.change.class")
        @Subcommand("class reset|c reset")
        public void onAdminClassChange(CommandSender sender, @Name("игрок") OnlinePlayer target) {
            Mage mage = MineTail.getDatabaseManager().getMage(target.getPlayer().getUniqueId());
            if (mage == null) return;

            // Delete from DB
            MineTail.getDatabaseManager().delete(mage);

            // Svae on server restart
            mage.changed = true;

            // Delete Mage from lobby Mages()
            MineTail.getServerManager().sendForwardMage(target.getPlayer(), "lobby", "DatabaseChannel", "MageSetDelete", mage);
            sender.sendMessage(ChatColor.GREEN + "[MineTail] Вы сбросили магический класс игрока " + mage.getName() + "!");

            // Delete from Set
            MineTail.getDatabaseManager().getMages().remove(mage);
            MineTail.getServerManager().teleportToServer(target.getPlayer(), "lobby");
        }

        @Subcommand("manabar|mb color")
        @CommandCompletion("white|red|blue|pink|yellow|purple|green")
        @Description("Изменить цвет отображаемой маны.")
        public void onManaBar(Player sender, @Name("цвет") String color) {
            Mage mage = MineTail.getDatabaseManager().getMage(sender.getUniqueId());
            if (mage == null) return;

            for (BarColor barColor : BarColor.values()) {
                if (barColor.name().equalsIgnoreCase(color)) {
                    mage.setManaBarColor(barColor.name());
                    mage.changed = true;
                    plugin.getManaBars().get(sender.getUniqueId()).setColor(BarColor.valueOf(mage.getManaBarColor()));
                    sender.sendMessage(ChatColor.GREEN + "[MineTail] Вы успешно сменили себе цвет маны!");
                }
            }
        }

        @Description("Изменить цвет отображаемой маны игрока.")
        @CommandCompletion("@players red|blue|pink|yellow|purple|green")
        @Subcommand("manabar|mb color set")
        @CommandPermission("minetail.admin.change.manabar")
        public void onAdminManaBarChange(CommandSender sender, @Name("игрок") OfflinePlayer target, @Name("цвет") String color) {
            Mage mage = MineTail.getDatabaseManager().getMage(target.getUniqueId());
            if (mage == null) return;

            for (BarColor barColor : BarColor.values()) {
                if (barColor.name().equalsIgnoreCase(color)) {
                    mage.setManaBarColor(barColor.name());
                    mage.changed = true;
                    plugin.getManaBars().get(target.getUniqueId()).setColor(BarColor.valueOf(mage.getManaBarColor()));
                    sender.sendMessage(ChatColor.GREEN + "[MineTail] Вы успешно изменили цвет маны игрока " + mage.getName() + "!");
                }
            }
        }

        @Description("Изменить магический уровень игрока.")
        @CommandCompletion("@players @range:0-20")
        @Subcommand("level|lvl set")
        @CommandPermission("minetail.admin.level")
        public void onAdminLevelChange(CommandSender sender, @Name("игрок") OfflinePlayer target, @Name("уровень") Integer level) {
            Mage mage = MineTail.getDatabaseManager().getMage(target.getUniqueId());
            if (mage == null) return;

            mage.setMagicLevel(level);
            mage.changed = true;
            sender.sendMessage(ChatColor.GREEN + "[MineTail] Вы успешно изменили магический уровень игрока " + target.getName() + "!");
        }

        @Description("Изменить время отображения маны.")
        @CommandCompletion("forever|short|medium|long")
        @Subcommand("manabar|mb appeartime|at set")
        public void onPlayerManaBarAppearTime(Player sender, @Name("время") String time) {
            Mage mage = MineTail.getDatabaseManager().getMage(sender.getUniqueId());
            if (mage == null) return;

            for (ManaBar appearTime : ManaBar.values()) {
                if (appearTime.name().equalsIgnoreCase(time)) {
                    mage.setManaBarAppearTime(appearTime.name());
                    mage.changed = true;
                    sender.sendMessage(ChatColor.GREEN + "[MineTail] Вы изменили время отображения маны!");
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
    @Description("Получить справку по плагину.")
    public static void onHelp(CommandSender sender, @Name("страница/команда") CommandHelp help) {
        help.showHelp();
    }
}
