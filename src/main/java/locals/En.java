package locals;

public enum En {
    //GLOBAL
    RAINY("rainy"),
    SUNNY("sunny"),
    NAME("name"),
    VERSION("version"),
    ISACTIVE("is active"),
    ISINACTIVE("is inactive"),
    DOREGISTER("Register on the Discord® server"),
    MINECRAFT_ALREADYREGISTERED("This account is already confirmed..."),
    ACCOUNTSINFOS("Accounts informations"),

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
    PLUGIN_HELLO_ERROR("**`ERROR:` The plugin `%s` as encountered errors at initialisation**\n"),
    PLUGIN_GOODBYE("**The plugin `%s` %s**\n\n"),
    PLUGIN_NAME("Name: `%s`"),
    PLUGIN_VERSION("Version: `%s`"),
    PLUGIN_DEVBY("Developped by: %s"),

    //INFOS
    INFO_LEGITIMATE("If this request seems illegitimate to you, contact an administrator!!!"),
    INFO_TRYREGISTERAGAIN("Try to do a registration request on Discord®."),

    //WARNS
    WARN_REGISTRATIONDELAY("You're already registered, but the delay to confirm this account is overdue..."),

    //ERRORS
    ERROR("ERROR"),
    CONTACT_ADMNIN("Contact an administrator..."),
    CHECK_LOGS("**Check your `log` files!!!!**"),
    NOTREGISTERED("Your registration could not be fetched..."),
    CMD_ERROR("Sorry... an error as occured during this request!!!"),

    //MINECRAFT_CMD
    CMD_LINK("wje-link"),

    //DISCORD CMD
    CMD_SERVER("server"),
    CMD_REGISTER("register"),
    CMD_LOOKUP("search"),
    //PARAMS
    PARAM_PJAVA("java-pseudo"),
    PARAM_PBEDR("bedrock-pseudo"),
    PARAM_REGISTR_LABELJ("Your Java pseudo"),
    PARAM_REGISTR_LABELB("Your Bedrock pseudo"),
    PARAM_LOOKUP_LABEL("The uuid or pseudo to search for"),
    //DESC
    DESC_SERVER("Show the `Minecraft®` server informations."),
    DESC_REGISTR("Register on the `Minecraft®` server."),
    DESC_LOOKUP("Find `Minecraft®` players infos by uuid or pseudo."),

    //ACL
    USERONLY_CMD("This command id reserved for Discord® registered users only."),

    ;

    final String trans;

    En(String trans) {
        this.trans = trans;
    }

}