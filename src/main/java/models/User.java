package models;

import org.json.JSONArray;
import org.json.JSONObject;


import dao.DaoManager;
import net.dv8tion.jda.api.entities.Member;
import services.sentry.SentryService;

public class User extends BaseModel {
    private Integer id = -1;
    private String discordId;
    private String discordTag;
    private String lang = "fr";
    private String createdAt;
    private String updatedAt;

    private JSONArray javaData = new JSONArray();
    private JSONArray bedData = new JSONArray();

    public User() {}

    public User(JSONObject json) {
        this.id = json.getInt("id");
        this.discordId = json.getString("discord_id");
        this.discordTag = json.getString("discord_tag");
        this.createdAt = json.optString("created_at");
        this.updatedAt = json.optString("updated_at");
        this.lang = json.getString("lang");

        this.javaData = DaoManager.getJavaDataDao().findWithUser(id);
        this.bedData = DaoManager.getBedrockDataDao().findWithUser(id);
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public String getDiscordId() {
        return this.discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getDiscordTag() {
        return this.discordTag;
    }

    public void setDiscordTag(String discordTag) {
        this.discordTag = discordTag;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", this.id);
            jsonObj.put("discord_id", this.discordId);
            jsonObj.put("discord_tag", this.discordTag);
            jsonObj.put("javaData", this.javaData);
            jsonObj.put("bedData", this.bedData);
            jsonObj.put("created_at", this.createdAt);
            jsonObj.put("updated_at", this.updatedAt);
            jsonObj.put("lang", this.lang);

        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return jsonObj;
    }

    @Override
    public User deepCopy(BaseModel model) {
        User copied = new User();
        User userObj = (User) model;
        try {
            copied.id = userObj.id;
            copied.discordId = userObj.discordId;
            copied.discordTag = userObj.discordTag;
            copied.lang = userObj.lang;
            copied.createdAt = userObj.createdAt;
            copied.updatedAt = userObj.updatedAt;

        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return copied;
    }

    public Integer saveUser() {
        return this.save(DaoManager.getUsersDao());
    }

    public static User getFromMember(Member member) {
        final net.dv8tion.jda.api.entities.User userDc = member.getUser();
        final String userId = userDc.getId();

        User user = DaoManager.getUsersDao().findByDiscordId(userId);

        if (user == null || user.getId() < 1) {
            return null;
        }

        return user;
    }

    public static User updateFromMember(Member member) {
        final net.dv8tion.jda.api.entities.User userDc = member.getUser();
        final String userId = userDc.getId();
        final String userTag = userDc.getAsTag();

        User user = DaoManager.getUsersDao().findByDiscordId(userId);

        if (user == null || user.getId() < 1) {
            user = new User();
        }

        user.setDiscordId(userId);
        user.setDiscordTag(userTag);
        user.saveUser();

        return getFromMember(member);
    }

    public JavaData getJavaData(String uuid) {
        for (int i = 0; i < javaData.length(); i++) {
            if(javaData.getJSONObject(i).optString("uuid").equals(uuid))
                return new JavaData(javaData.getJSONObject(i));
        }
        return null;
    }

    public BedrockData getBedrockData(String uuid) {
        for (int i = 0; i < javaData.length(); i++) {
            if(bedData.getJSONObject(i).optString("uuid").equals(uuid))
                return new BedrockData(bedData.getJSONObject(i));
        }
        return null;
    }

    public boolean isAllowedJava(String uuid) {
        return this.getJavaData(uuid).isAllowed();
    }

    public boolean isAllowedBedrock(String uuid) {
        return this.getBedrockData(uuid).isAllowed();
    }

    public boolean isConfirmedJava(String uuid) {
        return this.getJavaData(uuid).isConfirmed();
    }

    public boolean isConfirmedBedrock(String uuid) {
        return this.getBedrockData(uuid).isConfirmed();
    }

    public boolean isAllowed(String type, String uuid) {
        type = type != null ? type.toLowerCase() : "all";

        if(uuid == null) {
            return false;
        }

        switch (type) {
            case "java":
                return isAllowedJava(uuid);
            
            case "bedrock":
                return isAllowedBedrock(uuid);
        
            default:
                return false;
        }
    }
}
