//modifié

package configs;

import java.util.HashMap;

public final class ConfigManager {

    /////////////////////////// EDIT THE PRIVATE VARS ONLY \\\\\\\\\\\\\\\\\\\\\\\\\\\

    private final String envType="production";
    private final String discordBotToken="MTA5MTg2OTM0MDI0MjAxODMyNA.G8Qz7R.GDvtlQrp-T3PCitHqCuRwWMfNzcO2dOKRiqn_w";

    private final String discordServerId="770057600867237898";
    private final String discordWelcomeChanelId="1088581487470850140";
    private final String discordAdminChanelId="1060368458816172112";
    private final String discordWhitelistChanelId="1091582563971248149";
    private final String botLogChannelId="927283856053252106"; // unused yet
    private final String javaLogChannelId="927283856053252106"; // unused yet

    private final String discordAdminRoleId="1090853358786588795";
    private final String discordModeratorRoleId="1090853850489036811";
    private final String discordDevRoleId="1090854262751371344";
    private final String discordHelperRoleId="1090854380745535538";
    
    private final String mysqlHost = "localhost";
    private final String mysqlPort = "3306";
    private final String mysqlDb = "minecraft";
    private final String mysqlUser = "minecraft";
    private final String mysqlPass = "h6u9effkcj735vcypxxj8durh9rjzkjz";
    private final String mysqlDefTable="wje_users";
    private final String mysqlJdbcUrl="jdbc:mysql://"+ mysqlHost + ":" + mysqlPort + "/" + mysqlDb;
    private final String mysqlMaxConnection="15";
    private final String mysqlMaxConnectionIDLE="5";

    private final String portJava="25565";
    private final String portBedrock="19132";
    private final String javaIp="tempcity.click";
    private final String bedrockIp="bedrock.tempcity.click";
    private final String showSubWorlddMeteo="false";
    private final String hoursToConfirmMcAccount="48"; // zero to not use this feature

    private final String minecrafInfosLink="https://www.fiverr.com/rvdprojects";

    private final String pluginVersion="2023.1";

    // FR, EN, FR_EN, EN_FR
    private final String defaultLang="EN";

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
        configs.put("javaIp", this.javaIp);
        configs.put("bedrockIp", this.bedrockIp);
        configs.put("pluginVersion", this.pluginVersion);
        configs.put("showSubWorlddMeteo", this.showSubWorlddMeteo);
        configs.put("hoursToConfirmMcAccount", this.hoursToConfirmMcAccount);
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
