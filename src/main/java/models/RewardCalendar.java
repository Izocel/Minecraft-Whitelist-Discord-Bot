package models;

import java.util.LinkedList;

import org.jooq.tools.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// https://minecraft-ids.grahamedgecombe.com/
public class RewardCalendar {
    private boolean active;
    private boolean needsWipe;
    private boolean claimsActive;
    private String name;
    private String type;
    private String calendarStop;
    private String calendarStart;
    private String claimableUntil;
    private LinkedList<Reward> rewards = new LinkedList<>();

    public RewardCalendar(Object object, String name) {
        if (object == null) {
            return;
        }

        String jsonString = new Gson().toJson(object);
        JsonObject data = new Gson().fromJson(jsonString, JsonObject.class);

        this.name = name;
        active = data.get("active") != null
                ? data.get("active").getAsBoolean()
                : false;
        needsWipe = data.get("wipeNow") != null
                ? data.get("wipeNow").getAsBoolean()
                : false;

        type = data.get("type") != null
                ? data.get("type").getAsString()
                : null;
        claimsActive = data.get("claimsActive") != null
                ? data.get("claimsActive").getAsBoolean()
                : null;
        calendarStop = data.get("calendarStop") != null
                ? data.get("calendarStop").getAsString()
                : null;
        calendarStart = data.get("calendarStart") != null
                ? data.get("calendarStart").getAsString()
                : null;
        claimableUntil = data.get("claimableUntil") != null
                ? data.get("claimableUntil").getAsString()
                : null;

        final JsonArray rewardData = data.get("rewards") != null
                ? data.get("rewards").getAsJsonArray()
                : null;

        for (int i = 0; i < rewardData.size(); ++i) {
            JsonObject r_data = rewardData.get(i).getAsJsonObject();
            rewards.add(new Reward(r_data));
        }

        if (needsWipe) {
            return;
        }

        if (!active) {
            return;
        }
    }

    public boolean isActive() {
        return active;
    }

    public String getType() {
        return type;
    }

    public LinkedList<Reward> getRewards() {
        return rewards;
    }

    public void parsePrepareEvent(JSONObject event) {
        for (int i = 0; i < rewards.size(); ++i) {

        }
    }

    public void parseClaimEvent(JSONObject event) {

    }
}
