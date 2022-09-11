package models;

import java.sql.Timestamp;
import java.util.logging.Logger;

import org.json.JSONObject;

import dao.UsersDao;
import helpers.Helper;

public class User {
    private Integer id = -1;
    private String mcName;
    private String discordId;
    private Integer acceptedBy;
    private Integer revokedBy;
    private boolean checked;
    private boolean confirmed;
    private String mcUUID;
    private String msgId;

    private String createdAt;
    private String updatedAt;

    private Logger logger = Logger.getLogger("JWE:" + this.getClass().getName());

    public User() {
        this.checked = false;
        this.confirmed = false;
        this.createdAt = Helper.getTimestamp().toString();
        this.updatedAt = Helper.getTimestamp().toString();
    }

    public User(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.mcName = json.getString("mcName");
            this.discordId = json.getString("discordId");
            this.acceptedBy = json.getInt("acceptedBy");
            this.revokedBy = json.getInt("revokedBy");
            this.checked = json.getBoolean("checked");
            this.confirmed = json.getBoolean("confirmed");
            this.mcUUID = json.getString("mcUUID");
            this.msgId = json.getString("msgId");
            this.createdAt = json.getString("createdAt");
            this.updatedAt = json.getString("updatedAt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User deepCopy(User userObj) {
        User copied = new User();
        try {
            copied.id = userObj.id;
            copied.mcName = userObj.mcName;
            copied.discordId = userObj.discordId;
            userObj.acceptedBy = copied.acceptedBy;
            userObj.revokedBy = copied.revokedBy;
            copied.checked = userObj.checked;
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

    public Integer save() {
        this.updatedAt = Helper.getTimestamp().toString();
        return this.getDao().SaveUser(this.toJson());
    }

    public JSONObject toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", this.id);
            jsonObj.put("mcName", this.mcName);
            jsonObj.put("discordId", this.discordId);
            jsonObj.put("acceptedBy", this.acceptedBy);
            jsonObj.put("revokedBy", this.revokedBy);
            jsonObj.put("checked", this.checked);
            jsonObj.put("confirmed", this.confirmed);
            jsonObj.put("mcUUID", this.mcUUID);
            jsonObj.put("msgId", this.msgId);
            jsonObj.put("createdAt", this.createdAt);
            jsonObj.put("updatedAt", this.updatedAt);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    public boolean setAschecked(String msgId) {
        if (this.checked) {
            return true;
        }

        this.checked = msgId.length() > 0
                && this.id > 0
                && this.acceptedBy > 0
                && this.mcName.length() > 0
                && this.createdAt.length() > 0
                && this.discordId.length() > 0;

        return this.checked;
    }

    public boolean setAsconfirmed(String msgId) {
        if (this.confirmed) {
            return true;
        }

        this.confirmed = this.checked
                && this.mcUUID.length() == 36
                && this.updatedAt.length() > 0;

        final Timestamp comparator = Helper.convertStringToTimestamp(this.createdAt);
        this.confirmed = Helper.isWithin24Hour(comparator);

        return this.confirmed;
    }

    private UsersDao getDao() {
        return new UsersDao();
    }
}
