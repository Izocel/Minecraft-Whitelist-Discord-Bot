package mainSpark;

import static spark.Spark.get;
import static spark.Spark.secure;
import static spark.Spark.staticFiles;

import java.io.File;
import java.util.logging.Logger;

import configs.ConfigManager;
import helpers.FileHelper;

public class WhitelistSpark {

    static Logger logger = Logger.getLogger("WDMC-Spark:" + WhitelistSpark.class.getSimpleName());

    public static void main(ConfigManager configs) {
        if (setSSL(configs)) {
            setPaths(configs);
        }
    }

    private static boolean setSSL(ConfigManager configs) {
        try {
            final String configPass = configs.get("httpServer.keyStorePassword", "");
            final String configFile = configs.get("httpServer.keyStoreFile", null);
            final String keyStorePath = FileHelper.writeResourceToPluginDir(configFile, false);
            final File keyStoreFile = new File(keyStorePath);

            if (!keyStoreFile.exists()) {
                logger.warning("Could not find the SSL keystore: " + keyStorePath + "\nSkipping initialization...");
                return false;
            }

            secure(keyStoreFile.getAbsolutePath(), configPass, null, null);

        } catch (Exception e) {
            logger.warning("Could not initialize a secure SSL server, skipping initialization...");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void setPaths(ConfigManager configs) {
        final String appName = configs.get("httpServer.appName", "WDMC-Spark");
        final String appRootPath = FileHelper.PLUGIN_DIR.toString()
                + FileHelper.fSep + configs.get("httpServer.appRoot", "app");
        try {
            staticFiles.externalLocation(appRootPath);

            get("/ping", (req, res) -> "Hello World from: " + appName);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }
}