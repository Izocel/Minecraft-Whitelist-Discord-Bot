package configs;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import helpers.parsers.YamlFileParser;
import services.sentry.SentryService;

public final class ConfigManager {
    private final String pluginVersion = "2023.3";
    private final String envType = "production";

    protected LinkedHashMap<String, Object> DEFAULTS = new LinkedHashMap<>();
    protected LinkedHashMap<String, Object> FROM_CONFIGS = new LinkedHashMap<>();
    boolean wasAltered = false;
    private Logger logger;

    public ConfigManager() {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
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

        LinkedHashMap<String, Object> discord_channels = (LinkedHashMap<String, Object>) discord.get("channels");

        LinkedHashMap<String, Object> discord_roles = (LinkedHashMap<String, Object>) discord.get("roles");

        LinkedHashMap<String, Object> database = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("database");

        LinkedHashMap<String, Object> minecraft = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("minecraft");

        LinkedHashMap<String, Object> features = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("features");

        LinkedHashMap<String, Object> misc = (LinkedHashMap<String, Object>) FROM_CONFIGS.get("misc");

        FROM_CONFIGS.put("discordBotToken", discord.get("botToken"));
        FROM_CONFIGS.put("discordServerId", discord.get("serverId"));

        FROM_CONFIGS.put("botLogChannelId", discord_channels.get("welcomeChanelId"));
        FROM_CONFIGS.put("javaLogChannelId", discord_channels.get("adminChanelId"));
        FROM_CONFIGS.put("discordWelcomeChanelId", discord_channels.get("whitelistChanelId"));
        FROM_CONFIGS.put("discordAdminChanelId", discord_channels.get("botLogChannelId"));
        FROM_CONFIGS.put("discordWhitelistChanelId", discord_channels.get("javaLogChannelId"));

        FROM_CONFIGS.put("discordAdminRoleId", discord_roles.get("discordAdminRoleId"));
        FROM_CONFIGS.put("discordModeratorRoleId", discord_roles.get("moderatorRoleId"));
        FROM_CONFIGS.put("discordDevRoleId", discord_roles.get("devRoleId"));
        FROM_CONFIGS.put("discordHelperRoleId", discord_roles.get("helperRoleId"));

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

        FROM_CONFIGS.put("confirmLinkCmdName", misc.get("confirmLinkCmdName"));
        FROM_CONFIGS.put("minecraftInfosLink", misc.get("minecraftInfosLink"));
        FROM_CONFIGS.put("serverContactEmail", misc.get("serverContactEmail"));

        this.setHidden();
    }

    private void setHidden() {
        FROM_CONFIGS.put("envType", this.envType);
        FROM_CONFIGS.put("pluginVersion", this.pluginVersion);
        FROM_CONFIGS.put("dbJdbcUrl", "jdbc:"
                + get("dbType") + "://"
                + get("dbHost") + ":"
                + get("dbPort") + "/"
                + get("dbName"));
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

}
