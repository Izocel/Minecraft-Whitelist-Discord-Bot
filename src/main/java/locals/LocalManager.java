package locals;

import java.util.logging.Logger;

import configs.ConfigManager;
import main.WhitelistJe;
import services.sentry.SentryService;

public class LocalManager {

    private Logger logger;
    private String defaultLang;
    private String nextInteractionLang;
    private String[] baseLangs = { Lang.FR.value, Lang.EN.value, Lang.ES.value };
    private String[] extraLangs = {
            Lang.FR_EN.value, Lang.FR_ES.value,
            Lang.ES_EN.value, Lang.ES_FR.value,
            Lang.EN_FR.value
    };
    private Fr Fr;
    private En En;
    private Es Es;

    public LocalManager(WhitelistJe plugin) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.Fr = new Fr();
        this.En = new En();
        this.Es = new Es();

        final var configs = plugin.getConfigManager();
        this.setDefault(configs.get("defaultLang"));
        this.setNextLang(configs.get("defaultLang"));
    }

    public final String[] getBaseLangs() {
        return this.baseLangs;
    }

    public final String[] getExtraLangs() {
        return this.extraLangs;
    }

    public final void setNextLang(String lang) {
        lang = lang.toUpperCase();
        if (lang.length() < 1 || !this.isSupported(lang)) {
            this.logger.warning("Using default language: " + this.defaultLang);
            this.nextInteractionLang = this.defaultLang;
            return;
        }

        this.nextInteractionLang = lang;
    }

    public final String setDefault(String lang) {
        lang = lang.toUpperCase();
        if (lang.length() < 1 || !this.isSupported(lang)) {
            this.logger.warning("Using default language: " + this.defaultLang);
            lang = this.defaultLang;
        }

        this.defaultLang = lang;
        return this.defaultLang;
    }

    public final void nextIsDefault() {
        this.nextInteractionLang = this.defaultLang;
    }

    public final String getDefaultLang() {
        return this.defaultLang;
    }

    public final String getNextLang() {
        return this.nextInteractionLang;
    }

    public final String useDefault(String key) {
        return this.translateBy(key, this.defaultLang);
    }

    public final String translate(String key) {

        if (key.length() < 1) {
            final String msg = "Local key was empty for: " + key;
            SentryService.captureEx(new Exception(msg));
            this.logger.warning(msg);
            return msg;
        }

        return translateBy(key, this.getNextLang());
    }

    public final String translateBy(String key, String lang) {

        if (key.length() < 1) {
            final String msg = "Local key was empty for: " + key;
            SentryService.captureEx(new Exception(msg));
            this.logger.warning(msg);
            return msg;
        }

        switch (lang.toUpperCase()) {
            case "FR":
                return getFr(key);
            case "EN":
                return getEn(key);
            case "ES":
                return getEs(key);
            case "FR_EN":
                return "\n🇨🇦 FRANÇAIS: " + getFr(key) + "\n🇺🇸 ENGLISH: " + getEn(key);
            case "FR_ES":
                return "\n🇨🇦 FRANÇAIS: " + getFr(key) + "\n🇺🇸 ESPAÑOLA: " + getEs(key);
            case "EN_FR":
                return "\n🇺🇸 ENGLISH: " + getEn(key) + "\n🇨🇦 FRANÇAIS: " + getFr(key);
            case "EN_ES":
                return "\n🇨🇦 ENGLISH: " + getEn(key) + "\n🇺🇸 ESPAÑOLA: " + getEs(key);
            case "ES_FR":
                return "\n🇨🇦 ESPAÑOLA: " + getEs(key) + "\n🇺🇸 FRANÇAIS: " + getFr(key);
            case "ES_EN":
                return "\n🇨🇦 ESPAÑOLA: " + getEs(key) + "\n🇺🇸 ENGLISH: " + getEn(key);
            default:
                break;
        }

        final String msg = "!!!!!! UNSUPPORTED TRADUCTION LANG !!!!!!";

        this.logger.warning(msg);
        SentryService.captureEx(new Exception(msg));

        return useDefault(key);
    }

    private final String getEn(String key) {
        final String msg = "MISSING KEY: " + key;

        try {
            return En.getTranslation(key);
        } catch (Exception e) {
            this.logger.warning(e.getMessage());
            SentryService.captureEx(e);
        }

        this.logger.warning(msg);
        SentryService.captureEx(new Exception(msg));
        return "MISSING KEY: " + key;
    }

    private final String getFr(String key) {
        final String msg = "MISSING KEY: " + key;

        try {
            return Fr.getTranslation(key);
        } catch (Exception e) {
            this.logger.warning(e.getMessage());
            SentryService.captureEx(e);
        }

        this.logger.warning(msg);
        SentryService.captureEx(new Exception(msg));
        return "MISSING KEY: " + key;
    }

    private final String getEs(String key) {
        final String msg = "MISSING KEY: " + key;

        try {
            return Es.getTranslation(key);
        } catch (Exception e) {
            this.logger.warning(e.getMessage());
            SentryService.captureEx(e);
        }

        this.logger.warning(msg);
        SentryService.captureEx(new Exception(msg));
        return "MISSING KEY: " + key;
    }

    public final Boolean isSupported(String lang) {
        final String msg = "Is not a supported traduction: " + lang;
        lang = lang.toUpperCase();

        try {
            if (Lang.valueOf(lang).value.equals(lang)) {
                return true;
            }
        } catch (Exception e) {
            this.logger.warning(e.getMessage());
            SentryService.captureEx(e);
        }

        this.logger.warning(msg);
        SentryService.captureEx(new Exception(msg));

        return false;
    }

    public final Boolean isUserSupported(String lang) {
        return this.isSupported(lang) && !lang.contains("_");
    }

    public final boolean setCheckEventLocal(String eventName, String cmdLocalKey) {
        String translatedName = "";

        this.setNextLang(Lang.FR.value);
        translatedName = this.translate(cmdLocalKey);
        if (eventName.equals(translatedName)) {
            return true;
        }

        this.setNextLang(Lang.EN.value);
        translatedName = this.translate(cmdLocalKey);
        if (eventName.equals(translatedName)) {
            return true;
        }

        this.setNextLang(Lang.ES.value);
        translatedName = this.translate(cmdLocalKey);
        if (eventName.equals(translatedName)) {
            return true;
        }

        this.nextIsDefault();
        return false;
    }
}
