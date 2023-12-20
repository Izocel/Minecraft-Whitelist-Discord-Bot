package models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Reward {
    private Integer qualifiedAt;
    private JsonArray items;

    public Reward(JsonObject data) {
        qualifiedAt = data.get("qualifiedAt") != null
                ? data.get("qualifiedAt").getAsInt()
                : null;

        items = data.get("items") != null
                ? data.get("items").getAsJsonArray()
                : new JsonArray();
    }

}
