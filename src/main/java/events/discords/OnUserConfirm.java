package events.discords;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import commands.bukkit.ConfirmLinkCmd;
import helpers.EconomyManager;
import helpers.StatsManager;
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

    private boolean handleQuestSuccess(ButtonClickEvent event, Member member, String playerUuid) {
        try {
            final User user = User.getFromMember(member);
            final Player player = user.getOnlinePlayer(playerUuid);

            LinkedHashMap<String, Object> questMap = plugin.getConfigManager().getAsMap("quests");
            LinkedHashMap<String, Object> quest = (LinkedHashMap<String, Object>) questMap.get("registration");

            if (quest.size() < 1) {
                logger.warning("Could not find the quest config for this event. No exp or currency will be donated...");
                return false;
            }

            if (player == null) {
                logger.warning("Error at registration quest completion. No exp or currency will be donated...");
                return false;
            }

            final UUID npcUUID = UUID.fromString(quest.getOrDefault("npc", "xxx").toString());
            final int orbsCount = (int) quest.getOrDefault("xpOrbsCount", 0) > 0
                    ? (int) quest.getOrDefault("xpOrbsCount", 0)
                    : 0;
            final int orbsValue = (int) quest.getOrDefault("xpOrbsValue", 0) > 0
                    ? (int) quest.getOrDefault("xpOrbsValue", 0)
                    : 0;
            final int questXp = (int) quest.getOrDefault("baseXp", 0) > 0 ? (int) quest.getOrDefault("baseXp", 0) : 0;
            final double vaultDeposit = (double) quest.getOrDefault("vaultDeposit", 0) > 0
                    ? (double) quest.getOrDefault("vaultDeposit", 0)
                    : 0;

            Location orbsLocation = player.getLocation();
            Player registrarNpc = plugin.getServer().getPlayer(npcUUID);

            if (registrarNpc != null) {
                logger.info(("Found the quest NPC! using its location for the XPOrbs drop..."));
                orbsLocation = registrarNpc.getLocation();
            } else {
                logger.warning("The quest NPC was not found! Using its players for the XPOrbs drop...");
            }

            StatsManager.giveXp(player, questXp);
            StatsManager.dropExpOrbs(orbsLocation, orbsCount, orbsValue);
            EconomyManager.depositPlayer(player, vaultDeposit);

            player.sendMessage("Hurray! Your registration quest is completed.");
            player.sendMessage("--> Here's your rewards (if any):");
            player.sendMessage(String.format("Currency: %s$", String.valueOf(vaultDeposit)));
            player.sendMessage(String.format("Direct-Xp: %s", String.valueOf(questXp)));
            player.sendMessage(
                    String.format("Orbs: %1$s x %2$sxp ", String.valueOf(orbsCount), String.valueOf(orbsValue)));

            return true;

        } catch (Exception e) {
            logger.warning("Error at registration quest completion. No exp or currency will be donated...");
            SentryService.captureEx(e);
            return false;
        }
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
