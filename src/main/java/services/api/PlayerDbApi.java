package services.api;

import java.util.logging.Logger;

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
        if(username == null ) {
            return null;
        }
        try {

            if(!Helper.isMcPseudo(username)) {
                throw new Exception("Bad username provided");
            }
            JSONArray data = Fetcher.toJson(
                Fetcher.fetch("GET", xboxEndpoint + username, null, null)
            );

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");
            final String pseudo = playerData.optString("username");

            if(!pseudo.equals(username))
                return null;

            return Helper.sanitizeUUID(playerData.optString("id"));

        } catch (Exception e) {
            Logger.getLogger("WJE:" + "PlayerDbApi").warning("No Bedrock UUID found with pseudo: " + username);
            return null;
        }
        
    }

    public static String getMinecraftUUID(String username) {
        if(username == null ) {
            return null;
        }

        try {
            if(!Helper.isMcPseudo(username)) {
                throw new Exception("Bad username provided");
            }
            JSONArray data = Fetcher.toJson(
                Fetcher.fetch("GET", minecraftEndpoint + username, null, null)
            );

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");
            final String pseudo = playerData.optString("username");

            if(!pseudo.equals(username))
                return null;

            return Helper.sanitizeUUID(playerData.optString("id"));

        } catch (Exception e) {
            Logger.getLogger("WJE:" + "PlayerDbApi").warning("No Java UUID found with pseudo: " + username);
            return null;
        }
    }

    public static String getXboxPseudo(String uuid) {
        if(uuid == null ) {
            return null;
        }

        String pseudo = null;

        try {
            uuid = Helper.sanitizeUUID(uuid);
            uuid = Helper.toXboxDecimal(uuid);

            JSONArray data = Fetcher.toJson(
                Fetcher.fetch("GET", xboxEndpoint + uuid, null, null)
            );

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");

            if(!uuid.equals(playerData.optString("id")))
                return null;

            uuid = playerData.optString("id");
            final String name = playerData.optString("username");
            if(Helper.isMcPseudo(name)) {
                pseudo = name;
            }
        }
        catch (Exception e) {
            SentryService.captureEx(e);
        }

        return pseudo;
    }

    public static String getMinecraftPseudo(String uuid) {
        if(uuid == null ) {
            return null;
        }
        String pseudo = null;

        try {
            if(!Helper.isMCUUID(uuid)) {
                throw new Exception("Bad uuid provided");
            }
            JSONArray data = Fetcher.toJson(
                Fetcher.fetch("GET", minecraftEndpoint + uuid, null, null)
            );

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");

            if(!uuid.equals(playerData.optString("id")))
                return null;

            uuid = playerData.optString("id");
            final String name = playerData.optString("username");
            if(Helper.isMcPseudo(name)) {
                pseudo = name;
            }
        }
        catch (Exception e) {
            SentryService.captureEx(e);
        }

        return pseudo;
    }


    
}
