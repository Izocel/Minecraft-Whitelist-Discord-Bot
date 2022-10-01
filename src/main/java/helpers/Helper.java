package helpers;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

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
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(strDate);

            return new Timestamp(date.getTime());
        } catch (Exception e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    public static boolean isWithin24Hour(Timestamp comparator) {
        final long now = getTimestamp().getTime();
        final long end = comparator.getTime() + dayMSLONG;
        return end < now;
    }

    public static boolean isWithin48Hour(Timestamp comparator) {
        final long now = getTimestamp().getTime();
        final long end = comparator.getTime() + (dayMSLONG * 2);
        return end < now;
    }

    public static boolean isWithinXXHour(Timestamp comparator, Integer xHours) {

        if (xHours == null || xHours < 1) {
            return true;
        }

        final long now = getTimestamp().getTime();
        final long end = comparator.getTime() + (xHours * hourMSLONG);
        return end < now;
    }

    public static boolean isWithinXXSecond(Timestamp comparator, Integer xSecond) {

        if (xSecond == null || xSecond < 1) {
            return true;
        }

        final long now = getTimestamp().getTime();
        final long end = comparator.getTime() + (1000 * xSecond);
        return end < now;
    }

    /**
     * Converts a {@link JsonObject} to {@link MessageEmbed}.
     * Supported Fields: Title, Author, Description, Color, Fields, Thumbnail,
     * Footer.
     * 
     * @param json The JsonObject
     * @return The Embed
     */
    public static MessageEmbed jsonToEmbed(JsonObject json) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        JsonPrimitive titleObj = json.getAsJsonPrimitive("title");
        if (titleObj != null) { // Make sure the object is not null before adding it onto the embed.
            embedBuilder.setTitle(titleObj.getAsString());
        }

        JsonObject authorObj = json.getAsJsonObject("author");
        if (authorObj != null) {
            String authorName = authorObj.get("name").getAsString();
            String authorIconUrl = authorObj.get("icon_url").getAsString();
            if (authorIconUrl != null) // Make sure the icon_url is not null before adding it onto the embed. If its
                                       // null then add just the author's name.
                embedBuilder.setAuthor(authorName, authorIconUrl);
            else
                embedBuilder.setAuthor(authorName);
        }

        JsonPrimitive descObj = json.getAsJsonPrimitive("description");
        if (descObj != null) {
            embedBuilder.setDescription(descObj.getAsString());
        }

        JsonPrimitive colorObj = json.getAsJsonPrimitive("color");
        if (colorObj != null) {
            embedBuilder.setColor(new Color(colorObj.getAsInt()));
        }

        JsonArray fieldsArray = json.getAsJsonArray("fields");
        if (fieldsArray != null) {
            // Loop over the fields array and add each one by order to the embed.
            fieldsArray.forEach(ele -> {
                String name = ele.getAsJsonObject().get("name").getAsString();
                String content = ele.getAsJsonObject().get("value").getAsString();
                boolean inline = ele.getAsJsonObject().get("inline").getAsBoolean();
                embedBuilder.addField(name, content, inline);
            });
        }

        JsonPrimitive thumbnailObj = json.getAsJsonPrimitive("thumbnail");
        if (thumbnailObj != null) {
            embedBuilder.setThumbnail(thumbnailObj.getAsString());
        }

        JsonObject footerObj = json.getAsJsonObject("footer");
        if (footerObj != null) {
            String content = footerObj.get("text").getAsString();
            String footerIconUrl = footerObj.get("icon_url").getAsString();

            if (footerIconUrl != null)
                embedBuilder.setFooter(content, footerIconUrl);
            else
                embedBuilder.setFooter(content);
        }

        return embedBuilder.build();
    }

}
