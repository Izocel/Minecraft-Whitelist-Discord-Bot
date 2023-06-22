package helpers.parsers;

import java.util.Map;
import org.yaml.snakeyaml.Yaml;

import helpers.Helper;
import services.sentry.SentryService;

public class YamlFileParser {

    public static Map<String, Object> fromRessourceFile(String filename) {
        try {
            final Yaml yaml = new Yaml();
            final String contents = Helper.getRessourceFileContent(filename);
            return yaml.load(contents);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return null;
    }
}