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
    PLUGIN_HELLO("**Le plugin `%s` %s **\n\n"),
    PLUGIN_HELLO_ERROR("**`ERREUR:` Le plugin `%s` a rencontré des `problèmes` à l'initialisation**\n"),
    PLUGIN_GOODBYE("**Le plugin `%s` %s **\n\n"),
    PLUGIN_NAME("Nom: `%s`"),
    PLUGIN_VERSION("Version: `%s`"),
    PLUGIN_DEVBY("Développer par: `%s`"),

    //ERRORS
    CHECK_LOGS("**Regarder les fichers de `log` !!!!**"),

    //DISCORD CMD
    CMD_SERVER("serveur"),
    CMD_REGISTER("enregistrer"),
    CMD_LOOKUP("recherche"),
    //PARAMS
    PARAM_PJAVA("pseudo-java"),
    PARAM_PBEDR("pseudo-bedrock"),
    PARAM_REGISTR_LABELJ("Votre pseudo Java"),
    PARAM_REGISTR_LABELB("Votre pseudo Bedrock"),
    PARAM_LOOKUP_LABEL("Le uuid ou le pseudo de recherche"),
    //DESC
    DESC_SERVER("Afficher les informations du serveur `Minecraft®`."),
    DESC_REGISTR("S'enregister sur le serveur `Minecraft®`."),
    DESC_LOOKUP("Trouver des infos de joueurs Minecraft® par uuid ou pseudo."),

    ;

    final String trans;

    Fr(String trans) {
        this.trans = trans;
    }
}