package ru.minat0.minetail.conversation.prompts;

import com.Zrips.CMI.Modules.Economy.Economy;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.data.Mage;

import java.util.UUID;

public class ConfirmClassChange extends StringPrompt {
    private final Player player;
    private final OfflinePlayer offlinePlayer;
    private final Mage mage;

    public ConfirmClassChange(UUID uuid) {
        this.player = Bukkit.getPlayer(uuid);
        this.offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        this.mage = MineTail.getDatabaseManager().getMage(uuid);

    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        final TextComponent textComponent = Component.text().
                append(MineTail.getConfiguration().getConfig().getStringList("Conversations.ChangeClass").stream().
                        map(message -> MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(player, message), Template.of("isHaveMoney", isHaveMoney()), Template.of("isHaveMagicLevel", isHaveMagicLevel()))).toArray(Component[]::new)).build();


        return GsonComponentSerializer.gson().serialize(textComponent);
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String input) {
        if (input != null && input.equalsIgnoreCase("Да")) {
            if (mage != null) {
                if (Economy.has(offlinePlayer, 10000) && mage.getMagicLevel() >= 10) {
                    MineTail.getDatabaseManager().delete(mage);
                    MineTail.getServerManager().sendForwardMage(player, "lobby", "DatabaseChannel", "MageSetDelete", mage);
                    player.sendMessage(ChatColor.GREEN + "[MineTail] Вы подтвердили своё согласие на смену класса!");
                    MineTail.getServerManager().teleportToServer(player, "lobby");
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "[MineTail] Вы не удовлетворяете требованиям!");
                }
            }
        }
        return null;
    }

    String isHaveMoney() {
        return Economy.has(offlinePlayer, 10000) ? "<green>✓</green>" : "<red>Вам не хватает " + (10000 - (int) Economy.getBalance(offlinePlayer)) + " драгоценностей!</red>";
    }

    String isHaveMagicLevel() {
        return mage != null ? mage.getMagicLevel() >= 10 ? "<green>✓</green>" : "<red>Вам не хватает " + (10 - mage.getMagicLevel()) + " уровней! </red>" : null;
    }
}
