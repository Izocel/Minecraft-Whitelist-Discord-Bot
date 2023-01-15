package locals;

import java.util.logging.Logger;

import configs.ConfigManager;

public class LocalManager {

    private Logger logger;
    private String defaultLang = "FR";
    private String nextInteractionLang = "FR";

    public LocalManager(ConfigManager configs) {
        this.setNextLang(configs.get("defaultLang", "FR"));
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
    }

    public void setNextLang(String lang) {
        if (lang.length() < 1 || !this.isSupported(lang)) {
            this.logger.info("Using default language: " + this.defaultLang);
            this.nextInteractionLang = this.defaultLang;
            return;
        }

        this.nextInteractionLang = lang;
    }

    public String getNextLang() {
        return this.nextInteractionLang;
    }

    public String translate(String key) {

        if (key.length() < 1) {
            this.logger.warning("Local key was empty: " + key);
            return "!!!!!! EMPTY TRADUCTION KEY !!!!!!";
        }

        switch (this.getNextLang()) {
            case "FR":
                return getFr(key);
            case "EN":
                return getEn(key);
            case "EN_FR":
                return "\nENGLISH: " + getEn(key) + "\nFRANÇAIS: " + getFr(key);
            case "FR_EN":
                return "\nFRANÇAIS: " + getFr(key) + "\nENGLISH: " + getEn(key);

            default:
                break;
        }

        return "!!!!!! UNSUPPORTED TRADUCTION LANG !!!!!!";
    }

    private final String getEn(String key) {
        try {
            return En.valueOf(key).trans;
        } catch (Exception e) {
            this.logger.warning("MISSING KEY: " + key);
        }

        return "MISSING KEY: " + key;
    }

    private final String getFr(String key) {
        try {
            return Fr.valueOf(key).trans;
        } catch (Exception e) {
            this.logger.warning("MISSING KEY: " + key);
        }

        return "MISSING KEY: " + key;
    }

    Boolean isSupported(String lang) {
        try {
            return Lang.valueOf(lang).value == lang;
        } catch (Exception e) {
            this.logger.warning("Is not a supported language: " + nextInteractionLang);
        }
        
        return false;
    }
}
