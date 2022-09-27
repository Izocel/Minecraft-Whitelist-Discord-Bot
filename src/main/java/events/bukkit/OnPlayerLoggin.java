package events.bukkit;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import main.WhitelistJe;
import models.User;

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
        final Player loginPlayer = event.getPlayer();
        final UUID pUUID = loginPlayer.getUniqueId();
        final String pName = loginPlayer.getName();

        final Integer allowedWithUUID = this.main.playerIsAllowed(pUUID);
        final Integer allowedWithPseudo = this.main.playerIsAllowed(pName);
        
        final boolean isAllowedUUID = allowedWithUUID != null && allowedWithUUID > 0;
        final boolean isAllowedPseudo = allowedWithPseudo != null && allowedWithPseudo > 0;

        boolean isAllowed = isAllowedUUID || isAllowedPseudo;

        // Mistmatch possible
        if(isAllowed && !allowedWithUUID.equals(allowedWithPseudo)) {
            
            final Integer usedId = allowedWithUUID > 0 
                ? allowedWithUUID 
                : allowedWithPseudo > 0 
                ? allowedWithPseudo 
                : -1;

            if(usedId > 0) {
                final boolean tempConfirmed = true;
                User user = this.main.getDaoManager().getUsersDao().findUser(usedId);
                user.setMcUUID(pUUID.toString());
                user.setMcName(pName);
                user.setAsConfirmed(tempConfirmed);
                user.save(this.main.getDaoManager().getUsersDao());
            }
            
        }

        boolean isWhitelisted = false;

        if(isAllowed) {
            Bukkit.getServer().setWhitelist(true);
            loginPlayer.setWhitelisted(true);
        }

        String ip = Bukkit.getServer().getIp();
        ip = ip.equals("") ? "Not bound to any IP" : ip;
        final String version = Bukkit.getServer().getVersion();
        final boolean usingWhiteList = Bukkit.getServer().hasWhitelist();
        final boolean forceWhitelist = Bukkit.getServer().isWhitelistEnforced();

        if(!usingWhiteList) {
            this.logger.warning("Server is not using a whitelist");
        }
        if(!forceWhitelist) {
            this.logger.warning("Server is not enforcing a whitelist");
            event.allow();
            return;
        }
        else {
            Set<OfflinePlayer> w_players = Bukkit.getServer().getWhitelistedPlayers();
            for (OfflinePlayer player : w_players) {
                if(player.getUniqueId() == pUUID) {
                    isWhitelisted = true;
                    break;
                }
            }
        }
        
        if(isAllowed && !isWhitelisted) {
            this.logger.warning("Le joueur est allowed mais n'a pas été retrouver dans la whitelist du serveur !!!");
        }

        if(!isWhitelisted) {
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