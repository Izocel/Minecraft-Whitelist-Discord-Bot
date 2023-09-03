package models;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import services.sentry.SentryService;

public class NotificationData extends Object {
    public String url;
    public String email;
    public String topic;
    public String message;
    public String title;
    public String attach;
    public String delay;
    public String icon;
    public Boolean markdown;
    public Integer priority;
    public JSONArray tags = new JSONArray();

    // [{ "action": "view", "label": "Admin panel", "url":
    // "https://rvdprojects.synology.me:3000/#/dashboard" }]
    public JSONArray actions = new JSONArray();

    public NotificationData(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public Object addViewAction(String actionLabel, String actionUrl) {
        final JSONObject action = new JSONObject();
        action.put("action", "view");
        action.put("label", actionLabel);
        action.put("url", actionUrl);

        this.actions.add(action);
        return action;
    }

    public JSONObject toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("title", this.title);
            jsonObj.put("message", this.message);
            jsonObj.put("topic", this.topic);
            jsonObj.put("url", this.url);

            jsonObj.put("attach", this.attach);
            jsonObj.put("priority", this.priority);
            jsonObj.put("email", this.email);
            jsonObj.put("markdown", this.markdown);
            jsonObj.put("delay", this.delay);
            jsonObj.put("icon", this.icon);

            jsonObj.put("tags", this.tags);
            jsonObj.put("actions", this.actions);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return jsonObj;
    }
}
