package helpers;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import services.sentry.SentryService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class Helper {

    public static long dayMSLONG = 86400000;
    public static long hourMSLONG = dayMSLONG / 24;

    public static boolean isNumeric(String string) {
        return string.matches("^[0-9]+$");
    }

    public static boolean isAlphanumeric(String string) {
        return string.matches("^[a-zA-Z0-9]+$");
    }

    public static boolean isMcPseudo(String string) {
        return string.matches("^[a-zA-Z0-9_-]+{2,16}");
    }

    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp convertStringToTimestamp(String strDate) {
        try {
            strDate = strDate.replace("T", " ");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(strDate);

            return new Timestamp(date.getTime());
        } catch (Exception e) {
            SentryService.captureEx(e);
            return null;
        }
    }

    public static boolean isWithin24Hour(Timestamp comparator) {
        final long now = getTimestamp().getTime();
        final long end = comparator.getTime() + dayMSLONG;
        return end > now;
    }

    public static boolean isWithin48Hour(Timestamp comparator) {
        final long now = getTimestamp().getTime();
        final long end = comparator.getTime() + (dayMSLONG * 2);
        return end > now;
    }

    public static boolean isWithinXXHour(Timestamp comparator, Integer xHours) {

        if (xHours == null || xHours < 1) {
            return true;
        }

        final long now = getTimestamp().getTime();
        final long end = comparator.getTime() + (xHours * hourMSLONG);
        return end > now;
    }

    public static boolean isWithinXXSecond(Timestamp comparator, Integer xSecond) {
        if (xSecond == null || xSecond < 1) {
            return true;
        }

        final long now = getTimestamp().getTime();
        final long end = comparator.getTime() + (1000 * xSecond);
        return end > now;
    }

    public static MessageAction preparePrivateCustomMsg(PrivateChannel channel, MessageEmbed embededs, ArrayList<ActionRow> actionRows) {
        if (embededs.isSendable()) {
            return channel.sendMessageEmbeds(embededs).setActionRows(actionRows);
        }
        return null;
    }

    public static MessageAction prepareTectCustomMsg(TextChannel channel, MessageEmbed embededs, ArrayList<ActionRow> actionRows) {
        if (embededs.isSendable()) {
            return channel.sendMessageEmbeds(embededs).setActionRows(actionRows);
        }
        return null;
    }

    public static ArrayList<ActionRow> getActionRowsfromJson(String jsonString) {

        JsonObject json = new Gson().fromJson(jsonString, JsonObject.class);

        ArrayList<ActionRow> rows = new ArrayList<ActionRow>();
        JsonArray actionRowsArray = json.getAsJsonArray("components");

        // Loop over the components array
        if (actionRowsArray != null) {

            for (int i = 0; i < actionRowsArray.size(); i++) {

                ArrayList<Component> components = new ArrayList<Component>();
                JsonArray componentsArray = actionRowsArray.get(i).getAsJsonObject()
                    .get("components").getAsJsonArray();

                for (int j = 0; j < componentsArray.size(); j++) {

                    JsonObject comp = componentsArray.get(j).getAsJsonObject();
                    
                    if (comp == null) {
                        break;
                    }

                    final Integer type = comp.get("type").getAsInt();

                    // Buttons
                    if (type == 2) {
                        components.add(getButtonFromComponent(comp));
                    }
                    // drowpdown list
                    else {
                        try {
                            throw new Exception("Don't touch the source code if you don't know what your doing!!!");
                        } catch (Exception e) {
                            SentryService.captureEx(e);
                        }
                    }

                }

                if (components.size() > 0) {
                    rows.add(ActionRow.of(components));
                }

            }
        }

        return rows.size() > 0 ? rows : null;
    }

    private static Button getButtonFromComponent(JsonObject comp) {
        final String label = comp.get("label").getAsString();
        final String id = comp.get("custom_id").getAsString();

        final JsonElement style = comp.get("style");
        final JsonElement disabled = comp.get("disabled");
        final JsonElement url = comp.get("url");

        Component resolved = null;
        if (style != null) {

            switch (style.getAsInt()) {
                case 1:
                    resolved = Button.primary(id, label);
                    break;
                case 2:
                    resolved = Button.secondary(id, label);
                    break;
                case 3:
                    resolved = Button.success(id, label);
                    break;
                case 4:
                    resolved = Button.danger(id, label);
                    break;

                default:
                    resolved = Button.link(url == null ? "" : url.getAsString(), label);
                    break;
            }
        }

        Button button = (Button) resolved;
        if (disabled != null && disabled.getAsBoolean() == true) {
            return button.asDisabled();
        }
        return button;
    }

    /**
     * Converts a {@link JsonObject} to {@link MessageEmbed}.
     * Supported Fields: Title, Author, Description, Color, Fields, Thumbnail,
     * Footer.
     * 
     * @param json The JsonObject
     * @return The Embed
     */
    public static MessageEmbed jsonToMessageEmbed(String jsonString) {

        JsonObject json = new Gson().fromJson(jsonString, JsonObject.class);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        JsonArray embedsArray = json.getAsJsonArray("embeds");

        // Loop over the embeds array
        if (embedsArray != null) {
            embedsArray.forEach(ele -> {
                JsonObject obj = ele.getAsJsonObject();

                JsonPrimitive titleObj = obj.getAsJsonPrimitive("title");
                JsonPrimitive urlObj = obj.getAsJsonPrimitive("url");

                if (titleObj != null) {
                    embedBuilder.setTitle(
                            titleObj.getAsString(),
                            urlObj == null ? null : urlObj.getAsString());
                }

                JsonObject imageObj = obj.getAsJsonObject("image");
                if (imageObj != null) {
                    String url = imageObj.get("url").getAsString();

                    if (url != null)
                        embedBuilder.setImage(url);
                }

                JsonObject authorObj = obj.getAsJsonObject("author");
                if (authorObj != null) {
                    String authorName = authorObj.get("name").getAsString();
                    String authorIconUrl = authorObj.get("icon_url").getAsString();

                    if (authorIconUrl != null)
                        embedBuilder.setAuthor(authorName, null, authorIconUrl);
                    else
                        embedBuilder.setAuthor(authorName);
                }

                JsonPrimitive descObj = obj.getAsJsonPrimitive("description");
                if (descObj != null) {
                    embedBuilder.setDescription(descObj.getAsString());
                }

                JsonPrimitive colorObj = obj.getAsJsonPrimitive("color");
                if (colorObj != null) {
                    final String hex = colorObj.getAsString();
                    embedBuilder.setColor(new Color(Integer.parseInt(hex, 16)));
                }

                JsonArray fieldsArray = obj.getAsJsonArray("fields");
                if (fieldsArray != null) {

                    fieldsArray.forEach(field -> {
                        String name = field.getAsJsonObject().get("name").getAsString();
                        String content = field.getAsJsonObject().get("value").getAsString();
                        boolean inline = field.getAsJsonObject().get("inline").getAsBoolean();
                        embedBuilder.addField(name, content, inline);
                    });
                }

                JsonPrimitive thumbnailObj = obj.getAsJsonPrimitive("thumbnail");
                if (thumbnailObj != null) {
                    embedBuilder.setThumbnail(thumbnailObj.getAsString());
                }

                JsonObject footerObj = obj.getAsJsonObject("footer");
                if (footerObj != null) {
                    String content = footerObj.get("text").getAsString();
                    JsonElement footerIconUrl = footerObj.get("icon_url");

                    if (footerIconUrl != null)
                        embedBuilder.setFooter(content, footerIconUrl.getAsString());
                    else
                        embedBuilder.setFooter(content);
                }
            });
        }

        MessageEmbed newEmbeded = embedBuilder.build();
        if (newEmbeded.isSendable()) {
            return newEmbeded;
        }

        return null;
    }

}
