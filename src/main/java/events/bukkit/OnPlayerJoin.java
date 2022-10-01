package events.bukkit;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import helpers.Helper;
import main.WhitelistJe;
import models.User;

public class OnPlayerJoin implements Listener {
    private WhitelistJe main;
    private Logger logger;

    public OnPlayerJoin(WhitelistJe main) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.main = main;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            final Player loginPlayer = event.getPlayer();
            final UUID pUUID = loginPlayer.getUniqueId();

            User user = this.main.getDaoManager().getUsersDao().findByMcUUID(pUUID.toString());

            if(user == null) {
                return;
            }

            this.handleConfirmation(user, event);
            user.save(this.main.getDaoManager().getUsersDao());

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void handleConfirmation(User user, PlayerJoinEvent event) {
        
        Timestamp comparator = Helper.convertStringToTimestamp(user.getCreatedAt());
        final Integer confirmHourDelay = Integer.valueOf(
            this.main.getConfigManager().get("hoursToConfirmMcAccount", "48"));

        final String tagDiscord = this.main.getGuildManager().getGuild().getMemberById(user.getDiscordId())
        .getUser().getAsTag();

        if(user.isConfirmed() || confirmHourDelay < 0) {
            user.setAsConfirmed(true);
            return;
        }

        String msg = getAllowMsg(tagDiscord, user.getMcUUID());
        Player mcPlayer = event.getPlayer();
        boolean canConfirm = Helper.isWithinXXHour(comparator, confirmHourDelay);

        if(!canConfirm) {
            event.getPlayer().setWhitelisted(false);
            user.delete(this.main.getDaoManager().getUsersDao());

            msg = getDisallowMsg(tagDiscord, user.getMcUUID());
            event.getPlayer().kickPlayer(msg);
        }

        mcPlayer.sendMessage(msg);
    }

    private String getDisallowMsg(String tagDiscord, String mcUUID) {
        final String cmdName = this.main.getConfigManager().get("registerCmdName", "register");
        return "\n\n§c§lLe délai pour confirmer ce compte est dépassé..."+
                "\n§fLe compte " + tagDiscord + " Discord® a fait une demande pour relier ce compte Minecraft®." +
                "\n\n§lEssayez de refaire une demande sur discord, utiliser la commande:\n§9    /" + cmdName +
                "\n\n§cSi cette demande vous semble illégitime, contactez un administrateur!!!" + 
                "\n§fIdentifiant de la demande: " + mcUUID + "\n \n";
    }

    private String getAllowMsg(String tagDiscord, String mcUUID) {
        final String cmdName = this.main.getConfigManager().get("confirmLinkCmdName", "wje-link");
        return "§f§lLe compte " + tagDiscord + " Discord® a fait une demande pour relier ce compte Minecraft®." +
                "\n\n§f§lPour comfirmer cette demande utiliser la commande:\n§9    /" + cmdName +
                "\n\n§cSi cette demande vous semble illégitime, contactez un administrateur!!!" + 
                "\n§fIdentifiant de la demande: \n" + mcUUID + "\n \n";
    }
}
