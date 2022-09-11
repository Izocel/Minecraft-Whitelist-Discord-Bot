package models;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import helpers.Helper;

public class User {
    private Integer id = -1;
    private String mcName;
    private String discordId;
    private User acceptedBy;
    private User revokedBy;
    private boolean allowed;
    private boolean confirmed;
    private String mcUUID;
    private String msgId;

    private String createdAt;
    private String updatedAt;

    private Logger logger = Logger.getLogger("JWE:" + this.getClass().getName());


    public User() {
        this.allowed = false;
        this.confirmed = false;
        this.createdAt = Helper.getTimestamp().toString();
        this.updatedAt = Helper.getTimestamp().toString();
    }

    public User(Integer id) {
        if(id < 1) {
            this.createdAt = Helper.getTimestamp().toString();
            this.updatedAt = Helper.getTimestamp().toString();
            this.allowed = false;
            this.confirmed = false;
            return;
        }
    }

    public User(JSONObject json) {
        try {
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer save() {
        return null;
    }

    public boolean setAsAllowed(String msgId) {
        if(this.allowed) {return true;}

        this.allowed = msgId.length() > 0
        && this.id > 0
        && this.acceptedBy.id > 0
        && this.mcName.length() > 0
        && this.createdAt.length() > 0
        && this.discordId.length() > 0;

        return this.allowed;
    }

    public boolean setAsconfirmed(String msgId) {
        if(this.confirmed) {return true;}

        this.confirmed = this.allowed
        && this.mcUUID.length() == 36
        && this.updatedAt.length() > 0;

        
        Timestamp Helper.convertStringToTimestamp(this.createdAt).getTime() + Helper.dayMSLONG;

        if(end - now > 0)

        return this.confirmed;
    }
}
