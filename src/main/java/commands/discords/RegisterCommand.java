package commands.discords;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import services.api.PlayerDbApi;
import dao.DaoManager;
import discord.GuildManager;
import helpers.Helper;
import helpers.NotificationManager;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import services.sentry.SentryService;
import main.WhitelistDmc;
import models.BedrockData;
import models.JavaData;
import models.NotificationData;
import models.User;

import java.awt.*;
import java.util.LinkedList;

public class RegisterCommand extends BaseCmd {

    private final static String KEY_CMD_NAME = "CMD_REGISTER";
    private final static String KEY_CMD_DESC = "DESC_REGISTR";
    private final static String KEY_PARAM_JAVA = "PARAM_PJAVA";
    private final static String KEY_PARAM_BEDR = "PARAM_PBEDR";
    private final static String KEY_PARAM_JAVA_LABEL = "PARAM_REGISTR_LABELJ";
    private final static String KEY_PARAM_BEDR_LABEL = "PARAM_REGISTR_LABELB";

    public static void REGISTER_CMD(JDA jda, WhitelistDmc plugin) {
        String cmdName = LOCAL.translate(KEY_CMD_NAME);
        String cmdDesc = LOCAL.translate(KEY_CMD_DESC);
        String paramJ = LOCAL.translate(KEY_PARAM_JAVA);
        String paramB = LOCAL.translate(KEY_PARAM_BEDR);
        String paramLabelJ = LOCAL.translate(KEY_PARAM_JAVA_LABEL);
        String paramLabelB = LOCAL.translate(KEY_PARAM_BEDR_LABEL);

        jda.addEventListener(new RegisterCommand(plugin));
        jda.upsertCommand(cmdName, cmdDesc)
                .addOption(OptionType.STRING, paramJ, paramLabelJ, false)
                .addOption(OptionType.STRING, paramB, paramLabelB, false)
                .submit(true);
    }

    private String acceptId = "acceptAction";
    private String rejectId = "rejectAction";
    private String acceptId_conf = "acceptConfAction";
    private String rejectId_conf = "rejectConfAction";

    public RegisterCommand(WhitelistDmc plugin) {
        super(plugin,
                "RegisterCommand",
                "CMD_REGISTER",
                "RegisterCommand",
                "Register Mc®");
    }

    @Override
    protected final void execute() {

        if (this.member == null) {
            final String reply = useTranslator("GUILDONLY_CMD");
            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "guild reserved");
            tx.finish(SpanStatus.UNAVAILABLE);
            return;
        }

        String reply = "";
        String replyJava = "";
        String replyBedrock = "";
        String javaUuid = null;
        String bedrockUuid = null;

        final OptionMapping javaOpt = event.getOption(LOCAL.translate(KEY_PARAM_JAVA));
        final OptionMapping bedOpt = event.getOption(LOCAL.translate(KEY_PARAM_BEDR));

        if (javaOpt == null && bedOpt == null) {
            event.reply("❌**" + useTranslator("REGISTER_CMD_PARAM_ERROR") + "**")
                    .setEphemeral(true).submit(true);

            tx.setData("state", "Empty pseudo credentials");
            tx.finish(SpanStatus.OK);
            return;
        }

        final String pseudoJava = javaOpt != null ? javaOpt.getAsString() : null;
        final String pseudoBedrock = bedOpt != null ? bedOpt.getAsString() : null;
        if (pseudoJava == null && pseudoBedrock == null) {
            event.reply("❌**" + useTranslator("REGISTER_CMD_PARAM_ERROR") + "**")
                    .setEphemeral(true).submit(true);

            tx.setData("state", "User-Mc-Data not found");
            tx.finish(SpanStatus.OK);
            return;
        }

