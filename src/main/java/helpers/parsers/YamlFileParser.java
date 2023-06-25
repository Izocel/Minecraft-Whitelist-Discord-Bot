package helpers.parsers;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

import org.yaml.snakeyaml.Yaml;

import helpers.FileHelper;
import services.sentry.SentryService;

public class YamlFileParser {

    public static LinkedHashMap<String, Object> fromResourceFile(String filename) {
        try {
            final Yaml yaml = new Yaml();
            final String contents = FileHelper.getResourceFileContent(filename);

            if(contents == null) {
                return new LinkedHashMap<>();
            }

            return yaml.load(contents);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return new LinkedHashMap<>();
    }

    public static LinkedHashMap<String, Object> fromPluginFile(String filename) {
        try {
            final InputStream contents = FileHelper.getPluginFileStream(filename);
            if(contents == null) {
                return new LinkedHashMap<>();
            }

            final Yaml yaml = new Yaml();
            return yaml.load(contents);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return new LinkedHashMap<>();
    }

    public static void toPluginFile(String filename, LinkedHashMap<String, Object> keyValMap) {
        try {
            final File pluginFile = FileHelper.getPluginFile(filename);
            pluginFile.getParentFile().mkdir();
            final PrintWriter writer = new PrintWriter(pluginFile, StandardCharsets.UTF_8);
            new Yaml().dump(keyValMap, writer);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }
}