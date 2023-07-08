package events.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

import locals.LocalManager;
import main.WhitelistDMC;

public class OnServerLoad implements Listener {
    private WhitelistDMC plugin;

    public OnServerLoad(WhitelistDMC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        LocalManager LOCAL = WhitelistDMC.LOCALES;
        StringBuilder sb = new StringBuilder();
        sb.append("**" + LOCAL.translate("SERVER_IS_UP") + "** ");
        sb.append(this.plugin.getBukkitManager().getServerInfoString(LOCAL.getNextLang()));
        
        this.plugin.getGuildManager().getAdminChannel()
            .sendMessage(sb.toString()).submit(true);
    }

}
