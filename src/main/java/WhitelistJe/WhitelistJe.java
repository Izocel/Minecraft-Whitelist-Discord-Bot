package WhitelistJe;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import WhitelistJe.functions.WhitelistManager;
import WhitelistJe.mysql.DatabaseManager;
import WhitelistJe.mysql.dbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class WhitelistJe extends JavaPlugin implements Listener {

    public WhitelistJe instance;
    private DiscordManager discordManager;
    private DatabaseManager databaseManager;
    private dbConnection userinfo;
    private WhitelistManager whitelistManager;

    @Override
    public void onEnable() {
        this.instance = this;
        this.discordManager = new DiscordManager(this);
        this.databaseManager = new DatabaseManager();
        this.whitelistManager = new WhitelistManager(this);
        discordManager.connect();

        userinfo = this.getDatabaseManager().getUserinfo();
        final Connection connection;
        try {
            connection = userinfo.getConnection();
            final PreparedStatement preparedstatement = connection.prepareStatement("SELECT * FROM users WHERE checked = 1");
            preparedstatement.executeQuery();
            final ResultSet resultset = preparedstatement.executeQuery();
            while (resultset.next()) {
                whitelistManager.getPlayersAllowed().add(resultset.getString("users.name"));
                System.out.println("Le joueur " + resultset.getString("users.name") + " a été mis sur la whitelist");
            }
        } catch (SQLException e) {
            e.printStackTrace();;
        }
    }

    @Override
    public void onDisable() {
       discordManager.disconnect();
        this.databaseManager.close();
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    public DiscordManager getDiscordManager() {
        return discordManager;
    }
    public WhitelistManager getWhitelistManager() {
        return whitelistManager;
    }
}
