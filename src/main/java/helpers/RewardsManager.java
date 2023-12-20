package helpers;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import configs.ConfigManager;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import models.RewardCalendar;

public class RewardsManager {
    private WhitelistDmc plugin;
    private ConfigManager configs;
    private Logger logger;
    private boolean active;
    private boolean needsWipe;
    private LinkedHashMap<String, Object> calendarsData;

    public RewardsManager(WhitelistDmc plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("RewardsManager");

        this.plugin = plugin;
        configs = plugin.getConfigManager();
        logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        active = configs.get("rewardsSystem.active", "false") == "true" ? true : false;
        needsWipe = configs.get("rewardsSystem.wipeAll", "false") == "true" ? true : false;
        calendarsData = configs.getAsMap("rewardsSystem.calendars");

        if (needsWipe) {
            return;
        }

        if (!active) {
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public RewardCalendar getCalendar(String name) {
        return new RewardCalendar(calendarsData.get(name), name);
    }

    public void prepareRewardsFor(String calendarName, int discordId) {
        if (!active || needsWipe) {
            return;
        }

        RewardCalendar calendar = getCalendar(calendarName);
        if (calendar != null) {
            calendar.prepareRewardsFor(discordId);
        }
    }

    public void prepareAllRewardsFor(int discordId) {
        for (String key : calendarsData.keySet()) {
            prepareRewardsFor(key, discordId);
        }
    }
}
