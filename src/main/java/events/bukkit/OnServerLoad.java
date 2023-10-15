package events.bukkit;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jooq.tools.json.JSONObject;

import helpers.NotificationManager;
import locals.LocalManager;
import main.WhitelistDmc;
import models.NotificationData;
import services.sentry.SentryService;

public class OnServerLoad implements Listener {
    private WhitelistDmc plugin;
    private Logger logger;

    public OnServerLoad(WhitelistDmc plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        try {
            LocalManager LOCAL = WhitelistDmc.LOCALES;
            StringBuilder sb = new StringBuilder();
            final String title = LOCAL.translate("SERVER_IS_UP");
            final String msg = this.plugin.getBukkitManager().getServerInfoString(LOCAL.getNextLang());
            sb.append("**" + title + "**");
            sb.append(msg);

            this.plugin.getGuildManager().getBotLogChannel()
                    .sendMessage(sb.toString()).submit(true);

            final var notification = new NotificationData(title, msg);
            notification.topic = NotificationManager.miscTopic;
            notification.addViewAction("Admin panel", "https://rvdprojects.synology.me:3000/#/dashboard");
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
