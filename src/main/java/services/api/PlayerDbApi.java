package services.api;

import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import helpers.Fetcher;
import helpers.Helper;
import io.sentry.Sentry;
import services.sentry.SentryService;

public class PlayerDbApi extends Api {
    private static String baseUri = "https://playerdb.co/api/player";
    private static String minecraftEndpoint = baseUri + "/minecraft/";
    private static String xboxEndpoint = baseUri + "/xbox/";
    private static String steamEndpoint = baseUri + "/steam/";

    public static JSONArray fetchInfosWithUuid(String uuidOrDecimal) {
        Sentry.startTransaction("fetchInfosWithUuid", "ApiFetching-ResponseBuild");
        JSONArray response = new JSONArray();
        JSONObject respoObj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {
            uuidOrDecimal = Helper.sanitizeUUID(uuidOrDecimal);

            if (!Helper.isMCUUID(uuidOrDecimal)) {
                respoObj.put("code", "404");
                respoObj.put("tested-uuid", uuidOrDecimal);
                respoObj.put("message",
                        "bad uuid format \n\tUse -> \n\t\t0c003c29-8675-4856-914b-9641e4b6bac3 \n\t\t 76561198963898147");
                response.put(0, respoObj);

            } else {
                final String uuid = uuidOrDecimal;
                String decimal = null;
                try {
                    decimal = String.valueOf(Helper.hexToDecimal(uuid));
                } catch (Exception e) {
                    decimal = null;
                }

                JSONArray dataJ = Fetcher.toJson(
                        Fetcher.fetch("GET", minecraftEndpoint + uuid, null, null));
                JSONArray dataX = Fetcher.toJson(
                        Fetcher.fetch("GET", xboxEndpoint + decimal, null, null));
                JSONArray dataS = Fetcher.toJson(
                        Fetcher.fetch("GET", steamEndpoint + decimal, null, null));

                if (dataJ != null
                        && dataJ.getJSONObject(0).getString("code").contains("player.found")) {
                    dataObj.append("Java",
                            dataJ.getJSONObject(0).getJSONObject("data").getJSONObject("player"));
                }
                if (dataX != null && decimal != null
                        && dataX.getJSONObject(0).getString("code").contains("player.found")) {
                    dataObj.append("Xbox",
                            dataX.getJSONObject(0).getJSONObject("data").getJSONObject("player"));
                }
                if (dataS != null && decimal != null
                        && dataS.getJSONObject(0).getString("code").contains("player.found")) {
                    dataObj.append("Steam",
                            dataS.getJSONObject(0).getJSONObject("data").getJSONObject("player"));
                }

                if (dataJ == null && dataX == null && dataS == null) {
                    respoObj.clear();
                    respoObj.put("code", "404");
                    respoObj.put("tested-uuid", uuidOrDecimal);
                    respoObj.put("message", "No users where found with this uuid");
                    response.put(0, respoObj);
                }
                 else {
                    respoObj.put("code", "200");
                    respoObj.put("tested-uuid", uuidOrDecimal);
                    respoObj.put("message", "success");
                    response.put(0, respoObj);

                    if(dataObj.length() == 0) {
                        respoObj.put("message", "No users where found with this uuid");
                        respoObj.put("code", "404");
                        return response;
                    }
                    response.put(1, dataObj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            respoObj.clear();
            respoObj.put("code", "500");
            respoObj.put("tested-uuid", uuidOrDecimal);
            respoObj.put("message", "internal error");
            response.put(0, respoObj);
            return response;
        }

        return response;
    }

    public static JSONArray fetchInfosWithPseudo(String pseudo) {
        Sentry.startTransaction("fetchInfosWithPseudo", "ApiFetching-ResponseBuild");
        JSONArray response = new JSONArray();
        JSONObject respoObj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {
            if (!Helper.isMcPseudo(pseudo)) {
                respoObj.put("code", "404");
                respoObj.put("tested-pseudo", pseudo);
                respoObj.put("message", "le pseudo devrait comporter entre `3` et `16` caractères" +
                        "\n et ne doit pas comporter de caractères spéciaux à part des underscores `_` ou tirets `-`");
                response.put(0, respoObj);
            } else {
                JSONArray dataJ = Fetcher.toJson(
                        Fetcher.fetch("GET", minecraftEndpoint + pseudo, null, null));
                JSONArray dataX = Fetcher.toJson(
                        Fetcher.fetch("GET", xboxEndpoint + pseudo, null, null));

                if (dataJ != null
                        && dataJ.getJSONObject(0).getString("code").contains("player.found")) {
                    dataObj.append("Java",
                            dataJ.getJSONObject(0).getJSONObject("data").getJSONObject("player"));
                }
                if (dataX != null
                        && dataX.getJSONObject(0).getString("code").contains("player.found")) {
                    dataObj.append("Xbox",
                            dataX.getJSONObject(0).getJSONObject("data").getJSONObject("player"));
                }

                if (dataJ == null && dataX == null) {
                    respoObj.clear();
                    respoObj.put("code", "404");
                    respoObj.put("tested-pseudo", pseudo);
                    respoObj.put("message", "No users where found with this pseudo");
                    response.put(0, respoObj);
                }

                else {
                    respoObj.put("code", "200");
                    respoObj.put("tested-pseudo", pseudo);
                    respoObj.put("message", "success");
                    response.put(0, respoObj);

                    if(dataObj.length() == 0) {
                        respoObj.put("message", "No users where found with this pseudo");
                        respoObj.put("code", "404");
                        return response;
                    }

                    response.put(1, dataObj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            respoObj.clear();
            respoObj.put("code", "500");
            respoObj.put("tested-pseudo", pseudo);
            respoObj.put("message", "internal error");
            response.put(0, respoObj);
            return response;
        }

        return response;
    }

    public static String getXboxUUID(String username) {
        if (username == null) {
            return null;
        }
        try {

            if (!Helper.isMcPseudo(username)) {
                throw new Exception("Bad username provided");
            }
            JSONArray data = Fetcher.toJson(
                    Fetcher.fetch("GET", xboxEndpoint + username, null, null));

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");
            final String pseudo = playerData.optString("username");

            if (!pseudo.equals(username))
                return null;

            return Helper.sanitizeUUID(playerData.optString("id"));

        } catch (Exception e) {
            Logger.getLogger("WJE:" + "PlayerDbApi").warning("No Bedrock UUID found with pseudo: " + username);
            return null;
        }

    }

    public static String getMinecraftUUID(String username) {
        if (username == null) {
            return null;
        }

        try {
            if (!Helper.isMcPseudo(username)) {
                throw new Exception("Bad username provided");
            }
            JSONArray data = Fetcher.toJson(
                    Fetcher.fetch("GET", minecraftEndpoint + username, null, null));

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");
            final String pseudo = playerData.optString("username");

            if (!pseudo.equals(username))
                return null;

            return Helper.sanitizeUUID(playerData.optString("id"));

        } catch (Exception e) {
            Logger.getLogger("WJE:" + "PlayerDbApi").warning("No Java UUID found with pseudo: " + username);
            return null;
        }
    }

    public static String getXboxPseudo(String uuid) {
        if (uuid == null) {
            return null;
        }

        String pseudo = null;

        try {
            uuid = Helper.sanitizeUUID(uuid);
            uuid = Helper.toXboxDecimal(uuid);

            JSONArray data = Fetcher.toJson(
                    Fetcher.fetch("GET", xboxEndpoint + uuid, null, null));

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");

            if (!uuid.equals(playerData.optString("id")))
                return null;

            uuid = playerData.optString("id");
            final String name = playerData.optString("username");
            if (Helper.isMcPseudo(name)) {
                pseudo = name;
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return pseudo;
    }

    public static String getMinecraftPseudo(String uuid) {
        if (uuid == null) {
            return null;
        }
        String pseudo = null;

        try {
            if (!Helper.isMCUUID(uuid)) {
                throw new Exception("Bad uuid provided");
            }
            JSONArray data = Fetcher.toJson(
                    Fetcher.fetch("GET", minecraftEndpoint + uuid, null, null));

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");

            if (!uuid.equals(playerData.optString("id")))
                return null;

            uuid = playerData.optString("id");
            final String name = playerData.optString("username");
            if (Helper.isMcPseudo(name)) {
                pseudo = name;
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return pseudo;
    }

}
