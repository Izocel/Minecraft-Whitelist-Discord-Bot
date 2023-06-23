package configs;

import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import main.WhitelistJe;

public final class ConfigManager {
    private final String pluginVersion = "2023.3";
    private final String envType = "production";

    private HashMap<String, String> configs = new HashMap<>();
    private String[] sectionsNames = {
            "discord",
            "database",
            "minecraft",
            "features",
            "misc"
    };

    public ConfigManager() {
        configs.put("envType", this.envType);
        configs.put("pluginVersion", this.pluginVersion);

        final HashMap<String, ConfigurationSection> sections = new HashMap<>();
        FileConfiguration yamlConfigs = WhitelistJe.getPlugin(WhitelistJe.class).getConfig();
        for (final String name : sectionsNames) {
            final ConfigurationSection section = yamlConfigs.getConfigurationSection(name);
            sections.put(name, section);
        }

        final ConfigurationSection discord = sections.get("discord");
        final ConfigurationSection discord_channels = discord.getConfigurationSection("channels");
        final ConfigurationSection discord_roles = discord.getConfigurationSection("roles");
        final ConfigurationSection database = sections.get("database");
        final ConfigurationSection minecraft = sections.get("minecraft");
        final ConfigurationSection features = sections.get("features");
        final ConfigurationSection misc = sections.get("misc");

        configs.put("discordBotToken", discord.getString("boToken"));
        configs.put("discordServerId", discord.getString("serverId"));

        configs.put("botLogChannelId", discord_channels.getString("welcomeChanelId"));
        configs.put("javaLogChannelId", discord_channels.getString("adminChanelId"));
        configs.put("discordWelcomeChanelId", discord_channels.getString("whitelistChanelId"));
        configs.put("discordAdminChanelId", discord_channels.getString("botLogChannelId"));
        configs.put("discordWhitelistChanelId", discord_channels.getString("javaLogChannelId"));

        configs.put("discordAdminRoleId", discord_roles.getString("discordAdminRoleId"));
        configs.put("discordModeratorRoleId", discord_roles.getString("moderatorRoleId"));
        configs.put("discordDevRoleId", discord_roles.getString("devRoleId"));
        configs.put("discordHelperRoleId", discord_roles.getString("helperRoleId"));

        configs.put("dbType", database.getString("type"));
        configs.put("dbHost", database.getString("host"));
        configs.put("dbPort", database.getString("port"));
        configs.put("dbName", database.getString("name"));
        configs.put("dbUser", database.getString("user"));
        configs.put("dbPass", database.getString("password"));
        configs.put("dbDefTable", database.getString("defaultTable"));
        configs.put("dbMaxConnection", database.getString("maxConnection"));
        configs.put("dbMaxConnectionIDLE", database.getString("maxIdleConnection"));

        configs.put("dbJdbcUrl", "jdbc:"
                + get("dbType") + "://"
                + get("dbHost") + ":"
                + get("dbPort") + "/"
                + get("dbName"));

        configs.put("portJava", minecraft.getString("portJava"));
        configs.put("portBedrock", minecraft.getString("portBedrock"));
        configs.put("javaIp", minecraft.getString("javaIp"));
        configs.put("bedrockIp", minecraft.getString("bedrockIp"));

        configs.put("hoursToConfirmMcAccount", features.getString("maxHoursFor2FA"));
        configs.put("showSubWorlddMeteo", features.getString("showAllWordldsMeteo"));
        configs.put("defaultLang", features.getString("defaultLang"));

        configs.put("confirmLinkCmdName", misc.getString("confirmLinkCmdName"));
        configs.put("minecraftInfosLink", misc.getString("minecraftInfosLink"));
    }

    public String get(String key, String defaultValue) {
        String result = this.configs.get(key);
        return result == null ? defaultValue : result;
    }

    public String get(String key) {
        return this.configs.get(key);
    }

}
