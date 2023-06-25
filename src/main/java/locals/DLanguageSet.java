package locals;

import java.util.HashMap;
import java.util.logging.Logger;

import helpers.FileHelper;
import helpers.parsers.YamlFileParser;
import services.sentry.SentryService;

public class DLanguageSet {
    protected HashMap<String, Object> DEFAULTS = new HashMap<>();
    protected HashMap<String, Object> FROM_CONFIGS = new HashMap<>();
    private Logger logger;

    public DLanguageSet() {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
    }

    private void setMaps() {
        try {
            if (DEFAULTS == null || DEFAULTS.isEmpty()) {
                throw new Exception("Default traduction not implemented");
            }

            final String fileName = this.getClass().getSimpleName().concat(".yml");
            FROM_CONFIGS = YamlFileParser.fromPluginFile(fileName);

            DEFAULTS.forEach((k, v) -> {
                FROM_CONFIGS.putIfAbsent(k,v);
            });

            YamlFileParser.toPluginFile(fileName, FROM_CONFIGS);

        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public final String getTranslation(String key) {
        if (FROM_CONFIGS == null || FROM_CONFIGS.isEmpty()) {
            this.setMaps();
        }

        return FROM_CONFIGS.getOrDefault(key, DEFAULTS.get(key)).toString();
    }
}
