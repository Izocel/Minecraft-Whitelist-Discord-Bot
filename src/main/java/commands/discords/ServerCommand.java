package commands.discords;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.World;

import configs.ConfigManager;
import helpers.Helper;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import net.dv8tion.jda.api.JDA;

public class ServerCommand extends BaseCmd {
    private final static String KEY_CMD_NAME = "CMD_SERVER";
    private final static String KEY_CMD_DESC = "DESC_SERVER";

    public static void REGISTER_CMD(JDA jda, WhitelistDmc plugin) {
        String cmdName = LOCAL.translate(KEY_CMD_NAME);
        String cmdDesc = LOCAL.translate(KEY_CMD_DESC);

        jda.addEventListener(new ServerCommand(plugin));
        jda.upsertCommand(cmdName, cmdDesc)
                .submit(true);
    }

    private ConfigManager configs;

    public ServerCommand(WhitelistDmc plugin) {
        super(plugin,
                "ServerCommand",
                "CMD_SERVER",
                "ServerInfosCommand",
                "GET MC® server infos");
        this.configs = this.plugin.getConfigManager();
    }

    @Override
    protected final void execute() {

        if (this.member == null) {
            final String reply = useTranslator("GUILDONLY_CMD");
            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "guild reserved");
            tx.finish(SpanStatus.UNAVAILABLE);
            return;
        }

        final Integer msgDelaySec = 120;
        final String guildName = event.getGuild().getName();

        final String informationField = useTranslator("INFORMATION");
        final String serverField = useTranslator("SERVER");
        final String worldsField = useTranslator("WORLDS");
        final String devSField = useTranslator("DEVS");

        final String title = "** 📝`" + guildName + "` | " + informationField + " ** ";

        event.reply(title + this.plugin.getBukkitManager().getServerInfoString(this.cmdLang) +
                "\n\n**" + serverField + ": ** \n\t" + getPlayersOnline() +
                "\n\n**" + worldsField + ": **" + getWorldsInfos() +
                "\n**" + devSField + "**: <@272924120142970892> 👨‍💻"

        ).setEphemeral(false).queue((message) -> message.deleteOriginal().queueAfter(msgDelaySec, TimeUnit.SECONDS));

        tx.setData("state", "infos delivered");
        tx.finish(SpanStatus.OK);
    }

    public String getWorldsInfos() {
        List<World> worlds = Bukkit.getWorlds();
        StringBuilder sb = new StringBuilder();
        
        final String stormyField = useTranslator("STORMY");
        final String rainyField = useTranslator("RAINY");
        final String WORD_YES = useTranslator("WORD_YES");
        final String WORD_NO = useTranslator("WORD_NO");

        for (World world : worlds) {
            final String name = world.getName();

            long gameTime = world.getTime(),
                    hours = gameTime / 1000 + 6,
                    minutes = (gameTime % 1000) * 60 / 1000;

            String weather = Helper.capitalize(rainyField) + ": `"  + (world.hasStorm() ? WORD_YES : WORD_NO) + "`\n\t" +
                    Helper.capitalize(stormyField) + ": `" + (world.isThundering() ? WORD_YES : WORD_NO) + "`",
                    isDay = hours <= 17 ? useTranslator("DAY") : useTranslator("NIGHT"), emotes;

            emotes = hours <= 17
                ? "☀️ " 
                : "🌒 ";

            if(world.hasStorm() && world.isThundering())
                emotes += "⛈️";
            else if (world.hasStorm()) 
                emotes += "🌧️";
            else if (world.isThundering()) 
                emotes += "🌩️";

            if (hours >= 24) 
                hours -= 24;

            sb.append("\n\t" + emotes + " " + useTranslator("TIME_METEO") + ": **" + name + "**\n\t`" 
                + (hours <= 9 ? "0" + hours : hours)
                + ":"
                + (minutes <= 9 ? "0" + minutes : minutes) + " (" + isDay + ")`\n\t" + weather + "\n\n\t");

            if (!this.configs.get("showAllWorldsMeteo", "false").equals("true")) {
                break;
            }
        }

        return sb.toString();
    }

    public String getPlayersOnline() {
        final int nbPlayers = Bukkit.getOnlinePlayers().size();
        final String title = useTranslator("SERVER_ACTIVITIES");
        return "**" + title + "**\n\t`" +
            (nbPlayers > 1 
                ? nbPlayers + " " + useTranslator("CONNECTED_USERS")
                : nbPlayers + " " + useTranslator("CONNECTED_USER")
            )
            + "`\n\t" +
            (nbPlayers != 0
                ? Bukkit.getOnlinePlayers().toString().replace("CraftPlayer{name=", "")
                    .replace("}", "") .replace("]", "") .replace("{", "")
                    .replace("_", "⎽") .replace("[", "")
                : ""
            );
    }

}
