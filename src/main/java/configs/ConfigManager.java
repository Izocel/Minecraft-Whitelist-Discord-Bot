package configs;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.json.JSONObject;

import helpers.Helper;
import helpers.parsers.YamlFileParser;
import services.sentry.SentryService;

public final class ConfigManager {
    private final String pluginVersion = "2023.7";

    private final String[] SENSIBLE_KEYS = {
            "hidden",
            "database.password",
            "database.user",
            "database.host",
            "database.port",
            "discord.botToken",
            "discord.registrarMemberIds",
            "api.keyStorePassword",
            "api.keyStoreFile",
            "api.apiKey",
            "notificationsApi.token"
    };

    private final String[] PRIVATE_KEYS = {};

    protected LinkedHashMap<String, Object> DEFAULTS = new LinkedHashMap<>();
    protected LinkedHashMap<String, Object> FROM_CONFIGS = new LinkedHashMap<>();
    protected LinkedHashMap<String, Object> FROM_MAPS = new LinkedHashMap<>();
    boolean wasAltered = false;
    private Logger logger;

    public ConfigManager() {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        this.setMaps();
    }

    private void setMaps() {
        final String fileName = "config.yml";
        DEFAULTS = YamlFileParser.fromResourceFile(fileName);
        FROM_CONFIGS = YamlFileParser.fromPluginFile(fileName);

        DEFAULTS.forEach((k, v) -> {
            if (FROM_CONFIGS.containsKey(k)) {
                return;
            }

            FROM_CONFIGS.put(k, v);
            wasAltered = true;
        });

        if (wasAltered) {
            YamlFileParser.toPluginFile(fileName, FROM_CONFIGS);
        }

        LinkedHashMap<String, Object> discord = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("discord");
        FROM_MAPS.put("discord", discord);

        LinkedHashMap<String, Object> discord_channels = (LinkedHashMap<String, Object>) discord.get("channels");
        FROM_MAPS.put("discord_channels", discord_channels);

        LinkedHashMap<String, Object> discord_roles = (LinkedHashMap<String, Object>) discord.get("roles");
        FROM_MAPS.put("discord_roles", discord_roles);

        LinkedHashMap<String, Object> database = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("database");
        FROM_MAPS.put("database", database);

        LinkedHashMap<String, Object> minecraft = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("minecraft");
        FROM_MAPS.put("minecraft", minecraft);

        LinkedHashMap<String, Object> features = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("features");
        FROM_MAPS.put("features", features);

        LinkedHashMap<String, Object> api = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("api");
        FROM_MAPS.put("api", api);

        LinkedHashMap<String, Object> notificationsApi = (LinkedHashMap<String, Object>) FROM_CONFIGS
                .get("notificationsApi");
        FROM_MAPS.put("notificationsApi", api);

        LinkedHashMap<String, Object> linksCommands = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("linksCommands");
        FROM_MAPS.put("linksCommands", linksCommands);

        LinkedHashMap<String, Object> quests = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("quests");
        FROM_MAPS.put("quests", quests);

        LinkedHashMap<String, Object> misc = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("misc");
        FROM_MAPS.put("misc", misc);

        FROM_CONFIGS.put("discordBotToken", discord.get("botToken"));
        FROM_CONFIGS.put("discordServerId", discord.get("serverId"));

        FROM_CONFIGS.put("discordAdminChanelId", discord_channels.get("adminChanelId"));
        FROM_CONFIGS.put("botLogChannelId", discord_channels.get("botLogChannelId"));
        FROM_CONFIGS.put("javaLogChannelId", discord_channels.get("javaLogChannelId"));
        FROM_CONFIGS.put("discordWelcomeChanelId", discord_channels.get("welcomeChanelId"));
        FROM_CONFIGS.put("discordWhitelistChanelId", discord_channels.get("whitelistChanelId"));

        FROM_CONFIGS.put("discordAdminRoleId", discord_roles.get("discordAdminRoleId"));
        FROM_CONFIGS.put("discordModeratorRoleId", discord_roles.get("moderatorRoleId"));
        FROM_CONFIGS.put("discordDevRoleId", discord_roles.get("devRoleId"));
        FROM_CONFIGS.put("discordHelperRoleId", discord_roles.get("helperRoleId"));

        FROM_CONFIGS.put("discord.registrarMemberIds", discord.get("registrarMemberIds"));

        FROM_CONFIGS.put("dbType", database.get("type"));
        FROM_CONFIGS.put("dbHost", database.get("host"));
        FROM_CONFIGS.put("dbPort", database.get("port"));
        FROM_CONFIGS.put("dbName", database.get("name"));
        FROM_CONFIGS.put("dbUser", database.get("user"));
        FROM_CONFIGS.put("dbPass", database.get("password"));
        FROM_CONFIGS.put("dbDefTable", database.get("defaultTable"));
        FROM_CONFIGS.put("dbMaxConnection", database.get("maxConnection"));
        FROM_CONFIGS.put("dbMaxConnectionIDLE", database.get("maxIdleConnection"));

        FROM_CONFIGS.put("portJava", minecraft.get("portJava"));
        FROM_CONFIGS.put("portBedrock", minecraft.get("portBedrock"));
        FROM_CONFIGS.put("javaIp", minecraft.get("javaIp"));
        FROM_CONFIGS.put("bedrockIp", minecraft.get("bedrockIp"));

        FROM_CONFIGS.put("hoursToConfirmMcAccount", features.get("maxHoursFor2FA"));
        FROM_CONFIGS.put("showAllWorldsMeteo", features.get("showAllWorldsMeteo"));
        FROM_CONFIGS.put("defaultLang", features.get("defaultLang"));

        FROM_CONFIGS.put("api.appRoot", api.get("appRoot"));
        FROM_CONFIGS.put("api.apiKey", api.get("apiKey"));
        FROM_CONFIGS.put("api.localPort", api.get("localPort"));
        FROM_CONFIGS.put("api.keyStoreFile", api.get("keyStoreFile"));
        FROM_CONFIGS.put("api.keyStorePassword", api.get("keyStorePassword"));

        FROM_CONFIGS.put("notificationsApi.ntfyUrl", notificationsApi.get("ntfyUrl"));
        FROM_CONFIGS.put("notificationsApi.token", notificationsApi.get("token"));
        FROM_CONFIGS.put("notificationsApi.topics", notificationsApi.get("topics"));
        FROM_CONFIGS.put("notificationsApi.emailGroup", notificationsApi.get("emailGroup"));

        FROM_CONFIGS.put("linksCommands", linksCommands);
        FROM_CONFIGS.put("quests", quests);

        FROM_CONFIGS.put("confirmLinkCmdName", misc.get("confirmLinkCmdName"));
        FROM_CONFIGS.put("minecraftInfosLink", misc.get("minecraftInfosLink"));
        FROM_CONFIGS.put("serverContactEmail", misc.get("serverContactEmail"));
        FROM_CONFIGS.put("adminPanelUrl", misc.get("adminPanelUrl"));
        FROM_CONFIGS.put("envType", misc.get("envType"));

        this.setHidden();
    }