        boolean javaValid = this.validatePseudo(event, pseudoJava, "Java");
        boolean bedrockValid = this.validatePseudo(event, pseudoBedrock, "Bedrock");
        if (!javaValid && !bedrockValid) {
            reply = useTranslator("REGISTER_CMD_FORMAT_ERROR") + "\n" +
                    useTranslator("INFO_CHECK_YOUR_MSG");

            event.reply(reply).setEphemeral(true).submit(true);

            tx.setData("state", "Invalid MC's pseudo format");
            tx.finish(SpanStatus.OK);
            return;
        }

        if (pseudoJava != null) {
            javaUuid = PlayerDbApi.getMinecraftUUID(pseudoJava);
            if (javaUuid == null) {
                replyJava = String.format(useTranslator("REGISTER_CMD_NOT_FOUND_UUID"), "Java");
            }
        }
        if (pseudoBedrock != null) {
            bedrockUuid = PlayerDbApi.getXboxUUID(pseudoBedrock);

            if (bedrockUuid == null) {
                replyBedrock = String.format(useTranslator("REGISTER_CMD_NOT_FOUND_UUID"), "Bedrock");
            }
        }

        if (javaUuid == null && bedrockUuid == null) {
            event.reply(replyJava + "\n\n" + replyBedrock).setEphemeral(true).submit(true);

            tx.setData("state", "MC's uuids not found within web-api");
            tx.finish(SpanStatus.ABORTED);
            return;
        }

        final String discordId = member.getId();
        final String confirmHoursString = plugin.getConfigManager().get("hoursToConfirmMcAccount");
        final Integer hoursToConfirm = confirmHoursString != null
                ? Integer.parseInt(confirmHoursString)
                : null;

        boolean sendJava = false;
        if (javaUuid != null) {

            replyJava = String.format(useTranslator("REGISTER_CMD_ERROR"), "Java");
            JavaData jData = DaoManager.getJavaDataDao().findWithUuid(javaUuid);
            sendJava = jData == null;

            if (!sendJava) {
                final boolean isAllowed = jData.isAllowed();
                final boolean isConfirmed = jData.isConfirmed();

                if (jData.getUserId() != user.getId()) {
                    replyJava = String.format(useTranslator("WARN_ALREADY_REGISTERED"), "Java");
                }

                else if (!isAllowed) {
                    replyJava = String.format(useTranslator("WARN_NOT_ACCEPTED_YET"), "Java") + "\n"
                            + useTranslator("INFO_CONTACT_ADMIN_MORE_INFO");
                }

                else if (isAllowed && isConfirmed) {
                    replyJava = String.format(useTranslator("INFO_ALREADY_ACCEPTED_CONNECT"), "Java");
                }

                else if (isAllowed && !isConfirmed && hoursToConfirm > 0) {
                    replyJava = String.format(useTranslator("INFO_MUST_CONFIRM_ACCOUNT"), "Java") +
                            String.format(useTranslator("INFO_TIME_TO_CONFIRM_SINCE"), hoursToConfirm);
                }
            }
        }

        boolean sendBedrock = false;
        if (bedrockUuid != null) {

            replyBedrock = String.format(useTranslator("REGISTER_CMD_ERROR"), "Bedrock");
            BedrockData bData = DaoManager.getBedrockDataDao().findWithUuid(bedrockUuid);
            sendBedrock = bData == null;

            if (!sendBedrock) {
                final boolean isAllowed = bData.isAllowed();
                final boolean isConfirmed = bData.isConfirmed();

                if (bData.getUserId() != user.getId()) {
                    replyBedrock = String.format(useTranslator("WARN_ALREADY_REGISTERED"), "Bedrock");
                }

                else if (!isAllowed) {
                    replyBedrock = String.format(useTranslator("WARN_NOT_ACCEPTED_YET"), "Bedrock") + "\n"
                            + useTranslator("INFO_CONTACT_ADMIN_MORE_INFO");
                }

                else if (isAllowed && isConfirmed) {
                    replyBedrock = String.format(useTranslator("INFO_ALREADY_ACCEPTED_CONNECT"), "Bedrock");
                }

                else if (isAllowed && !isConfirmed && hoursToConfirm > 0) {
                    replyBedrock = String.format(useTranslator("INFO_MUST_CONFIRM_ACCOUNT"), "Bedrock") +
                            String.format(useTranslator("INFO_TIME_TO_CONFIRM_SINCE"), hoursToConfirm);
                }
            }
        }

