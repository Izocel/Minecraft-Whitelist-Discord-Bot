package commands;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;

import configs.ConfigManager;
import main.WhitelistJe;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerCommand extends ListenerAdapter {
    private WhitelistJe main;
    private ConfigManager configs;

    public ServerCommand(WhitelistJe main) {
        this.main = main;
        this.configs = this.main.getConfigManager();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        Logger.getLogger("WJE").info("new /ssrrvvvvvvvvvvvvvvvvvvvvrrrrrrrrrrrrrrr");
        for (int i = 0; i < 50; i++) {
            this.main.updateAllPlayers();
            this.main.updateAllowedPlayers();
        }

        if (!event.getName().equals("serveur"))
            return;

        final Integer msgDelaySec = 120;
        final String serverName = event.getGuild().getName();
        final String protJ = configs.get("portJava");
        final String portB = configs.get("portBedrock");
        final String paperMcIp = configs.get("paperMcIp");

        event.reply("** 📝`" + serverName + "` | Informations**" +
                "\n**Adresse I.P. :** `" + paperMcIp + "`" +
                "\n**Port Java:** `" + protJ + "` " +
                "\n**Port Bedrock:** `" + portB + "` " +
                "\n\n**Serveur:** \n\t" + getPlayersOnline() +
                "\n\n**Mondes:** \n\t" + getWorldsInfos() +
                "\n**Développeurs:** [<@272924120142970892>] 👨‍💻"

        ).setEphemeral(false).queue((message) -> message.deleteOriginal().queueAfter(msgDelaySec, TimeUnit.SECONDS));
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

            sb.append("***" + name + ": ***\n\t" + emotes + " Météo et temps\n\t`" + (hours <= 9 ? "0" + hours : hours) + ":"
                    + (minutes <= 9 ? "0" + minutes : minutes) + " (" + isDay + ")`\n\t" + weather + "\n\n\t");
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
