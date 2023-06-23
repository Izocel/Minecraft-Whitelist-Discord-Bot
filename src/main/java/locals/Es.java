package locals;

public enum Es {
    // GLOBAL
    YES("Si"),
    NO("No"),
    RAINY("lluvioso"),
    SUNNY("soleado"),
    STORMY("tormentoso"),
    DAY("Día"),
    NIGHT("Noche"),
    NAME("nombre"),
    VERSION("Versión"),
    ISACTIVE("está activo"),
    ISINACTIVE("está inactivo"),
    DOREGISTER("regístrate en el servidor Discord®"),
    MINECRAFT_ALREADYCONFIRMED("Esta cuenta ya está confirmada"),
    MINECRAFT_CONFIRMATIONONITSWAY("Revisa tus mensajes de Discord® para confirmar tu suscripción..."),
    ACCOUNTSINFOS("Información de cuentas"),
    INFORMATION("Informacións"),
    SERVER("Servidor"),
    WORLDS("Mundos"),
    DEVS("Desarrolladores"),
    PORT("Port"),
    ONLINE_MODE("Modo en línea"),
    WHITELISTED("ListaBlanca"),
    DEFAULT_GAMEMOD("Modo de juego"),
    DESCRIPTION("Descripción"),
    CONNECTED_USER("Usuario conectado"),
    CONNECTED_USERS("Usuarios conectados"),
    SERVER_ACTIVITIES("🌿 Actividades del servidor"),
    TIME_METEO("Clima y tiempo"),
    SOME_EXAMPLES("Aquí hay ejemplos"),

    // BUTTONS
    BTN_ACCEPT("✔️ Aceptar"),
    BTN_REFUSE("❌ Rechazar"),

    // REPLIES
    LANG_CHANGED("Tu idioma fue cambiado"),
    LANG_CURRENT("Su idioma es actualmente"),

    // TITLES
    TITLE_ACCOUNT_CONFIRM("Confirmación de sus cuentas"),

    // EMBEDS
    EMBD_LINK_DESC("Porfavor confirme la demanda de relación"),
    EMBD_LINK_YESME("Sí, soy yo"),
    EMBD_LINK_NOTME("No, no soy yo"),
    EMBD_LINK_POLICY(
            "Eligiendo `Sí` Usted confirma que estas cuentas estan enlace y que usted es el detentor de ellas.\n" +
                    "Eligiendo `No` los enlaces temporarios seran destruidos y todas las actividades corrientes y futuras seran sospendidas."),

    // LABELS
    LABEL_LONG_MC("Identificación Minecraft®"),
    LABEL_MINECRAFT_UUID("Minecraft® uuid"),
    LABEL_LONG_DC("Identificación Discord®"),
    LABEL_DISCORD_ID("Discord® id"),
    LABEL_DISCORD_TAG("Discord® tag"),
    LABEL_LONG_BEDROCK("Identificación Bedrock®"),
    LABEL_BEDROCK_ID("Bedrock® id"),
    LABEL_LONG_JAVA("Identificación Java"),
    LABEL_JAVA_ID("Java® id"),
    LABEL_USECMD("Usa la comanda"),

    // PLUGIN
    PLUGIN_HELLO("**El plugin `%s` %s **\n\n"),
    PLUGIN_HELLO_ERROR("❌ **`ERROR:` El Plugin `%s` encontró `problemas` al inicializar**\n"),
    PLUGIN_GOODBYE("**El plugin `%s` %s **\n\n"),
    PLUGIN_NAME("Nombre: `%s`"),
    PLUGIN_VERSION("Versión: `%s`"),
    PLUGIN_DEVBY("Desarollado por: %s"),

    // INFOS
    INFO_LEGITIMATE("Si esta demanda le parece ilegitima, contacte un administrador!!!"),
    INFO_TRYREGISTERAGAIN("Intente volver a solicitar el registro en Discord®."),
    INFO_CHECK_YOUR_MSG("Ver detalles en sus mensajes privados."),
    INFO_CONTACT_ADMIN_MORE_INFO("Para saber mas contacte un administrador directamente..."),
    INFO_ALREADY_ACCEPTED_CONNECT("**Su cuenta `%s` ya esta aceptada en el servidor...**\n"
            + "Solo tiene que conectarse usted. `Provecho` ⛏🧱"),
    INFO_MUST_CONFIRM_ACCOUNT("**Una confirmación de su cuenta `%s` es necesaria.**\n"),
    INFO_TIME_TO_CONFIRM_SINCE(
            "Para confirmar su cuenta usted tiene `%sh` de la aprobacion para conectarse al servidor Mincecraft®\n"),
    INFO_ACCES_REQUESTED("**Su demanda de acceso `%s` para `%s` ha sido enviada a los moderadores.**"),
    INFO_PLZ_AWAIT("**Merci de patienter jusqu'à une prise de décision de leur part.**"),
    INFO_REGISTER_REQUEST("Un jugador `%s` quiere registrarse en su servidor `Minecraft®`"),
    INFO_ACCEPTED_BY("✔️ Aceptado por: %s"),
    INFO_REJECTED_BY("❌ Denegado por: %s"),
    INFO_ACCEPTED_REQUEST("Petición aceptada"),
    INFO_REJECTED_REQUEST("Solicitud rechazada"),
    INFO_WELCOME_USER("**Te dicemos bienvenidos, <@%s> :: `%s`. Provecho  ⛏🧱 !!!**"),
    INFO_TIME_TO_CONFIRM("**Tiene `%sh` para conectarse al servidor `Minecraft®` y asi `confirmar` su cuenta.**"),
    INFO_USER_WAS_ACCEPTED("✔️ **El jugador <@%s> ha sido aprobado bien por el apodo: `%s`.**"),
    INFO_USER_WAS_REJECTED("❌ **El jugador <@%s> ha sido rechazado por el apodo: `%s`.**"),
    INFO_REJECTED_USER("**❌ Su registracion en el servidor ha sido rechazada.**"),

