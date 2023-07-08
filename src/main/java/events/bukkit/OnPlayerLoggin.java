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
import main.WhitelistDMC;
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
    private WhitelistDMC plugin;
    private Logger logger;

    private String getDisallowMsg() {
        final String javaIp = plugin.getConfigManager().get("javaIp", "Not bind to any IP");
        final String bedrockIp = plugin.getConfigManager().get("bedrockIp", "Not bind to any IP");
        final String version = Bukkit.getServer().getVersion();

        final String ds_srvName = this.plugin.getDiscordManager().getServerName();
        final String ds_inviteUrl = this.plugin.getDiscordManager().getInviteUrl();
        return "§c§lCe serveur est sous whitelist Discord®§l" +
                "§a\n\nJoin §l" + ds_srvName + "§a at: §9§n§l" + ds_inviteUrl +
                "§f\n\n§lServer Version: §f" + version +
                "§f\n\n§lServer Java Address: §f" + javaIp +
                "§f\n\n§lServer Bedrock Address: §f" + bedrockIp;
    }

    private String getDisallowBannedMsg() {
        final String javaIp = plugin.getConfigManager().get("javaIp", "Not bind to any IP");
        final String bedrockIp = plugin.getConfigManager().get("bedrockIp", "Not bind to any IP");
        final String version = Bukkit.getServer().getVersion();

        final String ds_srvName = this.plugin.getDiscordManager().getServerName();
        return "§c§lCe serveur est sous whitelist Discord®§l" +
                "§f\n\nIl semble que vous ayez été§l banni§f du serveur: §a" + ds_srvName +
                ".\n§fMeilleur chance la prochaine fois..." +
                "§f\n\n§lServer Version: §f" + version +
                "§f\n\n§lServer Java Address: §f" + javaIp +
                "§f\n\n§lServer Bedrock Address: §f" + bedrockIp;
    }

    public OnPlayerLoggin(WhitelistDMC plugin) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        ITransaction tx = Sentry.startTransaction("onPlayerLogin", "validation");
        try {
            Server bukServer = plugin.getBukkitManager().getServer();

            final boolean usingWhiteList = bukServer.hasWhitelist();
            final boolean forceWhitelist = bukServer.isWhitelistEnforced();

            if (!usingWhiteList) {
                this.logger.warning("Server is not using a whitelist.. Adding whitelist...");
                Bukkit.getServer().setWhitelist(true);
            }
            if (!forceWhitelist) {
                this.logger.warning("Server is not enforcing a whitelist...");
                tx.setData("state", "no validation in place");
                tx.finish(SpanStatus.OK);
                return;
            }

            final Player loginPlayer = event.getPlayer();
            final String uuid = loginPlayer.getUniqueId().toString();

            final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
            final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);

            final boolean allowed = plugin.playerIsAllowed(loginPlayer.getUniqueId()) > 0;

            if (bedData == null && javaData == null) {
                this.logger.info("login player is not registered");

                if (loginPlayer.isBanned()) {
                    this.logger.info("login player is banned");
                    loginPlayer.setWhitelisted(false);
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, getDisallowBannedMsg());
                }

                else if (!loginPlayer.isWhitelisted()) {
                    this.logger.info("login player is not whitelisted");
                    event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, getDisallowMsg());
                }

                else {
                    this.logger.info("not registered player fallback to default event");
                    tx.setData("state", "not registered player fallback to default event");
                    tx.finish(SpanStatus.OK);
                    return;
                }
            }

            if (javaData != null) {
                javaData.setMcName(loginPlayer.getName());
                javaData.save(DaoManager.getJavaDataDao());
            }

            else if (bedData != null) {
                bedData.setMcName(PlayerDbApi.getXboxPseudo(loginPlayer.getUniqueId().toString()));
                bedData.save(DaoManager.getBedrockDataDao());
            }

            // For a registered players
            this.logger.info("login player is registered");
            if (loginPlayer.isBanned()) {
                this.logger.info("player is banned");
                loginPlayer.setWhitelisted(false);
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, getDisallowBannedMsg());
                plugin.getBukkitManager().sanitizeAndBanPlayer(uuid);
            }

            else if (allowed) {
                this.logger.info("login player is allowed -> whitelisting");
                loginPlayer.setWhitelisted(true);
                event.allow();
            }

            else {
                this.logger.info("login player is NOT allowed");
                loginPlayer.setWhitelisted(false);
                plugin.getBukkitManager().sanitizeAnKickPlayer(uuid);
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, getDisallowMsg());
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
