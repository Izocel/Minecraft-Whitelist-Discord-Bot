package events.bukkit;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

import main.WhitelistJe;

public class OnServerLoad implements Listener {
    private WhitelistJe main;
    private Logger logger;

    public OnServerLoad(WhitelistJe main) {
        this.logger = Logger.getLogger("WJE" + this.getClass().getName());
        this.main = main;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Le serveur est up and running boyyssss!** ");
        sb.append(this.main.gtBukkitManager().getServerInfoString());
        
        this.main.getGuildManager().getAdminChannel()
            .sendMessage(sb.toString()).queue();
    }

}
