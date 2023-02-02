package locals;

public enum Fr {
    //GLOBAL
    YES("Oui"),
    NO("Non"),
    RAINY("pluvieux"),
    SUNNY("ensoleillé"),
    STORMY("orageux"),
    DAY("Jour"),
    NIGHT("Nuit"),
    NAME("nom"),
    VERSION("Version"),
    ISACTIVE("est actif"),
    ISINACTIVE("est inactif"),
    DOREGISTER("Enregistrer vous sur le serveur Discord®"),
    MINECRAFT_ALREADYREGISTERED("Ce compte est déja confirmé..."),
    ACCOUNTSINFOS("Informations de comptes"),
    INFORMATION("Informations"),
    SERVER("Serveur"),
    WORLDS("Mondes"),
    DEVS("Développeurs"),
    PORT("Port"),
    ONLINE_MODE("Mode en ligne"),
    WHITELISTED("Whitelisted"),
    DEFAULT_GAMEMOD("Mode de jeu default"),
    DESCRIPTION("Description"),
    CONNECTED_USER("Joueur connecté"),
    CONNECTED_USERS("Joueurs connectés"),
    SERVER_ACTIVITIES("🌿 Activités du serveur"),
    TIME_METEO("Météo et temps"),
    SOME_EXAMPLES("Voici des examples"),

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
    INFO_CHECK_YOUR_MSG("Voir les détails dans vos messages privés."),
    INFO_CONTACT_ADMIN_MORE_INFO("Pour en s'avoir d'avantage, contactez un administrateur directement..."),
    INFO_ALREADY_ACCEPTED_CONNECT("**Votre compte `%s` est déjà accepté sur le serveur...**\n" + "Il suffit de vous connecter. `Enjoy` ⛏🧱"),
    INFO_MUST_CONFIRM_ACCOUNT("**Une confirmation de votre compte `%s` est nécéssaire.**\n"),
    INFO_TIME_TO_CONFIRM_SINCE("Pour confimer votre compte vous aviez `%ih` depuis l'aprobation pour vous connecter au server Mincecraft®\n"),

    //WARNS
    WARN_REGISTRATIONDELAY("⚠️ Vous êtes bien enregistré, mais le délai pour confirmer ce compte est dépassé..."),
    WARN_ALREADTY_REGISTERED("⚠️ **Ce pseudo `%s` est déjà enregistrer par un autre joueur**"),
    WARN_NOT_ACCEPTED_YET("⚠️ **Ce compte `%s` n'a pas encore été accepté sur le serveur.**"),

    //ERRORS
    ERROR("❌ ERREUR"),
    CONTACT_ADMNIN("Contactez un admin..."),
    CHECK_LOGS("**Regarder les fichers de `log`!!!!**"),
    NOTREGISTERED("Votre enregistrement n'a pas pu être retrouvé..."),
    CMD_ERROR("❌ Désoler... une erreur est survenu lors de cette demande!!!"),
    LOOKUP_ERROR("Cette valeur de recherche n'est pas valide..."),
    LOOKUP_PARAM_ERROR("Vous devez choisir un type de rcherche valide"),
    REGISTER_CMD_PARAM_ERROR("Vous devez fournir au moins un pseudo pour utiliser cette commande..."),
    REGISTER_CMD_FORMAT_ERROR("❌ Vos `identifiants` comportaient des `erreurs` de format."),
    REGISTER_CMD_NOT_FOUND_UUID("❌ **Votre UUID `%s` n'a pas pu être retrouvés sur les serveurs...**"),
    REGISTER_CMD_ERROR("❌ **Désoler, l'enregistrement pour votre pseudo `%s` ne c'est pas bien passé.**"),


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
    GUILDONLY_CMD("❌ Cette commande est disponible seulement dans les cannaux textuels de guild utilisant ce plugin."),
    USERONLY_CMD("❌ Cette commande est réservée aux utilisateurs enregistrés par Discord®."),

    //MISC
    SERVER_IS_UP("Le serveur est up and running boyyssss!"),

    ;

    final String trans;

    Fr(String trans) {
        this.trans = trans;
    }

}