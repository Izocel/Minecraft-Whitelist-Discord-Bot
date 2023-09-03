package helpers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import configs.ConfigManager;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import models.NotificationData;

public class NotificationManager {
    private final ConfigManager configs;
    private final WhitelistDmc plugin;
    private final Logger logger;
    private final String url;
    private final String emailGroup;
    public final LinkedHashMap<String, Object> topics;
    public String registrationTopic = "WDMC-Registration";
    private Map<String, String> headers;

    public NotificationManager(WhitelistDmc plugin) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("NotificationManager");

        this.plugin = plugin;
        this.configs = plugin.getConfigManager();

        final String API_TOKEN = this.configs.get("notificationsApi.token");
        this.url = this.configs.get("notificationsApi.ntfyUrl");
        this.emailGroup = this.configs.get("notificationsApi.emailGroup");
        this.topics = this.configs.getAsMap("notificationsApi.topics");

        this.headers = new LinkedHashMap<>();
        this.headers.put("Authorization", "Bearer " + API_TOKEN);

        final String registrationTopic = this.topics.get("registration").toString();
        if (registrationTopic != null) {
            this.registrationTopic = registrationTopic;
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public final String postFullNotification(NotificationData data) {
        data.url = this.url;
        data.email = this.emailGroup;
        final String jsonData = data.toJson().toString();

        return BypassedFetcher.fetch(Fetcher.POST, this.url, Optional.ofNullable(jsonData),
                Optional.ofNullable(this.headers));
    }

}