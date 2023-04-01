package locals;

public enum Es {
    //GLOBAL
    YES("Si"),
    NO("No"),
    RAINY("Lluvioso"),
    SUNNY("Soleado"),
    STORMY("Tormentoso"),
    DAY("Día"),
    NIGHT("Noche"),
    NAME("Nombre"),
    VERSION("Versión"), 
    ISACTIVE("Está activo"), 
    ISINACTIVE("Está inactivo"), 
    DOREGISTER("Regístrate en el servidor Discord®"),
    MINECRAFT_ALREADYREGISTERED("Esta cuenta ya está confirmada"),
    ACCOUNTSINFOS("Información de cuentas"), 
    INFORMATION("Informacións"),
    SERVER("Servidor"), 
    WORLDS("Mundos"),
    DEVS("Devs"),
    PORT("Port"),
    ONLINE_MODE("Modo en línea"),
    WHITELISTED("Whitelisted"),
    DEFAULT_GAMEMOD("Modo de juego"),
    DESCRIPTION("Descripción"),
    CONNECTED_USER("Usuario conectado"),
    CONNECTED_USERS("Usuarios conectados"),
    SERVER_ACTIVITIES("🌿 Actividades del servidor"),
    TIME_METEO("Clima y tiempo"),
    SOME_EXAMPLES("Aquí hay ejemplos"),

    //BUTTONS
    BTN_ACCEPT("✔️ Aceptar"),
    BTN_REFUSE("❌ Rechazar"),

    //REPLIES
    LANG_CHANGED("Tu idioma fue cambiado"), // à traduire   Votre langue a été modifié
    LANG_CURRENT("Su idioma es actualmente"), // à traduire   Votre langue est actuellement

    //TITLES
    TITLE_ACCOUNT_CONFIRM("Confirmación de sus cuentas"), // à traduire

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
    PLUGIN_HELLO("**El plugin `%s` %s **\n\n"), 
    PLUGIN_HELLO_ERROR("❌ **`ERROR:` El Plugin `%s` encontró `problemas` al inicializar**\n"),
    PLUGIN_GOODBYE("**El plugin `%s` %s **\n\n"),
    PLUGIN_NAME("Nombre: `%s`"),
    PLUGIN_VERSION("Versión: `%s`"),
    PLUGIN_DEVBY("Développer par: %s"),         // à traduire

    //INFOS
    INFO_LEGITIMATE("Si cette demande vous semble illégitime, contactez un administrateur!!!"), // à traduire
    INFO_TRYREGISTERAGAIN("Intente volver a solicitar el registro en Discord®."),
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
    WARN_REGISTRATIONDELAY("⚠️ Estás registrado, pero ya pasó el tiempo límite para confirmar esta cuenta..."), // à traduire
    WARN_ALREADTY_REGISTERED("⚠️ **Ce pseudo `%s` est déjà enregistrer par un autre joueur**"), // à traduire
    WARN_NOT_ACCEPTED_YET("⚠️ **Ce compte `%s` n'a pas encore été accepté sur le serveur.**"), // à traduire
    WARN_BAD_PSEUDO_FORMAT_EXPLAIN("⚠️ Votre pseudo `%s`: `%s`, devrait comporter entre `3` et `16` caractères.\nIl ne doit pas comporter de caractères spéciaux à part des underscores `_` ou tirets `-`."), // à traduire

    //ERRORS
    ERROR("❌ ERROR"),    // a traduire?
    CONTACT_ADMNIN("Contactar con un administrador..."),
    CHECK_LOGS("**Mira los archivos de `log`!!!!**"),
    NOTREGISTERED("No se pudo encontrar su registro..."),
    CMD_ERROR("❌ Lo sentimos... ¡Ocurrió un error durante esta solicitud!"),
    LOOKUP_ERROR("Este valor de búsqueda no es válido..."),
    LOOKUP_PARAM_ERROR("Debe elegir un tipo de búsqueda válido"),
    REGISTER_CMD_PARAM_ERROR("Vous devez fournir au moins un pseudo pour utiliser cette commande..."),       // à traduire
    REGISTER_CMD_FORMAT_ERROR("❌ Vos `identifiants` comportaient des `erreurs` de format."),       // à traduire
    REGISTER_CMD_NOT_FOUND_UUID("❌ **Votre UUID `%s` n'a pas pu être retrouvés sur les serveurs...**"),       // à traduire
    REGISTER_CMD_ERROR("❌ **Désoler, l'enregistrement pour votre pseudo `%s` ne c'est pas bien passé.**"),       // à traduire

    //MINECRAFT_CMD
    CMD_LINK("wje-link"),

    //DISCORD CMD
    CMD_SERVER("serveur"),       // à traduire
    CMD_REGISTER("enregistrer"),       // à traduire
    CMD_LOOKUP("recherche"),       // à traduire
    CMD_SETLOCAL("langue"),       // à traduire
    CMD_FETCHDB_USERS("membres"),       // à traduire
    CMD_REMOVEDB_USERS("retirer"),       // à traduire
    //PARAMS
    PARAM_PJAVA("java"),
    PARAM_PBEDR("bedrock"),
    PARAM_REGISTR_LABELJ("Su pseudo Java"),
    PARAM_REGISTR_LABELB("Su pseudo Bedrock"),
    PARAM_LOOKUP_LABEL("El uuid o el pseudo de búsqueda"),
    PARAM_LOCAL_SETLOCAL("idioma"),
    PARAM_LOCAL_LABEL("Language disponible: ['FR, EN, ES']"),
    PARAM_MEMBER("membre"),    // à traduire svp
    PARAM_MEMBER_LABEL("Un membre de la guild"),    // à traduire svp
    //DESC
    DESC_SERVER("Mostrar la información del servidor `Minecraft®`."),
    DESC_REGISTR("Regístrarse en el servidor `Minecraft®`."),
    DESC_LOOKUP("Encontrar información de jugador de Minecraft® por uuid o pseudo."),
    DESC_SETLOCAL("Cambiar el idioma de visualización.\n FR: Francés, EN: Inglés, ES: Español."),
    DESC_FETCHDB_USERS("Récuperer les infos sur un membre."),   // à traduire svp
    DESC_REMOVEDB_USERS("Supprimer toutes les données d'un membre et expulsez-le de Discord®."),    // à traduire svp

    //ACL
    TEXTONLY_CMD("❌ Esta orden solo está disponible en los canales de texto."),
    GUILDONLY_CMD("❌ Esta orden solo está disponible en los canales de texto de los gremios que usan este plugin."),
    USERONLY_CMD("❌ Esta orden está reservada para usuarios registrados por Discord®."),
    ROLE_NOT_ALLOWED("🔒 Dommage vous n'avez pas les roles nécéssaires... 🔒"),    // à traduire svp

    //MISC
    SERVER_IS_UP("El servidor está sirviendo!"),

    ;

    final String trans;

    Es(String trans) {
        this.trans = trans;
    }

}