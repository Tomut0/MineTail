package ru.minat0.minetail.managers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import ru.minat0.minetail.data.BaseCommand;
import ru.minat0.minetail.utils.ErrorsUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    Reflections reflections = new Reflections("ru.minat0.minetail.commands");
    Set<Class<? extends BaseCommand>> commands = reflections.getSubTypesOf(BaseCommand.class).stream().sorted(Comparator.comparing(Class::getSimpleName)).collect(Collectors.toCollection(LinkedHashSet::new));

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            ErrorsUtil.debug("Commands Reflections: " + commands.toString(), true);

            if (command.getName().equalsIgnoreCase("minetail")) {
                if (args.length > 0) {
                    for (Class<?> cmd : commands) {
                        if (args[0].equalsIgnoreCase(cmd.getSimpleName())) {
                            try {
                                BaseCommand bcommand = (BaseCommand) cmd.getDeclaredConstructor().newInstance();

                                if (p.hasPermission(bcommand.getCommandPermission()))
                                    bcommand.initialize(args, p);
                                else
                                    p.sendMessage(ChatColor.DARK_RED + "У вас недостаточно прав, чтобы использовать эту команду!");
                            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                                ErrorsUtil.error("Error with basic commands: " + ex.getMessage());
                            }

                            return true;
                        } else {
                            Pattern pattern = Pattern.compile(args[0], Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(cmd.getSimpleName());

                            if (matcher.find()) {
                                p.sendMessage("§cОшибка: §7может, вы имели ввиду: §a/minetail " + cmd.getSimpleName().toLowerCase());
                            }
                        }
                    }

                    p.sendMessage("§cОшибка: такой команды не существует!");

                    return false;
                } else {
                    if (p.hasPermission("minetail.help")) {
                        p.sendMessage(ChatColor.DARK_GREEN + "<< MineTail Help >>");

                        for (Class<?> cmd : commands) {
                            try {
                                BaseCommand bcommand = (BaseCommand) cmd.getDeclaredConstructor().newInstance();

                                String description = bcommand.getCommandDescription();
                                String parameters = bcommand.getCommandParameters();

                                if (parameters != null)
                                    p.sendMessage("§b/minetail " + cmd.getSimpleName().toLowerCase() + " " + parameters + "§7 - " + description);
                                else
                                    p.sendMessage("§b/minetail " + cmd.getSimpleName().toLowerCase() + "§7 - " + description);
                            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
                                ErrorsUtil.error("Error with minetail help: " + ex.getMessage());
                            }
                        }

                        return true;
                    } else
                        p.sendMessage(ChatColor.DARK_RED + "У вас недостаточно прав, чтобы использовать эту команду!");
                }
            }
        } else
            ErrorsUtil.warning("Вы должны быть игроком, чтобы использовать эту команду!");

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String aliases, String[] args) {
        if (command.getName().equalsIgnoreCase("minetail")) {
            if (args.length == 1)
                return commands.stream().map(cmd -> cmd.getSimpleName().toLowerCase()).collect(Collectors.toList());
        }
        return null;
    }
}
