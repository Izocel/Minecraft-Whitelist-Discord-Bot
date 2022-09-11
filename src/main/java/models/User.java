package models;

import java.util.UUID;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

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


    public User() {

    }

    public User(Integer id) {
        if(id < 1) {
            new User();
            return;
        } 
    }

    public User(JSONObject json) {
        try {
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
