package locals;

import java.util.logging.Logger;

import configs.ConfigManager;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import services.sentry.SentryService;

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

    public String setDefault(String lang) {
        if (lang.length() < 1 || !this.isSupported(lang)) {
            this.logger.info("Using default language: " + this.defaultLang);
            lang = this.defaultLang;
        }

        this.defaultLang = lang;
        return this.defaultLang;
    }

    public void nextIsDefault() {
        this.nextInteractionLang = this.defaultLang;
    }

    public String getNextLang() {
        return this.nextInteractionLang;
    }

    public String translate(String key) {
        ITransaction transaction = Sentry.startTransaction("LOCALES", "translate");
        String msg = "!!!!!! UNSUPPORTED TRADUCTION LANG !!!!!!";

        if (key.length() < 1) {
            msg = "!!!!!! EMPTY TRADUCTION KEY !!!!!!";
            
            Exception e = new Exception(msg);
            transaction.setData("msg", msg);
            transaction.setThrowable(e);
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);

            this.logger.warning("Local key was empty: " + key);
            return msg;
        }

        switch (this.getNextLang()) {
            case "FR":
                return getFr(key);
            case "EN":
                return getEn(key);
            case "EN_FR":
                return "\n🇺🇸 ENGLISH: " + getEn(key) + "\n🇨🇦 FRANÇAIS: " + getFr(key);
            case "FR_EN":
                return "\n🇨🇦 FRANÇAIS: " + getFr(key) + "\n🇺🇸 ENGLISH: " + getEn(key);

            default:
                break;
        }

        Exception e = new Exception(msg);
        transaction.setData("msg", msg);
        transaction.setThrowable(e);
        transaction.setStatus(SpanStatus.INTERNAL_ERROR);
        SentryService.captureEx(e);

        this.logger.warning(msg);
        return msg;
    }

    private final String getEn(String key) {
        ITransaction transaction = Sentry.startTransaction("LOCALES", "getEn");
        final String msg = "MISSING KEY: " + key;

        try {
            return En.valueOf(key).trans;
        } catch (Exception e) {
            this.logger.warning(e.getMessage());
            transaction.setData("msg", msg);
            transaction.setThrowable(e);
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
        }

        this.logger.warning(msg);
        return "MISSING KEY: " + key;
    }

    private final String getFr(String key) {
        ITransaction transaction = Sentry.startTransaction("LOCALES", "getFr");
        final String msg = "MISSING KEY: " + key;

        try {
            return Fr.valueOf(key).trans;
        } catch (Exception e) {
            this.logger.warning(e.getMessage());
            transaction.setData("msg", msg);
            transaction.setThrowable(e);
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
        }

        this.logger.warning(msg);
        return "MISSING KEY: " + key;
    }

    Boolean isSupported(String lang) {
        ITransaction transaction = Sentry.startTransaction("LOCALES", "isSupported");
        final String msg = "Is not a supported language: " + nextInteractionLang;

        try {
            if(Lang.valueOf(lang).value == lang) {
                transaction.setStatus(SpanStatus.OK);
                transaction.finish();
                return true;
            }
        } catch (Exception e) {
            this.logger.warning(e.getMessage());
            transaction.setData("msg", msg);
            transaction.setThrowable(e);
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
        }
        
        this.logger.warning(msg);
        return false;
    }
}
