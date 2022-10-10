package models;

import org.json.JSONObject;

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

        this.bedData = bedData.initialize(this.id);
        this.javaData = javaData.initialize(this.id);
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
}
