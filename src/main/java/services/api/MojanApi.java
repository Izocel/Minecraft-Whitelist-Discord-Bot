package services.api;

import org.json.JSONArray;

import helpers.Fetcher;
import helpers.Helper;

public class MojanApi extends Api{
    private static String baseUri = "https://api.mojang.com";
    private static String userProfileUri = baseUri + "/users/profiles/minecraft/";

    public static String getPlayerUUID(String username) {
        String uuid = null;
        try {
            if(!Helper.isMcPseudo(username)) {
                throw new Exception("Bad username provided");
            }
            JSONArray data =Fetcher.toJson(
                Fetcher.fetch("GET", userProfileUri + username, null, null)
            );

            // id === uuid
            java.util.logging.Logger.getLogger("test").info(data.toString());
            uuid = data.getJSONObject(0).optString("id");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return uuid;
    }
}

