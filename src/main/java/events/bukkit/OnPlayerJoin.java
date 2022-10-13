package events.bukkit;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dao.DaoManager;
import helpers.Helper;
import services.sentry.SentryService;
import main.WhitelistJe;
import models.JavaData;
import models.User;

public class OnPlayerJoin implements Listener {
    private WhitelistJe plugin;
    private Logger logger;

    public OnPlayerJoin(WhitelistJe plugin) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            final Player loginPlayer = event.getPlayer();
            final UUID UUID = loginPlayer.getUniqueId();

            Object dataObj = plugin.getBukkitManager().getPlayerData(UUID.toString());
            if(dataObj == null) {
                return;
            }

            JavaData data = (JavaData) dataObj;
            this.handleConfirmation(data, event);

        } catch (Exception e) {
            SentryService.captureEx(e);
            return;
        }
    }

    public void handleConfirmation(JavaData data, PlayerJoinEvent event) {
        
        Timestamp comparator = Helper.convertStringToTimestamp(data.getCreatedAt());
        final Integer confirmHourDelay = Integer.valueOf(
            this.plugin.getConfigManager().get("hoursToConfirmMcAccount", "-1"));

        final User user = DaoManager.getUsersDao().findUser(data.getId());
        final String tagDiscord = this.plugin.getGuildManager().getGuild().getMemberById(user.getDiscordId())
        .getUser().getAsTag();

        if(data.isConfirmed() || confirmHourDelay < 0) {
            data.setAsConfirmed(true);
            return;
        }

        String msg = getAllowMsg(tagDiscord, data.getUUID());
        Player mcPlayer = event.getPlayer();
        boolean canConfirm = Helper.isWithinXXHour(comparator, confirmHourDelay);

        if(!canConfirm) {
            msg = getDisallowMsg(tagDiscord, data.getUUID());
            event.getPlayer().setWhitelisted(false);
            event.getPlayer().kickPlayer(msg);

            user.delete(DaoManager.getUsersDao());
            return;
        }
        
        mcPlayer.sendMessage(msg);
    }

    private String getDisallowMsg(String tagDiscord, String mcUUID) {
        final String cmdName = this.plugin.getConfigManager().get("registerCmdName", "register");
        return "\n\n§c§lLe délai pour confirmer ce compte est dépassé..."+
                "\n§fLe compte " + tagDiscord + " Discord® a fait une demande pour relier ce compte Minecraft®." +
                "\n\n§lEssayez de refaire une demande sur discord, utiliser la commande:\n§9    /" + cmdName +
                "\n\n§cSi cette demande vous semble illégitime, contactez un administrateur!!!" + 
                "\n§fIdentifiant de la demande: " + mcUUID + "\n \n";
    }

    private String getAllowMsg(String tagDiscord, String mcUUID) {
        final String cmdName = this.plugin.getConfigManager().get("confirmLinkCmdName", "wje-link");
        return "§f§lLe compte " + tagDiscord + " Discord® a fait une demande pour relier ce compte Minecraft®." +
                "\n\n§f§lPour comfirmer cette demande utiliser la commande:\n§9    /" + cmdName +
                "\n\n§cSi cette demande vous semble illégitime, contactez un administrateur!!!" + 
                "\n§fIdentifiant de la demande: \n" + mcUUID + "\n \n";
    }
}
