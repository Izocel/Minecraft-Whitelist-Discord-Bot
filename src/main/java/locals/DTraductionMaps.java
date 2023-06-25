package locals;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import helpers.FileHelper;
import helpers.parsers.YamlFileParser;
import services.sentry.SentryService;

public class DTraductionMaps {
    protected LinkedHashMap<String, Object> DEFAULTS = new LinkedHashMap<>();
    protected LinkedHashMap<String, Object> FROM_CONFIGS = new LinkedHashMap<>();
    boolean wasAltered = false;
    private Logger logger;

    public DTraductionMaps() {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
    }

    private void setMaps() {
        try {
            if (DEFAULTS == null || DEFAULTS.isEmpty()) {
                throw new Exception("Default traduction not implemented");
            }

            final String fileName = FileHelper.TRADUCTION_DIR_NAME + "\\"
                    + this.getClass().getSimpleName().concat(".yml");

            FROM_CONFIGS = YamlFileParser.fromPluginFile(fileName);

            DEFAULTS.forEach((k, v) -> {
                if(FROM_CONFIGS.containsKey(k)) {
                    return;
                }

                FROM_CONFIGS.put(k, v);
                wasAltered = true;
            });

            if(wasAltered) {
                YamlFileParser.toPluginFile(fileName, FROM_CONFIGS);
            }

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
