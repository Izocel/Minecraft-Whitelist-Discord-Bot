package locals;

import java.util.logging.Logger;

import configs.ConfigManager;

public class LocalManager {

    private Logger logger;
    private String defaultLang = "FR";
    private String nextInteractionLang;

    public LocalManager(ConfigManager configs) {
        this.defaultLang = configs.get("defaultLang", "FR");
        this.nextInteractionLang = this.defaultLang;
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
        if (this.nextInteractionLang.length() < 1 || !this.isSupported(this.nextInteractionLang)) {
            this.logger.info("Using default language: " + this.defaultLang);
            this.nextInteractionLang = this.defaultLang;
        }

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
        final String txt = En.valueOf(key).trans;
        if (txt.length() < 1) {
            return key;
        }
        return txt;
    }

    private final String getFr(String key) {
        final String txt = Fr.valueOf(key).trans;
        if (txt.length() < 1) {
            return key;
        }
        return txt;
    }

    Boolean isSupported(String lang) {
        if (Lang.valueOf(lang).value == lang) {
            return true;
        }

        this.logger.warning("Is not a supported language: " + nextInteractionLang);
        return false;
    }
}
