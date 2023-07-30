package mainSpark;

import static spark.Spark.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import configs.ConfigManager;

public class WhitelistSpark {

    static Logger logger = Logger.getLogger("WDMC:" + WhitelistSpark.class.getSimpleName());

    public static void main(ConfigManager configs) {
        // secure(keystoreFilePath, keystorePassword, truststoreFilePath,
        // truststorePassword);

        final String pluginDir = System.getProperty("user.dir");
        final String appDirPath = "/plugins/WhitelistDmc/app";
        final String staticPublicEntryPoint = pluginDir + appDirPath;

        try {
            FileUtils.forceMkdir(new File(staticPublicEntryPoint));
        } catch (IOException e) {
            e.printStackTrace();
        }

        staticFiles.externalLocation(staticPublicEntryPoint);
        setPaths();
    }

    public static void setPaths() {
        get("/hello", (req, res) -> "Hello World");
    }
}