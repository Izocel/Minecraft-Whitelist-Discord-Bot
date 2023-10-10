package helpers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.jooq.tools.json.JSONObject;

import configs.ConfigManager;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import models.NotificationData;
import services.sentry.SentryService;

public class NotificationManager {
    private static ConfigManager configs;
    private static WhitelistDmc plugin;
    private static Logger logger;
    private static String url;
    private static String emailGroup;
    public static LinkedHashMap<String, Object> topics;
    public static String registrationTopic = "WDMC-Registration";
    public static String guildJoinTopic = "WDMC-GuildJoin";
    public static String miscTopic = "WDMC-Misc";
    private static Map<String, String> headers;

    public NotificationManager(WhitelistDmc plugin) {
        NotificationManager.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("NotificationManager");

        NotificationManager.plugin = plugin;
        NotificationManager.configs = plugin.getConfigManager();

        final String API_TOKEN = NotificationManager.configs.get("notificationsApi.token");
        NotificationManager.url = NotificationManager.configs.get("notificationsApi.ntfyUrl");
        NotificationManager.emailGroup = NotificationManager.configs.get("notificationsApi.emailGroup");
        NotificationManager.topics = NotificationManager.configs.getAsMap("notificationsApi.topics");

        NotificationManager.headers = new LinkedHashMap<>();
        NotificationManager.headers.put("Authorization", "Bearer " + API_TOKEN);

        final String registrationTopic = NotificationManager.topics.get("registration").toString();
        if (registrationTopic != null) {
            NotificationManager.registrationTopic = registrationTopic;
        }

        final String guildJoinTopic = NotificationManager.topics.get("guildJoin").toString();
        if (guildJoinTopic != null) {
            NotificationManager.guildJoinTopic = guildJoinTopic;
        }

        final String miscTopic = NotificationManager.topics.get("misc").toString();
        if (miscTopic != null) {
            NotificationManager.miscTopic = miscTopic;
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public static final JSONObject postNotification(NotificationData data, Boolean putEmail) {
        var resp = new JSONObject();
        resp.put("status", 500);
        resp.put("data", null);

        try {
            data.url = NotificationManager.url;
            data.email = NotificationManager.emailGroup;
            
            if (data.topic == null) {
               data.topic = NotificationManager.miscTopic;
            }

            if (putEmail == false) {
                data.email = null;
            }

            final String jsonData = data.toJson().toString();
            final String respData = BypassedFetcher.fetch(
                    Fetcher.POST, NotificationManager.url,
                    Optional.ofNullable(jsonData),
                    Optional.ofNullable(NotificationManager.headers));

            final var outData = Fetcher.toJson(respData).getJSONObject(0);
            final String errorMsg = outData.optString("error");
            final Integer errorCode = outData.optInt("http");
            final Integer time = outData.optInt("time");

            if (!errorMsg.isEmpty()) {
                logger.warning("Notification delivery error: " + errorMsg);
                resp.put("error", errorMsg);
                resp.put("status", errorCode != null ? errorCode : "500");
                return resp;
            }

            if (time.toString().isEmpty()) {
                logger.warning("Notification response error: response was invalid (time not found).");
                resp.put("error", "Notification request response was invalid (time not found).");
                resp.put("status", "204");
                return resp;
            }

            resp.put("data", outData);
            resp.put("status", 200);

        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return resp;
    }

}