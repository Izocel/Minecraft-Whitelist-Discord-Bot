package models;

import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Referral {
        private int id;
        private String userKey;
        private String referralKey;
        private String invitedBy;
        private JsonArray referralsData;

        public Referral(JsonObject data) {
                id = data.get("id") != null
                                ? data.get("id").getAsInt()
                                : null;

                userKey = data.get("userKey") != null
                                ? data.get("userKey").getAsString()
                                : null;

                referralKey = data.get("referralKey") != null
                                ? data.get("referralKey").getAsString()
                                : null;

                invitedBy = data.get("invitedBy") != null
                                ? data.get("invitedBy").getAsString()
                                : null;

                referralsData = data.get("data") != null
                                ? data.get("data").getAsJsonArray()
                                : new JsonArray();
        }
}
