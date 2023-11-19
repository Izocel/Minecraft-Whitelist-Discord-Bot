package events.bukkit;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import dao.DaoManager;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import services.api.PlayerDbApi;
import services.sentry.SentryService;
import main.WhitelistDmcNode;
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

/**
 * 
 */
public class OnPlayerLogin implements Listener {
    private WhitelistDmcNode plugin;
    private Logger logger;

    private String getDisallowMsg() {
        final String javaIp = plugin.getConfigManager().get("javaIp", "Not bind to any IP");
        final String bedrockIp = plugin.getConfigManager().get("bedrockIp", "Not bind to any IP");
        final String ds_srvName = plugin.getConfigManager().get("misc.discordServerName", "[DS Server Name]");
        final String ds_inviteUrl = plugin.getConfigManager().get("misc.discordInviteUrl", "[DS Invite Link]");
        final String version = Bukkit.getServer().getVersion();

        return "§c§lCe serveur est sous whitelist Discord®§l" +
                "§a\n\nJoin §l" + ds_srvName + "§a at: §9§n§l" + ds_inviteUrl +
                "§f\n\n§lServer Version: §f" + version +
                "§f\n\n§lServer Java Address: §f" + javaIp +
                "§f\n\n§lServer Bedrock Address: §f" + bedrockIp;
    }

    private String getDisallowBannedMsg() {
        return getDisallowMsg() +
                "§f\n\nIl semble que vous ayez été§l banni§f du serveur... §a";
    }

    public OnPlayerLogin(WhitelistDmcNode plugin) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        ITransaction tx = Sentry.startTransaction("onPlayerLogin", "validation");
        try {
            Server bukServer = plugin.getBukkitManager().getServer();

            final Player loginPlayer = event.getPlayer();
            final String uuid = loginPlayer.getUniqueId().toString();
            final boolean usingWhiteList = bukServer.hasWhitelist();
            final boolean forceWhitelist = bukServer.isWhitelistEnforced();

            if (!usingWhiteList) {
                this.logger.warning("Server is not using a whitelist.. Adding whitelist...");
                Bukkit.getServer().setWhitelist(true);
            }

            try {
                if (loginPlayer.isBanned()) {
                    this.logger.info("player is banned, falling back to default handling");
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, getDisallowBannedMsg());
                    plugin.removePlayerRegistry(loginPlayer.getUniqueId(), getDisallowBannedMsg());
                    return;
                }
            } catch (Exception e) {
                this.logger.warning("Error while checking if the player was banned:");
                this.logger.warning(e.getMessage());
            }

            if (!forceWhitelist) {
                this.logger.warning("Server is not enforcing a whitelist...");
                tx.setData("state", "no validation in place");
                tx.finish(SpanStatus.OK);
                return;
            }

            final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
            final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);
            final boolean allowed = plugin.playerIsAllowed(loginPlayer.getUniqueId()) > 0;

            if (javaData != null) {
                javaData.setMcName(loginPlayer.getName());
                javaData.setAvatarUrl("https://mc-heads.net/head/" + loginPlayer.getName());
                javaData.save(DaoManager.getJavaDataDao());
            }

            else if (bedData != null) {
                bedData.setAvatarUrl("https://api.tydiumcraft.net/v1/players/skin?uuid="
                        + loginPlayer.getUniqueId().toString() + "&size=72&type=head/");
                bedData.setMcName(PlayerDbApi.getXboxPseudo(loginPlayer.getUniqueId().toString()));
                bedData.save(DaoManager.getBedrockDataDao());
            }

            if (!allowed) {
                this.logger.info("login player is NOT allowed");
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, getDisallowMsg());
                plugin.removePlayerRegistry(loginPlayer.getUniqueId(), getDisallowMsg());
            } else {
                this.logger.info("login player is allowed -> whitelisting");
                loginPlayer.setWhitelisted(true);
                event.allow();
            }

            this.logger.info("continuing default login procedures");
            tx.setData("state", "end of validation");
            tx.finish(SpanStatus.OK);

        } catch (Exception e) {
            tx.setThrowable(e);
            tx.finish(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
            return;
        }

    }
}
