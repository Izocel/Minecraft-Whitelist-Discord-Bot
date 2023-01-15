package commands.discords;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.World;

import configs.ConfigManager;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import locals.Lang;
import locals.LocalManager;
import main.WhitelistJe;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerCommand extends ListenerAdapter {
    private WhitelistJe plugin;
    private ConfigManager configs;

    public ServerCommand(WhitelistJe plugin) {
        this.plugin = plugin;
        this.configs = this.plugin.getConfigManager();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        final LocalManager LOCAL = WhitelistJe.LOCALES;

        LOCAL.setNextLang(Lang.FR.value);
        final String frCmdName = LOCAL.translate("CMD_SERVER");
        LOCAL.setNextLang(Lang.EN.value);
        final String enCmdName = LOCAL.translate("CMD_SERVER");

        if (!event.getName().equals(frCmdName) && !event.getName().equals(enCmdName))
            return;

        ITransaction tx = Sentry.startTransaction("ServerInfosCommand", "get MC® server infos");

        try {
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

        } catch (Exception e) {
            tx.setThrowable(e);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
        }
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

            sb.append("\n\t" + emotes + " Météo et temps: **" + name + "**\n\t`" + (hours <= 9 ? "0" + hours : hours) + ":"
                    + (minutes <= 9 ? "0" + minutes : minutes) + " (" + isDay + ")`\n\t" + weather + "\n\n\t");

            if(!configs.get("showSubWorlddMeteo", "false").equals("true")) {
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
