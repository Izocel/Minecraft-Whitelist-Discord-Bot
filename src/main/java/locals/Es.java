package locals;

public enum Es {
    //GLOBAL
    YES("Si"),
    NO("No"),
    RAINY("lluvioso"),
    SUNNY("soleado"),
    STORMY("tormentoso"),
    DAY("Día"),
    NIGHT("Noche"),
    NAME("nom"), // à traduire
    VERSION("Version"), // à traduire
    ISACTIVE("est actif"), // à traduire
    ISINACTIVE("est inactif"), // à traduire
    DOREGISTER("Enregistrer vous sur le serveur Discord®"), // à traduire
    MINECRAFT_ALREADYREGISTERED("Ce compte est déja confirmé..."), // à traduire
    ACCOUNTSINFOS("Informations de comptes"), // à traduire
    INFORMATION("Informations"), // à traduire
    SERVER("Serveur"), // à traduire
    WORLDS("Mondes"), // à traduire
    DEVS("Développeurs"), // à traduire
    PORT("Port"), // à traduire
    ONLINE_MODE("Mode en ligne"), // à trauire
    WHITELISTED("Whitelisted"), // à traduire
    DEFAULT_GAMEMOD("Mode de jeu"), // à traduire
    DESCRIPTION("Description"), // à traduire
    CONNECTED_USER("Joueur connecté"), // à traduire
    CONNECTED_USERS("Joueurs connectés"), // à traduire
    SERVER_ACTIVITIES("🌿 Activités du serveur"), // à traduire
    TIME_METEO("Météo et temps"), // à traduire
    SOME_EXAMPLES("Voici des examples"), // à traduire

    //BUTTONS
    BTN_ACCEPT("✔️ Aceptar"),
    BTN_REFUSE("❌ Rechazar"),

    //REPLIES
    LANG_CHANGED("Tu idioma fue cambiado"), // à traduire
    LANG_CURRENT("Su idioma es actualmente"), // à traduire

    //TITLES
    TITLE_ACCOUNT_CONFIRM("Confirmation de vos comptes"), // à traduire

    //EMBEDS
    EMBD_LINK_DESC("Veuillez confirmer la demande de relation"), // à traduire
    EMBD_LINK_YESME("Oui, c\'est bien moi"), // à traduire
    EMBD_LINK_NOTME("Non, ce n\'est pas moi"), // à traduire
    EMBD_LINK_POLICY("En cliquant sur `OUI` vous confirmez que ces comptes seront reliés et que vous en êtes le détenteur.\n" + // à traduire
        "En cliquant sur `NON` les liens temporaires seront détruits et toutes activitées courrantes et futures seront suspendues."), // à traduire

    //LABELS
    LABEL_LONG_MC("Identifiant Minecraft®"), // à traduire
    LABEL_MINECRAFT_UUID("Minecraft® uuid"), // à traduire
    LABEL_LONG_DC("Identifiant Discord®"), // à traduire
    LABEL_DISCORD_ID("Discord® id"), // à traduire
    LABEL_DISCORD_TAG("Discord® tag"), // à traduire
    LABEL_LONG_BEDROCK("Identifiant Bedrock®"), // à traduire
    LABEL_BEDROCK_ID("Bedrock® id"), // à traduire
    LABEL_LONG_JAVA("Identifiant Java"), // à traduire
    LABEL_JAVA_ID("Java® id"), // à traduire
    LABEL_USECMD("Utilisez la commande"), // à traduire

    //PLUGIN
    PLUGIN_HELLO("**Le plugin `%s` %s **\n\n"), // à traduire
    PLUGIN_HELLO_ERROR("❌ **`ERREUR:` Le plugin `%s` a rencontré des `problèmes` à l'initialisation**\n"), // à traduire
    PLUGIN_GOODBYE("**Le plugin `%s` %s **\n\n"), // à traduire
    PLUGIN_NAME("Nom: `%s`"), // à traduire
    PLUGIN_VERSION("Version: `%s`"), // à traduire
    PLUGIN_DEVBY("Développer par: %s"), // à traduire

    //INFOS
    INFO_LEGITIMATE("Si cette demande vous semble illégitime, contactez un administrateur!!!"), // à traduire
    INFO_TRYREGISTERAGAIN("Essayez de refaire une demande d'enregistrment sur Discord®."), // à traduire
    INFO_CHECK_YOUR_MSG("Voir les détails dans vos messages privés."), // à traduire
    INFO_CONTACT_ADMIN_MORE_INFO("Pour en s'avoir d'avantage, contactez un administrateur directement..."), // à traduire
    INFO_ALREADY_ACCEPTED_CONNECT("**Votre compte `%s` est déjà accepté sur le serveur...**\n" + "Il suffit de vous connecter. `Enjoy` ⛏🧱"), // à traduire
    INFO_MUST_CONFIRM_ACCOUNT("**Une confirmation de votre compte `%s` est nécéssaire.**\n"), // à traduire
    INFO_TIME_TO_CONFIRM_SINCE("Pour confimer votre compte vous aviez `%sh` depuis l'aprobation pour vous connecter au server Mincecraft®\n"), // à traduire
    INFO_ACCES_REQUESTED("**Votre demande d'accès `%s` pour `%s` a été envoyé aux modérateurs.**"), // à traduire
    INFO_PLZ_AWAIT("**Merci de patienter jusqu'à une prise de décision de leur part.**"), // à traduire
    INFO_REGISTER_REQUEST("Un joueur `%s` veut s'enregister sur votre serveur `Minecraft®`"), // à traduire
    INFO_ACCEPTED_BY("✔️ Aceptado por: %s"),
    INFO_REJECTED_BY("❌ Denegado por: %s"),
    INFO_ACCEPTED_REQUEST("Petición aceptada"),
    INFO_REJECTED_REQUEST("Solicitud rechazada"),
    INFO_WELCOME_USER("**Nous te souhaitons bienvenue, <@%s> :: `%s`.Enjoy  ⛏🧱 !!!**"), // à traduire
    INFO_TIME_TO_CONFIRM("**Vous avez `%sh` pour vous connecter au serveur `Minecraft®` et ainsi `confirmer` votre compte.**"), // à traduire
    INFO_USER_WAS_ACCEPTED("✔️ **Le joueur <@%s> a bien été approuvé pour le pseudo: `%s`.**"), // à traduire
    INFO_USER_WAS_REJECTED("❌ **Le joueur <@%s> a bien été refusé pour le pseudo: `%s`.**"), // à traduire
    INFO_REJECTED_USER("**❌ Votre enregistrement sur le serveur a été refusé.**"), // à traduire

