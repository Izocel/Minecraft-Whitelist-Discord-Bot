package models;

import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Reward {
    private int id;
    private String calendarName;
    private String calendarType;
    private String qualifiedWith;
    private String recurseEvery;
    private String recurseOnlyUntil;
    private String requiredRole;

    private JsonArray items;
    private JsonArray recurseItems;

    public Reward(JsonObject data , String calendarName, String calendarType) {
        this.calendarName = calendarName;
        this.calendarType = calendarType;

        id = data.get("id") != null
                ? data.get("id").getAsInt()
                : null;

        qualifiedWith = data.get("qualifiedWith") != null
                ? data.get("qualifiedWith").getAsString()
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
