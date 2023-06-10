//modifié
package events.discords;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import commands.bukkit.ConfirmLinkCmd;
import main.WhitelistJe;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import services.sentry.SentryService;

public class OnUserConfirm extends ListenerAdapter {
    private Logger logger;
    private WhitelistJe plugin;
    private String acceptId = ConfirmLinkCmd.acceptId;
    private String rejectId = ConfirmLinkCmd.rejectId;

    public OnUserConfirm(WhitelistJe plugin) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        try {
            if (!event.getChannel().getType().toString().equals("PRIVATE")) {
                return;
            }

            final List<Field> fields = event.getMessage().getEmbeds().get(0).getFields();
            final String uuid = fields.get(1).getValue();

            final boolean alreadyConfirmed = plugin.playerIsConfirmed(UUID.fromString(uuid)) > 0;

            if(alreadyConfirmed) {
                event.reply("✔️ Vos comptes sont déjà reliés et Whitelister.\n Minecraft-UUID: " + uuid).submit(true);
                event.getMessage().editMessage("All good").setActionRows(ActionRow.of(
                    Button.primary("All good", "✔️ All good").asDisabled()
                )).submit(true);

                return;
            }

            final String componentId = event.getComponentId();    
            if (componentId.equals(acceptId)) {
                this.handleAccepted(event);
    
            } else if (componentId.equals(rejectId)) {
                this.handleRejected(event);
            }
            
        } catch (Exception e) {
            event.reply("❌ We're having problem with your request. Please contact an Admin on Discord!").submit(true);
            SentryService.captureEx(e);
        }
    }

    private void handleAccepted(ButtonClickEvent event) {
        try {

            final List<Field> fields = event.getMessage().getEmbeds().get(0).getFields();
            final String uuid = fields.get(1).getValue();

            plugin.getBukkitManager().setPlayerAsConfirmed(uuid);

            event.reply("✔️ Your accounts are now linked and confirmed.").submit(true);
            event.getMessage().editMessage("All good").setActionRows(ActionRow.of(
                Button.primary("All good", "✔️ All good").asDisabled()
            )).submit(true);
        } catch (Exception e) {
            event.reply("❌ Your accounts couldn't be linked and confirmed. Please contact an Admin on Discord!").submit(true);
            SentryService.captureEx(e);
        }
    }

    private void handleRejected(ButtonClickEvent event) {
        try {
            final List<Field> fields = event.getMessage().getEmbeds().get(0).getFields();
            final String uuid = fields.get(1).getValue();

            plugin.getBukkitManager().sanitizeAndBanPlayer(uuid);

            event.reply("✔️ The request has been rejected.").submit(true);
            event.getMessage().editMessage("All good").setActionRows(ActionRow.of(
                Button.primary("All good", "✔️ All good").asDisabled()
            )).submit(true);

        } catch (Exception e) {
            event.reply("❌ This request has encountered some issues. Please contact an Admin on Discord!").submit(true);
            SentryService.captureEx(e);
        }
    }
}
