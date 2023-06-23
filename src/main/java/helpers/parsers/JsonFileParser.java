package helpers.parsers;

import org.json.JSONObject;

import helpers.FileHelper;
import services.sentry.SentryService;

public class JsonFileParser {

    public static JSONObject fromFile(String filename) {
        try {
            final String contents = FileHelper.getRessourceFileContent(filename);
            return new JSONObject(contents);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return null;
    }
}
