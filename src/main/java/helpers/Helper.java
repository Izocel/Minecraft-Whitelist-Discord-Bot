package helpers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import services.sentry.SentryService;

public class Helper {

    public static long dayMSLONG = 86400000;
    public static long hourMSLONG = dayMSLONG / 24;

    public static String capitalize(String str) {
        String firstLetter = str.substring(0, 1);
        final String remainingLetters = str.substring(1, str.length());

        firstLetter = firstLetter.toUpperCase();
        return firstLetter + remainingLetters;
    }

    public static String decimalToHex(Long decimal) {
        if (decimal == null) {
            return null;
        }
        return Long.toHexString(decimal);
    }

    public static Long hexToDecimal(String hex) {
        if (hex == null) {
            return null;
        }
        if (hex.contains("-")) {
            hex = hex.replaceAll("-", "");
        }
        return Long.parseLong(hex, 16);
    }

    public static boolean isNumeric(String string) {
        if (string == null) {
            return false;
        }
        return string.matches("^[0-9]+$");
    }

    public static Integer asInteger(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer asInteger(String string, Integer defValue) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static boolean isAlphanumeric(String string) {
        if (string == null) {
            return false;
        }
        return string.matches("^[a-zA-Z0-9]+$");
    }

    public static boolean isMcPseudo(String string) {
        if (string == null) {
            return false;
        }
        return string.matches("^[a-zA-Z0-9_-]{2,16}$");
    }

    public static boolean isHexaDecimal(String string) {
        if (string == null) {
            return false;
        }
        return string.matches("^[a-fA-F0-9-]+$");
    }

    public static boolean isMCUUID(String string) {
        if (string == null) {
            return false;
        }

        return isHexaDecimal(string)
                && string.length() == 36;
    }

    public static boolean isDatimeString(String string) {
        if (string == null) {
            return false;
        }
        return string.matches("^[a-zA-Z0-9-]+[0-9]{2}:[0-9]{2}:[0-9]{2}$");
    }

    public static String sanitizeUUID(String uuid) {
        if (uuid == null || uuid.length() > 36) {
            return null;
        }

        try {
            if (isMCUUID(uuid)) {
                return uuid;
            }

            if (isNumeric(uuid)) {
                uuid = decimalToHex(Long.parseLong(uuid));
            }

            if (uuid.length() < 32) {
                uuid = StringUtils.repeat("0", 32 - uuid.length()) + uuid;
            }

            if (uuid.length() == 32 && !uuid.contains("-")) {
                String sanitized = "";
                final Integer[] lengths = { 8, 4, 4, 4 };

                int j = 0;
                int k = 0;

                for (int i = 0; i < uuid.length(); i++) {
                    sanitized += uuid.charAt(i);
                    k++;
                    if (j < lengths.length && k == lengths[j]) {
                        sanitized += "-";
                        k = 0;
                        j++;
                    }
                }

                uuid = sanitized;
            }

            if (!isMCUUID(uuid)) {
                return null;
            }

            return uuid;
        }

        catch (Exception e) {
            SentryService.captureEx(e);
            return null;
        }
    }

    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Date getSystemDate() {
        return Date.from(getTimestamp().toInstant());
    }

    public static Timestamp convertStringToTimestamp(String strDate) {
        try {

            if (!isDatimeString(strDate)) {
                strDate = strDate.concat(":01");
            }

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

    public static String toXboxDecimal(String mcUUID) {
        if (mcUUID == null) {
            return null;
        }

        try {
            if (!isMCUUID(mcUUID)) {
                return null;
            }
            mcUUID = String.valueOf(Helper.hexToDecimal(mcUUID));

            if (!isNumeric(mcUUID)) {
                mcUUID = null;
            }

            return mcUUID;

        } catch (Exception e) {
            SentryService.captureEx(e);
            return null;
        }
    }

    public static JSONObject jsonGet(String key, JSONObject json) {
        final String[] keys = key.split("\\.");
        if (keys.length < 1) {
            return json.getJSONObject(key);
        }

        for (int i = 0; i < keys.length; i++) {
            json = json.getJSONObject(keys[i]);
            if (json == null) {
                return null;
            }
        }

        return json;
    }

    public static String jsonGetString(String key, JSONObject json) {
        final String[] keys = key.split("\\.");
        if (keys.length < 1) {
            return json.get(key).toString();
        }

        Object curJson = json;
        for (int i = 0; i < keys.length; i++) {
            curJson = json.get(keys[i]);
            if (curJson == null) {
                return null;
            }
        }

        return curJson.toString();
    }

    public static JSONObject removeValue(JSONObject jsonObject, String[] keys) {
        String currentKey = keys[0];

        if (keys.length == 1 && jsonObject.has(currentKey)) {
            jsonObject.remove(currentKey);
            return jsonObject;
        } else if (!jsonObject.has(currentKey)) {
            return null;
        }

        JSONObject nestedJsonObjectVal = jsonObject.getJSONObject(currentKey);
        int nextKeyIdx = 1;
        String[] remainingKeys = Arrays.copyOfRange(keys, nextKeyIdx, keys.length);
        JSONObject updatedNestedValue = removeValue(nestedJsonObjectVal, remainingKeys);
        return jsonObject.put(currentKey, updatedNestedValue);
    }
}
