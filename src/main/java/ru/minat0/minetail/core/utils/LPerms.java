package ru.minat0.minetail.core.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LPerms {
    public static net.luckperms.api.LuckPerms getLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            return provider.getProvider();
        }
        return null;
    }

    public static void addPermission(UUID userUuid, String permission, boolean value) {
        if (getLuckPerms() != null)
            getLuckPerms().getUserManager().modifyUser(userUuid, user -> {
                user.data().add(Node.builder(permission).value(value).build());
            });
    }

    public static CompletableFuture<Boolean> isAdmin(UUID who) {
        return getLuckPerms().getUserManager().loadUser(who)
                .thenApplyAsync(user -> {
                    Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
                    return inheritedGroups.stream().anyMatch(g -> g.getName().equals("admin"));
                });
    }
}
