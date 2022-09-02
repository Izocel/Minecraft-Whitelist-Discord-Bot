package ayoub.whitelistje;

import ayoub.whitelistje.commands.DenyCommand;
import ayoub.whitelistje.commands.RequestCommand;
import ayoub.whitelistje.commands.ServerCommand;
import ayoub.whitelistje.commands.WhitelistCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class DiscordManager {
    public WhitelistJe main;
    DiscordManager(WhitelistJe main) {
        this.main = main;
    }
    public JDA jda;

    public void connect() {
        try {
            jda = JDABuilder.create(
            "OTk1OTg5NTkxNzAwMDI5NDYw.Gd-Yfh.kg0heHU5f_Xzt-K6u30u-Z3WtzcnHa250L0q9w",
            EnumSet.allOf(GatewayIntent.class))
            .build()
            .awaitReady();
            
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            Bukkit.shutdown();
        }
        if (jda == null) Bukkit.shutdown();
        jda.addEventListener(new WhitelistCommand(main));
        jda.addEventListener(new RequestCommand(main));
        jda.addEventListener(new ServerCommand(main));
        jda.addEventListener(new DenyCommand(main));
        jda.upsertCommand("whitelist", "Commande whitelist du serveur").addOption(OptionType.STRING, "action", "add/remove/on/off", true).addOption(OptionType.STRING, "pseudo", "Pseudo du joueur", false).queue(); // This can take up to 1 hour to show up in the client
        jda.upsertCommand("server", "Afficher les informations du serveur").queue(); // This can take up to 1 hour to show up in the client
        jda.upsertCommand("request", "Demander l'accès au serveur").addOption(OptionType.STRING, "pseudo", "Indiquer un pseudo", true).queue(); // This can take up to 1 hour to show up in the client
        jda.upsertCommand("deny", "Refuser l'accès au serveur (réservé au staff)").addOption(OptionType.MENTIONABLE, "mention", "Mentionner un membre", true).queue(); // This can take up to 1 hour to show up in the client

    }

    public void disconnect() {
        if (jda != null) jda.shutdownNow();
    }
}
