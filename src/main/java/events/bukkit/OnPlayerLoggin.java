package events.bukkit;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import main.WhitelistJe;

public class OnPlayerLoggin implements Listener {
    private WhitelistJe main;

    public OnPlayerLoggin(WhitelistJe main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {

        final String pName = event.getPlayer().getName();
        final UUID pUUID = event.getPlayer().getUniqueId();
        final Integer allowedId = this.main.playerIsAllowed(pName);

        if(allowedId < 1) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§cLe serveur est sous whitelist discord");
        }
        else {
            this.main.updatePlayerUUID(allowedId, pUUID);
            event.allow();
        }
    }
}
