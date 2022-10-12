package models;

import org.json.JSONObject;

import dao.DaoManager;
import net.dv8tion.jda.api.entities.Member;
import services.sentry.SentryService;

public class User extends BaseModel {
    private Integer id = -1;
    private String discordId;
    private String discordTag;
    private String lang;
    private String createdAt;
    private String updatedAt;

    private BedrockData bedData = new BedrockData();
    private JavaData javaData = new JavaData();

    public User() {
    }

    public User(JSONObject json) {
        this.id = json.optInt("id");
        this.discordId = json.optString("discord_id");
        this.discordTag = json.optString("discord_tag");
        this.createdAt = json.optString("created_at");
        this.updatedAt = json.optString("updated_at");
        this.lang = json.optString("lang");

        this.bedData = bedData.getBedrockData(this.id);
        this.javaData = javaData.getJavaData(this.id);
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
            jsonObj.put("lang", this.lang);
            jsonObj.put("created_at", this.createdAt);
            jsonObj.put("updated_at", this.updatedAt);

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

    public static User updateFromMember(Member member) {
        final net.dv8tion.jda.api.entities.User userDc = member.getUser();
        final String userId = userDc.getId();
        final String userTag = userDc.getAsTag();

        User user = DaoManager.getUsersDao().findByDisccordTag(userTag);

        if (user == null || user.getId() < 1) {
            user = new User();
        }

        user.setDiscordId(userId);
        user.setDiscordTag(userTag);

        return user;
    }

    public String getJavaUuid() {
        return this.javaData.getUUID();
    }

    public String getBedrockUuid() {
        return this.bedData.getUUID();
    }

    public boolean isAllowedJava() {
        return this.javaData.isAllowed();
    }

    public boolean isAllowedBedrock() {
        return this.bedData.isAllowed();
    }

    public boolean isConfirmedJava() {
        return this.javaData.isConfirmed();
    }

    public boolean isConfirmedBedrock() {
        return this.bedData.isConfirmed();
    }

    public boolean isAllowed() {
        return isAllowedBedrock() || isAllowedJava();
    }

    public boolean isConfirmed() {
        return isConfirmedJava() || isConfirmedBedrock();
    }
}
