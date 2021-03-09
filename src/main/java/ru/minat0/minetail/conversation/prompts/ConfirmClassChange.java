package ru.minat0.minetail.conversation.prompts;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.data.Mage;

public class ConfirmClassChange extends StringPrompt {
    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        Player player = (Player) conversationContext.getForWhom();

        final TextComponent textComponent = Component.text("Внимание! ", NamedTextColor.DARK_RED).
                append(Component.text("Вы действительно хотите сменить свой класс? \n", NamedTextColor.WHITE)).
                append(Component.text("Сбрасывая класс на 20 уровне, вы повышаете шанс выбить более лучший набор.\n", NamedTextColor.WHITE)).
                append(Component.text("Требования:\n", NamedTextColor.AQUA)).
                append(Component.text("- 10 уровень \n", NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(Component.text(isHaveMagicLevel(player))))).
                append(Component.text("- 10000 драгоценностей", NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(Component.text(isHaveMoney(player))))).
                append(Component.newline()).
                append(Component.text("Введите в чат: ", NamedTextColor.WHITE)).
                append(Component.text("Да", NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "Да"))).
                append(Component.text("/", NamedTextColor.GRAY).
                        append(Component.text("Нет", NamedTextColor.RED).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "Нет"))));

        return GsonComponentSerializer.gson().serialize(textComponent);
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String input) {
        Player player = ((Player) conversationContext.getForWhom());

        if (input != null && input.equalsIgnoreCase("Да")) {
            Mage mage = MineTail.getDatabaseManager().getMage(player.getUniqueId());
            if (mage != null) {
                MineTail.getDatabaseManager().delete(mage);
                MineTail.getServerManager().sendForwardMage(player, "lobby", "DatabaseChannel", "MageSetDelete", mage);
                MineTail.getServerManager().teleportToServer(player, "lobby");
                player.sendMessage("Вы подтвердили своё согласие на смену класса!");
            }
        }
        return null;
    }

    String isHaveMoney(@NotNull Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        return MineTail.getEcon().has(offlinePlayer, 10000) ? "✓" : "Вам не хватает " + (10000 - (int) MineTail.getEcon().getBalance(offlinePlayer)) + " драгоценностей!";
    }

    String isHaveMagicLevel(@NotNull Player player) {
        Mage mage = MineTail.getDatabaseManager().getMage(player.getUniqueId());
        if (mage != null) {
            return mage.getMagicLevel().equals(10) ? "✓" : "Вам не хватает " + (10 - mage.getMagicLevel()) + " уровней!";
        }
        return null;
    }
}
