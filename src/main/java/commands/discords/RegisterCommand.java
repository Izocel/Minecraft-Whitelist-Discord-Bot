package commands.discords;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import services.api.MojanApi;
import dao.UsersDao;
import functions.GuildManager;
import helpers.Helper;
import main.WhitelistJe;
import models.User;

import java.awt.*;
import java.util.logging.Logger;

public class RegisterCommand extends ListenerAdapter {
    private Logger logger;
    private WhitelistJe main;
    private String acceptId = "acceptAction";
    private String rejectId = "rejectAction";
    private String acceptId_conf = "acceptConfAction";
    private String rejectId_conf = "rejectConfAction";

    public RegisterCommand(WhitelistJe main) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.main = main;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        final String cmdName = this.main.getConfigManager().get("registerCmdName", "register");
        if (!event.getName().equals(cmdName))
            return;

        final String pseudo = event.getOption("pseudo").getAsString();
        final String discordId = event.getMember().getId();

        if(!this.validatePseudo(event, pseudo)) {
            return;
        }

        final String mc_uuid = MojanApi.getPlayerUUID(pseudo);
        if(mc_uuid == null) {
            event.reply("❌**Le UUID pour " + pseudo + " n'a pas pu être retrouver sur le serveur mojan...**")
            .setEphemeral(true).queue();
            return;
        }

        if (!this.handleKnownUser(event, mc_uuid, discordId)) {
            return;
        }

        EmbedBuilder embeded = this.getRequestEmbeded(event, pseudo);
        GuildManager gManager = this.main.getGuildManager();

        event.reply("**Votre demande d'accès pour `" + pseudo
                + "` a été envoyé aux modérateurs.**\n**Merci de patienter jusqu'à une prise de décision de leur part.**")
                .setEphemeral(true).queue();
        
