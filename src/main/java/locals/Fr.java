package locals;

public enum Fr {
    //GLOBAL
    RAINY("pluvieux"),
    SUNNY("ensoleillé"),
    NAME("nom"),
    VERSION("version"),
    ISACTIVE("est actif"),
    ISINACTIVE("est inactif"),
    DOREGISTER("Enregistrer vous sur le serveur Discord®"),
    MINECRAFT_ALREADYREGISTERED("Ce compte est déja confirmé..."),
    ACCOUNTSINFOS("Informations de comptes"),

    //REPLIES
    LANG_CHANGED("Votre langue a été modifié"),
    LANG_CURRENT("Votre langue est actuellement"),


    //TITLES
    TITLE_ACCOUNT_CONFIRM("Confirmation de vos comptes"),

    //EMBEDS
    EMBD_LINK_DESC("Veuillez confirmer la demande de relation"),
    EMBD_LINK_YESME("Oui, c'est bien moi"),
    EMBD_LINK_NOTME("Non, ce n'est pas moi"),
    EMBD_LINK_POLICY("En cliquant sur `OUI` vous confirmez que ces comptes seront reliés et que vous en êtes le détenteur.\n" +
        "En cliquant sur `NON` les liens temporaires seront détruits et toutes activitées courrantes et futures seront suspendues."),

    //LABELS
    LABEL_LONG_MC("Identifiant Minecraft®"),
    LABEL_MIBECRAFT_UUID("Minecraft® uuid"),
    LABEL_LONG_DC("Identifiant Discord®"),
    LABEL_DISCORD_ID("Discord® id"),
    LABEL_DISCORD_TAG("Discord® tag"),
    LABEL_LONG_BEDROCK("Identifiant Bedrock®"),
    LABEL_BEDROCK_ID("Bedrock® id"),
    LABEL_LONG_JAVA("Identifiant Java"),
    LABEL_JAVA_ID("Java® id"),
    LABEL_USECMD("Utilisez la commande"),

    //PLUGIN
    PLUGIN_HELLO("**Le plugin `%s` %s **\n\n"),
    PLUGIN_HELLO_ERROR("❌ **`ERREUR:` Le plugin `%s` a rencontré des `problèmes` à l'initialisation**\n"),
    PLUGIN_GOODBYE("**Le plugin `%s` %s **\n\n"),
    PLUGIN_NAME("Nom: `%s`"),
    PLUGIN_VERSION("Version: `%s`"),
    PLUGIN_DEVBY("Développer par: %s"),

    //INFOS
    INFO_LEGITIMATE("Si cette demande vous semble illégitime, contactez un administrateur!!!"),
    INFO_TRYREGISTERAGAIN("Essayez de refaire une demande d'enregistrment sur Discord®."),

    //WARNS
    WARN_REGISTRATIONDELAY("❌ Vous êtes bien enregistré, mais le délai pour confirmer ce compte est dépassé..."),

    //ERRORS
    ERROR("❌ ERREUR"),
    CONTACT_ADMNIN("Contactez un admin..."),
    CHECK_LOGS("**Regarder les fichers de `log`!!!!**"),
    NOTREGISTERED("Votre enregistrement n'a pas pu être retrouvé..."),
    CMD_ERROR("❌ Désoler... une erreur est survenu lors de cette demande!!!"),

    //MINECRAFT_CMD
    CMD_LINK("wje-link"),

    //DISCORD CMD
    CMD_SERVER("serveur"),
    CMD_REGISTER("enregistrer"),
    CMD_LOOKUP("recherche"),
    CMD_SETLOCAL("langue"),
    //PARAMS
    PARAM_PJAVA("pseudo-java"),
    PARAM_PBEDR("pseudo-bedrock"),
    PARAM_REGISTR_LABELJ("Votre pseudo Java"),
    PARAM_REGISTR_LABELB("Votre pseudo Bedrock"),
    PARAM_LOOKUP_LABEL("Le uuid ou le pseudo de recherche"),
    PARAM_LOCAL_SETLOCAL("langue"),
    PARAM_LOCAL_LABEL("Language disponible: ['FR, EN, ES']"),
    //DESC
    DESC_SERVER("Afficher les informations du serveur `Minecraft®`."),
    DESC_REGISTR("S'enregister sur le serveur `Minecraft®`."),
    DESC_LOOKUP("Trouver des infos de joueurs Minecraft® par uuid ou pseudo."),
    DESC_SETLOCAL("Changer la langue d'affichage.\n FR: francais, EN: english, ES: española."),

    //ACL
    TEXTONLY_CMD("❌ Cette commande est disponible seulement dans les cannaux textuels."),
    MEMBERONLY_CMD("❌ Cette commande est disponible seulement dans les cannaux textuels de guild utilisant ce plugin."),
    USERONLY_CMD("❌ Cette commande est réservée aux utilisateurs enregistrés par Discord®."),

    ;

    final String trans;

    Fr(String trans) {
        this.trans = trans;
    }

}