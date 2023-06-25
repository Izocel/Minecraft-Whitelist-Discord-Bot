package helpers.parsers;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;

import helpers.FileHelper;
import services.sentry.SentryService;

public class YamlFileParser {

    public static HashMap<String, Object> fromResourceFile(String filename) {
        try {
            final Yaml yaml = new Yaml();
            final String contents = FileHelper.getResourceFileContent(filename);

            if(contents == null) {
                return new HashMap<>();
            }

            return yaml.load(contents);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return new HashMap<>();
    }

    public static HashMap<String, Object> fromPluginFile(String filename) {
        try {
            final InputStream contents = FileHelper.getPluginFileStream(filename);
            if(contents == null) {
                return new HashMap<>();
            }

            final Yaml yaml = new Yaml();
            return yaml.load(contents);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return new HashMap<>();
    }

    public static void toPluginFile(String filename, HashMap<String, Object> keyValMap) {
        try {
            final File pluginFile = FileHelper.getPluginFile(filename);
            final PrintWriter writer = new PrintWriter(pluginFile);
            new Yaml().dump(keyValMap, writer);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }
}