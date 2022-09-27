package models;

import java.sql.Timestamp;
import java.util.logging.Logger;

import org.json.JSONObject;

import helpers.Helper;

public class User extends BaseModel {
    private Integer id = -1;
    private String mcName;
    private String discordId;
    private String acceptedBy;
    private String revokedBy;
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
        this.discordId = json.optString("discord_id");
        this.acceptedBy = json.optString("accepted_by");
        this.revokedBy = json.optString("revoked_by");
        this.mcUUID = json.optString("mc_uuid");
        this.msgId = json.optString("msg_id");
        this.createdAt = json.optString("created_at");
        this.updatedAt = json.optString("updated_at");

        final Object isConfirmed = json.opt("confirmed");
        final Object isAllowed = json.opt("allowed");

        this.confirmed = isConfirmed != null ?
            isConfirmed.toString() == "1" 
            || (int)isConfirmed == 1 : false;

        this.allowed = isAllowed != null ?
            isAllowed.toString() == "1" 
            || (int)isAllowed == 1 : false;
    }

    public boolean setAsAllowed(String msgId, boolean allowed, String moderatorId) {

        this.msgId = msgId;

        if(allowed == false && moderatorId.length() > 0) {
            this.confirmed = false;
            this.allowed = false;
            this.acceptedBy = null;
            this.revokedBy = moderatorId;
            return this.allowed;
        }
        
        this.allowed = msgId.length() > 0
                && this.mcName.length() > 0
                && this.discordId.length() > 0
                && moderatorId.length() > 0;
        
        this.acceptedBy = moderatorId;
        return this.allowed;
    }

    public boolean setAsConfirmed(boolean confirmed) {

        if(confirmed == false) {
            this.confirmed = false;
            return this.confirmed;
        }

        this.confirmed = this.allowed
                && this.acceptedBy.length() > 0
                && this.mcUUID.length() == 36
                && this.updatedAt.length() > 0;

        final Timestamp comparator = Helper.convertStringToTimestamp(this.createdAt);
        this.confirmed = Helper.isWithin24Hour(comparator);

        return this.confirmed;
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

	public String getDiscordId() {
		return this.discordId;
	}

	public void setDiscordId(String discordId) {
		this.discordId = discordId;
	}

	public String getAcceptedBy() {
		return this.acceptedBy;
	}

	public void setAcceptedBy(String acceptedBy) {
		this.acceptedBy = acceptedBy;
	}

	public String getRevokedBy() {
		return this.revokedBy;
	}

	public void setRevokedBy(String revokedBy) {
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

    public void executeOrder66(String moderatorId) {
        if(moderatorId == null) {
            moderatorId = "#666-TheChancelor";
        }
        this.setAsAllowed("order-66", false, moderatorId);
    }


    @Override
    public JSONObject toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", this.id);
            jsonObj.put("mc_name", this.mcName);
            jsonObj.put("discord_id", this.discordId);
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

    @Override
    public User deepCopy(BaseModel model) {
        User copied = new User();
        User userObj = (User) model;
        try {
            copied.id = userObj.id;
            copied.mcName = userObj.mcName;
            copied.discordId = userObj.discordId;
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
}