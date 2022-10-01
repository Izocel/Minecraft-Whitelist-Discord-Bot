package events.bukkit;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
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

    private String getDisallowMsg() {
        String ip = Bukkit.getServer().getIp();
        ip = ip.equals("") ? "Not bound to any IP" : ip;
        final String version = Bukkit.getServer().getVersion();

        final String ds_srvName = this.main.getDiscordManager().getServerName();
        final String ds_inviteUrl = this.main.getDiscordManager().getInviteUrl();
        return "§c§lCe serveur est sous whitelist Discord®§l" +
                "§a\n\nJoin §l" + ds_srvName + "§a at: §9§n§l" + ds_inviteUrl +
                "§f\n\n§lServer Version: §f" + version +
                "§f\n\n§lServer IP: §f" + ip;
    }

    private String getAllowMsg() {
        String ip = Bukkit.getServer().getIp();
        ip = ip.equals("") ? "Not bound to any IP" : ip;
        final String version = Bukkit.getServer().getVersion();

        final String ds_srvName = this.main.getDiscordManager().getServerName();
        final String ds_inviteUrl = this.main.getDiscordManager().getInviteUrl();
        return "§c§lCe serveur est sous whitelist Discord®§l" +
                "§a\n\nJoin §l" + ds_srvName + "§a at: §9§n§l" + ds_inviteUrl +
                "§f\n\n§lServer Version: §f" + version +
                "§f\n\n§lServer IP: §f" + ip;
    }

    public OnPlayerJoin(WhitelistJe main) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.main = main;
    }

    public void handleConfirmation(User user, PlayerJoinEvent event) {
        
        Timestamp comparator = Helper.convertStringToTimestamp(user.getCreatedAt());
        final Integer confirmHourDelay = Integer.valueOf(
            this.main.getConfigManager().get("hoursToConfirmMcAccount", "48"));

        if(user.isConfirmed() || confirmHourDelay < 0) {
            user.setAsConfirmed(true);
            return;
        }

        String msg = getAllowMsg();
        Player mcPlayer = event.getPlayer();
        boolean canConfirm = Helper.isWithinXXHour(comparator, confirmHourDelay);

        if(!canConfirm) {
            msg = getDisallowMsg();
            event.getPlayer().kickPlayer(msg);
        }

        mcPlayer.sendMessage(msg);
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
}
