package locals;

public class En extends DLanguageSet {

    public En() {
        this.DEFAULTS.put("YES", "Yes");
        this.DEFAULTS.put("NO", "No");
        this.DEFAULTS.put("RAINY", "rainy");
        this.DEFAULTS.put("SUNNY", "sunny");
        this.DEFAULTS.put("STORMY", "stormy");
        this.DEFAULTS.put("DAY", "Day");
        this.DEFAULTS.put("NIGHT", "Night");
        this.DEFAULTS.put("NAME", "name");
        this.DEFAULTS.put("VERSION", "Version");
        this.DEFAULTS.put("ISACTIVE", "is active");
        this.DEFAULTS.put("ISINACTIVE", "is inactive");
        this.DEFAULTS.put("DOREGISTER", "Register on the Discord® server");
        this.DEFAULTS.put("MINECRAFT_ALREADYCONFIRMED", "This account is already confirmed...");
        this.DEFAULTS.put("MINECRAFT_CONFIRMATIONONITSWAY",
                "Check your Discord® messages to confirm your registration...");
        this.DEFAULTS.put("ACCOUNTSINFOS", "Accounts information");
        this.DEFAULTS.put("INFORMATION", "Information");
        this.DEFAULTS.put("SERVER", "Server");
        this.DEFAULTS.put("WORLDS", "Worlds");
        this.DEFAULTS.put("DEVS", "Developers");
        this.DEFAULTS.put("PORT", "Port");
        this.DEFAULTS.put("ONLINE_MODE", "Online mode");
        this.DEFAULTS.put("WHITELISTED", "Whitelisted");
        this.DEFAULTS.put("DEFAULT_GAMEMOD", "Default gamemode");
        this.DEFAULTS.put("DESCRIPTION", "Description");
        this.DEFAULTS.put("CONNECTED_USER", "Connected user");
        this.DEFAULTS.put("CONNECTED_USERS", "Connected users");
        this.DEFAULTS.put("SERVER_ACTIVITIES", "🌿 Server activities");
        this.DEFAULTS.put("TIME_METEO", "Meteo and time");
        this.DEFAULTS.put("SOME_EXAMPLES", "Here's some examples");
        this.DEFAULTS.put("BTN_ACCEPT", "✔️ Accept");
        this.DEFAULTS.put("BTN_REFUSE", "❌ Refuse");
        this.DEFAULTS.put("LANG_CHANGED", "Your language was changed");
        this.DEFAULTS.put("LANG_CURRENT", "You current language is");
        this.DEFAULTS.put("TITLE_ACCOUNT_CONFIRM", "Your accounts confirmation");
        this.DEFAULTS.put("EMBD_LINK_DESC", "Please confirm this relation request");
        this.DEFAULTS.put("EMBD_LINK_YESME", "Yes it\'s me");
        this.DEFAULTS.put("EMBD_LINK_NOTME", "No it\'s not me");
        this.DEFAULTS.put("EMBD_LINK_POLICY",
                "When clicking on `YES` you assert that those accounts are to be linked, and that you are the legitimate holder.\nWhen clicking on `NO` the temporary links will be dissolved, and all current or further activities will be suspended.");
        this.DEFAULTS.put("LABEL_LONG_MC", "Minecraft® identification");
        this.DEFAULTS.put("LABEL_MINECRAFT_UUID", "Minecraft® uuid");
        this.DEFAULTS.put("LABEL_LONG_DC", "Discord® identification");
        this.DEFAULTS.put("LABEL_DISCORD_ID", "Discord® id");
        this.DEFAULTS.put("LABEL_DISCORD_TAG", "Discord® tag");
        this.DEFAULTS.put("LABEL_LONG_BEDROCK", "Bedrock® identification");
        this.DEFAULTS.put("LABEL_BEDROCK_ID", "Bedrock® id");
        this.DEFAULTS.put("LABEL_LONG_JAVA", "Java identification");
        this.DEFAULTS.put("LABEL_JAVA_ID", "Java® id");
        this.DEFAULTS.put("LABEL_USECMD", "Use the command");
        this.DEFAULTS.put("PLUGIN_HELLO", "**The plugin `%s` %s**\n\n");
        this.DEFAULTS.put("PLUGIN_HELLO_ERROR",
                "❌ **`ERROR:` The plugin `%s` as encountered errors at initialization**\n");
        this.DEFAULTS.put("PLUGIN_GOODBYE", "**The plugin `%s` %s**\n\n");
        this.DEFAULTS.put("PLUGIN_NAME", "Name: `%s`");
        this.DEFAULTS.put("PLUGIN_VERSION", "Version: `%s`");
        this.DEFAULTS.put("PLUGIN_DEVBY", "Developed by: %s");
        this.DEFAULTS.put("INFO_LEGITIMATE", "If this request seems illegitimate to you, contact an administrator!!!");
        this.DEFAULTS.put("INFO_TRYREGISTERAGAIN", "Try to do a registration request on Discord®.");
        this.DEFAULTS.put("INFO_CHECK_YOUR_MSG", "See more details in your private messages.");
        this.DEFAULTS.put("INFO_CONTACT_ADMIN_MORE_INFO", "To get more information, contact an admin directly...");
        this.DEFAULTS.put("INFO_ALREADY_ACCEPTED_CONNECT",
                "**Your `%s` account is already registered...**\nYou can simply join it now. `Enjoy` ⛏🧱");
        this.DEFAULTS.put("INFO_MUST_CONFIRM_ACCOUNT", "**A confirmation for your `%s` account is needed.**\n");
        this.DEFAULTS.put("INFO_TIME_TO_CONFIRM_SINCE",
                "To confirm your account, you had `%sh` since the approbation, and connect to the Mincecraft® server\n");
        this.DEFAULTS.put("INFO_ACCES_REQUESTED", "**Your `%s` access request for `%s` was sent to moderators.**");
        this.DEFAULTS.put("INFO_PLZ_AWAIT", "**Thank you for waiting until the approval by one of them.**");
        this.DEFAULTS.put("INFO_REGISTER_REQUEST", "A `%s` player want to register on your `Minecraft®` server");
        this.DEFAULTS.put("INFO_ACCEPTED_BY", "✔️ Accepted by: %s");
        this.DEFAULTS.put("INFO_REJECTED_BY", "❌ Rejected by: %s");
        this.DEFAULTS.put("INFO_ACCEPTED_REQUEST", "Request accepted");
        this.DEFAULTS.put("INFO_REJECTED_REQUEST", "Request rejected");
        this.DEFAULTS.put("INFO_WELCOME_USER", "**We are glad to welcome, <@%s> :: `%s`.Enjoy  ⛏🧱 !!!**");
        this.DEFAULTS.put("INFO_TIME_TO_CONFIRM",
                "**You have `%sh` to connect to the `Minecraft®` server, and `confirm` your account.**");
        this.DEFAULTS.put("INFO_USER_WAS_ACCEPTED",
                "✔️ **The player <@%s> was gently accepted for the pseudo: `%s`.**");
        this.DEFAULTS.put("INFO_USER_WAS_REJECTED",
                "❌ **The player <@%s> was strongly refused for the pseudo: `%s`.**");
        this.DEFAULTS.put("INFO_REJECTED_USER", "**❌ Your registration on the server was rejected.**");
        this.DEFAULTS.put("WARN_REGISTRATIONDELAY",
                "⚠️ You're already registered, but the delay to confirm this account is overdue...");
        this.DEFAULTS.put("WARN_ALREADTY_REGISTERED",
                "⚠️ **This `%s` pseudo is already registered to another player**");
        this.DEFAULTS.put("WARN_NOT_ACCEPTED_YET",
                "⚠️ **This `%s` account was not yet accepted on the server by admins.**");
        this.DEFAULTS.put("WARN_BAD_PSEUDO_FORMAT_EXPLAIN",
                "⚠️ Your `%s` pseudo: `%s`, should contains between `3` and `16` characters.\nIt should not contain specials characters except underscores `_` or dashes `-`.");
        this.DEFAULTS.put("ERROR", "❌ ERROR");
        this.DEFAULTS.put("CONTACT_ADMNIN", "Contact an administrator...");
        this.DEFAULTS.put("CHECK_LOGS", "**Check your `log` files!!!!**");
        this.DEFAULTS.put("NOTREGISTERED", "Your registration could not be fetched...");
        this.DEFAULTS.put("CMD_ERROR", "❌ Sorry... an error as occurred during this request!!!");
        this.DEFAULTS.put("LOOKUP_ERROR", "This lookup value is not valid...");
        this.DEFAULTS.put("LOOKUP_PARAM_ERROR", "You must choose a valid lookup type");
        this.DEFAULTS.put("REGISTER_CMD_PARAM_ERROR", "You must provide at least one pseudo for this command...");
        this.DEFAULTS.put("REGISTER_CMD_FORMAT_ERROR", "❌ Your `identifications` contains format `errors`.");
        this.DEFAULTS.put("REGISTER_CMD_NOT_FOUND_UUID", "❌ **Your `%s` UUID could not be found on the servers...**");
        this.DEFAULTS.put("REGISTER_CMD_ERROR",
                "❌ **Sorry, the registration for your `%s` account, could not be completed.**");
        this.DEFAULTS.put("CMD_LINK", "wje-link");
        this.DEFAULTS.put("CMD_SERVER", "server");
        this.DEFAULTS.put("CMD_REGISTER", "register");
        this.DEFAULTS.put("CMD_LOOKUP", "search");
        this.DEFAULTS.put("CMD_SETLOCAL", "language");
        this.DEFAULTS.put("CMD_FETCHDB_USERS", "members");
        this.DEFAULTS.put("CMD_REMOVEDB_USERS", "remove");
        this.DEFAULTS.put("PARAM_PJAVA", "java-pseudo");
        this.DEFAULTS.put("PARAM_PBEDR", "bedrock-pseudo");
        this.DEFAULTS.put("PARAM_REGISTR_LABELJ", "Your Java pseudo");
        this.DEFAULTS.put("PARAM_REGISTR_LABELB", "Your Bedrock pseudo");
        this.DEFAULTS.put("PARAM_LOOKUP_LABEL", "The uuid or pseudo to search for");
        this.DEFAULTS.put("PARAM_LOCAL_SETLOCAL", "language");
        this.DEFAULTS.put("PARAM_LOCAL_LABEL", "Available language: ['FR, EN, ES']");
        this.DEFAULTS.put("PARAM_MEMBER", "member");
        this.DEFAULTS.put("PARAM_MEMBER_LABEL", "A guild member");
        this.DEFAULTS.put("PARAM_UUID", "uuid");
        this.DEFAULTS.put("PARAM_UUID_LABEL", "A Minecraft uuid");
        this.DEFAULTS.put("DESC_SERVER", "Show the `Minecraft®` server information.");
        this.DEFAULTS.put("DESC_REGISTR", "Register on the `Minecraft®` server.");
        this.DEFAULTS.put("DESC_LOOKUP", "Find `Minecraft®` players infos by uuid or pseudo.");
        this.DEFAULTS.put("DESC_SETLOCAL", "Change the display language.\n FR: francais, EN: english, ES: española.");
        this.DEFAULTS.put("DESC_FETCHDB_USERS", "Get info on a member.");
        this.DEFAULTS.put("DESC_REMOVEDB_USERS", "Delete all of a member's data and kick them from Discord®.");
        this.DEFAULTS.put("TEXTONLY_CMD", "❌ This command is available only through textual channels.");
        this.DEFAULTS.put("GUILDONLY_CMD",
                "❌ This command is available only through textual guild channels using this.");
        this.DEFAULTS.put("USERONLY_CMD", "❌ This command id reserved for Discord® registered users only.");
        this.DEFAULTS.put("ROLE_NOT_ALLOWED", "🔒 Too bad you don't have the necessary roles... 🔒");
        this.DEFAULTS.put("SERVER_IS_UP", "The server is up and running boys!");

    }
}
