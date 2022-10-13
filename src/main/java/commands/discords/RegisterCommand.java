package commands.discords;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import services.api.McHeadsApi;
import services.api.MojanApi;
import services.api.PlayerDbApi;
import dao.DaoManager;
import dao.UsersDao;
import functions.GuildManager;
import helpers.Helper;
import services.sentry.SentryService;
import main.WhitelistJe;
import models.BedrockData;
import models.JavaData;
import models.User;

import java.awt.*;
import java.util.logging.Logger;

public class RegisterCommand extends ListenerAdapter {
    private Logger logger;
    private WhitelistJe plugin;
    private String acceptId = "acceptAction";
    private String rejectId = "rejectAction";
    private String acceptId_conf = "acceptConfAction";
    private String rejectId_conf = "rejectConfAction";

    public RegisterCommand(WhitelistJe plugin) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        try {
            if (!event.getChannel().getType().toString().equals("TEXT")) {
                return;
            }
    
            final String cmdName = this.plugin.getConfigManager().get("registerCmdName", "register");
            if (!event.getName().equals(cmdName))
                return;
            
            final OptionMapping javaOpt = event.getOption("pseudo-java");
            final OptionMapping bedOpt = event.getOption("pseudo-bedrock");

            if(javaOpt == null && bedOpt == null) {
                event.reply("❌**Vous devez fournir au moins un pseudo pour utiliser cette commande...**")
                .setEphemeral(true).queue();
                return;
            }

            final String pseudoJava = javaOpt != null  ? javaOpt.getAsString() : null;
            final String pseudoBedrock = bedOpt != null ? bedOpt.getAsString() : null;
            if(pseudoJava == null && pseudoBedrock == null) {
                event.reply("❌**Vos UUIDs n'ont pas pu être retrouvés dans la commande...**")
                .setEphemeral(true).queue();
                return;
            }
            
            boolean javaValid = pseudoJava != null && this.validatePseudo(event, pseudoJava);
            boolean bedrockValid = pseudoBedrock != null && this.validatePseudo(event, pseudoBedrock);;

            if(!javaValid && !bedrockValid) {
                return;
            }
            
            final String javaUuid = PlayerDbApi.getMinecraftUUID(pseudoJava);
            final String bedrockUuid = PlayerDbApi.getXboxUUID(pseudoBedrock);
            if(javaUuid == null && bedrockUuid == null) {
                event.reply("❌**Vos UUIDs n'ont pas pu être retrouvés sur les serveurs...**")
                .setEphemeral(true).queue();
                return;
            }

            final String discordId = event.getMember().getId();
            final User user = User.updateFromMember(event.getMember());
            final String confirmHoursString = plugin.getConfigManager().get("hoursToConfirmMcAccount");
            final Integer hoursToConfirm = confirmHoursString != null
                ? Integer.parseInt(confirmHoursString)
                : null;

            if(javaUuid != null) {

                boolean isAllowed = false;
                boolean isConfirmed = false;

                final JavaData found = DaoManager.getJavaDataDao().findWithUuid(javaUuid);
    
                if (found != null && !found.getUserId().equals(user.getId())) {
                    event.reply("❌ **Ce pseudo Java est déjà enregistrer par un autre joueur**")
                            .setEphemeral(true).queue();
                }
    
                else {
                    isAllowed = found.isAllowed();
                    isConfirmed = found.isConfirmed();
    
                    if(!isAllowed) {
                        event.reply("❌ **Ce compte Java n'a pas encore été accepté sur le serveur.**\n" +
                        "Pour en s'avoir d'avantage, contactez un administrateur directement...")
                        .setEphemeral(true).queue();
                    }
    
                    else if (isAllowed && isConfirmed) {
                        event.reply("**Votre compte Java est déjà accepté sur le serveur...**" + 
                        "Il suffit de vous connecter. `Enjoy` ⛏🧱").setEphemeral(true).queue();
                    }
                    
                    else if (isAllowed && !isConfirmed && hoursToConfirm > 0) {
                        String msg = "**Une confirmation de votre compte Java est nécéssaire.**\n" +
                        "Pour confimer votre compte vous aviez `"+ hoursToConfirm +"h` depuis l'aprobation pour vous connecter au server Mincecraft®\n";
    
                        event.reply(msg).setEphemeral(true).queue();
                    }

                    else {
                        EmbedBuilder embeded = this.getRequestEmbeded(event, pseudoBedrock);
                        GuildManager gManager = this.plugin.getGuildManager();
                
                        event.reply("**Votre demande d'accès `Java` pour `" + pseudoBedrock
                                + "` a été envoyé aux modérateurs.**\n**Merci de patienter jusqu'à une prise de décision de leur part.**")
                                .setEphemeral(true).queue();
                        
                                gManager.getAdminChannel().sendMessageEmbeds(embeded.build())
                                .setActionRows(ActionRow.of(
                                        Button.primary(this.acceptId + " " + pseudoBedrock + " " + 
                                            discordId + " " + javaUuid, "✔️ Accepter"),
                                        Button.secondary(this.rejectId + " " + pseudoBedrock + " " + 
                                            discordId + " " + javaUuid, "❌ Refuser"))
                                ).queue();
                    }

                }

            }

            if(bedrockUuid != null) {

                boolean isAllowed = false;
                boolean isConfirmed = false;

                final BedrockData found = DaoManager.getBedrockDataDao().findWithUuid(bedrockUuid);
    
                if (found != null && !found.getUserId().equals(user.getId())) {
                    event.reply("❌ **Ce pseudo Bedrock est déjà enregistrer par un autre joueur**")
                            .setEphemeral(true).queue();
                }
    
                else {
                    isAllowed = found.isAllowed();
                    isConfirmed = found.isConfirmed();
    
                    if(!isAllowed) {
                        event.reply("❌ **Ce compte Bedrock n'a pas encore été accepté sur le serveur.**\n" +
                        "Pour en s'avoir d'avantage, contactez un administrateur directement...")
                        .setEphemeral(true).queue();
                    }
    
                    else if (isAllowed && isConfirmed) {
                        event.reply("**Votre compte Bedrock est déjà accepté sur le serveur...**" + 
                        "Il suffit de vous connecter. `Enjoy` ⛏🧱").setEphemeral(true).queue();
                    }
                    
                    else if (isAllowed && !isConfirmed && hoursToConfirm > 0) {
                        String msg = "**Une confirmation de votre compte Bedrock est nécéssaire.**\n" +
                        "Pour confimer votre compte vous aviez `"+ hoursToConfirm +"h` depuis l'aprobation pour vous connecter au server Mincecraft®\n";
    
                        event.reply(msg).setEphemeral(true).queue();
                    }

                    else {
                        EmbedBuilder embeded = this.getRequestEmbeded(event, pseudoBedrock);
                        GuildManager gManager = this.plugin.getGuildManager();
                
                        event.reply("**Votre demande d'accès `Bedrock` pour `" + pseudoBedrock
                                + "` a été envoyé aux modérateurs.**\n**Merci de patienter jusqu'à une prise de décision de leur part.**")
                                .setEphemeral(true).queue();
                        
                        gManager.getAdminChannel().sendMessageEmbeds(embeded.build())
                            .setActionRows(ActionRow.of(
                                    Button.primary(this.acceptId + " " + pseudoBedrock + " " + 
                                        discordId + " " + bedrockUuid, "✔️ Accepter"),
                                    Button.secondary(this.rejectId + " " + pseudoBedrock + " " + 
                                        discordId + " " + bedrockUuid, "❌ Refuser"))
                            ).queue();
                    }

                }
            }
    

        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        
    }

