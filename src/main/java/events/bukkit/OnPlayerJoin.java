package events.bukkit;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dao.DaoManager;
import helpers.Helper;
import services.sentry.SentryService;
import main.WhitelistDmc;
import models.BedrockData;
import models.JavaData;
import models.User;

public class OnPlayerJoin implements Listener {
    private WhitelistDmc plugin;
    private Logger logger;

    public OnPlayerJoin(WhitelistDmc plugin) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            final Player loginPlayer = event.getPlayer();
            
            if(plugin.playerIsConfirmed(loginPlayer.getUniqueId()) > 0) {
                return;
            }

            final String uuid = loginPlayer.getUniqueId().toString();
            final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
            final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);

            final Integer confirmHourDelay = Integer.valueOf(
                    this.plugin.getConfigManager().get("hoursToConfirmMcAccount", "24"));

            Integer userId = -1;
            String registrationDate = null;
            if (javaData != null) {
                if (confirmHourDelay <= 0) {
                    javaData.setAsConfirmed(true);
                    return;
                }
                userId = javaData.getUserId();
                registrationDate = javaData.getCreatedAt();
            }

            else if (bedData != null) {
                if (confirmHourDelay <= 0) {
                    bedData.setAsConfirmed(true);
                    return;
                }
                userId = bedData.getUserId();
                registrationDate = bedData.getCreatedAt();
            }
            else {
                return;
            }

            final boolean canConfirm = Helper.isWithinXXHour(
                Helper.convertStringToTimestamp(registrationDate), confirmHourDelay);

            final User user = DaoManager.getUsersDao().findUser(userId);

            String msg = getAllowMsg(user.getDiscordTag(), uuid);
            Player player = event.getPlayer();

            if (!canConfirm) {
                msg = getDisallowMsg(user.getDiscordTag(), uuid);
                event.getPlayer().setWhitelisted(false);
                event.getPlayer().kickPlayer(msg);
                plugin.deletePlayerRegistration(player.getUniqueId());
                return;
            }

            player.sendMessage(msg);

        } catch (Exception e) {
            SentryService.captureEx(e);
            return;
        }
    }

    private String getDisallowMsg(String tagDiscord, String mcUUID) {
        final String cmdName = this.plugin.getConfigManager().get("registerCmdName", "register");
        return "\n\n§c§lLe délai pour confirmer ce compte est dépassé..." +
                "\n§fLe compte  Discord®: " + tagDiscord + " a fait une demande pour relier ce compte Minecraft®." +
                "\n\n§lEssayez de refaire une demande sur discord, utiliser la commande:\n§9    /" + cmdName +
                "\n\n§cSi cette demande vous semble illégitime, contactez un administrateur!!!" +
                "\n§fIdentifiant de la demande: " + mcUUID + "\n \n";
    }

    private String getAllowMsg(String tagDiscord, String mcUUID) {
        final String cmdName = this.plugin.getConfigManager().get("confirmLinkCmdName", "w-link");
        return "§f§lLe compte  Discord®: " + tagDiscord + " a fait une demande pour relier ce compte Minecraft®." +
                "\n\n§f§lPour comfirmer cette demande utiliser la commande:\n§9    /" + cmdName +
                "\n\n§cSi cette demande vous semble illégitime, contactez un administrateur!!!" +
                "\n§fIdentifiant de la demande: \n" + mcUUID + "\n \n";
    }
}
