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

    @Override
    public void onEnable() {
        this.instance = this;
        this.discordManager = new DiscordManager(this);
    }

    @Override
    public void onDisable() {
       discordManager.disconnect();
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }
}
