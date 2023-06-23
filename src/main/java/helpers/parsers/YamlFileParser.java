package helpers.parsers;

import java.util.Map;
import org.yaml.snakeyaml.Yaml;

import helpers.FileHelper;
import services.sentry.SentryService;

public class YamlFileParser {

    public static Map<String, Object> fromResourceFile(String filename) {
        try {
            final Yaml yaml = new Yaml();
            final String contents = FileHelper.getRessourceFileContent(filename);
            return yaml.load(contents);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return null;
    }
}