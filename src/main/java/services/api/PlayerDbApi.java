package services.api;

import org.json.JSONArray;
import org.json.JSONObject;

import helpers.Fetcher;
import helpers.Helper;
import services.sentry.SentryService;

public class PlayerDbApi extends Api{
    private static String baseUri = "https://playerdb.co/api/player";
    private static String minecraftEndpoint = baseUri + "/minecraft/";
    private static String xboxEndpoint = baseUri + "/xbox/";
    private static String steamEndpoint = baseUri + "/steam/";


    public static String getXboxUUID(String username) {
        String uuid = null;
        String formatedUuid = null;
        try {
            if(!Helper.isMcPseudo(username)) {
                throw new Exception("Bad username provided");
            }
            JSONArray data = Fetcher.toJson(
                Fetcher.fetch("GET", xboxEndpoint + username, null, null)
            );

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");

            // id === uuid
            uuid = playerData.optString("id");
            final String name = playerData.optString("name");

            if(!name.equals(username))
                return null;

            if(uuid.contains("-")) {
                return uuid;
            }

            if(Helper.isNumeric(uuid)) {
                uuid = Helper.decimalToHex(Long.parseLong(uuid));
            }

            final Integer[] lengths = {8,4,4,4};

            int j = 0;
            int k = 0;
            formatedUuid = "";
            for (int i = 0; i < uuid.length(); i++) {            
                formatedUuid += uuid.charAt(i);
                k++;
                if(j < lengths.length && k == lengths[j]) {
                    formatedUuid += "-";
                    k = 0;
                    j++;
                }
            }
            

        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return formatedUuid;
    }

    public static String getMinecraftUUID(String username) {
        String uuid = null;
        String formatedUuid = null;
        try {
            if(!Helper.isMcPseudo(username)) {
                throw new Exception("Bad username provided");
            }
            JSONArray data = Fetcher.toJson(
                Fetcher.fetch("GET", minecraftEndpoint + username, null, null)
            );

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");

            // id === uuid
            uuid = playerData.optString("id");
            final String name = playerData.optString("name");

            if(!name.equals(username))
                return null;

            if(Helper.isMCUUID(uuid)) {
                return uuid;
            }

            if(Helper.isNumeric(uuid)) {
                uuid = Helper.decimalToHex(Long.parseLong(uuid));
            }

            final Integer[] lengths = {8,4,4,4};

            int j = 0;
            int k = 0;
            formatedUuid = "";
            for (int i = 0; i < uuid.length(); i++) {            
                formatedUuid += uuid.charAt(i);
                k++;
                if(j < lengths.length && k == lengths[j]) {
                    formatedUuid += "-";
                    k = 0;
                    j++;
                }
            }
            

        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        if(!Helper.isMCUUID(formatedUuid)) {
            return null;
        }

        return formatedUuid;
    }
    
}
