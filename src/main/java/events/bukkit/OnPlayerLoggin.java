package events.bukkit;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import dao.DaoManager;
import services.sentry.SentryService;
import main.WhitelistJe;
import models.BedrockData;
import models.JavaData;

/**
 * §l = bold text
 * §m = strikethrough text
 * §o = italicize text
 * §n = underline text
 * 
 * Color Code Foreground Color Hex Background Color Hex
 * Black (black) §0 000000 000000
 * Dark Blue (dark_blue) §1 0000AA 00002A
 * Dark Green (dark_green) §2 00AA00 002A00
 * Dark Aqua (dark_aqua) §3 00AAAA 002A2A
 * Dark Red (dark_red) §4 AA0000 2A0000
 * Dark Purple (dark_purple) §5 AA00AA 2A002A
 * Gold (gold) §6 FFAA00 2A2A00 | 402A00
 * Gray (gray) §7 AAAAAA 2A2A2A
 * Dark Gray (dark_gray) §8 555555 151515
 * Blue (blue) §9 5555FF 15153F
 * Green (green) §a 55FF55 153F15
 * Aqua (aqua) §b 55FFFF 153F3F
 * Red (red) §c FF5555 3F1515
 * Light Purple (light_purple) §d FF55FF FF55FF
 * Yellow (yellow) §e FFFF55 FFFF55
 * White (white) §f FFFFFF FFFFFF
 * Minecoin Gold (minecoin_gold) §g DDD605 DDD605
 */

public class OnPlayerLoggin implements Listener {
    private WhitelistJe plugin;
    private Logger logger;

    private String getDisallowMsg() {
        String ip = Bukkit.getServer().getIp();
        final String staticIp = plugin.getConfigManager().get("paperMcIp", "Not bind to any IP");
        ip = ip.equals("") ? staticIp : ip;
        final String version = Bukkit.getServer().getVersion();

        final String ds_srvName = this.plugin.getDiscordManager().getServerName();
        final String ds_inviteUrl = this.plugin.getDiscordManager().getInviteUrl();
        return "§c§lCe serveur est sous whitelist Discord®§l" +
                "§a\n\nJoin §l" + ds_srvName + "§a at: §9§n§l" + ds_inviteUrl +
                "§f\n\n§lServer Version: §f" + version +
                "§f\n\n§lServer IP: §f" + ip;
    }

    public OnPlayerLoggin(WhitelistJe plugin) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        try {
            Server bukServer = this.plugin.getBukkitManager().getServer();
            final Player loginPlayer = event.getPlayer();
            final String uuid = loginPlayer.getUniqueId().toString();

            final Object dataObj = plugin.getBukkitManager().getPlayerData(uuid);
            boolean allowed = false;

            JavaData data = null;
            if(dataObj != null) {

                final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
                final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);

                data = (JavaData) dataObj;
                data.setMcName(loginPlayer.getName());
                allowed = data.isAllowed();

                if(data.getUUID().equals(javaData.getUUID())) {
                    data.save(DaoManager.getJavaDataDao());
                }
                else if(data.getUUID().equals(bedData.getUUID())) {
                    data.save(DaoManager.getBedrockDataDao());
                }
                else {
                    allowed = false;
                }
            }

            if(!allowed) {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, getDisallowMsg());
                return;
            }

            boolean isWhitelisted = false;

            if (allowed) {
                Bukkit.getServer().setWhitelist(true);
                loginPlayer.setWhitelisted(true);
            }

            final boolean usingWhiteList = bukServer.hasWhitelist();
            final boolean forceWhitelist = bukServer.isWhitelistEnforced();

            if (!usingWhiteList) {
                this.logger.warning("Server is not using a whitelist");
            }
            if (!forceWhitelist) {
                this.logger.warning("Server is not enforcing a whitelist");
                event.allow();
                return;
            } else {
                Set<OfflinePlayer> w_players = Bukkit.getServer().getWhitelistedPlayers();
                for (OfflinePlayer player : w_players) {
                    if (player.getUniqueId().equals(UUID.fromString(uuid))) {
                        isWhitelisted = true;
                        break;
                    }
                }
            }

            if (allowed && !isWhitelisted) {
                this.logger.warning("Le joueur est allowed mais n'a pas été retrouver dans la whitelist du serveur !!!");
            }

            if (!isWhitelisted) {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, getDisallowMsg());
                return;
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, getDisallowMsg());
            return;
        }

        event.allow();
    }
}
