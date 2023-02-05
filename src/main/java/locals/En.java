package locals;

public enum En {
    //GLOBAL
    YES("Yes"),
    NO("No"),
    RAINY("rainy"),
    SUNNY("sunny"),
    STORMY("stormy"),
    DAY("Day"),
    NIGHT("Night"),
    NAME("name"),
    VERSION("Version"),
    ISACTIVE("is active"),
    ISINACTIVE("is inactive"),
    DOREGISTER("Register on the Discord® server"),
    MINECRAFT_ALREADYREGISTERED("This account is already confirmed..."),
    ACCOUNTSINFOS("Accounts informations"),
    INFORMATION("Informations"),
    SERVER("Server"),
    WORLDS("Worlds"),
    DEVS("Developers"),
    PORT("Port"),
    ONLINE_MODE("Online mode"),
    WHITELISTED("Whitelisted"),
    DEFAULT_GAMEMOD("Default gamemode"),
    DESCRIPTION("Description"),
    CONNECTED_USER("Connected user"),
    CONNECTED_USERS("Connected users"),
    SERVER_ACTIVITIES("🌿 Server activities"),
    TIME_METEO("Meteo and time"),
    SOME_EXAMPLES("Here's some examples"),

    //BUTTONS
    BTN_ACCEPT("✔️ Accept"),
    BTN_REFUSE("❌ Refuse"),    

    //REPLIES
    LANG_CHANGED("Your language was changed"),
    LANG_CURRENT("You current language is"),

    //TITLES
    TITLE_ACCOUNT_CONFIRM("Your accounts confirmation"),

    //EMBEDS
    EMBD_LINK_DESC("Please confirm this relation request"),
    EMBD_LINK_YESME("Yes it's me"),
    EMBD_LINK_NOTME("No it's not me"),
    EMBD_LINK_POLICY("When clicking on `YES` you assert that those accounts are to be linked, and that you're the legitimate holder.\n" +
        "When clicking on `NO` the temporary links will be dissolved, and all current or further activities will be suspended."),

    //LABELS
    LABEL_LONG_MC("Minecraft® identification"),
    LABEL_MIBECRAFT_UUID("Minecraft® uuid"),
    LABEL_LONG_DC("Discord® identification"),
    LABEL_DISCORD_ID("Discord® id"),
    LABEL_DISCORD_TAG("Discord® tag"),
    LABEL_LONG_BEDROCK("Bedrock® identification"),
    LABEL_BEDROCK_ID("Bedrock® id"),
    LABEL_LONG_JAVA("Java identification"),
    LABEL_JAVA_ID("Java® id"),
    LABEL_USECMD("Use the command"),

    //PLUGIN
    PLUGIN_HELLO("**The plugin `%s` %s**\n\n"),
    PLUGIN_HELLO_ERROR("❌ **`ERROR:` The plugin `%s` as encountered errors at initialisation**\n"),
    PLUGIN_GOODBYE("**The plugin `%s` %s**\n\n"),
    PLUGIN_NAME("Name: `%s`"),
    PLUGIN_VERSION("Version: `%s`"),
    PLUGIN_DEVBY("Developped by: %s"),

    //INFOS
    INFO_LEGITIMATE("If this request seems illegitimate to you, contact an administrator!!!"),
    INFO_TRYREGISTERAGAIN("Try to do a registration request on Discord®."),
    INFO_CHECK_YOUR_MSG("See more deetails in your private messages."),
    INFO_CONTACT_ADMIN_MORE_INFO("To get more informations, contact an admin directly..."),
    INFO_ALREADY_ACCEPTED_CONNECT("**Your `%s` account is already registered...**\n" + "You can simply join it now. `Enjoy` ⛏🧱"),
    INFO_MUST_CONFIRM_ACCOUNT("**A confrimation for your `%s` account is needed.**\n"),
    INFO_TIME_TO_CONFIRM_SINCE("To confirm your account, you had `%ih` since the approbation, and connect to the Mincecraft® server\n"),
    INFO_ACCES_REQUESTED("**Your `%s` access request for `%s` was sent to moderators.**"),
    INFO_PLZ_AWAIT("**Thank you for waiting until the approval by one of them.**"),
    INFO_REGISTER_REQUEST("A `%s` player want to register on your `Minecraft®` server"),
    INFO_ACCEPTED_BY("✔️ Accepted by: %s"),
    INFO_REJECTED_BY("❌ Rejected by: %s"),
    INFO_ACCEPTED_REQUEST("Request acepted"),
    INFO_REJECTED_REQUEST("Request rejected"),
    INFO_WELCOME_USER("**We are glad to welcome, <@%s> :: `%s`.Enjoy  ⛏🧱 !!!**"),
    INFO_TIME_TO_CONFIRM("**You have `%sh` to connect to the `Minecraft®` server, and `confirm` your account.**"),
    INFO_USER_WAS_ACCEPTED("✔️ **The player <@%s> was gently accepted for the pseudo: `%s`.**"),
    INFO_USER_WAS_REJECTED("❌ **The player <@%s> was strongly refused for the pseudo: `%s`.**"),
    INFO_REJECTED_USER("**❌ Your regitration on the server was rejected.**"),

