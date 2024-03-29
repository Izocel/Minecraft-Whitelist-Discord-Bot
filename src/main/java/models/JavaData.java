package models;

import org.json.JSONObject;

import helpers.Helper;
import services.sentry.SentryService;

public class JavaData extends BaseModel {
    private Integer id = -1;
    private Integer userId = -1;
    private String pseudo;
    private String uuid;
    private String acceptedBy;
    private String revokedBy;
    private boolean allowed;
    private boolean confirmed;
    private String msgId;
    private String createdAt;
    private String updatedAt;
    private String avatarUrl;

    public JavaData() {
        this.allowed = false;
        this.confirmed = false;
        this.createdAt = Helper.getTimestamp().toString();
    }

    public JavaData(JSONObject json) {
        this.id = json.getInt("id");
        this.userId = json.getInt("user_id");
        this.pseudo = json.getString("pseudo");
        this.uuid = json.getString("uuid");
        this.acceptedBy = json.optString("accepted_by");
        this.revokedBy = json.optString("revoked_by");
        this.msgId = json.getString("msg_id");
        this.createdAt = json.optString("created_at");
        this.updatedAt = json.optString("updated_at");
        this.avatarUrl = json.optString("avatar_url");

        final Object isConfirmed = json.opt("confirmed");
        final Object isAllowed = json.opt("allowed");

        this.confirmed = isConfirmed != null ? isConfirmed.toString().equals("1")
                || (int) isConfirmed == 1 : false;

        this.allowed = isAllowed != null ? isAllowed.toString().equals("1")
                || (int) isAllowed == 1 : false;
    }

    public boolean setAsAllowed(String msgId, boolean allowed, String moderatorId) {

        this.msgId = msgId;

        if (allowed == false && moderatorId.length() > 0) {
            this.confirmed = false;
            this.allowed = false;
            this.acceptedBy = null;
            this.revokedBy = moderatorId;
            return this.allowed;
        }

        this.allowed = msgId.length() > 0
                && this.pseudo.length() > 0
                && this.uuid.length() > 0
                && moderatorId.length() > 0;

        this.acceptedBy = moderatorId;
        return this.allowed;
    }

    public boolean setAsConfirmed(boolean confirmed) {

        if (confirmed == false) {
            this.confirmed = false;
            return this.confirmed;
        }

        this.confirmed = this.allowed
                && this.acceptedBy.length() > 0
                && this.uuid.length() == 36;

        return this.confirmed;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public Integer setUserId(Integer id) {
        this.userId = id;
        return userId;
    }

    public String getPseudo() {
        return this.pseudo;
    }

    public void setMcName(String pseudo) {
        this.pseudo = pseudo;
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

    public void setRevokedBy(String moderatorId) {
        this.revokedBy = moderatorId;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String url) {
        this.avatarUrl = url;
    }

    public boolean isAllowed() {
        return this.allowed;
    }

    public boolean isConfirmed() {
        return this.confirmed;
    }

    public String getUUID() {
        return this.uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
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
        if (moderatorId == null) {
            moderatorId = "#666-TheChancelor";
        }
        this.setAsAllowed("order-66", false, moderatorId);
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", this.id);
            jsonObj.put("user_id", this.userId);
            jsonObj.put("pseudo", this.pseudo);
            jsonObj.put("uuid", this.uuid);
            jsonObj.put("accepted_by", this.acceptedBy);
            jsonObj.put("revoked_by", this.revokedBy);
            jsonObj.put("allowed", this.allowed);
            jsonObj.put("confirmed", this.confirmed);
            jsonObj.put("msg_id", this.msgId);
            jsonObj.put("created_at", this.createdAt);
            jsonObj.put("updated_at", this.updatedAt);
            jsonObj.put("avatar_url", this.avatarUrl);

        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return jsonObj;
    }

    @Override
    public JavaData deepCopy(BaseModel model) {
        JavaData copied = new JavaData();
        JavaData userObj = (JavaData) model;
        try {
            copied.id = userObj.id;
            copied.pseudo = userObj.pseudo;
            copied.uuid = userObj.uuid;
            userObj.acceptedBy = copied.acceptedBy;
            userObj.revokedBy = copied.revokedBy;
            copied.allowed = userObj.allowed;
            copied.confirmed = userObj.confirmed;
            copied.msgId = userObj.msgId;
            copied.createdAt = userObj.createdAt;
            copied.updatedAt = userObj.updatedAt;
            copied.avatarUrl = userObj.avatarUrl;

        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return copied;
    }

}
