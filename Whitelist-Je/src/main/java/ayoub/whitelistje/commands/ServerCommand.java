package ayoub.whitelistje.commands;

import ayoub.whitelistje.WhitelistJe;
import ayoub.whitelistje.functions.WhitelistManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.concurrent.TimeUnit;

public class ServerCommand extends ListenerAdapter {
    private WhitelistJe main;
    private WhitelistManager whitelistManager;
    
    public ServerCommand(WhitelistJe main) {
        this.main = main;
    }
    
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("serveur")) return; // make sure we handle the right command
        event.reply("**Tu me Niaises\n" +
                "**\n⎯ 📝<@258071819108614144> Informations**" +
                "\n**Adresse I.P. :** `server.minecraft.tumeniaises.ca`" +
                "\n**Port Java:** `25565` " +
                "\n**Port Bedrock:** `19132` " +
                "\n\n" + getPlayersOnline() + "\n" + getTime() +
                "\n\n*© 👨‍💻 Développeurs: <@982971022640840806> et <@272924120142970892> ! 👨‍💻*\n\n"

        ).setEphemeral(false).queue((message) -> message.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
    }

    public String getTime() {
        World world = Bukkit.getWorld("world");
        long gameTime = world.getTime(),
                hours = gameTime / 1000 + 6,
                minutes = (gameTime % 1000) * 60 / 1000;
        String weather = "`" + (world.hasStorm() ? "Orageux" : "Non orageux") + "`\n`" + (world.isThundering() ? "Pluvieux" : "Non pluvieux") + "`",
                isDay = hours <= 17 ? "Jour" : "Nuit",
                emotes;
        if(isDay.equals("Jour")) {
            emotes = "☀️";
        } else {
            emotes = "🌙";
        }

        if (hours >= 24) { hours -= 24; }
        if(world.hasStorm()) emotes += "🌩";
        if(world.isThundering()) emotes += "🌧";

        return "**⎯ " + emotes + " Météo et temps du serveur**\n`" + (hours <= 9 ? "0" + hours : hours) + ":" + (minutes <= 9 ? "0" + minutes : minutes) + " (" + isDay + ")`\n" + weather;
    }

    public String getPlayersOnline() {
        whitelistManager = main.getWhitelistManager();
        return "**⎯ 🌿 Activités du serveur**\n`" + (Bukkit.getOnlinePlayers().size() <= 1 ? Bukkit.getOnlinePlayers().size() + " joueurs connectés" : Bukkit.getOnlinePlayers().size() + " joueurs connectés") + "`\n" + (Bukkit.getOnlinePlayers().size() != 0 ? Bukkit.getOnlinePlayers().toString().replace("CraftPlayer{name=", "")
                .replace("}", "")
                .replace("]", "")
                .replace("{", "")
                .replace("_", "⎽")
                .replace("[", "") : "");
    }
}
