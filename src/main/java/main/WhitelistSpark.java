package main;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.secure;
import static spark.Spark.staticFiles;

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonObject;

import configs.ConfigManager;
import helpers.FileHelper;
import helpers.Helper;
import services.sentry.SentryService;

public class WhitelistSpark {

    static Logger logger = Logger.getLogger("WDMC-API");

    public static void main(ConfigManager configs) {
        final String apiKey = configs.get("api.apiKey");
        if (apiKey == null) {
            logger.warning("Could not find your api key.\nSkipping initialization...");
        }
        if (setSSL(configs)) {
            setPaths(configs);
        }
    }

    private static boolean setSSL(ConfigManager configs) {
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

    public static void setPaths(ConfigManager configs) {
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

        get("/ping", (req, res) -> "Discord server id: " + discordId);
        setApiPaths(configs);
    }

    public static void setApiPaths(ConfigManager configs) {

        path("/api", () -> {
            before("/*", (req, res) -> {
                req.session(true);
                res.type("application/json");
            });

            path("/server", () -> {
                before("/*", (req, res) -> logger.info("Received an api call [server]:"));

            });
            path("/users", () -> {
                before("/*", (req, res) -> logger.info("Received a api call [users]:"));
                get("/:id", (req, res) -> {
                    final JsonObject data = new JsonObject();
                    try {
                        final Integer id = Helper.asInteger(req.params(":id"), 0);

                        data.addProperty("id", id);
                        data.addProperty("time", Helper.getSystemDate().toString());
                    } catch (Exception e) {
                        SentryService.captureEx(e);
                        res.status(500);
                        data.addProperty("error", "Server Error");
                    }

                    res.body(data.toString());
                    return res.body();
                });

            });
            path("/players", () -> {
                before("/*", (req, res) -> logger.info("Received a api call [players]:"));

            });
        });

    }
}