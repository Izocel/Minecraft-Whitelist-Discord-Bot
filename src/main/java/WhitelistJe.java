package WhitelistJe;

import java.net.http.WebSocket.Listener;
import java.sql.ResultSet;

import org.bukkit.plugin.java.JavaPlugin;

import dao.UsersDao;


public final class WhitelistJe extends JavaPlugin implements Listener {

    public WhitelistJe instance;
    private DiscordManager discordManager;

    @Override
    public void onEnable() {
        this.instance = this;
        this.discordManager = new DiscordManager(this);
        
        UsersDao dao = new UsersDao();
        dao.find(1);
    }

    @Override
    public void onDisable() {
       this.discordManager.disconnect();
    }

    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }
}
