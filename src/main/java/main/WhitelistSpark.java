package main;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.secure;
import static spark.Spark.options;
import static spark.Spark.staticFiles;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import configs.ConfigManager;
import dao.DaoManager;
import helpers.FileHelper;
import helpers.Helper;
import models.User;
import services.api.PlayerDbApi;
import services.sentry.SentryService;

public class WhitelistSpark {

    private final static Logger logger = Logger.getLogger("WDMC-API");
    private static ConfigManager configs;
    private static WhitelistDmcNode plugin;

    public static void main(WhitelistDmcNode plugin, ConfigManager configs) {
        WhitelistSpark.plugin = plugin;
        WhitelistSpark.configs = configs;

        final String apiKey = configs.get("api.apiKey");
        if (apiKey == null) {
            logger.warning("Could not find your api key.\nSkipping initialization...");
        }
        if (setSSL()) {
            setPaths();
        }
    }

    private static boolean setSSL() {
        try {
            final int port = Integer.parseInt(configs.get("api.localPort", "8085"));
            final String configPass = configs.get("api.keyStorePassword", "");
            final String configFile = configs.get("api.keyStoreFile", null);
            final String keyStorePath = FileHelper.writeResourceToPluginDir(configFile, false);
            final File keyStoreFile = new File(keyStorePath);

            if (!keyStoreFile.exists()) {
                logger.warning("Could not find the SSL keystore: " + keyStorePath + "\nSkipping initialization...");
                return false;
            }

            port(port);
            secure(keyStoreFile.getAbsolutePath(), configPass, null, null);

        } catch (Exception e) {
            logger.warning("Could not initialize a secure SSL server, skipping initialization...");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static void enableCORS(final String methods, final String headers,
            final LinkedHashMap<String, Object> allowedHosts) {

        options("*/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Vary", "Origin");
            response.header("Server", "NONE");
            response.header("Access-Control-Allow-Headers", headers);
            response.header("Access-Control-Request-Method", methods);

            final String[] host = request.headers("Host").split(":");
            if (allowedHosts.containsValue(host[0])) {
                response.header("Access-Control-Allow-Origin", request.headers("Origin"));
                return;
            }

            response.header("Access-Control-Allow-Origin", "null");
        });
    }

    private static void setPaths() {
        final String appRootPath = FileHelper.PLUGIN_DIR.toString()
                + FileHelper.fSep + configs.get("api.appRoot", "www");
        try {
            File rootDir = new File(appRootPath);
            FileUtils.forceMkdir(rootDir);
            staticFiles.externalLocation(appRootPath);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }

        final LinkedHashMap<String, Object> allowedHosts = getAllowedHosts();
        enableCORS("*", "*", allowedHosts);
        setApiPaths();
    }

    private static void setApiPaths() {

        path("/api", () -> {
            before("/*", (req, res) -> {
                req.session(true);
                res.type("application/json");
            });

            path("/server", () -> {
                before("*", (req, res) -> logger.info("Received an api call [server]:"));

                get("", (req, res) -> {
                    res.body(configs.toJsonPublic().toString());
                    return res.body();
                });
            });

            path("/members", () -> {
                before("*", (req, res) -> logger.info("Received a api call [users]:"));

                get("", (req, res) -> {
                    final JSONArray usersData = DaoManager.getUsersDao().findAll();
                    final JSONArray data = new JSONArray();

                    for (int i = 0; i < usersData.length(); i++) {
                        final JSONObject userJson = (JSONObject) usersData.get(i);
                        final User user = new User(userJson);
                        data.put(user.toJson());
                    }

                    res.body(data.toString());
                    return res.body();
                });

                get("/:discordId", (req, res) -> {
                    final String discordId = req.params(":id");
                    final boolean isId = Helper.isNumeric(discordId);

                    if (isId) {
                        final User usersData = DaoManager.getUsersDao().findByDiscordId(discordId);
                        res.body(usersData.toJson().toString());
                        return res.body();
                    }

                    final User usersData = DaoManager.getUsersDao().findByDiscordTag(discordId);
                    res.body(usersData.toJson().toString());
                    return res.body();
                });

            });

            path("/players", () -> {
                before("*", (req, res) -> logger.info("Received a api call [players]:"));
            });

            path("/mclookup", () -> {
                before("*", (req, res) -> logger.info("Received a api call [mclookup]:"));
                get("/:identity", (req, res) -> {
                    JSONArray data = new JSONArray();
                    JSONObject query = new JSONObject();
                    JSONObject error = new JSONObject();

                    try {
                        final String nameOrUuid = req.params(":identity");
                        error.putOnce("identity", nameOrUuid);

                        if (Helper.isMCUUID(nameOrUuid)) {
                            data = PlayerDbApi.fetchInfosWithUuid(nameOrUuid);
                            res.status(data.getJSONObject(0).getInt("code"));
                        }

                        else if (Helper.isMcPseudo(nameOrUuid)) {
                            data = PlayerDbApi.fetchInfosWithPseudo(nameOrUuid);
                            res.status(data.getJSONObject(0).getInt("code"));
                        }

                        else {
                            res.status(404);
                            error.put("message", "The given identity is not a valid UUID or Pseudo");
                            query.put("error", error);
                            data.put(query);
                        }

                    } catch (Exception e) {
                        SentryService.captureEx(e);
                        res.status(500);
                        error.put("message", "Server Error");
                        query.put("error", error);
                        data.put(query);
                    }

                    res.body(data.toString());
                    return res.body();
                });
            });
        });

    }

    private static LinkedHashMap<String, Object> getAllowedHosts() {
        return configs.getAsMap("api.allowedHosts");
    }
}