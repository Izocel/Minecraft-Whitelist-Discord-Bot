package WhitelistJe.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import WhitelistJe.WhitelistJe;
import WhitelistJe.functions.RoleManager;
import WhitelistJe.functions.WhitelistManager;
import WhitelistJe.mysql.dbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DenyCommand extends ListenerAdapter {
    private WhitelistJe main;
    public DenyCommand(WhitelistJe main) {
        this.main = main;
    }
    private dbConnection userinfo;
    private RoleManager roleManager = new RoleManager();
    private JDA jda;
    private WhitelistManager whitelistManager;

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("deny")) {
            if(
                    !roleManager.hasRole(event.getMember(), "807839780309172255") ||
                    !roleManager.hasRole(event.getMember(), "809003930884505602") ||
                    !roleManager.hasRole(event.getMember(), "926270775298752512") ||
                    !roleManager.hasRole(event.getMember(), "783839953372053516")) {
                event.reply("Vous n'êtes pas autorisé à effectuer cette commmande !").setEphemeral(true).queue();
                return;
            }
            jda = main.getDiscordManager().jda;
            whitelistManager = main.getWhitelistManager();
            String player = event.getOption("mention").getAsString().replace("<@!", "").replace(">", "");
            System.out.println(player);
            userinfo = main.getDatabaseManager().getUserinfo();
            final Connection connection;
            try {
                connection = userinfo.getConnection();
                final PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users WHERE discord = ?");
                pstmt.setString(1, player);
                final ResultSet resultset = pstmt.executeQuery();
                
                if (!resultset.next()) {
                    event.reply("Joueur introuvable").setEphemeral(true).queue();
                    return;
                }
                event.reply("Vous venez de supprimer `" + resultset.getString("users.name") + "` (<@" + resultset.getString("users.discord") + ">) de la base de données !").setEphemeral(true).queue();
                if(Bukkit.getPlayer(resultset.getString("users.name")) != null && Bukkit.getPlayer(resultset.getString("users.name")).isOnline()) {
                    Bukkit.getScheduler().runTask(main, () -> {
                        try {
                            Bukkit.getPlayer(resultset.getString("users.name")).kickPlayer("§cVous avez été expulsé");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
                jda.getTextChannelById("1013374066540941362").sendMessage("Suppression - La demande de `" + resultset.getString("users.name") + "` (<@" + resultset.getString("users.discord") + ">) a été supprimée par <@" + event.getMember().getId() + ">").queue();
                if(whitelistManager.getPlayersAllowed().contains(resultset.getString("users.name"))) {
                    whitelistManager.getPlayersAllowed().remove(resultset.getString("users.name"));
                }
                event.getGuild().getMemberById(resultset.getString("users.discord")).modifyNickname(jda.getUserById(resultset.getString("users.discord")).getName()).queue();
                final PreparedStatement preparedstatement2 = connection.prepareStatement("DELETE FROM users WHERE discord = " + resultset.getString("users.discord"));
                
                preparedstatement2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