    private void setHidden() {
        FROM_CONFIGS.put("pluginVersion", this.pluginVersion);
        FROM_CONFIGS.put("dbJdbcUrl", "jdbc:"
                + get("dbType") + "://"
                + get("dbHost") + ":"
                + get("dbPort") + "/"
                + get("dbName"));

        final LinkedHashMap<String, Object> hidden = new LinkedHashMap<>() {
        };
        hidden.put("pluginVersion", this.pluginVersion);
        hidden.put("dbJdbcUrl", FROM_CONFIGS.get("dbJdbcUrl"));

        FROM_MAPS.put("hidden", hidden);
    }

    public String get(String key, String defaultValue) {
        try {
            var result = this.FROM_CONFIGS.get(key);
            if (result != null) {
                return result.toString();
            }

            this.logger.info(key + " :: Was inexistent from configs falling back to default");
            return defaultValue.toString();

        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        this.logger.warning(key + " :: Error for this configs key");
        return null;
    }

    public String get(String key) {
        try {
            return this.FROM_CONFIGS.get(key).toString();
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        this.logger.warning(key + " :: Error for this configs key");
        return null;
    }

    public Object getHasObject(String key, Object defaultValue) {
        try {
            var result = this.FROM_CONFIGS.get(key);
            if (result != null) {
                return result;
            }

            this.logger.info(key + " :: Was inexistent from configs falling back to default");
            return defaultValue;

        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        this.logger.warning(key + " :: Error for this configs key");
        return null;
    }

    public Object getHasObject(String key) {
        try {
            return this.FROM_CONFIGS.get(key);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        this.logger.warning(key + " :: Error for this configs key");
        return null;
    }

    public LinkedHashMap<String, Object> getAsMap(String key) {
        try {
            return (LinkedHashMap<String, Object>) this.FROM_CONFIGS.get(key);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        this.logger.warning(key + " :: Error for this configs key");
        return null;
    }

    public JSONObject toJsonPrivate() {
        try {
            return new JSONObject(FROM_MAPS);
        } catch (Exception e) {
            this.logger.warning("Could not convert private configs maps to json.");
            SentryService.captureEx(e);
        }

        return null;
    }

    public JSONObject toJsonPublic() {
        try {
            JSONObject json = toJsonPrivate();

            for (String key : SENSIBLE_KEYS) {
                final String[] keys = key.split("\\.");
                json = Helper.removeValue(json, keys);

                if (json == null) {
                    return null;
                }
            }

            return json;
        } catch (Exception e) {
            this.logger.warning("Could not convert public configs maps to json:");
            SentryService.captureEx(e);
        }

        return null;
    }

    public boolean isProduction() {
        return !this.get("envType").equals("devU2");
    }
}
