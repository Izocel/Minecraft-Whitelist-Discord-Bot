package models;

import java.sql.Timestamp;

import org.json.JSONObject;

import dao.UsersDao;
import helpers.Helper;

public class User {
    private Integer id = -1;
    private String mcName;
    private String discordTag;
    private Integer acceptedBy;
    private Integer revokedBy;
    private boolean allowed;
    private boolean confirmed;
    private String mcUUID;
    private String msgId;
    private String createdAt;
    private String updatedAt;

	public User() {
        this.allowed = false;
        this.confirmed = false;
        this.createdAt = Helper.getTimestamp().toString();
    }

    public User(JSONObject json) {
        this.id = json.optInt("id");
        this.mcName = json.optString("mc_name");
        this.discordTag = json.optString("discord_tag");
        this.acceptedBy = json.optInt("accepted_by");
        this.revokedBy = json.optInt("revoked_by");
        this.mcUUID = json.optString("mc_uuid");
        this.msgId = json.optString("msg_id");
        this.createdAt = json.optString("created_at");
        this.updatedAt = json.optString("updated_at");

        this.confirmed = json.optBoolean("confirmed");
        this.allowed = json.optBoolean("allowed");
    }

	public User deepCopy(User userObj) {
        User copied = new User();
        try {
            copied.id = userObj.id;
            copied.mcName = userObj.mcName;
            copied.discordTag = userObj.discordTag;
            userObj.acceptedBy = copied.acceptedBy;
            userObj.revokedBy = copied.revokedBy;
            copied.allowed = userObj.allowed;
            copied.confirmed = userObj.confirmed;
            copied.mcUUID = userObj.mcUUID;
            copied.msgId = userObj.msgId;
            copied.createdAt = userObj.createdAt;
            copied.updatedAt = userObj.updatedAt;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return copied;
    }

    public JSONObject toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", this.id);
            jsonObj.put("mc_name", this.mcName);
            jsonObj.put("discord_tag", this.discordTag);
            jsonObj.put("accepted_by", this.acceptedBy);
            jsonObj.put("revoked_by", this.revokedBy);
            jsonObj.put("allowed", this.allowed);
            jsonObj.put("confirmed", this.confirmed);
            jsonObj.put("mc_uuid", this.mcUUID);
            jsonObj.put("msg_id", this.msgId);
            jsonObj.put("created_at", this.createdAt);
            jsonObj.put("updated_at", this.updatedAt);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    public boolean setAsAllowed(String msgId, boolean allowed, Integer moderatorId) {

        this.msgId = msgId;

        if(allowed == false) {
            this.confirmed = false;
            this.allowed = false;
            this.acceptedBy = null;
            this.revokedBy = moderatorId;
            return this.allowed;
        }
        
        this.allowed = msgId.length() > 0
                && this.id > 0
                && this.mcName.length() > 0
                && this.createdAt.length() > 0
                && this.discordTag.length() > 0;
        
        this.acceptedBy = moderatorId;
        return this.allowed;
    }

    public boolean setAsConfirmed(boolean confirmed) {

        if(confirmed == false) {
            this.confirmed = false;
            return this.confirmed;
        }

        this.confirmed = this.allowed
                && this.acceptedBy > 0
                && this.mcUUID.length() == 36
                && this.updatedAt.length() > 0;

        final Timestamp comparator = Helper.convertStringToTimestamp(this.createdAt);
        this.confirmed = Helper.isWithin24Hour(comparator);

        return this.confirmed;
    }

    public Integer save() {
        return this.getDao().saveUser(this.toJson());
    }

    public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMcName() {
		return this.mcName;
	}

	public void setMcName(String mcName) {
		this.mcName = mcName;
	}

	public String getDiscordTag() {
		return this.discordTag;
	}

	public void setDiscordTag(String discordTag) {
		this.discordTag = discordTag;
	}

	public Integer getAcceptedBy() {
		return this.acceptedBy;
	}

	public void setAcceptedBy(Integer acceptedBy) {
		this.acceptedBy = acceptedBy;
	}

	public Integer getRevokedBy() {
		return this.revokedBy;
	}

	public void setRevokedBy(Integer revokedBy) {
		this.revokedBy = revokedBy;
	}

	public boolean isAllowed() {
		return this.allowed;
	}

	public boolean isConfirmed() {
		return this.confirmed;
	}

	public String getMcUUID() {
		return this.mcUUID;
	}

	public void setMcUUID(String mcUUID) {
		this.mcUUID = mcUUID;
	}

	public String getMsgId() {
		return this.msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
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

    private UsersDao getDao() {
        return new UsersDao();
    }

    public void executeOrder66(Integer moderatorId) {
        if(moderatorId == null) {
            moderatorId = 1;
        }
        this.setAsAllowed("order-66", false, moderatorId);
    }
}
