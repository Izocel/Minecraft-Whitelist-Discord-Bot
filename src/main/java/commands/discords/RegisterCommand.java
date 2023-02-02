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
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import services.sentry.SentryService;
import main.WhitelistJe;
import models.BedrockData;
import models.JavaData;
import models.User;

import java.awt.*;

public class RegisterCommand extends BaseCmd {

    private final static String KEY_CMD_NAME = "CMD_REGISTER";
    private final static String KEY_CMD_DESC = "DESC_REGISTR";
    private final static String KEY_PARAM_JAVA = "PARAM_PJAVA";
    private final static String KEY_PARAM_BEDR = "PARAM_PBEDR";
    private final static String KEY_PARAM_JAVA_LABEL = "PARAM_REGISTR_LABELJ";
    private final static String KEY_PARAM_BEDR_LABEL = "PARAM_REGISTR_LABELB";

    public static void REGISTER_CMD(JDA jda, WhitelistJe plugin) {
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

    public RegisterCommand(WhitelistJe plugin) {
        super(plugin, 
            "RegisterCommand", 
            "CMD_REGISTER",
            "RegisterCommand",
            "Register Mc®"
        );
    }

    @Override
    protected final void execute() {
        
        if (this.member == null) {
            final String reply = LOCAL.translate("GUILDONLY_CMD");
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
            event.reply("❌**Vous devez fournir au moins un pseudo pour utiliser cette commande...**")
                    .setEphemeral(true).submit(true);

            tx.setData("state", "Empty pseudo credentials");
            tx.finish(SpanStatus.OK);
            return;
        }

        final String pseudoJava = javaOpt != null ? javaOpt.getAsString() : null;
        final String pseudoBedrock = bedOpt != null ? bedOpt.getAsString() : null;
        if (pseudoJava == null && pseudoBedrock == null) {
            event.reply("❌**Vos pseudo n'ont pas pu être retrouvés dans la commande...**")
                    .setEphemeral(true).submit(true);

            tx.setData("state", "User-Mc-Data not found");
            tx.finish(SpanStatus.OK);
            return;
        }

        boolean javaValid = this.validatePseudo(event, pseudoJava, "Java");
        boolean bedrockValid = this.validatePseudo(event, pseudoBedrock, "Bedrock");
        if (!javaValid && !bedrockValid) {
            reply = "❌ Vos `identifiants` comportaient des `erreurs` de format.\n" + 
                "Voir les détails dans vos messages privés.";

            event.reply(reply).setEphemeral(true).submit(true);

            tx.setData("state", "Invalid MC's pseudo format");
            tx.finish(SpanStatus.OK);
            return;
        }

        if (pseudoJava != null) {
            javaUuid = PlayerDbApi.getMinecraftUUID(pseudoJava);
            if (javaUuid == null) {
                replyJava = "❌ **Votre UUID `Java` n'a pas pu être retrouvés sur les serveurs...**";
            }
        }
        if (pseudoBedrock != null) {
            bedrockUuid = PlayerDbApi.getXboxUUID(pseudoBedrock);

            if (bedrockUuid == null) {
                replyBedrock = "❌ **Votre UUID `Bedrock` n'a pas pu être retrouvés sur les serveurs...**";
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

            replyJava = "❌ **Désoler, l'enregistrement pour votre pseudo Java ne c'est pas bien passé.**";
            JavaData jData = DaoManager.getJavaDataDao().findWithUuid(javaUuid);
            sendJava = jData == null;

            if (!sendJava) {
                final boolean isAllowed = jData.isAllowed();
                final boolean isConfirmed = jData.isConfirmed();

                if (jData.getUserId() != user.getId()) {
                    replyJava = "❌ **Ce pseudo Java est déjà enregistrer par un autre joueur**";
                }

                else if (!isAllowed) {
                    replyJava = "❌ **Ce compte Java n'a pas encore été accepté sur le serveur.**\n" +
                            "Pour en s'avoir d'avantage, contactez un administrateur directement...";
                }

                else if (isAllowed && isConfirmed) {
                    replyJava = "**Votre compte Java est déjà accepté sur le serveur...**\n" +
                            "Il suffit de vous connecter. `Enjoy` ⛏🧱";
                }

                else if (isAllowed && !isConfirmed && hoursToConfirm > 0) {
                    replyJava = "**Une confirmation de votre compte Java est nécéssaire.**\n" +
                            "Pour confimer votre compte vous aviez `" + hoursToConfirm
                            + "h` depuis l'aprobation pour vous connecter au server Mincecraft®\n";
                }
            }
        }

        boolean sendBedrock = false;
        if (bedrockUuid != null) {

            replyBedrock = "❌ **Désoler, l'enregistrement pour votre pseudo Bedrock ne c'est pas bien passé.**";
            BedrockData bData = DaoManager.getBedrockDataDao().findWithUuid(bedrockUuid);
            sendBedrock = bData == null;

            if (!sendBedrock) {
                final boolean isAllowed = bData.isAllowed();
                final boolean isConfirmed = bData.isConfirmed();

                if (bData.getUserId() != user.getId()) {
                    replyBedrock = "❌ **Ce pseudo Bedrock est déjà enregistrer par un autre joueur**";
                }

                else if (!isAllowed) {
                    replyBedrock = "❌ **Ce compte Bedrock n'a pas encore été accepté sur le serveur.**\n" +
                            "Pour en s'avoir d'avantage, contactez un administrateur directement...";
                }

                else if (isAllowed && isConfirmed) {
                    replyBedrock = "**Votre compte Bedrock est déjà accepté sur le serveur...**\n" +
                            "Il suffit de vous connecter. `Enjoy` ⛏🧱";

                }

                else if (isAllowed && !isConfirmed && hoursToConfirm > 0) {
                    replyBedrock = "**Une confirmation de votre compte Bedrock est nécéssaire.**\n" +
                            "Pour confimer votre compte vous aviez `" + hoursToConfirm
                            + "h` depuis l'aprobation pour vous connecter au server Mincecraft®\n";
                }

            }
        }
        
        
        if(!sendJava && !sendBedrock) {
            event.reply(replyJava + "\n\n" + replyBedrock).setEphemeral(true).submit(true);
            tx.setData("state", "Registration request was aborted.");
            tx.finish(SpanStatus.PERMISSION_DENIED);
            return;
        }

        if(sendJava == true) {
            EmbedBuilder embeded = this.getRequestEmbeded(event, pseudoJava, javaUuid, "Java");
            GuildManager gManager = this.plugin.getGuildManager();

            gManager.getAdminChannel().sendMessageEmbeds(embeded.build())
                    .setActionRows(ActionRow.of(
                            Button.primary(this.acceptId + " " + pseudoJava + " " +
                                    discordId + " " + javaUuid, "✔️ Accepter"),
                            Button.secondary(this.rejectId + " " + pseudoJava + " " +
                                    discordId + " " + javaUuid, "❌ Refuser")))
                    .submit(true);

            replyJava = "**Votre demande d'accès `Java` pour `" + pseudoJava
                    + "` a été envoyé aux modérateurs.**\n**Merci de patienter jusqu'à une prise de décision de leur part.**";
        }

        if(sendBedrock == true) {
            EmbedBuilder embeded = this.getRequestEmbeded(event, pseudoBedrock, bedrockUuid, "Bedrock");
            GuildManager gManager = this.plugin.getGuildManager();

            gManager.getAdminChannel().sendMessageEmbeds(embeded.build())
                    .setActionRows(ActionRow.of(
                            Button.primary(this.acceptId + " " + pseudoBedrock + " " +
                                    discordId + " " + bedrockUuid, "✔️ Accepter"),
                            Button.secondary(this.rejectId + " " + pseudoBedrock + " " +
                                    discordId + " " + bedrockUuid, "❌ Refuser")))
                    .submit(true);

            replyBedrock = "**Votre demande d'accès `Bedrock` pour `" + pseudoBedrock
                    + "` a été envoyé aux modérateurs.**\n**Merci de patienter jusqu'à une prise de décision de leur part.**";
        }


        event.reply(replyJava + "\n\n" + replyBedrock).setEphemeral(true).submit(true);
        tx.setData("state", "Registration request sent succesfully.");
        tx.finish(SpanStatus.OK);

    }

    private boolean validatePseudo(SlashCommandEvent event, String pseudo, String type) {
        if(pseudo == null) {
            return false;
        }

        if (!Helper.isMcPseudo(pseudo)) {
            final String errMsg = "❌ Votre pseudo `" + type + "`: `" + pseudo
                    + "` devrait comporter entre `3` et `16` caractères" +
                    "\n et ne doit pas comporter de caractères spéciaux à part des underscores `_` ou tirets `-`";

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

        return new EmbedBuilder().setTitle("Un joueur `" + type + "` veut s'enregister sur votre serveur `Minecraft®`")
                .setImage(avatarUrl)
                .addField("Pseudo", pseudo, true)
                .addField("Discord", "<@" + member.getId() + ">", true)
                .setThumbnail(eventUser.getAvatarUrl())
                .setFooter("ID: " + member.getId() + "\nUUID: " + uuid)
                .setColor(new Color(0x9b7676));
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ITransaction tx = Sentry.startTransaction("RegisterCommand", "reply to /register Mc®");
        ISpan child = null;

        if (!this.isValidButtonToContinue(event)) {
            return;
        }

        try {
            final GuildManager gManager = this.plugin.getGuildManager();
            if (!event.getChannel().getId().equals(gManager.adminChannelId)
                    && !event.getChannel().getId().equals(gManager.whitelistChannelId)) {

                tx.setData("state", "bad channel -> stoping event");
                tx.finish(SpanStatus.OK);
                return;
            }

            final Member respMember = event.getMember();
            final boolean isAuthorized = gManager.isOwner(respMember.getId())
                    || gManager.isAdmin(respMember.getId())
                    || gManager.isModo(respMember.getId())
                    || gManager.isDev(respMember.getId());

            if (!isAuthorized) {
                event.reply("Dommage vous n'avez pas les accès...¯\\_(ツ)_/¯")
                        .setEphemeral(true).submit(true);

                throw new Exception("Commande répondu pas un role non authorisé." +
                        "\nUser name: <@" + respMember + ">" +
                        "\nChannel name:" + event.getChannel().getName() +
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

                final User newUser = User.updateFromMember(member);
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

    private EmbedBuilder getAcceptedEmbeded(String pseudo, String discordId) {
        return new EmbedBuilder()
                .setTitle("Demande acceptée")
                .addField("Pseudo", pseudo, true)
                .addField("Discord", "<@" + discordId + ">", true)
                .setThumbnail(this.plugin.getDiscordManager().jda
                        .getUserById(discordId).getAvatarUrl())
                .setFooter("ID " + discordId)
                .setColor(new Color(0x484d95));
    }

    private EmbedBuilder getRejectedEmbeded(String pseudo, String discordId) {
        return new EmbedBuilder().setTitle("Demande refusée")
                .addField("Pseudo", pseudo, true)
                .addField("Discord", "<@" + discordId + ">", true)
                .setThumbnail(this.plugin.getDiscordManager().jda
                        .getUserById(discordId).getAvatarUrl())
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
            final boolean ok = plugin.getBukkitManager().setPlayerAsAllowed(newUser.getId(), messageId, true,
                    moderatorId, uuid,
                    confirmed, pseudo);

            if (!ok) {
                event.reply("❌**Le joueur n'a pas pu être enregistrer réessayez...**")
                        .setEphemeral(true).submit(true);
                return;
            }

            event.getMessage().editMessageEmbeds(newMsgContent.build())
                    .setActionRow(net.dv8tion.jda.api.interactions.components.Button
                            .primary(this.acceptId_conf, "✔️ Accepter par " + event.getMember().getEffectiveName())
                            .asDisabled())
                    .submit(true);

            final String avatarUrl = plugin.getBukkitManager().getAvatarUrl(uuid, "72");
            final String newMsg = "**Nous te souhaitons bienvenue, <@" + discordId + "> :: `" + pseudo
                    + "` Enjoy  ⛏🧱 !!!**\n" + avatarUrl;
            gManager.getWelcomeChannel().sendMessage(newMsg).submit(true);

            this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
                String msg = newMsg;
                if (hoursToConfirm > 0) {
                    msg = newMsg + "\n**Vous avez `" + hoursToConfirm
                            + "h` pour vous connecter au serveur `Minecraft®` et ainsi `confirmer votre compte`.**";
                }
                channel.sendMessage(msg).submit(true);
            });

            event.reply("✔️ **Le joueur <@" + discordId + "> a bien été approuvé avec le pseudo: `" + pseudo + "`.**")
                    .setEphemeral(true).submit(true);

        } catch (Exception e) {
            event.reply("❌**Une erreur est survenu, contactez un admin !!!**")
                    .setEphemeral(true).submit(true);
            SentryService.captureEx(e);
        }
    }

    // Reject
    private void handleRejected(ButtonClickEvent event, String discordId, String pseudo, String uuid) {
        try {
            final EmbedBuilder newMsgContent = this.getRejectedEmbeded(pseudo, discordId);

            event.getMessage().editMessageEmbeds(newMsgContent.build())
                    .setActionRow(net.dv8tion.jda.api.interactions.components.Button
                            .primary(this.rejectId_conf, "❌ Refuser par " + event.getMember().getEffectiveName())
                            .asDisabled())
                    .submit(true);

            final String newMsg = "**❌ Votre enregistrement sur le serveur a été refusé.**";

            this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
                channel.sendMessage(newMsg).submit(true);
            });

            event.reply("❌ **Le joueur <@" + discordId + "> a bien été refusé pour le pseudo: `" + pseudo + "`.**")
                    .setEphemeral(true).submit(true);

            plugin.getBukkitManager().sanitizeAndBanPlayer(uuid);

        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

}
