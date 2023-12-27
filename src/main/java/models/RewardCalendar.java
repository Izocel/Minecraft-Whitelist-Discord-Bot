package models;

import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class RewardCalendar {
    private boolean active;
    private boolean needsWipe = false;
    private boolean claimsActive = false;
    private String name;
    private String type;
    private String calendarStop;
    private String calendarStart;
    private String claimableUntil;
    private String requiredRole;

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

        if (!active) {
            return;
        }

        needsWipe = data.get("wipeNow") != null
                ? data.get("wipeNow").getAsBoolean()
                : false;

        type = data.get("type") != null
                ? data.get("type").getAsString()
                : null;

        claimsActive = data.get("claimsActive") != null
                ? data.get("claimsActive").getAsBoolean()
                : false;

        calendarStart = data.get("calendarStart") != null
                ? data.get("calendarStart").getAsString()
                : null;

        calendarStop = data.get("calendarStop") != null
                ? data.get("calendarStop").getAsString()
                : null;

        claimableUntil = data.get("claimableUntil") != null
                ? data.get("claimableUntil").getAsString()
                : null;

        requiredRole = data.get("requiredRole") != null
                ? data.get("requiredRole").getAsString()
                : null;

        final JsonArray rewardData = data.get("rewards") != null
                ? data.get("rewards").getAsJsonArray()
                : new JsonArray();

        for (int i = 0; i < rewardData.size(); ++i) {
            JsonObject r_data = rewardData.get(i).getAsJsonObject();
            rewards.add(new Reward(r_data, this.name, this.type));
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean needsWipe() {
        return needsWipe;
    }

    public boolean isClaimActive() {
        return claimsActive;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getRequiredRole() {
        return requiredRole;
    }

    public String getClaimableUntil() {
        return claimableUntil;
    }

    public LinkedList<Reward> getRewards() {
        return rewards;
    }

    public void publishReward(String qualification, String referralKey, String callerId) {
        // TODO: put rewards, referralId, callerId, type.calendarName into DB
    }

    public boolean wipe() {
        needsWipe = false;

        // TODO: change config.yml with needsWipe new value
        return needsWipe;
    }
}
