package locals;

public enum En {
    RAINY("rainy"),
    SUNNY("sunny"),
    VERSION("version"),
    NAME("name"),
    ISACTIVE("is active"),
    ISINACTIVE("is inactive"),
    ERROR("ERROR"),

    //PLUGIN
    PLUGIN_HELLO("**The plugin `%s` %s**\n\n"),
    PLUGIN_HELLO_ERROR("**`ERROR:` The plugin `%s` as encountered errors at initialisation**\n"),
    PLUGIN_GOODBYE("**The plugin `%s` %s**\n\n"),
    PLUGIN_NAME("Name: `%s`"),
    PLUGIN_VERSION("Version: `%s`"),
    PLUGIN_DEVBY("Developped by: `%s`"),

    //ERRORS
    CHECK_LOGS("**Check your `log` files !!!!**"),

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


    ;

    final String trans;

    En(String trans) {
        this.trans = trans;
    }

}