    //WARNS
    WARN_REGISTRATIONDELAY("⚠️ You're already registered, but the delay to confirm this account is overdue..."),
    WARN_ALREADTY_REGISTERED("⚠️ **This `%s` pseudo is already registered to another player**"),
    WARN_NOT_ACCEPTED_YET("⚠️ **This `%s` account was not yet accepted on the server by admins.**"),
    WARN_BAD_PSEUDO_FORMAT_EXPLAIN("⚠️ Your `%s` pseudo: `%s`, should containt between `3` and `16` characters.\nIt should not contain specials characters except underscores `_` or dashes `-`."),

    //ERRORS
    ERROR("❌ ERROR"),
    CONTACT_ADMNIN("Contact an administrator..."),
    CHECK_LOGS("**Check your `log` files!!!!**"),
    NOTREGISTERED("Your registration could not be fetched..."),
    CMD_ERROR("❌ Sorry... an error as occured during this request!!!"),
    LOOKUP_ERROR("This lookup value is not valid..."),
    LOOKUP_PARAM_ERROR("You must choose a valid lookup type"),
    REGISTER_CMD_PARAM_ERROR("You must provide at least one pseudo for this command..."),
    REGISTER_CMD_FORMAT_ERROR("❌ Your `identifications` contains format `errors`."),
    REGISTER_CMD_NOT_FOUND_UUID("❌ **Your `%s` UUID could not be found on the servers...**"),
    REGISTER_CMD_ERROR("❌ **Sorry, the registration for your `%s` account, could not be completed.**"),

    //MINECRAFT_CMD
    CMD_LINK("wje-link"),

    //DISCORD CMD
    CMD_SERVER("server"),
    CMD_REGISTER("register"),
    CMD_LOOKUP("search"),
    CMD_SETLOCAL("traduction"),
    //PARAMS
    PARAM_PJAVA("java"),
    PARAM_PBEDR("bedrock"),
    PARAM_REGISTR_LABELJ("Your Java pseudo"),
    PARAM_REGISTR_LABELB("Your Bedrock pseudo"),
    PARAM_LOOKUP_LABEL("The uuid or pseudo to search for"),
    PARAM_LOCAL_SETLOCAL("language"),
    PARAM_LOCAL_LABEL("Available language: ['FR, EN, ES']"),
    //DESC
    DESC_SERVER("Show the `Minecraft®` server informations."),
    DESC_REGISTR("Register on the `Minecraft®` server."),
    DESC_LOOKUP("Find `Minecraft®` players infos by uuid or pseudo."),
    DESC_SETLOCAL("Change the display language.\n FR: francais, EN: english, ES: española."),

    //ACL
    TEXTONLY_CMD("❌ This command is available only through textual channels."),
    GUILDONLY_CMD("❌ This command is available only through textual guild channels using this."),
    USERONLY_CMD("❌ This command id reserved for Discord® registered users only."),
    ROLE_NOT_ALLOWED("🔒 Too bad you don't have the necessary roles... 🔒"),

    //MISC
    SERVER_IS_UP("The server is up and running boyyssss!"),

    ;

    final String trans;

    En(String trans) {
        this.trans = trans;
    }

}