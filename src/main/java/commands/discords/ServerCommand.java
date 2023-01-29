package commands.discords;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.World;

import configs.ConfigManager;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import net.dv8tion.jda.api.JDA;

public class ServerCommand extends BaseCmd {
    private final static String KEY_CMD_NAME = "CMD_REGISTER";
    private final static String KEY_CMD_DESC = "DESC_REGISTR";

    public static void REGISTER_CMD(JDA jda, WhitelistJe plugin) {
        String cmdName = LOCAL.translate(KEY_CMD_NAME);
        String cmdDesc = LOCAL.translate(KEY_CMD_DESC);

        jda.addEventListener(new ServerCommand(plugin));
        jda.upsertCommand(cmdName, cmdDesc)
                .submit(true);
    }

    private ConfigManager configs;

    public ServerCommand(WhitelistJe plugin) {
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
            final String reply = LOCAL.translate("MEMBERONLY_CMD");
            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "guild reserved");
            tx.finish(SpanStatus.UNAVAILABLE);
            return;
        }

        final Integer msgDelaySec = 120;
        final String serverName = event.getGuild().getName();

        event.reply("** 📝`" + serverName + "` | Informations ** " +
                this.plugin.getBukkitManager().getServerInfoString() +
                "\n\n**Serveur: ** \n\t" + getPlayersOnline() +
                "\n\n**Mondes: **" + getWorldsInfos() +
                "\n**Développeurs:** <@272924120142970892> 👨‍💻"

        ).setEphemeral(false).queue((message) -> message.deleteOriginal().queueAfter(msgDelaySec, TimeUnit.SECONDS));

        tx.setData("state", "infos delivered");
        tx.finish(SpanStatus.OK);
    }

    public String getWorldsInfos() {
        List<World> worlds = Bukkit.getWorlds();
        StringBuilder sb = new StringBuilder();

        for (World world : worlds) {
            final String name = world.getName();

            long gameTime = world.getTime(),
                    hours = gameTime / 1000 + 6,
                    minutes = (gameTime % 1000) * 60 / 1000;

            String weather = "`" + (world.hasStorm() ? "Orageux" : "Non orageux") + "`\n\t`"
                    + (world.isThundering() ? "Pluvieux" : "Non pluvieux") + "`",
                    isDay = hours <= 17 ? "Jour" : "Nuit",
                    emotes;

            if (isDay.equals("Jour")) {
                emotes = "☀️";
            } else {
                emotes = "🌙";
            }

            if (hours >= 24) {
                hours -= 24;
            }
            if (world.hasStorm())
                emotes += "🌩";
            if (world.isThundering())
                emotes += "🌧";

            sb.append("\n\t" + emotes + " Météo et temps: **" + name + "**\n\t`" + (hours <= 9 ? "0" + hours : hours)
                    + ":"
                    + (minutes <= 9 ? "0" + minutes : minutes) + " (" + isDay + ")`\n\t" + weather + "\n\n\t");

            if (!configs.get("showSubWorlddMeteo", "false").equals("true")) {
                break;
            }
        }

        return sb.toString();
    }

    public String getPlayersOnline() {
        return "**🌿 Activités du serveur**\n\t`"
                + (Bukkit.getOnlinePlayers().size() <= 1 ? Bukkit.getOnlinePlayers().size() + " joueurs connectés"
                        : Bukkit.getOnlinePlayers().size() + " joueurs connectés")
                + "`\n\t"
                + (Bukkit.getOnlinePlayers().size() != 0
                        ? Bukkit.getOnlinePlayers().toString().replace("CraftPlayer{name=", "")
                                .replace("}", "")
                                .replace("]", "")
                                .replace("{", "")
                                .replace("_", "⎽")
                                .replace("[", "")
                        : "");
    }

}
