package ayoub.whitelistje.commands;

import ayoub.whitelistje.WhitelistJe;
import ayoub.whitelistje.functions.Alphanumeric;
import ayoub.whitelistje.functions.RoleManager;
import ayoub.whitelistje.functions.WhitelistManager;
import ayoub.whitelistje.mysql.dbConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bukkit.Bukkit;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class RequestCommand extends ListenerAdapter {
    private JDA jda;
    private WhitelistJe main;
    private dbConnection userinfo;
    private WhitelistManager whitelistManager;
    private RoleManager roleManager = new RoleManager();
    private Alphanumeric alphanumeric = new Alphanumeric();

    private HashMap<String, String> roles = new HashMap<>();
    hashmap.put("Owner", "807839780309172255");
    hashmap.put("Admins ", "809003930884505602");
    hashmap.put("Host & Développeur-Je", "926270775298752512");
    hashmap.put("Modérateurs ", "783839953372053516 ");


    private HashMap<String, String> allowedRoles = roles;

    private List<Role> getuserRole(Interaction event)  {
        return event.getMember().getRoles();
    }

    private boolean userIsValid(String userRole, HashMap<String, String> validRoles) {

        

        return true;
    }

    private boolean handleRoleValidation() {
        return false;
    }

    public RequestCommand(WhitelistJe main) {
        this.main = main;
    }
    
    public String requestMsg(String message) {
        return "**Demande d'accès**\n\n" + message;
    }

    @Override public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("request")) {

            String pseudo = event.getOption("pseudo").getAsString();
            if (pseudo.length() <= 2 || pseudo.length() > 16) {
                event.reply(requestMsg("Ce pseudo doit comporter entre 3 et 16 caractères")).setEphemeral(true).queue();
                return;
            }
            if (!alphanumeric.isAlphanumeric(pseudo)) {
                event.reply(requestMsg("Ce pseudo ne doit pas comporter de caractères spéciaux à part les underscores _ et tirets -")).setEphemeral(true).queue();
                return;
            }
            jda = main.getDiscordManager().jda;
            userinfo = main.getDatabaseManager().getUserinfo();
            final Connection connection;
            try {
                connection = userinfo.getConnection();
                PreparedStatement preparedstatement;
                preparedstatement = connection.prepareStatement("SELECT * FROM users");
                preparedstatement.executeQuery();
                final ResultSet resultset = preparedstatement.executeQuery();
                while (resultset.next()) {
                    final String discord = resultset.getString("users.discord");
                    final String name = resultset.getString("users.name");
                    if (name.equalsIgnoreCase(pseudo) && discord.equalsIgnoreCase(event.getMember().getId())) {
                        event.reply(requestMsg("*Vous êtes déjà identifié sur le serveur avec ce pseudo*")).setEphemeral(true).queue();
                        return;
                    }
                    if (event.getMember().getId().equals(discord)) {
                        event.reply(requestMsg("*Vous êtes déjà identifié sur le serveur sous un autre pseudo*")).setEphemeral(true).queue();
                        return;
                    }
                    if (name.equalsIgnoreCase(pseudo)) {
                        event.reply(requestMsg("*Ce pseudo est déjà pris par un autre joueur*")).setEphemeral(true).queue();
                        return;
                    }
                }
                EmbedBuilder builder = new EmbedBuilder().setTitle("Une demande a été transmise").addField("Pseudo", pseudo, true).addField("Discord", "<@" + event.getMember().getId() + ">", true).setThumbnail(event.getMember().getUser().getAvatarUrl()).setFooter("ID " + event.getMember().getId()).setColor(new Color(0x9b7676));
                event.reply(requestMsg("*Votre demande d'accès pour `" + pseudo + "` a été envoyé aux modérateurs.*\n*Merci de patienter jusqu'à une prise de décision de leur part.*")).setEphemeral(true).queue();
                jda.getTextChannelById("1013374066540941362").sendMessage(builder.build()).setActionRows(ActionRow.of(net.dv8tion.jda.api.interactions.components.Button.primary("yes", "Accepter"), net.dv8tion.jda.api.interactions.components.Button.secondary("no", "Refuser"))).queue(message -> {
                    final PreparedStatement preparedstatement2;
                    try {
                        preparedstatement2 = connection.prepareStatement("INSERT INTO users (name, discord, messageid)" + "VALUES (?, ?, ?)");
                        preparedstatement2.setString(1, pseudo);
                        preparedstatement2.setString(2, event.getMember().getId());
                        preparedstatement2.setString(3, message.getId());
                        preparedstatement2.executeUpdate();
                        event.getGuild().getMemberById(event.getMember().getId()).modifyNickname(pseudo).queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                //jda.getTextChannelById("990582849281355796").sendMessage("Demande - Une demande d'accès a été envoyé par `" + pseudo + "` - <@" + event.getMember().getId() + ">").queue();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {

        if (event.getChannel().getId().equals("1013374066540941362")) {

            if(!roleManager.hasRole(event.getMember(), "807839780309172255") ||
                    !roleManager.hasRole(event.getMember(), "809003930884505602") ||
                    !roleManager.hasRole(event.getMember(), "926270775298752512") ||
                    !roleManager.hasRole(event.getMember(), "783839953372053516")) {
                event.reply("Dommage... ¯\\_(ツ)_/¯").setEphemeral(true).queue();
                return;
            }

            String message = event.getMessage().getId();
            jda = main.getDiscordManager().jda;
            userinfo = main.getDatabaseManager().getUserinfo();
            whitelistManager = main.getWhitelistManager();
            if (event.getButton().getLabel().equals("Accepter")) {
                try {
                    final Connection connection;
                    connection = userinfo.getConnection();
                    final PreparedStatement preparedstatement = connection.prepareStatement("SELECT * FROM users WHERE messageid = ?");
                    preparedstatement.setString(1, message);
                    preparedstatement.executeQuery();
                    final ResultSet resultset = preparedstatement.executeQuery();
                    if (!resultset.next()) {
                        return;
                    }
                    String name = resultset.getString("users.name");
                    String discord = resultset.getString("users.discord");
                    EmbedBuilder builder = new EmbedBuilder().setTitle("Demande acceptée").addField("Pseudo", name, true).addField("Discord", "<@" + discord + ">", true).setThumbnail(jda.getUserById(discord).getAvatarUrl()).setFooter("ID " + discord).setColor(new Color(0x484d95));
                    jda.getTextChannelById("1013374066540941362").editMessageById(message, builder.build()).setActionRow(net.dv8tion.jda.api.interactions.components.Button.primary("valide", "Whitelist par " + event.getMember().getNickname()).asDisabled()).queue();
                    jda.openPrivateChannelById(discord).queue(channel -> {
                        channel.sendMessage("**Bienvenue sur le serveur, <@" + discord + "> !**").queue(null, new ErrorHandler().handle(ErrorResponse.CANNOT_SEND_TO_USER, (ex) -> {
                            jda.getTextChannelById("770148932075782176").sendMessage("**Bienvenue sur le serveur, <@" + discord + "> !**").queue();
                        }));
                    });
                    final PreparedStatement preparedstatement2 = connection.prepareStatement("UPDATE users SET checked = " + 1 + " WHERE discord = " + resultset.getString("users.discord"));
                    preparedstatement2.executeUpdate();
                    if (!whitelistManager.getPlayersAllowed().contains(resultset.getString("users.name"))) {
                        whitelistManager.getPlayersAllowed().add(resultset.getString("users.name"));
                    }
                    event.reply("Le joueur " + name + " a bien été whitelist").setEphemeral(true).queue();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (event.getButton().getLabel().equals("Refuser")) {
                try {
                    final Connection connection;
                    connection = userinfo.getConnection();
                    final PreparedStatement preparedstatement = connection.prepareStatement("SELECT * FROM users WHERE messageid = ?");
                    preparedstatement.setString(1, message);
                    preparedstatement.executeQuery();
                    final ResultSet resultset = preparedstatement.executeQuery();
                    if (!resultset.next()) {
                        return;
                    }
                    String name = resultset.getString("users.name");
                    String discord = resultset.getString("users.discord");
                    EmbedBuilder builder = new EmbedBuilder().setTitle("Demande refusée").addField("Pseudo", name, true).addField("Discord", "<@" + discord + ">", true).setThumbnail(jda.getUserById(discord).getAvatarUrl()).setFooter("ID " + discord).setColor(new Color(0x44474d));
                    jda.getTextChannelById("1013374066540941362").editMessageById(message, builder.build()).setActionRow(Button.secondary("unvaldie", "Refusé par " + event.getMember().getNickname()).asDisabled()).queue();
                    if (whitelistManager.getPlayersAllowed().contains(name)) {
                        whitelistManager.getPlayersAllowed().remove(name);
                    }
                    if (Bukkit.getPlayer(name) != null && Bukkit.getPlayer(name).isOnline()) {
                        Bukkit.getScheduler().runTask(main, () -> {
                            Bukkit.getPlayer(name).kickPlayer("§cVous avez été expulsé");
                        });
                    }
                    event.getGuild().getMemberById(discord).modifyNickname(jda.getUserById(discord).getName()).queue();
                    final PreparedStatement preparedstatement2 = connection.prepareStatement("DELETE FROM users WHERE discord = " + discord);
                    preparedstatement2.executeUpdate();
                    event.reply("Le joueur " + name + " a bien été refusé").setEphemeral(true).queue();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