    // WARNS
    WARN_REGISTRATIONDELAY("⚠️ Estás registrado, pero ya pasó el tiempo límite para confirmar esta cuenta..."),
    WARN_ALREADTY_REGISTERED("⚠️ **Este apodo `%s` ya fue registrado por otro jugador**"),
    WARN_NOT_ACCEPTED_YET("⚠️ **Esta cuenta `%s` no ha sido aceptada todavia por el servidor**"),
    WARN_BAD_PSEUDO_FORMAT_EXPLAIN(
            "⚠️ Su apodo `%s`: `%s`, debe contener entre `3` y `16` caracteres.\n No debe contener caracteres especiales aparte de las barras abajo `_` o líneas horizontales `-`."), //

    // ERRORS
    ERROR("❌ ERROR"),
    CONTACT_ADMNIN("Contactar con un administrador..."),
    CHECK_LOGS("**Mira los archivos de `log`!!!!**"),
    NOTREGISTERED("No se pudo encontrar su registro..."),
    CMD_ERROR("❌ Lo sentimos... ¡Ocurrió un error durante esta solicitud!"),
    LOOKUP_ERROR("Este valor de búsqueda no es válido..."),
    LOOKUP_PARAM_ERROR("Debe elegir un tipo de búsqueda válido"),
    REGISTER_CMD_PARAM_ERROR("Usted debe incluir al menos un apodo para usar esta comanda..."),
    REGISTER_CMD_FORMAT_ERROR("❌ Sus `identificaciones` tenian `errores` de formato."),
    REGISTER_CMD_NOT_FOUND_UUID("❌ **Su UUID `%s` no ha podido ser encontrado en los servidores...**"),
    REGISTER_CMD_ERROR("❌ **Disculpe, la registracion para su apodo `%s` no se ha podido realizar bien.**"),

    // MINECRAFT_CMD
    CMD_LINK("wje-link"),

    // DISCORD CMD
    CMD_SERVER("servidor"),
    CMD_REGISTER("registrar"),
    CMD_LOOKUP("busqueda"),
    CMD_SETLOCAL("idioma"),
    CMD_FETCHDB_USERS("miembros"),
    CMD_REMOVEDB_USERS("retirar"),
    // PARAMS
    PARAM_PJAVA("java"),
    PARAM_PBEDR("bedrock"),
    PARAM_REGISTR_LABELJ("Su pseudo Java"),
    PARAM_REGISTR_LABELB("Su pseudo Bedrock"),
    PARAM_LOOKUP_LABEL("El uuid o el apodo de búsqueda"),
    PARAM_LOCAL_SETLOCAL("idioma"),
    PARAM_LOCAL_LABEL("Language disponible: ['FR, EN, ES']"),
    PARAM_MEMBER("miembro"),
    PARAM_MEMBER_LABEL("Un miembro del gremio"),
    PARAM_UUID("uuid"),
    PARAM_UUID_LABEL("Un uuid minecraft"), // à traduire

    // DESC
    DESC_SERVER("Mostrar la información del servidor `Minecraft®`."),
    DESC_REGISTR("Regístrarse en el servidor `Minecraft®`."),
    DESC_LOOKUP("Encontrar información de jugador de Minecraft® por uuid o pseudo."),
    DESC_SETLOCAL("Cambiar el idioma de visualización.\n FR: Francés, EN: Inglés, ES: Español."),
    DESC_FETCHDB_USERS("Récuperer les infos sur un membre."), // à traduire svp
    DESC_REMOVEDB_USERS("Eliminar todos los datos de un miembro y expulsarlo de Discord®."),

    // ACL
    TEXTONLY_CMD("❌ Esta orden solo está disponible en los canales de texto."),
    GUILDONLY_CMD("❌ Esta orden solo está disponible en los canales de texto de los gremios que usan este plugin."),
    USERONLY_CMD("❌ Esta orden está reservada para usuarios registrados por Discord®."),
    ROLE_NOT_ALLOWED("🔒 Lastima que no tiene los rollos necesarios... 🔒"),

    // MISC
    SERVER_IS_UP("El servidor sirve!"),

    ;

    final String trans;

    Es(String trans) {
        this.trans = trans;
    }

}