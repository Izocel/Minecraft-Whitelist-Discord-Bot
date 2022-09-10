package WhitelistJe;

import java.net.http.WebSocket.Listener;
import java.sql.ResultSet;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import dao.UsersDao;


public final class WhitelistJe extends JavaPlugin implements Listener {

    public WhitelistJe instance;
    private DiscordManager discordManager;

    public String figlet ="""
          
         __       __  __        __    __                __  __              __               _____           
        /  |  _  /  |/  |      /  |  /  |              /  |/  |            /  |             /     |          
        $$ | / \\ $$ |$$ |____  $$/  _$$ |_     ______  $$ |$$/   _______  _$$ |_            $$$$$ |  ______  
        $$ |/$  \\$$ |$$      \\ /  |/ $$   |   /      \\ $$ |/  | /       |/ $$   |  ______      $$ | /      \\ 
        $$ /$$$  $$ |$$$$$$$  |$$ |$$$$$$/   /$$$$$$  |$$ |$$ |/$$$$$$$/ $$$$$$/  /      |__   $$ |/$$$$$$  |
        $$ $$/$$ $$ |$$ |  $$ |$$ |  $$ | __ $$    $$ |$$ |$$ |$$      \\   $$ | __$$$$$$//  |  $$ |$$    $$ |
        $$$$/  $$$$ |$$ |  $$ |$$ |  $$ |/  |$$$$$$$$/ $$ |$$ | $$$$$$  |  $$ |/  |      $$ \\__$$ |$$$$$$$$/ 
        $$$/    $$$ |$$ |  $$ |$$ |  $$  $$/ $$       |$$ |$$ |/     $$/   $$  $$/       $$    $$/ $$       |
        $$/      $$/ $$/   $$/ $$/    $$$$/   $$$$$$$/ $$/ $$/ $$$$$$$/     $$$$/         $$$$$$/   $$$$$$$/ 
                                 ______     __   __   _____        ______   ______     ______       __     ______     ______     ______   ______    
                                /\\  == \\   /\\ \\ / /  /\\  __-.     /\\  == \\ /\\  == \\   /\\  __ \\     /\\ \\   /\\  ___\\   /\\  ___\\   /\\__  _\\ /\\  ___\\   
                                \\ \\  __<   \\ \\ \\'/   \\ \\ \\/\\ \\    \\ \\  _-/ \\ \\  __<   \\ \\ \\/\\ \\   _\\_\\ \\  \\ \\  __\\   \\ \\ \\____  \\/_/\\ \\/ \\ \\___  \\  
                                 \\ \\_\\ \\_\\  \\ \\__|    \\ \\____-     \\ \\_\\    \\ \\_\\ \\_\\  \\ \\_____\\ /\\_____\\  \\ \\_____\\  \\ \\_____\\    \\ \\_\\  \\/\\_____\\ 
                                  \\/_/ /_/   \\/_/      \\/____/      \\/_/     \\/_/ /_/   \\/_____/ \\/_____/   \\/_____/   \\/_____/     \\/_/   \\/_____/ 
    
    """;

    @Override
    public void onEnable() {
        this.instance = this;
        this.discordManager = new DiscordManager(this);

        
        // UsersDao dao = new UsersDao();
        // dao.find(1);

        Logger.getLogger("WhiteList-Je").info(this.figlet);
    }

    @Override
    public void onDisable() {
       this.discordManager.disconnect();
    }

    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }
}
