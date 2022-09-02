package ayoub.whitelistje.events.bukkit;

import ayoub.whitelistje.WhitelistJe;
import ayoub.whitelistje.functions.WhitelistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoin {
    private WhitelistJe main;

    public PlayerJoin(WhitelistJe main) {
        this.main = main;
    }

    private WhitelistManager whitelistManager;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!whitelistManager.getPlayersAllowed().contains(player.getName())) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§cLe serveur est sous whitelist discord");
        }
    }
}
