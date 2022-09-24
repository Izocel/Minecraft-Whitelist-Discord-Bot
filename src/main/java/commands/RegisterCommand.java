package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bukkit.Bukkit;
import org.json.JSONArray;

import configs.ConfigManager;
import functions.GuildManager;
import helpers.Helper;
import main.WhitelistJe;
import models.User;

import java.awt.*;
import java.util.logging.Logger;

public class RegisterCommand extends ListenerAdapter {
    
    private JDA jda;
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
            final String errMsg = "Votre pseudo devrait comporter entre 3 et 16 caractères" +
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
            User foundWDiscord = this.main.getDaoManager().getUsersDao().findByDisccordTag(discordTag);
            User foundWPseudo = this.main.getDaoManager().getUsersDao().findByMcName(pseudo);

            try {
                this.logger.info(foundWDiscord.toJson().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                this.logger.info(foundWPseudo.toJson().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (foundWPseudo != null && !foundWPseudo.getDiscordTag().equals(discordTag)) {
                event.reply("*Ce pseudo est déjà enregistrer par un autre joueur*")
                        .setEphemeral(true).queue();
                return false;
            }

            else if (foundWDiscord != null && foundWDiscord.getId() > 0) {
                isAllowed = foundWDiscord.isAllowed();
                isConfirmed = foundWDiscord.isConfirmed();

                if(!isAllowed) {
                    event.reply("*Vous avez été refusé sur le serveur. Contactez un admin directement pour remédier à la situation...*").setEphemeral(true).queue();
                    return false;
                }

                if (isAllowed && isConfirmed) {
                    event.reply("*Vous êtes déjà accepté sur le serveur...*").setEphemeral(true).queue();
                    return false;
                }

                if (isAllowed && !isConfirmed) {
                    event.reply("*Une nouvelle demande de confirmation?*").setEphemeral(true).queue();
                    return false;
                }
            }

        } catch (Exception e) {
            event.reply("*Une erreur est survenu contactez un admin!!!*").setEphemeral(true).queue();
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private EmbedBuilder getRequestEmbeded(SlashCommandEvent event, String pseudo) {
        return new EmbedBuilder().setTitle("Un joueur veut s'enregister sur votre serveur Minecraft®")
                .addField("Pseudo", pseudo, true)
                .addField("Discord", "<@" + event.getMember().getId() + ">", true)
                .setThumbnail(event.getMember().getUser().getAvatarUrl())
                .setFooter("ID " + event.getMember().getId())
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

        //TODO: add user to DB as "not confirmed";

        EmbedBuilder embeded = this.getRequestEmbeded(event, pseudo);
        GuildManager gManager = this.main.getGuildManager();

        event.reply("*Votre demande d'accès pour `" + pseudo
                + "` a été envoyé aux modérateurs.*\n*Merci de patienter jusqu'à une prise de décision de leur part.*")
                .setEphemeral(true).queue();

        gManager.getWListChannel().sendMessage(embeded.build())
                .setActionRows(
                    ActionRow.of(Button.primary(this.acceptId, "✔️ Accepter"),
                    Button.secondary(this.rejectId, "❌ Refuser"))
                )
                .queue(message -> {
                    Member member = event.getMember();
                    User newUser = new User();
                    newUser.setMcName(pseudo);
                    newUser.setDiscordTag(discordTag);
                    newUser.setCreatedAt(Helper.getTimestamp().toString());
                    newUser.setAsAllowed(message.getId(), true, 1);
                    
                    try {// Can't modify a member with higher or equal highest role than yourself!
                        member.modifyNickname(pseudo).queue();
                    } catch (Exception e) {
                        this.logger.warning(e.getMessage());
                    }
                });
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        Member respMemberId = event.getMember();
        String componentId = event.getComponentId();
        String msgId = event.getComponentId();
        String pseudo = event.getComponentId();
        GuildManager gManager = this.main.getGuildManager();

        if (!event.getChannel().getId().equals(gManager.whitelistChannelId)) {
            return;
        }

        final boolean isAuthorized = gManager.isOwner(respMemberId.getId())
                || gManager.isAdmin(respMemberId.getId())
                || gManager.isModo(respMemberId.getId())
                || gManager.isDev(respMemberId.getId());

        if (!isAuthorized) {
            event.reply("Dommage vous n'avez pas les accès...¯\\_(ツ)_/¯")
                    .setEphemeral(true).queue();
            return;
        }

        if (componentId.equals(this.acceptId)) {
            //this.handleAccepted(event, respMemberId, pseudo);

        } else if (componentId.equals(this.rejectId)) {
            //this.handleRejected(event);

        } else {
            this.logger.warning("Commande inconnu venant d'un boutton." + 
            "\nChannel name:" + event.getChannel().getName() +
            "\nMessage id: " + event.getMessage().getId());
        }

    }

    private EmbedBuilder getAcceptedEmbeded(String pseudo, String discordId) {
        return new EmbedBuilder()
        .setTitle("Demande acceptée")
        .addField("Pseudo", pseudo, true)
        .addField("Discord", "<@" + discordId + ">", true)
        .setThumbnail(jda.getUserById(discordId).getAvatarUrl()).setFooter("ID " + discordId)
        .setColor(new Color(0x484d95));
    }

    private EmbedBuilder getRejectedEmbeded(String pseudo, String discordId) {
        return new EmbedBuilder().setTitle("Demande refusée")
        .addField("Pseudo", pseudo, true)
        .addField("Discord", "<@" + discordId + ">", true)
        .setThumbnail(jda.getUserById(discordId).getAvatarUrl()).setFooter("ID " + discordId)
        .setColor(new Color(0x44474d));
    }
    
//     // Accept
//     private void handleAccepted(ButtonClickEvent event, Member newUser, String pseudo) {
//         String messageId = event.getMessage().getId();
//         GuildManager gManager = this.main.getGuildManager();

//         final String discordTag = pseudo;
//         EmbedBuilder newMsgContent = this.getAcceptedEmbeded(pseudo ,discordTag);

//         event.getMessage().editMessage(newMsgContent.build())
//         .setActionRow(net.dv8tion.jda.api.interactions.components.Button
//         .primary(this.acceptId_conf, "✔️ Accepter par " + event.getMember().getNickname()).asDisabled())
//         .queue();

//         jda.openPrivateChannelById(discordTag).queue(channel -> {
//             channel.sendMessage("**Bienvenue sur le serveur, <@" + discord + "> !**")
//             .queue(null, new ErrorHandler().handle(ErrorResponse.CANNOT_SEND_TO_USER, (ex) -> {
                
//             jda.getTextChannelById("770148932075782176")
//             .sendMessage("**Bienvenue sur le serveur, <@" + discord + "> !**").queue();
//         }));
//         });

//         final PreparedStatement preparedstatement2 = connection
//         .prepareStatement("UPDATE users SET checked = " + 1 + " WHERE discord = "
//         + resultset.getString("users.discord"));
//         preparedstatement2.executeUpdate();

//         UUID pUUID;
//         if(this.main.playerIsAllowed(pUUID)) {
//             event.reply("Le joueur " + name + " a bien été enregistrer").setEphemeral(true).queue();
//         }
        
//     }

//     // Reject
//     private void handleRejected(ButtonClickEvent event) {
//         final Connection connection;
//         connection = userinfo.getConnection();
//         final PreparedStatement pstmt = connection
//         .prepareStatement("SELECT * FROM users WHERE messageid = ?");
//         pstmt.setString(1, message);
//         pstmt.executeQuery();
//         final ResultSet resultset = pstmt.executeQuery();
        
//         if (!resultset.next()) {
//         return;
//         }

//         jda.getTextChannelById("1013374066540941362").editMessageById(message, builder.build())
//         .setActionRow(Button.secondary(this.rejectId_conf, "Refusé par " + event.getMember().getNickname())
//         .asDisabled())
//         .queue();

//         if (whitelistManager.getPlayersAllowed().contains(name)) {
//             whitelistManager.getPlayersAllowed().remove(name);
//         }

//         if (Bukkit.getPlayer(name) != null && Bukkit.getPlayer(name).isOnline()) {
//             Bukkit.getScheduler().runTask(main, () -> {
//             Bukkit.getPlayer(name).kickPlayer("§cVous avez été expulsé");
//         });

        
//         event.getGuild().getMemberById(discord).modifyNickname(jda.getUserById(discord).getName()).queue();
//         final PreparedStatement preparedstatement2 = connection
//         .prepareStatement("DELETE FROM users WHERE discord = " + discord);
//         preparedstatement2.executeUpdate();
//         event.reply("Le joueur " + name + " a bien été refusé").setEphemeral(true).queue();
//     }
    
    
// }


}