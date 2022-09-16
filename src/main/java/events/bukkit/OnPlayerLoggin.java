package events.bukkit;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import main.WhitelistJe;

/**
 *  §l = bold text
    §m = strikethrough text
    §o = italicize text
    §n = underline text
    
    Color	Code	Foreground Color Hex	Background Color Hex
    Black (black)	§0	000000 	000000 
    Dark Blue (dark_blue)	§1	0000AA	00002A
    Dark Green (dark_green)	§2	00AA00	002A00
    Dark Aqua (dark_aqua)	§3	00AAAA	002A2A
    Dark Red (dark_red)	§4	AA0000	2A0000
    Dark Purple (dark_purple)	§5	AA00AA 	2A002A 
    Gold (gold)	§6	FFAA00 	2A2A00 | 402A00 
    Gray (gray)	§7	AAAAAA	2A2A2A
    Dark Gray (dark_gray)	§8	555555	151515
    Blue (blue)	§9	5555FF	15153F
    Green (green)	§a	55FF55	153F15
    Aqua (aqua)	§b	55FFFF	153F3F
    Red (red)	§c	FF5555	3F1515
    Light Purple (light_purple)	§d	FF55FF	FF55FF
    Yellow (yellow)	§e	FFFF55	FFFF55
    White (white)	§f	FFFFFF	FFFFFF
    Minecoin Gold (minecoin_gold)	§g	DDD605	DDD605
*/

public class OnPlayerLoggin implements Listener {
    private WhitelistJe main;
    private Logger logger;

    public OnPlayerLoggin(WhitelistJe main) {
        this.logger = Logger.getLogger("WJE" + this.getClass().getName());
        this.main = main;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String ip = Bukkit.getServer().getIp();
        final String version = Bukkit.getServer().getVersion();
        final boolean onlineMode = Bukkit.getServer().getOnlineMode();
        final boolean usingWhiteList = Bukkit.getServer().hasWhitelist();
        final boolean forceWhitelist = Bukkit.getServer().isWhitelistEnforced();

        ip = ip.equals("") ? "Not bound to any IP" : ip;

        final UUID pUUID = event.getPlayer().getUniqueId();
        final String pName = event.getPlayer().getName();
        Integer allowedId = this.main.playerIsAllowed(pUUID);
        boolean isAllowed = false;

        if(allowedId > 0) {
            this.main.updatePlayerUUID(allowedId, pUUID);
        }

        if(!usingWhiteList) {
            this.logger.warning("Server is not using a whitelist");
        }
        if(!forceWhitelist) {
            this.logger.warning("Server is not enforcing a whitelist");
            isAllowed = true;
        }
        else {
            Set<OfflinePlayer> w_players = Bukkit.getServer().getWhitelistedPlayers();
            for (OfflinePlayer player : w_players) {
                if(player.getUniqueId() == pUUID) {
                    isAllowed = true;
                    break;
                }
            }
        }

        if(!isAllowed) {
            final String ds_srvName = this.main.getDiscordManager().getServerName();
            final String ds_inviteUrl = this.main.getDiscordManager().getInviteUrl();
            final String msg = "§c§lLe serveur est sous whitelist Discord®§l" + 
            "§a\n\nJoin §l"+ ds_srvName +"§a at: §9§n§l"+ ds_inviteUrl +
            "§f\n\n§lServer Version: §f" + version +
            "§f\n\n§lServer IP: §f" + ip;
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, msg);
            return;
        }

        event.allow();
    }
}
