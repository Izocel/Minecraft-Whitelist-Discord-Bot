package helpers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import bukkit.BukkitManager;
import configs.ConfigManager;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import models.RewardCalendar;
import services.sentry.SentryService;

public class RewardsManager {
    private WhitelistDmc plugin;
    private ConfigManager configs;
    private Logger logger;
    private boolean active;
    private boolean needsWipe = false;
    final private LinkedHashMap<String, Object> calendarsData;

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
            wipe();
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public boolean isActive() {
        return active;
    }

    public boolean needsWipe() {
        return needsWipe;
    }

    public RewardCalendar getCalendar(String name) {
        return new RewardCalendar(calendarsData.get(name), name);
    }

    public LinkedList<RewardCalendar> getCalendars(String type, int active) {
        final LinkedList<RewardCalendar> calendars = new LinkedList<>();
        for (String name : calendarsData.keySet()) {
            final RewardCalendar calendar = new RewardCalendar(calendarsData.get(name), name);

            if (active == 0 && calendar.isActive()) {
                continue;
            }

            if (active == 1 && !calendar.isActive()) {
                continue;
            }

            if (type.length() > 0 && !calendar.getType().equals(type)) {
                continue;
            }

            calendars.add(calendar);
        }

        return calendars;
    }

    public void parsePublishRewardsEvent(JSONObject event) {
        if (!active || needsWipe) {
            return;
        }

        String calendarType = (String) event.getString("calendarType");
        String receiverKey = (String) event.getString("receiverKey");
        String giverKey = (String) event.getString("giverKey");
        String qualifier = (String) event.get("qualifier");

        try {
            LinkedList<RewardCalendar> calendars = getCalendars(calendarType, 1);
            Iterator<RewardCalendar> iterator = calendars.iterator();

            while (iterator.hasNext()) {
                RewardCalendar calendar = iterator.next();
                if (!calendar.isActive() || calendar.needsWipe()) {
                    continue;
                }

                calendar.publishReward(qualifier, receiverKey, giverKey);
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public void parseClaimRewardsEvent(Player player, JSONObject event) {
        if (!active || needsWipe) {
            return;
        }

        String calendarType = (String) event.getString("calendarType");
        ArrayList<String> extraLores = new ArrayList<String>();
        extraLores.add("Acquired via reward system.");

        try {
            LinkedList<RewardCalendar> calendars = getCalendars(calendarType, 1);
            for (RewardCalendar calendar : calendars) {
                if (calendar == null || !calendar.isActive() || !calendar.isClaimActive() || calendar.needsWipe()) {
                    continue;
                }

                final String requiredRole = calendar.getRequiredRole();
                if (requiredRole != null && !PermsManager.isPlayerInGroup(player, requiredRole)) {
                    continue;
                }

                // TODO: fech rewards from DB

                // Compare now with claim max time
                final Timestamp now = Helper.getTimestamp();
                final String until = calendar.getClaimableUntil();
                final bool claimable = true;

                if(!claimable) {
                    continue;
                }

                
                ArrayList<String> items = new ArrayList<>();
                items.add("EXPERIENCE_ORB 2");
                items.add("IRON_SWORD 1");
                items.add("MONEY 2");

                BukkitManager.givePlayerItemsReward(player, items, null, extraLores);
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
            needsWipe = true;
        }
    }

    public boolean wipe() {
        if (!active || !needsWipe) {
            return true;
        }

        needsWipe = false;

        try {
            LinkedList<RewardCalendar> calendars = getCalendars("", -1);
            Iterator<RewardCalendar> iterator = calendars.iterator();

            while (iterator.hasNext()) {
                RewardCalendar calendar = iterator.next();
                final boolean wipeFailed = calendar.wipe();
                if (wipeFailed) {
                    needsWipe = true;
                }
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
            needsWipe = true;
        }

        return needsWipe;
    }
}