    private boolean validatePseudo(SlashCommandEvent event, String pseudo) {
        if (!Helper.isMcPseudo(pseudo)) {
            final String errMsg = "❌ Votre pseudo: `" + pseudo + "` devrait comporter entre 3 et 16 caractères" +
                    "\n\n et ne doit pas comporter de caractères spéciaux à part des underscores `_` ou tirets `-`";

            event.reply(errMsg).setEphemeral(true).queue();
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
        try {
            final GuildManager gManager = this.plugin.getGuildManager();
            if (!event.getChannel().getId().equals(gManager.adminChannelId)
                && !event.getChannel().getId().equals(gManager.whitelistChannelId)) {
                return;
            }
    
            final Member respMember = event.getMember();    
            final boolean isAuthorized = gManager.isOwner(respMember.getId())
                    || gManager.isAdmin(respMember.getId())
                    || gManager.isModo(respMember.getId())
                    || gManager.isDev(respMember.getId());
    
            if (!isAuthorized) {
                this.logger.warning("Commande répondu pas un role non authorisé." + 
                "\nUser name: <@" + respMember + ">" +
                "\nChannel name:" + event.getChannel().getName() +
                "\nMessage id: " + event.getMessage().getId());
                event.reply("Dommage vous n'avez pas les accès...¯\\_(ツ)_/¯")
                        .setEphemeral(true).queue();
                return;
            }

            final String componentId = event.getComponentId();
    
            final String actionId = componentId.split(" ")[0];
            final String pseudo = componentId.split(" ")[1];
            final String discordId = componentId.split(" ")[2];
            final String uuid = componentId.split(" ")[3];
            Member newuser = event.getGuild().getMemberById(discordId);
    
            if (actionId.equals(this.acceptId)) {
                this.handleAccepted(event, newuser, pseudo, uuid);
    
            } else if (actionId.equals(this.rejectId)) {
                this.handleRejected(event, newuser, pseudo);
            }
            
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    private EmbedBuilder getAcceptedEmbeded(String pseudo, String discordId) {
        return new EmbedBuilder()
        .setTitle("Demande acceptée")
        .addField("Pseudo", pseudo, true)
        .addField("Discord", "<@" + discordId + ">", true)
        .setThumbnail(this.plugin.getDiscordManager().jda
            .getUserById(discordId).getAvatarUrl()).setFooter("ID " + discordId)
        .setColor(new Color(0x484d95));
    }

    private EmbedBuilder getRejectedEmbeded(String pseudo, String discordId) {
        return new EmbedBuilder().setTitle("Demande refusée")
        .addField("Pseudo", pseudo, true)
        .addField("Discord", "<@" + discordId + ">", true)
        .setThumbnail(this.plugin.getDiscordManager().jda
            .getUserById(discordId).getAvatarUrl()).setFooter("ID " + discordId)
        .setColor(new Color(0x44474d));
    }
    
    // Accept
    private void handleAccepted(ButtonClickEvent event, Member newUser, String pseudo, String uuid) {
        try {
            final String messageId = event.getMessage().getId();
            final String moderatorId = event.getMember().getId();
            final GuildManager gManager = this.plugin.getGuildManager();
    
            final String discordId = newUser.getId();
            final EmbedBuilder newMsgContent = this.getAcceptedEmbeded(pseudo, discordId);

            final String confirmHoursString = plugin.getConfigManager().get("hoursToConfirmMcAccount");
            final Integer hoursToConfirm = confirmHoursString != null
                ? Integer.parseInt(confirmHoursString)
                : null;

            final boolean confirmed = hoursToConfirm == null || hoursToConfirm < 1;
            final boolean ok = plugin.getBukkitManager().setPlayerAsAllowed(messageId, true, moderatorId, uuid, confirmed, pseudo);

            if(!ok) {
                event.reply("❌**Le joueur n'a pas pu être enregistrer réessayez...**")
                .setEphemeral(true).queue();
                return;
            }

            event.getMessage().editMessageEmbeds(newMsgContent.build())
            .setActionRow(net.dv8tion.jda.api.interactions.components.Button
            .primary(this.acceptId_conf, "✔️ Accepter par " + event.getMember().getEffectiveName()).asDisabled())
            .queue();

            final String newMsg = "**Nous te souhaitons bienvenue, <@" + discordId + "> :: `"+ pseudo +"` Enjoy  ⛏🧱 !!!**";
            gManager.getWelcomeChannel().sendMessage(newMsg).queue();

            this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
                String msg = newMsg;
                if(hoursToConfirm > 0 )  {
                    msg = newMsg + "\n**Vous avez `"+ hoursToConfirm +"h` pour vous connecter au serveur `Minecraft®` et ainsi `confirmer votre compte`.**";
                }
                channel.sendMessage(msg).queue();
            });

            event.reply("✔️ **Le joueur <@" + discordId + "> a bien été approuvé avec le pseudo: `" + pseudo + "`.**")
            .setEphemeral(true).queue();
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    // Reject
    private void handleRejected(ButtonClickEvent event, Member newUser, String pseudo) {
        try {
            final String discordId = newUser.getId();
            final EmbedBuilder newMsgContent = this.getRejectedEmbeded(pseudo ,discordId);

            event.getMessage().editMessageEmbeds(newMsgContent.build())
            .setActionRow(net.dv8tion.jda.api.interactions.components.Button
            .primary(this.rejectId_conf, "❌ Refuser par " + event.getMember().getEffectiveName()).asDisabled())
            .queue();

            final String newMsg = "**❌ Votre enregistrement sur le serveur a été refusé.**";

            this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
                channel.sendMessage(newMsg).queue();
            });

            event.reply("❌ **Le joueur <@" + discordId + "> a bien été refusé pour le pseudo: `" + pseudo + "`.**")
            .setEphemeral(true).queue();

        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

}