package helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jooq.tools.json.JSONObject;

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

        String calendarType = (String) event.get("calendarType");
        String receiverKey = (String) event.get("receiverKey");
        String giverKey = (String) event.get("giverKey");
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

        String calendarType = (String) event.get("calendarType");
        String receiverKey = (String) event.get("receiverKey");

        LinkedList<RewardCalendar> calendars = getCalendars(calendarType, 1);
        Iterator<RewardCalendar> iterator = calendars.iterator();

        try {
            while (iterator.hasNext()) {
                RewardCalendar calendar = iterator.next();
                if (calendar == null || !calendar.isActive() || !calendar.isClaimActive() || calendar.needsWipe()) {
                    continue;
                }

                final String requiredRole = calendar.getRequiredRole();
                if (requiredRole != null && !PermsManager.isPlayerInGroup(player, requiredRole)) {
                    continue;
                }

                final ArrayList<String> items = (ArrayList<String>) event.getOrDefault("items", new ArrayList<>());
                for (String item : items) {
                    final String[] split = item.split(" ");
                    final String ITEM_NAME = split[0].toUpperCase();

                    switch (ITEM_NAME) {
                        case "EXPERIENCE_ORB":
                            final int amount = Integer.parseInt(split[1]);
                            StatsManager.giveXp(player, amount);
                            break;

                        case "MONEY":
                            final double value = Double.parseDouble(split[1]);
                            EconomyManager.depositPlayer(player, value);
                            break;

                        default:
                            ArrayList<String> extraLore = new ArrayList<String>();
                            extraLore.add("Acquired via reward system.");

                            final ItemStack itemStack = BukkitManager.castItemStack(ITEM_NAME, 10, extraLore, null);
                            BukkitManager.givePlayerItem(player, itemStack);
                            break;
                    }
                }
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
