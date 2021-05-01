package ru.minat0.minetail.main.prompts;

import com.Zrips.CMI.Modules.Economy.Economy;
import com.nisovin.magicspells.MagicSpells;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.MineTail;

import java.util.UUID;

public class ConfirmClassChange extends StringPrompt {
    private final Player player;
    private final OfflinePlayer offlinePlayer;
    private final Mage mage;

    private final int PRICE = 500000;

    public ConfirmClassChange(UUID uuid) {
        this.player = Bukkit.getPlayer(uuid);
        this.offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        this.mage = MineTail.getMageDao().get(uuid).orElseThrow();
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        final TextComponent textComponent = Component.text().append(MineTail.getConfiguration().getConfig().
                getStringList("Conversations.ChangeClass").stream().map(message -> MiniMessage.get().parse
                (PlaceholderAPI.setPlaceholders(player, message), Template.of("isHaveMoney", isHaveMoney()), Template.of("isHaveMagicLevel", isHaveMagicLevel())))
                .toArray(Component[]::new)).build();

        return GsonComponentSerializer.gson().serialize(textComponent);
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String input) {
        if (input != null && input.equalsIgnoreCase("Да")) {
            if (mage != null) {
                if (Economy.has(offlinePlayer, PRICE) && mage.getMagicLVL() >= 10) {
                    MineTail.getMageDao().delete(mage.getUniqueId());
                    Economy.withdrawPlayer(offlinePlayer, PRICE);
                    mage.setMagicLVL(1);
                    mage.setSpells(null);
                    MagicSpells.getSpellbook(player).removeAllSpells();
                    MagicSpells.getSpellbook(player).save();
                    MagicSpells.getManaHandler().setMaxMana(player, 100);
                    MineTail.getServerManager().sendForwardMage(player, "lobby", "DatabaseChannel", "MageSetDelete", mage);
                    MineTail.getMageDao().getAll().remove(mage);
                    MineTail.getServerManager().teleportToServer(player, "lobby");
                }
            }
        }
        return null;
    }

    String isHaveMoney() {
        return Economy.has(offlinePlayer, PRICE) ? "<green>✓</green>" : "<red>Вам не хватает " + (PRICE - (int) Economy.getBalance(offlinePlayer)) + " драгоценностей!</red>";
    }

    String isHaveMagicLevel() {
        return mage != null ? mage.getMagicLVL() >= 10 ? "<green>✓</green>" : "<red>Вам не хватает " + (10 - mage.getMagicLVL()) + " уровней! </red>" : null;
    }
}
