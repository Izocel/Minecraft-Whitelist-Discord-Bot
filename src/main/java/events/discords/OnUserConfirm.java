package events.discords;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import bukkit.BukkitManager;
import commands.bukkit.ConfirmLinkCmd;
import main.WhitelistDmc;
import models.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import services.sentry.SentryService;

public class OnUserConfirm extends ListenerAdapter {
    private Logger logger;
    private WhitelistDmc plugin;
    private String acceptId = ConfirmLinkCmd.acceptId;
    private String rejectId = ConfirmLinkCmd.rejectId;

    public OnUserConfirm(WhitelistDmc plugin) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        try {
            if (!event.getChannel().getType().toString().equals("PRIVATE")) {
                return;
            }

            final Member member = plugin.getDiscordManager().getGuild().getMember(event.getUser());
            final List<Field> fields = event.getMessage().getEmbeds().get(0).getFields();
            final String uuid = fields.get(1).getValue();

            final boolean alreadyConfirmed = plugin.playerIsConfirmed(UUID.fromString(uuid)) > 0;

            if (alreadyConfirmed) {
                event.reply("✔️ Vos comptes sont déjà reliés et confirmés.\n Minecraft-UUID: " + uuid).submit(true);
                event.getMessage().editMessage("All good").setActionRows(ActionRow.of(
                        Button.primary("All good", "✔️ All good").asDisabled())).submit(true);

                return;
            }

            final String componentId = event.getComponentId();
            if (componentId.equals(acceptId)) {
                this.handleQuestSuccess(event, member, uuid);
                this.handleAccepted(event);
            } else if (componentId.equals(rejectId)) {
                this.handleRejected(event);
            }

        } catch (Exception e) {
            event.reply("❌ Cette demande a rencontrée des problèmes. Contactez un admin!").submit(true);
            SentryService.captureEx(e);
        }
    }

    private void handleQuestSuccess(ButtonClickEvent event, Member member, String playerUuid) {
        final User user = User.getFromMember(member);
        final Player player = user.getOnlinePlayer(playerUuid);
        if (player == null) {
            return;
        }

        BukkitManager.dropExpOrbsToPlayer(player, 6, 2);
    }

    private void handleAccepted(ButtonClickEvent event) {
        try {

            final List<Field> fields = event.getMessage().getEmbeds().get(0).getFields();
            final String uuid = fields.get(1).getValue();

            plugin.getBukkitManager().setPlayerAsConfirmed(uuid);

            event.reply("✔️ Vos compte sont maintenant reliés et confirmés.").submit(true);
            event.getMessage().editMessage("All good").setActionRows(ActionRow.of(
                    Button.primary("All good", "✔️ All good").asDisabled())).submit(true);
        } catch (Exception e) {
            event.reply("❌ Vos compte non pas pu être reliés et confrimés. Contactez un admin!").submit(true);
            SentryService.captureEx(e);
        }
    }

    private void handleRejected(ButtonClickEvent event) {
        try {
            final List<Field> fields = event.getMessage().getEmbeds().get(0).getFields();
            final String uuid = fields.get(1).getValue();

            plugin.getBukkitManager().sanitizeAndBanPlayer(uuid);

            event.reply("✔️ La demande a bien été rejetée.").submit(true);
            event.getMessage().editMessage("All good").setActionRows(ActionRow.of(
                    Button.primary("All good", "✔️ All good").asDisabled())).submit(true);

        } catch (Exception e) {
            event.reply("❌ Cette demande a rencontrée des problèmes. Contactez un admin!").submit(true);
            SentryService.captureEx(e);
        }
    }
}
