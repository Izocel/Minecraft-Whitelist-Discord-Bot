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
import net.dv8tion.jda.api.entities.User;
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

    public void prepareRewardsFor(String calendarName, int discordId) {
        if (!active || needsWipe) {
            return;
        }

        RewardCalendar calendar = getCalendar(calendarName);
        if (calendar != null) {

        }
    }

    public void prepareAllRewardsFor(int discordId) {
        for (String key : calendarsData.keySet()) {

        }
    }

    public void parsePrepareEvent(User sender, JSONObject event) {
        if (!active || needsWipe) {
            return;
        }
        
        String calendarType = (String) event.get("calendarType");
        LinkedList<RewardCalendar> calendar = getCalendars(calendarType, 1);
        if (calendar.size() < 1) {
            return;
        }
    }

    public void parseClaimEvent(Player player, JSONObject event) {
        if (!active || needsWipe) {
            return;
        }

        String calendarType = (String) event.get("calendarType");
        String calendarName = (String) event.get("calendarName");
        RewardCalendar calendar = getCalendar(calendarName);

        if (!calendar.isActive() || !calendar.isClaimActive() || calendar.needsWipe()
                || !calendar.getType().equals(calendarType)) {
            return;
        }

        String requiredRole = (String) event.get("requiredRole");
        if (!PermsManager.isPlayerInGroup(player, requiredRole)) {
            return;
        }

        ArrayList<String> items = (ArrayList<String>) event.getOrDefault("items", new ArrayList());

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
