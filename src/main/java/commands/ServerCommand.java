package commands;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.World;

import configs.ConfigManager;
import main.WhitelistJe;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerCommand extends ListenerAdapter {
    private WhitelistJe main;
    static private ConfigManager Configs = new ConfigManager();

    public ServerCommand(WhitelistJe main) {
        this.main = main;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        final Integer msgDelaySec = 120;
        final String serverName = event.getGuild().getName();
        final String protJ = Configs.get("portJava", null);
        final String portB = Configs.get("portBedrock", null);
        final String paperMcIp = Configs.get("paperMcIp", null);

        if (!event.getName().equals("serveur"))
            return; // make sure we handle the right command
        event.reply("\n\n**📝" + serverName + " | Informations**" +
                "\n\n**Adresse I.P. :** `" + paperMcIp + "`" +
                "\n**Port Java:** `" + protJ + "` " +
                "\n**Port Bedrock:** `" + portB + "` " +
                "\n\n" + getPlayersOnline() + "\n\n" + getTime() +
                "\n\n**👨‍💻 Développeur: <@272924120142970892>**"

        ).setEphemeral(false).queue((message) -> message.deleteOriginal().queueAfter(msgDelaySec, TimeUnit.SECONDS));
    }

    public String getTime() {
        World world = Bukkit.getWorld("world");
        long gameTime = world.getTime(),
                hours = gameTime / 1000 + 6,
                minutes = (gameTime % 1000) * 60 / 1000;
        String weather = "`" + (world.hasStorm() ? "Orageux" : "Non orageux") + "`\n`"
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

        return "**⎯ " + emotes + " Météo et temps du serveur**\n`" + (hours <= 9 ? "0" + hours : hours) + ":"
                + (minutes <= 9 ? "0" + minutes : minutes) + " (" + isDay + ")`\n" + weather;
    }

    public String getPlayersOnline() {
        return "**⎯ 🌿 Activités du serveur**\n`"
                + (Bukkit.getOnlinePlayers().size() <= 1 ? Bukkit.getOnlinePlayers().size() + " joueurs connectés"
                        : Bukkit.getOnlinePlayers().size() + " joueurs connectés")
                + "`\n"
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
