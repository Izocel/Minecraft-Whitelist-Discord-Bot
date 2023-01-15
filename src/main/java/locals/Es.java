package locals;

public enum Es {
    //GLOBAL
    RAINY("pluvieux"), // à traduire
    SUNNY("ensoleillé"), // à traduire
    NAME("nom"), // à traduire
    VERSION("version"), // à traduire
    ISACTIVE("est actif"), // à traduire
    ISINACTIVE("est inactif"), // à traduire
    ERROR("ERREUR"), // à traduire

    //PLUGIN
    PLUGIN_HELLO("**Le plugin `%s` " + ISACTIVE + "**\n\n"), // à traduire
    PLUGIN_HELLO_ERROR("**`" + ERROR + "`, Le plugin `%s` a rencontré des `problèmes` à l'initialisation**\n"), // à traduire
    PLUGIN_GOODBYE("**Le plugin `%s` " + ISINACTIVE + "**\n\n"), // à traduire
    PLUGIN_NAME("Nom: `%s`"), // à traduire
    PLUGIN_VERSION("Version: `%s`"), // à traduire
    PLUGIN_DEVBY("Développer par: `%s`"), // à traduire

    //ERRORS
    CHECK_LOGS("**Regarder les fichers de `log` !!!!**"), // à traduire

    //DISCORD CMD
    CMD_SERVER("serveur"), // à traduire
    CMD_REGISTER("enregistrer"), // à traduire
    CMD_LOOKUP("recherche"), // à traduire
    //PARAMS
    PARAM_PJAVA("pseudo-java"), // à traduire
    PARAM_PBEDR("pseudo-bedrock"), // à traduire
    PARAM_REGISTR_LABELJ("Votre pseudo Java"), // à traduire
    PARAM_REGISTR_LABELB("Votre pseudo Bedrock"), // à traduire
    PARAM_LOOKUP_LABEL("Le uuid ou le pseudo de recherche"), // à traduire
    //DESC
    DESC_SERVER("Afficher les informations du serveur `Minecraft®`."), // à traduire
    DESC_REGISTR("S'enregister sur le serveur `Minecraft®`."), // à traduire
    DESC_LOOKUP("Trouver des infos de joueurs Minecraft® par uuid ou pseudo."), // à traduire

    ;

    final String trans;

    Es(String trans) {
        this.trans = trans;
    }
}