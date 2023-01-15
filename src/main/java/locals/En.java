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
    PLUGIN_HELLO("**The plugin `%s` " + ISACTIVE + "**\n\n"),
    PLUGIN_HELLO_ERROR("**`" + ERROR + "`, The plugin `%s` as encountered errors at initialisation**\n"),
    PLUGIN_GOODBYE("**The plugin `%s` " + ISINACTIVE + "**\n\n"),
    PLUGIN_NAME("Name: `%s`"),
    PLUGIN_VERSION("Version: `%s`"),
    PLUGIN_DEVBY("Developped by: `%s`"),

    //ERRORS
    CHECK_LOGS("**Check your `log` files !!!!**"),

    ;

    final String trans;

    En(String trans) {
        this.trans = trans;
    }

}