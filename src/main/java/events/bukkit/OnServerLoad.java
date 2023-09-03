package events.bukkit;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.json.JSONArray;

import helpers.Fetcher;
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

            final var nf = plugin.getNotificationManager();
            final var notification = new NotificationData(title, msg, nf.registrationTopic);
            notification.addViewAction("Admin panel", "https://rvdprojects.synology.me:3000/#/dashboard");
            notification.icon = "https://rvdprojects.synology.me:3000/favicon.ico";
            notification.markdown = true;
            notification.tags.add("bot");

            final String resp = nf.postFullNotification(notification);
            final JSONArray json = Fetcher.toJson(resp);
            this.logger.warning(json.toString());
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

}
