package main;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.secure;
import static spark.Spark.options;
import static spark.Spark.staticFiles;

import java.io.File;
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
    private static WhitelistDmc plugin;

    public static void main(WhitelistDmc plugin, ConfigManager configs) {
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
            final int port = Integer.parseInt(configs.get("api.localPort", "8084"));
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

    private static void enableCORS(final String origin, final String methods, final String headers) {

        // TODO: for each from configs
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

        // TODO: for each from configs
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Allow-Headers", headers);
            response.header("Access-Control-Request-Method", methods);
        });
    }

    private static void setPaths() {
        final String discordId = configs.get("discordServerId", "????");
        final String appRootPath = FileHelper.PLUGIN_DIR.toString()
                + FileHelper.fSep + configs.get("api.appRoot", "www");
        try {
            File rootDir = new File(appRootPath);
            FileUtils.forceMkdir(rootDir);
            staticFiles.externalLocation(appRootPath);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }

        enableCORS("*", "*", "*");
        setApiPaths();
    }

    private static void setApiPaths() {

        path("/api", () -> {
            before("/*", (req, res) -> {
                req.session(true);
                res.type("application/json");
            });

            path("/server", () -> {
                before("/*", (req, res) -> logger.info("Received an api call [server]:"));

                get("/", (req, res) -> {
                    res.type("text/html");
                    final StringBuilder sb = new StringBuilder();
                    
                    sb.append("<bold> discordServerId: </bold> <small>" + configs.get("discordServerId") + "</small></br>");
                    sb.append("<bold> discordAdminChanelId: </bold> <small>" + configs.get("discordAdminChanelId") + "</small></br>");
                    sb.append("<bold> botLogChannelId: </bold> <small>" + configs.get("botLogChannelId") + "</small></br>");
                    sb.append("<bold> javaLogChannelId: </bold> <small>" + configs.get("javaLogChannelId") + "</small></br>");
                    sb.append("<bold> discordWelcomeChanelId: </bold> <small>" + configs.get("discordWelcomeChanelId") + "</small></br>");
                    sb.append("<bold> discordWhitelistChanelId: </bold> <small>" + configs.get("discordWhitelistChanelId") + "</small></br>");
                    sb.append("<bold> discordAdminRoleId: </bold> <small>" + configs.get("discordAdminRoleId") + "</small></br>");
                    sb.append("<bold> discordModeratorRoleId: </bold> <small>" + configs.get("discordModeratorRoleId") + "</small></br>");
                    sb.append("<bold> discordDevRoleId: </bold> <small>" + configs.get("discordDevRoleId") + "</small></br>");
                    sb.append("<bold> discordHelperRoleId: </bold> <small>" + configs.get("discordHelperRoleId") + "</small></br>");
                    sb.append("<bold> dbType: </bold> <small>" + configs.get("dbType") + "</small></br>");
                    sb.append("<bold> dbHost: </bold> <small>" + configs.get("dbHost") + "</small></br>");
                    sb.append("<bold> dbPort: </bold> <small>" + configs.get("dbPort") + "</small></br>");
                    sb.append("<bold> dbName: </bold> <small>" + configs.get("dbName") + "</small></br>");
                    sb.append("<bold> dbMaxConnection: </bold> <small>" + configs.get("dbMaxConnection") + "</small></br>");
                    sb.append("<bold> dbMaxConnectionIDLE: </bold> <small>" + configs.get("dbMaxConnectionIDLE") + "</small></br>");
                    sb.append("<bold> portJava: </bold> <small>" + configs.get("portJava") + "</small></br>");
                    sb.append("<bold> portBedrock: </bold> <small>" + configs.get("portBedrock") + "</small></br>");
                    sb.append("<bold> javaIp: </bold> <small>" + configs.get("javaIp") + "</small></br>");
                    sb.append("<bold> bedrockIp: </bold> <small>" + configs.get("bedrockIp") + "</small></br>");
                    sb.append("<bold> hoursToConfirmMcAccount: </bold> <small>" + configs.get("hoursToConfirmMcAccount") + "</small></br>");
                    sb.append("<bold> showAllWorldsMeteo: </bold> <small>" + configs.get("showAllWorldsMeteo") + "</small></br>");
                    sb.append("<bold> defaultLang: </bold> <small>" + configs.get("defaultLang") + "</small></br>");
                    sb.append("<bold> api.appRoot: </bold> <small>" + configs.get("api.appRoot") + "</small></br>");
                    sb.append("<bold> api.apiKey: </bold> <small>" + configs.get("api.apiKey") + "</small></br>");
                    sb.append("<bold> api.localPort: </bold> <small>" + configs.get("api.localPort") + "</small></br>");
                    sb.append("<bold> confirmLinkCmdName: </bold> <small>" + configs.get("confirmLinkCmdName") + "</small></br>");
                    sb.append("<bold> minecraftInfosLink: </bold> <small>" + configs.get("minecraftInfosLink") + "</small></br>");
                    sb.append("<bold> serverContactEmail: </bold> <small>" + configs.get("serverContactEmail") + "</small></br>");

                    res.body(sb.toString());
                    return res.body();
                });
            });

            path("/members", () -> {
                before("/*", (req, res) -> logger.info("Received a api call [users]:"));

                get("/", (req, res) -> {
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

                get("/:discordIdentity", (req, res) -> {
                    final String discordIdentity = req.params(":discordIdentity");
                    final boolean isId = Helper.isNumeric(discordIdentity);

                    if (isId) {
                        final User usersData = DaoManager.getUsersDao().findByDiscordId(discordIdentity);
                        res.body(usersData.toJson().toString());
                        return res.body();
                    }

                    final User usersData = DaoManager.getUsersDao().findByDiscordTag(discordIdentity);
                    res.body(usersData.toJson().toString());
                    return res.body();
                });

            });

            path("/players", () -> {
                before("/*", (req, res) -> logger.info("Received a api call [players]:"));
            });

            path("/mclookup", () -> {
                before("/*", (req, res) -> logger.info("Received a api call [mclookup]:"));
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
}