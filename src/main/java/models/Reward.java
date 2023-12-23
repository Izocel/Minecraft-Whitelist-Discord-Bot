package models;

import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Reward {
    private String qualifiedAt;
    private String recurseEvery;
    private String recurseOnlyUntil;
    private String requiredRole;

    private JsonArray items;
    private JsonArray recurseItems;

    public Reward(JsonObject data) {
        qualifiedAt = data.get("qualifiedAt") != null
                ? data.get("qualifiedAt").getAsString()
                : null;

        recurseEvery = data.get("recurseEvery") != null
                ? data.get("recurseEvery").getAsString()
                : null;

        recurseOnlyUntil = data.get("recurseOnlyUntil") != null
                ? data.get("recurseOnlyUntil").getAsString()
                : null;

        requiredRole = data.get("requiredRole") != null
                ? data.get("requiredRole").getAsString()
                : null;

        items = data.get("items") != null
                ? data.get("items").getAsJsonArray()
                : new JsonArray();

        recurseItems = data.get("recurseItems") != null
                ? data.get("recurseItems").getAsJsonArray()
                : items;
    }

    public LinkedList<String> getItems() {
        LinkedList<String> itemList = new LinkedList<>();
        for (int i = 0; i < items.size(); i++) {
            itemList.addLast(items.get(i).getAsString());
        }

        return itemList;
    }

    public LinkedList<String> getRecurseItems() {
        LinkedList<String> itemList = new LinkedList<>();
        for (int i = 0; i < recurseItems.size(); i++) {
            itemList.addLast(recurseItems.get(i).getAsString());
        }

        return itemList;
    }

}
