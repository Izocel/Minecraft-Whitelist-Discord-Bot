package services.api;

import org.json.JSONArray;
import org.json.JSONObject;

import helpers.Fetcher;
import helpers.Helper;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
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

        ITransaction tx = Sentry.startTransaction("fetchInfosWithUuid", "fetcfetchInfosWithUuidhInfosWithPseudo");
        String uuid = uuidOrDecimal;
        String decimal = uuidOrDecimal;

        try {
            uuid = Helper.sanitizeUUID(uuid);

            if (!Helper.isMCUUID(uuid)) {
                respoObj.put("code", "404");
                respoObj.put("tested-uuids", uuid);
                respoObj.put("message",
                        "bad uuid format \n\tUse -> \n\t\t0c003c29-8675-4856-914b-9641e4b6bac3 \n\t\t 76561198963898147");
                response.put(0, respoObj);

                tx.setData("state", "bad uuid input -> response sent");
                tx.finish(SpanStatus.OK);
                return response;
            }

            JSONArray dataX = null;
            JSONArray dataS = null;
            try {
                decimal = String.valueOf(Helper.hexToDecimal(uuid));
                dataX = Fetcher.toJson(
                        Fetcher.fetch("GET", xboxEndpoint + decimal, null, null));

                dataS = Fetcher.toJson(
                        Fetcher.fetch("GET", steamEndpoint + decimal, null, null));

            } catch (Exception e) {
                decimal = "(none) *Invalid";
            }

            JSONArray dataJ = Fetcher.toJson(
                    Fetcher.fetch("GET", minecraftEndpoint + uuid, null, null));

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
            if (dataS != null
                    && dataS.getJSONObject(0).getString("code").contains("player.found")) {
                dataObj.append("Steam",
                        dataS.getJSONObject(0).getJSONObject("data").getJSONObject("player"));
            }

            if (dataJ == null && dataX == null && dataS == null) {
                respoObj.clear();
                respoObj.put("code", "404");
                respoObj.put("tested-uuids", uuid + " and xbox: " + decimal);
                respoObj.put("message", "No users where found with this uuid");
                response.put(0, respoObj);
            } else {
                respoObj.put("code", "200");
                respoObj.put("tested-uuids", uuid + " and xbox: " + decimal);
                respoObj.put("message", "success");
                response.put(0, respoObj);

                if (dataObj.length() == 0) {
                    respoObj.put("message", "No users where found with this uuid");
                    respoObj.put("code", "404");

                    tx.setData("state", "no match -> response sent");
                    tx.finish(SpanStatus.OK);
                    return response;
                }
                response.put(1, dataObj);
            }

            tx.setData("state", "response sent");
            tx.finish(SpanStatus.OK);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            respoObj.clear();
            respoObj.put("code", "500");
            respoObj.put("tested-uuids", new String[]{uuid, decimal});
            respoObj.put("message", "internal error");
            response.put(0, respoObj);

            tx.setThrowable(e);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            return response;
        }
    }

    public static JSONArray fetchInfosWithPseudo(String pseudo) {
        Sentry.startTransaction("fetchInfosWithPseudo", "ApiFetching-ResponseBuild");
        JSONArray response = new JSONArray();
        JSONObject respoObj = new JSONObject();
        JSONObject dataObj = new JSONObject();

        ITransaction tx = Sentry.startTransaction("fetchInfosWithPseudo", "fetchInfosWithPseudo");

        try {
            if (!Helper.isMcPseudo(pseudo)) {
                respoObj.put("code", "404");
                respoObj.put("tested-pseudo", pseudo);
                respoObj.put("message", "le pseudo devrait comporter entre `3` et `16` caractères" +
                        "\n et ne doit pas comporter de caractères spéciaux à part des underscores `_` ou tirets `-`");
                response.put(0, respoObj);

                tx.setData("state", "bad pseudo input -> response sent");
                tx.finish(SpanStatus.OK);
                return response;
            }

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

                if (dataObj.length() == 0) {
                    respoObj.put("message", "No users where found with this pseudo");
                    respoObj.put("code", "404");
                    tx.setData("state", "no match -> response sent");
                    tx.finish(SpanStatus.OK);
                    return response;
                }

                response.put(1, dataObj);
            }

            tx.setData("state", "response sent");
            tx.finish(SpanStatus.OK);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            respoObj.clear();
            respoObj.put("code", "500");
            respoObj.put("tested-pseudo", pseudo);
            respoObj.put("message", "internal error");
            response.put(0, respoObj);

            tx.setThrowable(e);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            return response;
        }
    }

    public static String getXboxUUID(String username) {
        if (username == null) {
            return null;
        }
        ITransaction tx = Sentry.startTransaction("getXboxUUID", "getXboxUUID");

        try {
            if (!Helper.isMcPseudo(username)) {
                tx.setData("state", "bad pseudo format input");
                tx.finish(SpanStatus.OK);
                return null;
            }
            JSONArray data = Fetcher.toJson(
                    Fetcher.fetch("GET", xboxEndpoint + username, null, null));

            JSONObject playerData = null;
            String pseudo = "";
            try {
                playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");
                pseudo = playerData.optString("username");
            } catch (Exception e) {
                pseudo = "";
            }

            if (!pseudo.equals(username)) {
                tx.setData("state", "no pseudo match");
                tx.finish(SpanStatus.OK);
                return null;
            }

            tx.setData("state", "response was sent");
            tx.finish(SpanStatus.OK);
            return Helper.sanitizeUUID(playerData.optString("id"));

        } catch (Exception e) {
            tx.setThrowable(e);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
            return null;
        }

    }

    public static String getMinecraftUUID(String username) {
        if (username == null) {
            return null;
        }

        ITransaction tx = Sentry.startTransaction("getMinecraftUUID", "getMinecraftUUID");

        try {
            if (!Helper.isMcPseudo(username)) {
                tx.setData("state", "bad pseudo format input");
                tx.finish(SpanStatus.OK);
                return null;
            }
            JSONArray data = Fetcher.toJson(
                    Fetcher.fetch("GET", minecraftEndpoint + username, null, null));

            JSONObject playerData = null;
            String pseudo = "";
            try {
                playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");
                pseudo = playerData.optString("username");
            } catch (Exception e) {
                pseudo = "";
            }

            if (!pseudo.equals(username)) {
                tx.setData("state", "no pseudo match");
                tx.finish(SpanStatus.OK);
                return null;
            }

            tx.setData("state", "response was sent");
            tx.finish(SpanStatus.OK);
            return Helper.sanitizeUUID(playerData.optString("id"));

        } catch (Exception e) {
            tx.setThrowable(e);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
            return null;
        }
    }

    public static String getXboxPseudo(String uuid) {
        if (uuid == null) {
            return null;
        }

        ITransaction tx = Sentry.startTransaction("getXboxPseudo", "getXboxPseudo");

        try {
            uuid = Helper.sanitizeUUID(uuid);
            uuid = Helper.toXboxDecimal(uuid);

            JSONArray data = Fetcher.toJson(
                    Fetcher.fetch("GET", xboxEndpoint + uuid, null, null));

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");

            if (!uuid.equals(playerData.optString("id"))) {
                tx.setData("state", "no uuid match");
                tx.finish(SpanStatus.OK);
                return null;
            }

            uuid = playerData.optString("id");
            final String name = playerData.optString("username");
            if (!Helper.isMcPseudo(name)) {
                tx.setData("state", "pseudo -> bad returned pseudo format");
                tx.finish(SpanStatus.OK);
                return null;
            }

            tx.setData("state", "response sent");
            tx.finish(SpanStatus.OK);

            return name;

        } catch (Exception e) {
            tx.setThrowable(e);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
            return null;
        }

    }

    public static String getMinecraftPseudo(String uuid) {
        if (uuid == null) {
            return null;
        }

        ITransaction tx = Sentry.startTransaction("getMinecraftPseudo", "getMinecraftPseudo");

        try {
            if (!Helper.isMCUUID(uuid)) {
                tx.setData("state", "uuid -> bad format");
                tx.finish(SpanStatus.OK);
                return null;
            }
            JSONArray data = Fetcher.toJson(
                    Fetcher.fetch("GET", minecraftEndpoint + uuid, null, null));

            final JSONObject playerData = data.getJSONObject(0).getJSONObject("data").getJSONObject("player");

            if (!uuid.equals(playerData.optString("id"))) {
                tx.setData("state", "no uuid match");
                tx.finish(SpanStatus.OK);
                return null;
            }

            uuid = playerData.optString("id");
            final String name = playerData.optString("username");

            if (!Helper.isMcPseudo(name)) {
                tx.setData("state", "pseudo -> bad returned pseudo format");
                tx.finish(SpanStatus.OK);
                return null;
            }

            tx.setData("state", "response sent");
            tx.finish(SpanStatus.OK);

            return name;

        } catch (Exception e) {
            tx.setThrowable(e);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
            return null;
        }

    }

}
