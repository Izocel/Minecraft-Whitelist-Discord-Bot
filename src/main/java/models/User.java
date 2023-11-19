package models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import dao.DaoManager;
import main.WhitelistDmcNode;
import services.sentry.SentryService;

public class User extends BaseModel {
    private Integer id = -1;
    private String discordId;
    private String discordTag;
    private String lang;
    private String createdAt;
    private String updatedAt;

    private JSONArray javaData = new JSONArray();
    private JSONArray bedData = new JSONArray();
    private String avatarUrl;

    public User() {
    }

    public User(String discordId, String discordTag) {
        this.setDiscordId(discordId);
        this.setDiscordTag(discordTag);
        this.setLang(WhitelistDmcNode.LOCALES.getDefaultLang());
    }

    public User(JSONObject json) {
        this.id = json.getInt("id");
        this.discordId = json.getString("discord_id");
        this.discordTag = json.getString("discord_tag");
        this.avatarUrl = json.optString("avatar_url");
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

    public void setAvatarUrl(String url) {
        this.avatarUrl = url;
    }

    public String getDiscordTag() {
        return this.discordTag;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
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

    public String getLang() {
        return this.lang.toUpperCase();
    }

    public void setLang(String lang) {
        if (WhitelistDmcNode.LOCALES.isUserSupported(lang))
            this.lang = lang.toUpperCase();
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", this.id);
            jsonObj.put("lang", this.lang);
            jsonObj.put("discord_id", this.discordId);
            jsonObj.put("discord_tag", this.discordTag);
            jsonObj.put("avatar_url", this.avatarUrl);
            jsonObj.put("created_at", this.createdAt);
            jsonObj.put("updated_at", this.updatedAt);
            jsonObj.put("javaData", this.javaData);
            jsonObj.put("bedData", this.bedData);

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
            copied.avatarUrl = userObj.avatarUrl;
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

    public Integer deleteUser() {
        return this.delete(DaoManager.getUsersDao());
    }

    public JSONArray getJavaData() {
        return this.javaData;
    }

    public JSONArray getBedrockData() {
        return this.bedData;
    }

    public JavaData getJavaData(String uuid) {
        for (int i = 0; i < javaData.length(); i++) {
            if (javaData.getJSONObject(i).optString("uuid").equals(uuid))
                return new JavaData(javaData.getJSONObject(i));
        }
        return null;
    }

    public BedrockData getBedrockData(String uuid) {
        for (int i = 0; i < bedData.length(); i++) {
            if (bedData.getJSONObject(i).optString("uuid").equals(uuid))
                return new BedrockData(bedData.getJSONObject(i));
        }
        return null;
    }

    public boolean isAllowed(String uuid) {
        return this.getJavaData(uuid).isAllowed() ||
                this.getBedrockData(uuid).isAllowed();
    }

    public boolean isConfirmed(String uuid) {
        return this.getJavaData(uuid).isConfirmed() ||
                this.getBedrockData(uuid).isConfirmed();
    }

    public boolean hasPlayer(String uuid) {
        return this.getJavaData(uuid) != null || this.getBedrockData(uuid) != null;
    }

    public Player getOnlinePlayer(String uuid) {
        if (!this.hasPlayer(uuid))
            return null;
        return Bukkit.getPlayer(UUID.fromString(uuid));
    }

    public OfflinePlayer getOfflinePlayer(String uuid) {
        if (!this.hasPlayer(uuid))
            return null;
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }
}
