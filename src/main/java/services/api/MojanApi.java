package services.api;

import org.json.JSONArray;

import helpers.Fetcher;
import helpers.Helper;
import services.sentry.SentryService;

public class MojanApi extends Api{
    private static String baseUri = "https://api.mojang.com";
    private static String userProfileUri = baseUri + "/users/profiles/minecraft/";

    public static String getPlayerUUID(String username) {
        String uuid = null;
        String formatedUuid = null;
        try {
            if(!Helper.isMcPseudo(username)) {
                throw new Exception("Bad username provided");
            }
            JSONArray data = Fetcher.toJson(
                Fetcher.fetch("GET", userProfileUri + username, null, null)
            );

            // id === uuid
            uuid = data.getJSONObject(0).optString("id");
            final String name = data.getJSONObject(0).optString("name");

            if(!name.equals(username))
                return null;

            if(uuid.contains("-")) {
                return uuid;
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
}

