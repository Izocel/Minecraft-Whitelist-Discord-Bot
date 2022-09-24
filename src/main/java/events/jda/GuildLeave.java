package events.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import main.WhitelistJe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildLeave extends ListenerAdapter {
    private WhitelistJe main;
    public GuildLeave (WhitelistJe main) {
        this.main = main;
    }
    // private JDA jda;
    // private WhitelistManager whitelistManager;

    // @Override
    // public void onGuildMemberRemove(net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent event) {
    //     jda = main.getDiscordManager().jda;
    //     whitelistManager = main.getWhitelistManager();
    //     userinfo = main.getDatabaseManager().getUserinfo();
    //     final Connection connection;
    //     try {
    //         connection = userinfo.getConnection();
    //         final PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users WHERE discord = ?");
    //         pstmt.setString(1, event.getMember().getId());
    //         pstmt.executeQuery();
    //         final ResultSet resultset = pstmt.executeQuery();
    //         if(!resultset.next()) return;
    //         if(Bukkit.getPlayer(resultset.getString("users.name")) != null && Bukkit.getPlayer(resultset.getString("users.name")).isOnline()) {
    //             Bukkit.getScheduler().runTask(main, () -> {
    //                 try {
    //                     Bukkit.getPlayer(resultset.getString("users.name")).kickPlayer("§cVous avez été expulsé");
    //                 } catch (SQLException e) {
    //                     e.printStackTrace();
    //                 }
    //             });
    //         }
    //         final PreparedStatement preparedstatement2 = connection.prepareStatement("DELETE FROM users WHERE discord = " + resultset.getString("users.discord"));
    //         preparedstatement2.executeUpdate();
    //         if(whitelistManager.getPlayersAllowed().contains(resultset.getString("users.name"))) {
    //             whitelistManager.getPlayersAllowed().remove(resultset.getString("users.name"));
    //         }
    //         jda.getTextChannelById("1013374066540941362").sendMessage("Suppression - Le joueur `" + resultset.getString("users.name") + "` - <@" + event.getMember().getId() + "> a quitté le serveur discord").queue();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }
}
