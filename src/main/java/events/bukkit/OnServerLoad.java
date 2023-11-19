package events.bukkit;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import helpers.NotificationManager;
import locals.LocalManager;
import main.WhitelistDmcNode;
import models.NotificationData;
import services.sentry.SentryService;

public class OnServerLoad implements Listener {
    private WhitelistDmcNode plugin;
    private Logger logger;

    public OnServerLoad(WhitelistDmcNode plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        try {
            LocalManager LOCAL = WhitelistDmcNode.LOCALES;
            final String title = LOCAL.translate("SERVER_IS_UP");
            final String msg = this.plugin.getBukkitManager().getServerInfoString();
            final String dashboardUrl = plugin.getConfigManager().get("misc.adminPanelUrl");

            final var notification = new NotificationData(title, msg);
            notification.topic = NotificationManager.miscTopic;
            notification.addViewAction("Admin panel", dashboardUrl);
            notification.markdown = true;
            notification.tags.add("robot");

            NotificationManager.postNotification(notification, false);

            if(!plugin.isProduction()) {
                alwaysDayServer();
            }
            
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    private void alwaysDayServer() {
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    plugin.getServer().getWorlds().get(0).setTime(0L);
                    plugin.getServer().getWorlds().get(0).setStorm(false);
                    plugin.getServer().getWorlds().get(0).setThundering(false);
                } catch (Exception e) {

                }
            }
        }.runTaskTimer(plugin, 0L, 10000L);
    }

}
