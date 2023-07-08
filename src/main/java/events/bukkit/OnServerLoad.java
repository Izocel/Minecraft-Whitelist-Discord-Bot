package events.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

import locals.LocalManager;
import main.WhitelistDmc;

public class OnServerLoad implements Listener {
    private WhitelistDmc plugin;

    public OnServerLoad(WhitelistDmc plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        LocalManager LOCAL = WhitelistDmc.LOCALES;
        StringBuilder sb = new StringBuilder();
        sb.append("**" + LOCAL.translate("SERVER_IS_UP") + "** ");
        sb.append(this.plugin.getBukkitManager().getServerInfoString(LOCAL.getNextLang()));
        
        this.plugin.getGuildManager().getAdminChannel()
            .sendMessage(sb.toString()).submit(true);
    }

}
