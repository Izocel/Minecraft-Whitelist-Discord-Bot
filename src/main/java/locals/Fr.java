package locals;

public class Fr extends DLanguageSet {

    public Fr() {
        super();
        this.DEFAULTS.put("YES", "Oui");
        this.DEFAULTS.put("NO", "Non");
        this.DEFAULTS.put("RAINY", "pluvieux");
        this.DEFAULTS.put("SUNNY", "ensoleillé");
        this.DEFAULTS.put("STORMY", "orageux");
        this.DEFAULTS.put("DAY", "Jour");
        this.DEFAULTS.put("NIGHT", "Nuit");
        this.DEFAULTS.put("NAME", "nom");
        this.DEFAULTS.put("VERSION", "Version");
        this.DEFAULTS.put("ISACTIVE", "est actif");
        this.DEFAULTS.put("ISINACTIVE", "est inactif");
        this.DEFAULTS.put("DOREGISTER", "Enregistrer vous sur le serveur Discord®");
        this.DEFAULTS.put("MINECRAFT_CONFIRMATIONONITSWAY",
                "Vérifiez vos messages Discord® pour confirmer votre inscription...");
        this.DEFAULTS.put("ACCOUNTSINFOS", "Informations de comptes");
        this.DEFAULTS.put("INFORMATION", "Informations");
        this.DEFAULTS.put("SERVER", "Serveur");
        this.DEFAULTS.put("WORLDS", "Mondes");
        this.DEFAULTS.put("DEVS", "Développeurs");
        this.DEFAULTS.put("PORT", "Port");
        this.DEFAULTS.put("ONLINE_MODE", "Mode en ligne");
        this.DEFAULTS.put("WHITELISTED", "Whitelisted");
        this.DEFAULTS.put("DEFAULT_GAMEMOD", "Mode de jeu default");
        this.DEFAULTS.put("DESCRIPTION", "Description");
        this.DEFAULTS.put("CONNECTED_USER", "Joueur connecté");
        this.DEFAULTS.put("CONNECTED_USERS", "Joueurs connectés");
        this.DEFAULTS.put("SERVER_ACTIVITIES", "🌿 Activités du serveur");
        this.DEFAULTS.put("TIME_METEO", "Météo et temps");
        this.DEFAULTS.put("SOME_EXAMPLES", "Voici des examples");
        this.DEFAULTS.put("BTN_ACCEPT", "✔️ Accepter");
        this.DEFAULTS.put("BTN_REFUSE", "❌ Refuser");
        this.DEFAULTS.put("LANG_CHANGED", "Votre langue a été modifié");
        this.DEFAULTS.put("LANG_CURRENT", "Votre langue est actuellement");
        this.DEFAULTS.put("TITLE_ACCOUNT_CONFIRM", "Confirmation de vos comptes");
        this.DEFAULTS.put("EMBD_LINK_DESC", "Veuillez confirmer la demande de relation");
        this.DEFAULTS.put("EMBD_LINK_YESME", "Oui, c\'est bien moi");
        this.DEFAULTS.put("EMBD_LINK_NOTME", "Non, ce n\'est pas moi");
        this.DEFAULTS.put("EMBD_LINK_POLICY",
                "En cliquant sur `OUI` vous confirmez que ces comptes seront reliés et que vous en êtes le détenteur.\nEn cliquant sur `NON` les liens temporaires seront détruits et toutes activitées courrantes et futures seront suspendues.");
        this.DEFAULTS.put("LABEL_LONG_MC", "Identifiant Minecraft®");
        this.DEFAULTS.put("LABEL_MINECRAFT_UUID", "Minecraft® uuid");
        this.DEFAULTS.put("LABEL_LONG_DC", "Identifiant Discord®");
        this.DEFAULTS.put("LABEL_DISCORD_ID", "Discord® id");
        this.DEFAULTS.put("LABEL_DISCORD_TAG", "Discord® tag");
        this.DEFAULTS.put("LABEL_LONG_BEDROCK", "Identifiant Bedrock®");
        this.DEFAULTS.put("LABEL_BEDROCK_ID", "Bedrock® id");
        this.DEFAULTS.put("LABEL_LONG_JAVA", "Identifiant Java");
        this.DEFAULTS.put("LABEL_JAVA_ID", "Java® id");
        this.DEFAULTS.put("LABEL_USECMD", "Utilisez la commande");
        this.DEFAULTS.put("PLUGIN_HELLO", "**Le plugin `%s` %s **\n\n");
        this.DEFAULTS.put("PLUGIN_HELLO_ERROR",
                "❌ **`ERREUR:` Le plugin `%s` a rencontré des `problèmes` à l'initialisation**\n");
        this.DEFAULTS.put("PLUGIN_GOODBYE", "**Le plugin `%s` %s **\n\n");
        this.DEFAULTS.put("PLUGIN_NAME", "Nom: `%s`");
        this.DEFAULTS.put("PLUGIN_VERSION", "Version: `%s`");
        this.DEFAULTS.put("PLUGIN_DEVBY", "Développer par: %s");
        this.DEFAULTS.put("INFO_LEGITIMATE", "Si cette demande vous semble illégitime, contactez un administrateur!!!");
        this.DEFAULTS.put("INFO_TRYREGISTERAGAIN", "Essayez de refaire une demande d'enregistrment sur Discord®.");
        this.DEFAULTS.put("INFO_CHECK_YOUR_MSG", "Voir les détails dans vos messages privés.");
        this.DEFAULTS.put("INFO_CONTACT_ADMIN_MORE_INFO",
                "Pour en s'avoir d'avantage, contactez un administrateur directement...");
        this.DEFAULTS.put("INFO_ALREADY_ACCEPTED_CONNECT",
                "**Votre compte `%s` est déjà accepté sur le serveur...**\nIl suffit de vous connecter. `Enjoy` ⛏🧱");
        this.DEFAULTS.put("INFO_MUST_CONFIRM_ACCOUNT", "**Une confirmation de votre compte `%s` est nécéssaire.**\n");
        this.DEFAULTS.put("INFO_TIME_TO_CONFIRM_SINCE",
                "Pour confimer votre compte vous aviez `%sh` depuis l'aprobation pour vous connecter au server Mincecraft®\n");
        this.DEFAULTS.put("INFO_ACCES_REQUESTED",
                "**Votre demande d'accès `%s` pour `%s` a été envoyé aux modérateurs.**");
        this.DEFAULTS.put("INFO_PLZ_AWAIT", "**Merci de patienter jusqu'à une prise de décision de leur part.**");
        this.DEFAULTS.put("INFO_REGISTER_REQUEST", "Un joueur `%s` veut s'enregister sur votre serveur `Minecraft®`");
        this.DEFAULTS.put("INFO_ACCEPTED_BY", "✔️ Accepté par: %s");
        this.DEFAULTS.put("INFO_REJECTED_BY", "❌ Refusé par: %s");
        this.DEFAULTS.put("INFO_ACCEPTED_REQUEST", "Demande acceptée");
        this.DEFAULTS.put("INFO_REJECTED_REQUEST", "Demande refusée");
        this.DEFAULTS.put("INFO_WELCOME_USER", "**Nous te souhaitons bienvenue, <@%s> :: `%s`.Enjoy  ⛏🧱 !!!**");
        this.DEFAULTS.put("INFO_TIME_TO_CONFIRM",
                "**Vous avez `%sh` pour vous connecter au serveur `Minecraft®` et ainsi `confirmer` votre compte.**");
        this.DEFAULTS.put("INFO_USER_WAS_ACCEPTED", "✔️ **Le joueur <@%s> a bien été approuvé pour le pseudo: `%s`.**");
        this.DEFAULTS.put("INFO_USER_WAS_REJECTED", "❌ **Le joueur <@%s> a bien été refusé pour le pseudo: `%s`.**");
        this.DEFAULTS.put("INFO_REJECTED_USER", "**❌ Votre enregistrement sur le serveur a été refusé.**");
        this.DEFAULTS.put("WARN_REGISTRATIONDELAY",
                "⚠️ Vous êtes bien enregistré, mais le délai pour confirmer ce compte est dépassé...");
        this.DEFAULTS.put("WARN_ALREADTY_REGISTERED", "⚠️ **Ce pseudo `%s` est déjà enregistrer par un autre joueur**");
        this.DEFAULTS.put("WARN_NOT_ACCEPTED_YET", "⚠️ **Ce compte `%s` n'a pas encore été accepté sur le serveur.**");
        this.DEFAULTS.put("WARN_BAD_PSEUDO_FORMAT_EXPLAIN",
                "⚠️ Votre pseudo `%s`: `%s`, devrait comporter entre `3` et `16` caractères.\nIl ne doit pas comporter de caractères spéciaux à part des underscores `_` ou tirets `-`.");
        this.DEFAULTS.put("ERROR", "❌ ERREUR");
        this.DEFAULTS.put("CONTACT_ADMNIN", "Contactez un admin...");
        this.DEFAULTS.put("CHECK_LOGS", "**Regarder les fichers de `log`!!!!**");
        this.DEFAULTS.put("NOTREGISTERED", "Votre enregistrement n'a pas pu être retrouvé...");
        this.DEFAULTS.put("CMD_ERROR", "❌ Désoler... une erreur est survenu lors de cette demande!!!");
        this.DEFAULTS.put("LOOKUP_ERROR", "Cette valeur de recherche n'est pas valide...");
        this.DEFAULTS.put("LOOKUP_PARAM_ERROR", "Vous devez choisir un type de rcherche valide");
        this.DEFAULTS.put("REGISTER_CMD_PARAM_ERROR",
                "Vous devez fournir au moins un pseudo pour utiliser cette commande...");
        this.DEFAULTS.put("REGISTER_CMD_FORMAT_ERROR", "❌ Vos `identifiants` comportaient des `erreurs` de format.");
        this.DEFAULTS.put("REGISTER_CMD_NOT_FOUND_UUID",
                "❌ **Votre UUID `%s` n'a pas pu être retrouvés sur les serveurs...**");
        this.DEFAULTS.put("REGISTER_CMD_ERROR",
                "❌ **Désoler, l'enregistrement pour votre pseudo `%s` ne c'est pas bien passé.**");
        this.DEFAULTS.put("CMD_LINK", "wje-link");
        this.DEFAULTS.put("CMD_SERVER", "serveur");
        this.DEFAULTS.put("CMD_REGISTER", "enregistrer");
        this.DEFAULTS.put("CMD_LOOKUP", "recherche");
        this.DEFAULTS.put("CMD_SETLOCAL", "langue");
        this.DEFAULTS.put("CMD_FETCHDB_USERS", "membres");
        this.DEFAULTS.put("CMD_REMOVEDB_USERS", "retirer");
        this.DEFAULTS.put("PARAM_PJAVA", "pseudo-java");
        this.DEFAULTS.put("PARAM_PBEDR", "pseudo-bedrock");
        this.DEFAULTS.put("PARAM_REGISTR_LABELJ", "Votre pseudo Java");
        this.DEFAULTS.put("PARAM_REGISTR_LABELB", "Votre pseudo Bedrock");
        this.DEFAULTS.put("PARAM_LOOKUP_LABEL", "Le uuid ou le pseudo de recherche");
        this.DEFAULTS.put("PARAM_LOCAL_SETLOCAL", "langue");
        this.DEFAULTS.put("PARAM_LOCAL_LABEL", "Language disponible: ['FR, EN, ES']");
        this.DEFAULTS.put("PARAM_MEMBER", "membre");
        this.DEFAULTS.put("PARAM_MEMBER_LABEL", "Un membre de la guild");
        this.DEFAULTS.put("PARAM_UUID", "uuid");
        this.DEFAULTS.put("PARAM_UUID_LABEL", "Un uuid minecraft");
        this.DEFAULTS.put("DESC_SERVER", "Afficher les informations du serveur `Minecraft®`.");
        this.DEFAULTS.put("DESC_REGISTR", "S'enregister sur le serveur `Minecraft®`.");
        this.DEFAULTS.put("DESC_LOOKUP", "Trouver des infos de joueurs Minecraft® par uuid ou pseudo.");
        this.DEFAULTS.put("DESC_SETLOCAL", "Changer la langue d'affichage.\n FR: francais, EN: english, ES: española.");
        this.DEFAULTS.put("DESC_FETCHDB_USERS", "Récuperer les infos sur un membre.");
        this.DEFAULTS.put("DESC_REMOVEDB_USERS",
                "Supprimer toutes les données d'un membre et expulsez-le de Discord®.");
        this.DEFAULTS.put("TEXTONLY_CMD", "❌ Cette commande est disponible seulement dans les cannaux textuels.");
        this.DEFAULTS.put("GUILDONLY_CMD",
                "❌ Cette commande est disponible seulement dans les cannaux textuels de guild utilisant ce plugin.");
        this.DEFAULTS.put("USERONLY_CMD", "❌ Cette commande est réservée aux utilisateurs enregistrés par Discord®.");
        this.DEFAULTS.put("ROLE_NOT_ALLOWED", "🔒 Dommage vous n'avez pas les roles nécéssaires... 🔒");
        this.DEFAULTS.put("SERVER_IS_UP", "Le serveur est up and running boyyssss!");
    }
}
