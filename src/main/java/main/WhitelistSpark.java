package main;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.secure;
import static spark.Spark.staticFiles;

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import configs.ConfigManager;
import helpers.FileHelper;

public class WhitelistSpark {

    static Logger logger = Logger.getLogger("WDMC-Spark:" + WhitelistSpark.class.getSimpleName());

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

            get("/ping", (req, res) -> "Discord server id: " + discordId);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }
}