    //WARNS
    WARN_REGISTRATIONDELAY("⚠️ Vous êtes bien enregistré, mais le délai pour confirmer ce compte est dépassé..."), // à traduire
    WARN_ALREADTY_REGISTERED("⚠️ **Ce pseudo `%s` est déjà enregistrer par un autre joueur**"), // à traduire
    WARN_NOT_ACCEPTED_YET("⚠️ **Ce compte `%s` n'a pas encore été accepté sur le serveur.**"), // à traduire
    WARN_BAD_PSEUDO_FORMAT_EXPLAIN("⚠️ Votre pseudo `%s`: `%s`, devrait comporter entre `3` et `16` caractères.\nIl ne doit pas comporter de caractères spéciaux à part des underscores `_` ou tirets `-`."), // à traduire

    //ERRORS
    ERROR("❌ ERREUR"), // à traduire
    CONTACT_ADMNIN("Contactez un admin..."), // à traduire
    CHECK_LOGS("**Regarder les fichers de `log`!!!!**"), // à traduire
    NOTREGISTERED("Votre enregistrement n'a pas pu être retrouvé..."), // à traduire
    CMD_ERROR("❌ Désoler... une erreur est survenu lors de cette demande!!!"), // à traduire
    LOOKUP_ERROR("Cette valeur de recherche n'est pas valide..."), // à traduire
    LOOKUP_PARAM_ERROR("Vous devez choisir un type de recherche valide"), // à traduire
    REGISTER_CMD_PARAM_ERROR("Vous devez fournir au moins un pseudo pour utiliser cette commande..."), // à traduire
    REGISTER_CMD_FORMAT_ERROR("❌ Vos `identifiants` comportaient des `erreurs` de format."), // à traduire
    REGISTER_CMD_NOT_FOUND_UUID("❌ **Votre UUID `%s` n'a pas pu être retrouvés sur les serveurs...**"), // à traduire
    REGISTER_CMD_ERROR("❌ **Désoler, l'enregistrement pour votre pseudo `%s` ne c'est pas bien passé.**"), // à traduire

    //MINECRAFT_CMD
    CMD_LINK("wje-link"), // à traduire

    //DISCORD CMD
    CMD_SERVER("serveur"), // à traduire
    CMD_REGISTER("enregistrer"), // à traduire
    CMD_LOOKUP("recherche"), // à traduire
    CMD_SETLOCAL("traduccion"),
    CMD_FETCHDB_USERS("miembros"),
    CMD_REMOVEDB_USERS("eliminar"),
    //PARAMS
    PARAM_PJAVA("pseudo-java"), // à traduire
    PARAM_PBEDR("pseudo-bedrock"), // à traduire
    PARAM_REGISTR_LABELJ("Votre pseudo Java"), // à traduire
    PARAM_REGISTR_LABELB("Votre pseudo Bedrock"), // à traduire
    PARAM_LOOKUP_LABEL("Le uuid ou le pseudo de recherche"), // à traduire
    PARAM_LOCAL_SETLOCAL("idioma"),
    PARAM_LOCAL_LABEL("Idioma disponible: ['FR, EN, ES']"),
    PARAM_MEMBER("miembro"),
    PARAM_MEMBER_LABEL("Miembro del gremio"),
    //DESC
    DESC_SERVER("Afficher les informations du serveur `Minecraft®`."), // à traduire
    DESC_REGISTR("S'enregister sur le serveur `Minecraft®`."), // à traduire
    DESC_LOOKUP("Trouver des infos de joueurs Minecraft® par uuid ou pseudo."), // à traduire
    DESC_SETLOCAL("Changer la langue d'affichage.\n FR: francais, EN: english, ES: española."), // à traduire
    DESC_FETCHDB_USERS("Recuperar miembro registrado DB."),
    DESC_REMOVEDB_USERS("Elimina todos los datos de un miembro y sácalo de Discord®."),

    //ACL
    TEXTONLY_CMD("❌ Cette commande est disponible seulement dans les cannaux textuels."), // à traduire
    GUILDONLY_CMD("❌ Cette commande est disponible seulement dans les cannaux textuel de guild utilisant ce plugin."), // à traduire
    USERONLY_CMD("❌ Cette commande est réservée aux utilisateurs enregistrés par Discord®."), // à traduire
    ROLE_NOT_ALLOWED("🔒 Lástima que no tienes los roles necesarios... 🔒"),

    //MISC
    SERVER_IS_UP("Le serveur est up and running boyyssss!"), // à traduire

    ;

    final String trans;

    Es(String trans) {
        this.trans = trans;
    }

}