        gManager.getAdminChannel().sendMessage(embeded.build())
            .setActionRows(
                ActionRow.of(Button.primary(this.acceptId + " " + pseudo + " " + discordId, "✔️ Accepter"),
                Button.secondary(this.rejectId + " " + pseudo + " " + discordId, "❌ Refuser"))
            ).queue();
    }

    private boolean validatePseudo(SlashCommandEvent event, String pseudo) {
        if (!Helper.isMcPseudo(pseudo)) {
            final String errMsg = "❌ Votre pseudo devrait comporter entre 3 et 16 caractères" +
                    "\n\n et ne doit pas comporter de caractères spéciaux à part des underscores `_` ou tirets `-`";

            event.reply(errMsg).setEphemeral(true).queue();
            return false;
        }
        return true;
    }

    private boolean handleKnownUser(SlashCommandEvent event, String uuid, String discordId) {
        try {

            boolean isAllowed = false;
            boolean isConfirmed = false;
            User foundWUuid = this.main.getDaoManager().getUsersDao().findByMcUUID(uuid);

            if (foundWUuid == null) {
                return true;
            }

            if (foundWUuid != null && !foundWUuid.getDiscordId().equals(discordId)) {
                event.reply("❌ **Ce pseudo est déjà enregistrer par un autre joueur**")
                        .setEphemeral(true).queue();
                return false;
            }

            else if (foundWUuid != null && foundWUuid.getId() > 0) {
                isAllowed = foundWUuid.isAllowed();
                isConfirmed = foundWUuid.isConfirmed();

                if(!isAllowed) {
                    event.reply("❌ **Vous n'avez pas encore été accepté sur le serveur.**\n" +
                    "Pour en s'avoir d'avantage, contactez un administrateur directement...")
                    .setEphemeral(true).queue();
                    return false;
                }

                else if (isAllowed && isConfirmed) {
                    event.reply("**Vous êtes déjà accepté sur le serveur...**" + 
                    "Il suffit de vous connecter. `Enjoy` ⛏🧱").setEphemeral(true).queue();
                    return false;
                }

                else if (isAllowed && !isConfirmed) {
                    String msg = "**Une confirmation de votre compte est nécéssaire.**\n" + 
                    "Pour confimer votre compte vous avez 24h depuis l'aprobation pour vous connecter au server Mincecraft®\n";

                    //TODO: doUpdate if delay > xHours
                    //if:
                    //"Si ce délai est expiré l'administrateurs receveras votre nouvelle demande.✔️ "
                    //else:
                    //"Vous ne pouvez plus faire de demande jusqu'à l'expiration du délay. Contacterr l'admin....❌"

                    event.reply(msg).setEphemeral(true).queue();
                    return false;
                }
            }

        } catch (Exception e) {
            event.reply("❌ **Une erreur est survenu contactez un admin!!!**").setEphemeral(true).queue();
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private EmbedBuilder getRequestEmbeded(SlashCommandEvent event, String pseudo) {
        return new EmbedBuilder().setTitle("Un joueur veut s'enregister sur votre serveur `Minecraft®`")
                .addField("Pseudo", pseudo, true)
                .addField("Discord", "<@" + event.getMember().getId() + ">", true)
                .setThumbnail(event.getMember().getUser().getAvatarUrl())
                .setFooter("ID: " + event.getMember().getId())
                .setColor(new Color(0x9b7676));
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        final Member respMember = event.getMember();
        final String componentId = event.getComponentId();
        final GuildManager gManager = this.main.getGuildManager();

        final String actionId = componentId.split(" ")[0];
        final String pseudo = componentId.split(" ")[1];
        final String discordId = componentId.split(" ")[2];

        Member newuser = event.getGuild().getMemberById(discordId);

        if (!event.getChannel().getId().equals(gManager.whitelistChannelId)) {
            return;
        }

        final boolean isAuthorized = gManager.isOwner(respMember.getId())
                || gManager.isAdmin(respMember.getId())
                || gManager.isModo(respMember.getId())
                || gManager.isDev(respMember.getId());

        if (!isAuthorized) {
            this.logger.warning("Commande répondu pas un role non-approuvé." + 
            "\nUser name: <@" + respMember + ">" +
            "\nChannel name:" + event.getChannel().getName() +
            "\nMessage id: " + event.getMessage().getId());
            event.reply("Dommage vous n'avez pas les accès...¯\\_(ツ)_/¯")
                    .setEphemeral(true).queue();
            return;
        }

        if (actionId.equals(this.acceptId)) {
            this.handleAccepted(event, newuser, pseudo);

        } else if (actionId.equals(this.rejectId)) {
            this.handleRejected(event, newuser, pseudo);

        }
    }

    private EmbedBuilder getAcceptedEmbeded(String pseudo, String discordId) {
        return new EmbedBuilder()
        .setTitle("Demande acceptée")
        .addField("Pseudo", pseudo, true)
        .addField("Discord", "<@" + discordId + ">", true)
        .setThumbnail(this.main.getDiscordManager().jda
            .getUserById(discordId).getAvatarUrl()).setFooter("ID " + discordId)
        .setColor(new Color(0x484d95));
    }

    private EmbedBuilder getRejectedEmbeded(String pseudo, String discordId) {
        return new EmbedBuilder().setTitle("Demande refusée")
        .addField("Pseudo", pseudo, true)
        .addField("Discord", "<@" + discordId + ">", true)
        .setThumbnail(this.main.getDiscordManager().jda
            .getUserById(discordId).getAvatarUrl()).setFooter("ID " + discordId)
        .setColor(new Color(0x44474d));
    }
    
    // Accept
    private void handleAccepted(ButtonClickEvent event, Member newUser, String pseudo) {
        try {
            final String messageId = event.getMessage().getId();
            final String moderatorId = event.getMember().getId();
            final GuildManager gManager = this.main.getGuildManager();
    
            final String discordId = newUser.getId();
            final EmbedBuilder newMsgContent = this.getAcceptedEmbeded(pseudo, discordId);
            final String mc_uuid = MojanApi.getPlayerUUID(pseudo);

            if(mc_uuid == null) {
                event.reply("❌**Le UUID pour " + pseudo + " n'a pas pu être retrouver sur le serveur mojan...**")
                .setEphemeral(true).queue();
                return;
            }

            final User registeree = new User();
            registeree.setMcName(pseudo);
            registeree.setDiscordId(discordId);
            registeree.setMcUUID(mc_uuid);
            registeree.setCreatedAt(Helper.getTimestamp().toString());
            registeree.setAsAllowed(messageId, true, moderatorId);

            final UsersDao dao = this.main.getDaoManager().getUsersDao();
            final Integer userId = registeree.save(dao);

            if(userId < 0) {
                event.reply("❌**Le joueur n'a pas pu être enregistrer réessayez...**")
                .setEphemeral(true).queue();
                return;
            }

            event.getMessage().editMessage(newMsgContent.build())
            .setActionRow(net.dv8tion.jda.api.interactions.components.Button
            .primary(this.acceptId_conf, "✔️ Accepter par " + event.getMember().getEffectiveName()).asDisabled())
            .queue();

            final String newMsg = "**Nous te souhaitons bienvenue, <@" + discordId + "> Enjoy  ⛏🧱 !!!**";

            gManager.getWelcomeChannel().sendMessage(newMsg).queue();
            
            this.main.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
                channel.sendMessage(newMsg + 
                "\n**Vous avez `24H` pour vous connecter au serveur `Minecraft®` et ainsi `confirmer votre compte`.**")
                .queue();
            });

            event.reply("✔️ **Le joueur <@" + discordId + "> a bien été approuvé avec le pseudo: `" + pseudo + "`.**")
            .setEphemeral(true).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Reject
    private void handleRejected(ButtonClickEvent event, Member newUser, String pseudo) {
        try {
            final String discordId = newUser.getId();
            final EmbedBuilder newMsgContent = this.getRejectedEmbeded(pseudo ,discordId);

            event.getMessage().editMessage(newMsgContent.build())
            .setActionRow(net.dv8tion.jda.api.interactions.components.Button
            .primary(this.rejectId_conf, "❌ Refuser par " + event.getMember().getEffectiveName()).asDisabled())
            .queue();

            final String newMsg = "**❌ Votre enregistrement sur le serveur a été refusé.**";

            this.main.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
                channel.sendMessage(newMsg).queue();
            });

            event.reply("❌ **Le joueur <@" + discordId + "> a bien été refusé pour le pseudo: `" + pseudo + "`.**")
            .setEphemeral(true).queue();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}