        if (!sendJava && !sendBedrock) {
            event.reply(replyJava + "\n\n" + replyBedrock).setEphemeral(true).submit(true);
            tx.setData("state", "Registration request was aborted.");
            tx.finish(SpanStatus.PERMISSION_DENIED);
            return;
        }

        if (sendJava == true) {
            submitRequestEmbeded(discordId, pseudoJava, javaUuid, "Java");
            replyJava = String.format(useTranslator("INFO_ACCES_REQUESTED"), "Java", pseudoJava)
                    + "\n" + useTranslator("INFO_PLZ_AWAIT");
        }

        if (sendBedrock == true) {
            submitRequestEmbeded(discordId, pseudoBedrock, bedrockUuid, "Bedrock");
            replyBedrock = String.format(useTranslator("INFO_ACCES_REQUESTED"), "Bedrock", pseudoBedrock)
                    + "\n" + useTranslator("INFO_PLZ_AWAIT");
        }

        final String replyMsg = replyJava + "\n\n" + replyBedrock;
        final String registrarTitle = "🤖 " + member.getEffectiveName() + " is awaiting for registration.";
        final String registrarMsg = "\n\t`Discord-UserId`: " + member.getId() +
                "\n\t`Pseudo-Java`: " + pseudoJava +
                "\n\t`Pseudo-Bedrock`: " + pseudoBedrock +
                "\n\t`Discord-Server`: " + guild.getName();

        LinkedList<Member> registrars = plugin.getGuildManager().getNotifiableMembers();
        for (Member member : registrars) {
            this.plugin.getDiscordManager().jda.openPrivateChannelById(member.getUser().getId()).queue(channel -> {
                channel.sendMessage("**" + registrarTitle + "**" + registrarMsg).submit(true).isDone();
            });
        }

        final NotificationData notification = new NotificationData(registrarTitle, registrarMsg);
        notification.topic = NotificationManager.registrationTopic;
        notification.addViewAction("Admin panel", "https://rvdprojects.synology.me:3000/#/dashboard");
        notification.markdown = true;
        NotificationManager.postNotification(notification, true);

        event.reply(replyMsg).setEphemeral(true).submit(true);
        tx.setData("state", "Registration request sent successfully.");
        tx.finish(SpanStatus.OK);
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ITransaction tx = Sentry.startTransaction("RegisterCommand", "reply to /register Mc®");
        ISpan child = null;

        if (!this.isValidButtonToContinue(event)) {
            return;
        }

        this.user = null;
        this.cmdLang = null;
        this.event = null;
        this.member = null;
        this.eventUser = null;
        this.channel = null;

