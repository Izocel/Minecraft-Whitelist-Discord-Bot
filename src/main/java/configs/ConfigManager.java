package WhitelistJe.configs;

import io.github.cdimascio.dotenv.Dotenv;

public final class ConfigManager {

    private Dotenv dotenv;

    public ConfigManager(String relativePath, String filename) {
        relativePath = relativePath == null ? "./" :  relativePath;
        filename = filename == null ? ".env" :  filename;
        
        this.dotenv = Dotenv.configure()
            .directory(relativePath)
            .filename(filename)
            .load();
    }

    public String getConfig(String key, String defaultValue) {
        return this.dotenv.get(key, defaultValue);
    }


}
