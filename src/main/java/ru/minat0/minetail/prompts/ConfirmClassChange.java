package ru.minat0.minetail.prompts;

import org.bukkit.ChatColor;
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
        return ChatColor.translateAlternateColorCodes('&', "&4Внимание! &fВы действительно хотите сменить свой класс?" +
                " Ваш магический уровень и драгоценности будут сброшены! &2Да&f/&cНет");
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String input) {
        Player player = ((Player) conversationContext.getForWhom());

        if (input != null && input.equalsIgnoreCase("Да")) {
            Mage mage = MineTail.getDatabaseManager().getMage(player.getUniqueId());
            if (mage != null) {
                MineTail.getDatabaseManager().delete(mage);
                /*
                FIXME: "new-new" way, where message is Set<Mage>, so I overwrite an existing list in Auth server.
                   And, surely, don't forget to create the UPDATE&INSERT method and call it onDisable()
                 */
                MineTail.getServerManager().sendForwardMessage(player, "lobby", "DatabaseChannel", "Reload");
                MineTail.getServerManager().teleportToServer(player, "lobby");
                conversationContext.getForWhom().sendRawMessage("Вы подтвердили своё согласие на смену класса!");
            }
        }
        return null;
    }
}
