package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
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
        this.logger = Logger.getLogger("WJE" + this.getClass().getName());
        this.main = main;
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

    private boolean handleKnownUser(SlashCommandEvent event, String pseudo, String discordTag) {
        try {
            boolean valid = this.validatePseudo(event, pseudo);
            if (!valid) {
                return false;
            }

            boolean isAllowed = false;
            boolean isConfirmed = false;
            User foundWPseudo = this.main.getDaoManager().getUsersDao().findByMcName(pseudo);

            if (foundWPseudo == null) {
                return true;
            }

            User foundWDiscord = this.main.getDaoManager().getUsersDao().findByDisccordTag(discordTag);

            if (foundWPseudo != null && !foundWPseudo.getDiscordTag().equals(discordTag)) {
                event.reply("❌ **Ce pseudo est déjà enregistrer par un autre joueur**")
                        .setEphemeral(true).queue();
                return false;
            }

            else if (foundWDiscord != null && foundWDiscord.getId() > 0) {
                isAllowed = foundWDiscord.isAllowed();
                isConfirmed = foundWDiscord.isConfirmed();

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

                    //TODO: doUpdate if delay > 24h
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
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("register"))
            return;

        final String pseudo = event.getOption("pseudo").getAsString();
        final String discordTag = event.getMember().getUser().getAsTag();
        boolean parseAsNew = this.handleKnownUser(event, pseudo, discordTag);

        if (!parseAsNew) {
            return;
        }

        EmbedBuilder embeded = this.getRequestEmbeded(event, pseudo);
        GuildManager gManager = this.main.getGuildManager();

        event.reply("**Votre demande d'accès pour `" + pseudo
                + "` a été envoyé aux modérateurs.**\n**Merci de patienter jusqu'à une prise de décision de leur part.**")
                .setEphemeral(true).queue();
        
        gManager.getAdminChannel().sendMessage(embeded.build())
            .setActionRows(
                ActionRow.of(Button.primary(this.acceptId + " " + pseudo, "✔️ Accepter"),
                Button.secondary(this.rejectId + " " + pseudo, "❌ Refuser"))
            ).queue();
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        final Member respMember = event.getMember();
        final String componentId = event.getComponentId();
        final GuildManager gManager = this.main.getGuildManager();

        final String actionId = componentId.split(" ")[0];
        final String pseudo = componentId.split(" ")[1];

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
            this.handleAccepted(event, respMember, pseudo);

        } else if (actionId.equals(this.rejectId)) {
            //this.handleRejected(event);

        } else {
            this.logger.warning("Commande non reconnue venant d'un boutton." + 
            "\nUser name: <@" + respMember.getId() + ">" +
            "\nChannel name:" + event.getChannel().getName() +
            "\nMessage id: " + event.getMessage().getId());
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
            final String moderatorTag = event.getMember().getUser().getAsTag();
            final GuildManager gManager = this.main.getGuildManager();
    
            final String discordTag = newUser.getUser().getAsTag();
            final String discordId = newUser.getId();
            final EmbedBuilder newMsgContent = this.getAcceptedEmbeded(pseudo ,discordId);
            final Member member = event.getMember();

            final User registeree = new User();
            registeree.setMcName(pseudo);
            registeree.setDiscordTag(discordTag);
            registeree.setCreatedAt(Helper.getTimestamp().toString());
            registeree.setAsAllowed(messageId, true, moderatorTag);

            final UsersDao dao = this.main.getDaoManager().getUsersDao();
            final Integer userId = registeree.save(dao);

            if(userId < 0) {
                event.reply("❌**Le joueur n'a pas pu être enregistrer réessayez...**")
                .setEphemeral(true).queue();
                return;
            }

            //TODO: Set Bukkit whitelisted player................

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

            try {// Can't modify a member with higher or equal highest role than yourself!
                member.modifyNickname(pseudo).queue();
            } catch (Exception e) {
                this.logger.warning(e.getMessage());
            }

            event.reply("✔️ **Le joueur <@" + discordId + "> a bien été approuvé avec le pseudo: `" + pseudo + "`.**")
            .setEphemeral(true).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // // Reject
    // private void handleRejected(ButtonClickEvent event) {
    //     final Connection connection;
    //     connection = userinfo.getConnection();
    //     final PreparedStatement pstmt = connection
    //     .prepareStatement("SELECT ** FROM users WHERE BINARY messageid = ?");
    //     pstmt.setString(1, message);
    //     pstmt.executeQuery();
    //     final ResultSet resultset = pstmt.executeQuery();
        
    //     if (!resultset.next()) {
    //     return;
    //     }

    //     jda.getTextChannelById("1013374066540941362").editMessageById(message, builder.build())
    //     .setActionRow(Button.secondary(this.rejectId_conf, "Refusé par " + event.getMember().getNickname())
    //     .asDisabled())
    //     .queue();

    //     if (whitelistManager.getPlayersAllowed().contains(name)) {
    //         whitelistManager.getPlayersAllowed().remove(name);
    //     }

    //     if (Bukkit.getPlayer(name) != null && Bukkit.getPlayer(name).isOnline()) {
    //         Bukkit.getScheduler().runTask(main, () -> {
    //         Bukkit.getPlayer(name).kickPlayer("§cVous avez été expulsé");
    //     });

        
    //     event.getGuild().getMemberById(discord).modifyNickname(jda.getUserById(discord).getName()).queue();
    //     final PreparedStatement preparedstatement2 = connection
    //     .prepareStatement("DELETE FROM users WHERE BINARY discord = " + discord);
    //     preparedstatement2.executeUpdate();
    //     event.reply("Le joueur " + name + " a bien été refusé").setEphemeral(true).queue();
    // }
    
    
// }


}