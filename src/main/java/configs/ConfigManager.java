package configs;

import java.util.HashMap;

public final class ConfigManager {

    /////////////////////////// EDIT THE PRIVATE VARS ONLY \\\\\\\\\\\\\\\\\\\\\\\\\\\

    private final String envType="production";
    private final String discordBotToken="MTAxNzk3MTY5MDM0MjQ1NzM1Ng.Gn4Tqe.BtLPvmtze547Ib2QRFHywWClvvnbf5ScKAhbZ4";

    private final String discordServerId="276931890735218689";
    private final String discordWelcomeChanelId="925583159964352643";
    private final String discordAdminChanelId="927283856053252106";
    private final String discordWhitelistChanelId="927283856053252106";
    private final String botLogChannelId="927283856053252106"; // unused yet
    private final String javaLogChannelId="927283856053252106"; // unused yet

    private final String discordAdminRoleId="293133215500075010";
    private final String discordModeratorRoleId="1021054284214833182";
    private final String discordDevRoleId="1021054460438523976";
    private final String discordHelperRoleId="1021054460438523976";
    
    private final String dbType="mysql"; //msql or mariadb
    private final String dbHost="127.0.0.1";
    private final String dbPort="3306";
    private final String dbName="whitelist_je";
    private final String dbUser="whitelist_je";
    private final String dbPass="mysql";
    private final String dbDefTable="wje_users";
    private final String dbJdbcUrl="jdbc:" + dbType + "://"+ dbHost + ":" + dbPort + "/" + dbName;
    private final String dbMaxConnection="15";
    private final String dbMaxConnectionIDLE="5";

    private final String portJava="25565";
    private final String portBedrock="19132";
    private final String javaIp="rvdprojects.synology.me";
    private final String bedrockIp="rvdprojects.synology.me";
    private final String showSubWorlddMeteo="true";
    private final String hoursToConfirmMcAccount="24"; // zero to not use this feature

    private final String minecrafInfosLink="https://www.fiverr.com/rvdprojects";
    private final String pluginVersion="2023.3";

    // FR, EN, FR_EN, EN_FR
    private final String defaultLang="FR";
    private final String confirmLinkCmdName="wje-link";

    /////////////////////////// EDIT THE PRIVATE VARS ONLY \\\\\\\\\\\\\\\\\\\\\\\\\\\

    private HashMap<String, String> configs = new HashMap<>();

    public ConfigManager() {
        configs.put("envType", this.envType);
        configs.put("discordBotToken", this.discordBotToken);
        configs.put("discordServerId", this.discordServerId);
        configs.put("botLogChannelId", this.botLogChannelId);
        configs.put("javaLogChannelId", this.javaLogChannelId);
        configs.put("discordWelcomeChanelId", this.discordWelcomeChanelId);
        configs.put("discordAdminChanelId", this.discordAdminChanelId);
        configs.put("discordWhitelistChanelId", this.discordWhitelistChanelId);
        configs.put("discordAdminRoleId", this.discordAdminRoleId);
        configs.put("discordModeratorRoleId", this.discordModeratorRoleId);
        configs.put("discordDevRoleId", this.discordDevRoleId);
        configs.put("discordHelperRoleId", this.discordHelperRoleId);
        configs.put("dbType", this.dbType);
        configs.put("dbHost", this.dbHost);
        configs.put("dbPort", this.dbPort);
        configs.put("dbName", this.dbName);
        configs.put("dbUser", this.dbUser);
        configs.put("dbPass", this.dbPass);
        configs.put("dbDefTable", this.dbDefTable);
        configs.put("dbJdbcUrl", this.dbJdbcUrl);
        configs.put("dbMaxConnection", this.dbMaxConnection);
        configs.put("dbMaxConnectionIDLE", this.dbMaxConnectionIDLE);
        configs.put("portJava", this.portJava);
        configs.put("portBedrock", this.portBedrock);
        configs.put("javaIp", this.javaIp);
        configs.put("bedrockIp", this.bedrockIp);
        configs.put("pluginVersion", this.pluginVersion);
        configs.put("showSubWorlddMeteo", this.showSubWorlddMeteo);
        configs.put("hoursToConfirmMcAccount", this.hoursToConfirmMcAccount);
        configs.put("minecrafInfosLink", this.minecrafInfosLink);
        configs.put("defaultLang", this.defaultLang);
        configs.put("confirmLinkCmdName", this.confirmLinkCmdName);
    }

    public String get(String key, String defaultValue) {
        String result = this.configs.get(key);
        return result == null ? defaultValue : result;
    }

    public String get(String key) {
        return this.configs.get(key);
    }


}
