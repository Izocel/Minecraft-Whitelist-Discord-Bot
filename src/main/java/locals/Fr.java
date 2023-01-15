package locals;

public enum Fr {
    //GLOBAL
    RAINY("pluvieux"),
    SUNNY("ensoleillé"),
    NAME("nom"),
    VERSION("version"),
    ISACTIVE("est actif"),
    ISINACTIVE("est inactif"),
    ERROR("ERREUR"),

    //PLUGIN
    PLUGIN_HELLO("**Le plugin `%s` " + ISACTIVE + "**\n\n"),
    PLUGIN_HELLO_ERROR("**`" + ERROR + "`, Le plugin `%s` a rencontré des `problèmes` à l'initialisation**\n"),
    PLUGIN_GOODBYE("**Le plugin `%s` " + ISINACTIVE + "**\n\n"),
    PLUGIN_NAME("Nom: `%s`"),
    PLUGIN_VERSION("Version: `%s`"),
    PLUGIN_DEVBY("Développer par: `%s`"),


    //ERRORS
    CHECK_LOGS("**Regarder les fichers de `log` !!!!**"),

    ;

    final String trans;

    Fr(String trans) {
        this.trans = trans;
    }
}