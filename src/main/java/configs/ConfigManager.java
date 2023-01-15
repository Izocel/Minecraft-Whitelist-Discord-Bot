//fichier modifié

package configs;

import java.util.HashMap;

public final class ConfigManager {

    /////////////////////////// EDIT THE PRIVATE VARS ONLY
    /////////////////////////// \\\\\\\\\\\\\\\\\\\\\\\\\\\

    private final String envType="production";
    private final String discordBotToken="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    private final String discordOwnerId = "807839780309172255";
    private final String discordServerId = "770057600867237898";
    private final String discordWelcomeChanelId = "770148932075782176";
    private final String discordAdminChanelId = "805222201027985478";
    private final String discordWhitelistChanelId = "805222201027985478";
    private final String botLogChannelId = "805222201027985478";
    private final String javaLogChannelId = "892178805941215243";

    private final String discordAdminRoleId = "809003930884505602";
    private final String discordModeratorRoleId = "783839953372053516";
    private final String discordDevRoleId = "926270775298752512";

    private final String mysqlHost = "localhost";
    private final String mysqlPort = "3306";
    private final String mysqlDb = "minecraft";
    private final String mysqlUser = "minecraft";
    private final String mysqlPass = "h6u9effkcj735vcypxxj8durh9rjzkjz";
    private final String mysqlDefTable = "users";
    private final String mysqlJdbcUrl = "jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlDb;
    private final String mysqlMaxConnection = "10";
    private final String mysqlMaxConnectionIDLE = "5";

    private final String portJava = "25565";
    private final String portBedrock = "19132";
    private final String paperMcIp = "server.minecraft.tumeniaises.ca";
    private final String showSubWorlddMeteo = "false";
    private final String hoursToConfirmMcAccount = "48"; // zero to not use this feature

    // MC commands names
    private final String confirmLinkCmdName = "wje-link";


    private final String minecrafInfosLink = "https://www.fiverr.com/rvdprojects";

    private final String pluginVersion = "2023.1";

    // FR, EN, FR_EN, EN_FR
    private final String defaultLang="FR";

    /////////////////////////// EDIT THE PRIVATE VARS ONLY \\\\\\\\\\\\\\\\\\\\\\\\\\\

    private HashMap<String, String> configs = new HashMap<>();

    public ConfigManager() {
        configs.put("envType", this.envType);
        configs.put("discordBotToken", this.discordBotToken);
        configs.put("discordOwnerId", this.discordOwnerId);
        configs.put("discordServerId", this.discordServerId);
        configs.put("botLogChannelId", this.botLogChannelId);
        configs.put("javaLogChannelId", this.javaLogChannelId);
        configs.put("discordWelcomeChanelId", this.discordWelcomeChanelId);
        configs.put("discordAdminChanelId", this.discordAdminChanelId);
        configs.put("discordWhitelistChanelId", this.discordWhitelistChanelId);
        configs.put("discordAdminRoleId", this.discordAdminRoleId);
        configs.put("discordModeratorRoleId", this.discordModeratorRoleId);
        configs.put("discordDevRoleId", this.discordDevRoleId);
        configs.put("mysqlHost", this.mysqlHost);
        configs.put("mysqlPort", this.mysqlPort);
        configs.put("mysqlDb", this.mysqlDb);
        configs.put("mysqlUser", this.mysqlUser);
        configs.put("mysqlPass", this.mysqlPass);
        configs.put("mysqlDefTable", this.mysqlDefTable);
        configs.put("mysqlJdbcUrl", this.mysqlJdbcUrl);
        configs.put("mysqlMaxConnection", this.mysqlMaxConnection);
        configs.put("mysqlMaxConnectionIDLE", this.mysqlMaxConnectionIDLE);
        configs.put("portJava", this.portJava);
        configs.put("portBedrock", this.portBedrock);
        configs.put("paperMcIp", this.paperMcIp);
        configs.put("pluginVersion", this.pluginVersion);
        configs.put("showSubWorlddMeteo", this.showSubWorlddMeteo);
        configs.put("hoursToConfirmMcAccount", this.hoursToConfirmMcAccount);

        configs.put("confirmLinkCmdName", this.confirmLinkCmdName);

        configs.put("minecrafInfosLink", this.minecrafInfosLink);
        configs.put("defaultLang", this.defaultLang);
    }

    public String get(String key, String defaultValue) {
        String result = this.configs.get(key);
        return result == null ? defaultValue : result;
    }

    public String get(String key) {
        return this.configs.get(key);
    }

}
