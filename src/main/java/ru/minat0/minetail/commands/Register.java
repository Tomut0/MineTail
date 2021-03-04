package ru.minat0.minetail.commands;

import org.bukkit.entity.Player;
import ru.minat0.minetail.data.BaseCommand;
import ru.minat0.minetail.data.inventories.RegisterInventory;
import ru.minat0.minetail.utils.LuckPermsUtils;

public class Register extends BaseCommand {
    private static final String commandDescription = "Зарегистрироваться на сервере. Работает только один раз при заходе на сервер.";
    private static final String commandPermission = "minetail.register";

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
        RegisterInventory regInv = new RegisterInventory(this);
        regInv.gui.show(sender);

        LuckPermsUtils.addPermission(sender.getUniqueId(), commandPermission, false);
    }
}