        try {
            this.member = event.getMember();
            this.channel = event.getChannel();
            final GuildManager gManager = this.plugin.getGuildManager();

            if (!channel.getId().equals(gManager.adminChannelId)
                    && !channel.getId().equals(gManager.whitelistChannelId)) {

                tx.setData("state", "bad channel -> stopping event");
                tx.finish(SpanStatus.OK);
                return;
            }

            final boolean isAuthorized = gManager.isOwner(member.getId())
                    || gManager.isAdmin(member.getId())
                    || gManager.isModo(member.getId())
                    || gManager.isDev(member.getId())
                    || gManager.isHelper(member.getId());

            if (!isAuthorized) {
                event.reply(LOCAL.useDefault("ROLE_NOT_ALLOWED"))
                        .setEphemeral(true).submit(true);

                throw new Exception("🔒 Commande répondu par un role non autorisé. 🔒" +
                        "\nUser name: <@" + member.getId() + ">" +
                        "\nChannel name:" + channel.getName() +
                        "\nMessage id: " + event.getMessage().getId());
            }

            final String componentId = event.getComponentId();
            final String[] splitId = componentId.split(" ");
            final String actionId = splitId[0];
            final String pseudo = splitId[1];
            final String discordId = splitId[2];
            final String uuid = splitId[3];

            child = tx.startChild("actionHandler");
            child.setData("type", "none");

            if (actionId.equals(this.acceptId)) {
                child.setData("type", "accepted");

                final User newUser = User.updateFromMember(gManager.findMember(discordId));
                this.handleAccepted(event, newUser, pseudo, uuid);
                tx.setData("state", "ACCEPTED /register");

            } else if (actionId.equals(this.rejectId)) {
                child.setData("type", "rejected");
                this.handleRejected(event, discordId, pseudo, uuid);
                tx.setData("state", "REJECTED /register");
            }

            child.finish(SpanStatus.OK);
            tx.finish(SpanStatus.OK);

        } catch (Exception e) {
            if (child != null) {
                child.setData("state", "unkwown");
                child.finish();
            }

            tx.setThrowable(e);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
        }
    }

    private void submitRequestEmbeded(String discordId, String pseudo, String uuid, String type) {
        EmbedBuilder embeded = this.getRequestEmbeded(event, pseudo, uuid, type);
        GuildManager gManager = this.plugin.getGuildManager();

        gManager.getAdminChannel().sendMessageEmbeds(embeded.build())
                .setActionRows(ActionRow.of(
                        Button.primary(this.acceptId + " " + pseudo + " " + discordId + " " + uuid,
                                LOCAL.useDefault("BTN_ACCEPT")),
                        Button.secondary(this.rejectId + " " + pseudo + " " + discordId + " " + uuid,
                                LOCAL.useDefault("BTN_REFUSE"))))
                .submit(true);
    }

    private boolean validatePseudo(SlashCommandEvent event, String pseudo, String type) {
        if (pseudo == null) {
            return false;
        }

        if (!Helper.isMcPseudo(pseudo)) {
            final String errMsg = String.format(useTranslator("WARN_BAD_PSEUDO_FORMAT_EXPLAIN"), type, pseudo);
            this.sendMsgToUser(errMsg);
            return false;
        }
        return true;
    }

    private EmbedBuilder getRequestEmbeded(SlashCommandEvent event, String pseudo, String uuid, String type) {

        final String avatarUrl = type == "Bedrock"
                ? "https://api.tydiumcraft.net/v1/players/skin?uuid=" + uuid + "&size=72"
                : type == "Java"
                        ? "https://mc-heads.net/body/" + uuid + "/72"
                        : "https://incrypted.com/wp-content/uploads/2021/07/a4cf2df48e2218af11db8.jpeg";

        final String title = String.format(LOCAL.useDefault("INFO_REGISTER_REQUEST"), type);
        return new EmbedBuilder().setTitle(title)
                .setImage(avatarUrl)
                .addField("Pseudo", pseudo, true)
                .addField("Discord", "<@" + member.getId() + ">", true)
                .setThumbnail(eventUser.getAvatarUrl())
                .setFooter("ID: " + member.getId() + "\nUUID: " + uuid)
                .setColor(new Color(0x9b7676));
    }

    private EmbedBuilder getAcceptedEmbeded(String pseudo, String discordId) {
        return new EmbedBuilder()
                .setTitle(LOCAL.useDefault("INFO_ACCEPTED_REQUEST"))
                .addField("Pseudo", pseudo, true)
                .addField("Membre", "<@" + discordId + ">", true)
                .setThumbnail(this.plugin.getDiscordManager().jda.getUserById(discordId).getAvatarUrl())
                .setFooter("ID " + discordId)
                .setColor(new Color(0x484d95));
    }

    private EmbedBuilder getRejectedEmbeded(String pseudo, String discordId) {
        return new EmbedBuilder().setTitle(LOCAL.useDefault("INFO_REJECTED_REQUEST"))
                .addField("Pseudo", pseudo, true)
                .addField("Membre", "<@" + discordId + ">", true)
                .setThumbnail(this.plugin.getDiscordManager().jda.getUserById(discordId).getAvatarUrl())
                .setFooter("ID " + discordId)
                .setColor(new Color(0x44474d));
    }

    // Accept
    private void handleAccepted(ButtonClickEvent event, User newUser, String pseudo, String uuid) {
        try {
            final String messageId = event.getMessage().getId();
            final String moderatorId = event.getMember().getId();
            final GuildManager gManager = this.plugin.getGuildManager();

            final String discordId = newUser.getDiscordId();
            final EmbedBuilder newMsgContent = this.getAcceptedEmbeded(pseudo, discordId);

            final String confirmHoursString = plugin.getConfigManager().get("hoursToConfirmMcAccount");
            final Integer hoursToConfirm = confirmHoursString != null
                    ? Integer.parseInt(confirmHoursString)
                    : null;

            final boolean confirmed = hoursToConfirm == null || hoursToConfirm < 1;
            final boolean ok = plugin.getBukkitManager().setPlayerAsAllowed(
                    newUser.getId(), messageId, true,
                    moderatorId, uuid,
                    confirmed, pseudo);

            if (!ok) {
                event.reply(LOCAL.useDefault("CMD_ERROR")
                        + " --> " + LOCAL.useDefault("WARN_REGISTRATIONDELAY"))
                        .setEphemeral(true).submit(true);
                return;
            }

            final String label = String.format(LOCAL.useDefault("INFO_ACCEPTED_BY"), member.getEffectiveName());
            event.getMessage().editMessageEmbeds(newMsgContent.build())
                    .setActionRow(net.dv8tion.jda.api.interactions.components.Button
                            .primary(this.acceptId_conf, label)
                            .asDisabled())
                    .submit(true);

            final String avatarUrl = plugin.getBukkitManager().getAvatarUrl(uuid, "72");
            final String newMsg = String.format(translateBy("INFO_WELCOME_USER", newUser.getLang()), discordId, pseudo)
                    + "\n" + avatarUrl;
            gManager.getWelcomeChannel().sendMessage(newMsg).submit(true);

            this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
                String msg = newMsg;
                if (hoursToConfirm > 0) {
                    msg = newMsg + "\n"
                            + String.format(translateBy("INFO_TIME_TO_CONFIRM", newUser.getLang()), hoursToConfirm);
                }
                channel.sendMessage(msg).submit(true);
            });

            event.reply(String.format(LOCAL.useDefault("INFO_USER_WAS_ACCEPTED"), discordId, pseudo))
                    .setEphemeral(true).submit(true);

        } catch (Exception e) {
            event.reply(LOCAL.useDefault("CMD_ERROR"))
                    .setEphemeral(true).submit(true);
            SentryService.captureEx(e);
        }
    }

    // Reject
    private void handleRejected(ButtonClickEvent event, String discordId, String pseudo, String uuid) {
        try {
            plugin.getBukkitManager().sanitizeAndBanPlayer(uuid);
            final EmbedBuilder newMsgContent = this.getRejectedEmbeded(pseudo, discordId);

            final String label = String.format(LOCAL.useDefault("INFO_REJECTED_BY"), member.getEffectiveName());
            event.getMessage().editMessageEmbeds(newMsgContent.build())
                    .setActionRow(net.dv8tion.jda.api.interactions.components.Button
                            .primary(this.rejectId_conf, label)
                            .asDisabled())
                    .submit(true);

            final String newMsg = LOCAL.useDefault("INFO_REJECTED_USER");

            this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
                channel.sendMessage(newMsg).submit(true);
            });

            event.reply(String.format(LOCAL.useDefault("INFO_USER_WAS_REJECTED"), discordId, pseudo))
                    .setEphemeral(true).submit(true);

        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

}
