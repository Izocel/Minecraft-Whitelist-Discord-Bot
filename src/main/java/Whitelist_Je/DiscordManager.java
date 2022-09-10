package Whitelist_Je;

import java.util.EnumSet;
import javax.security.auth.login.LoginException;
import org.bukkit.Bukkit;

import Whitelist_Je.commands.ServerCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordManager {
    public WhitelistJe main;
    public JDA jda;

    DiscordManager(WhitelistJe main) { this.main = main; }

    public void connect() {
        try {

            jda = JDABuilder.create("MTAxNzk3MTY5MDM0MjQ1NzM1Ng.Gz1fRt.6zMP2hYuyaF-03LW0NPkibe3jCstAqvVzVLYlQ",
                    EnumSet.allOf(GatewayIntent.class))
                    .build()
                    .awaitReady();

            if (jda == null) {
                throw new LoginException("Cannot initialize JDA");
            }

        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            Bukkit.shutdown();
        }

        // Serveur
        jda.addEventListener(new ServerCommand(main));
        jda.upsertCommand("serveur", "Afficher les informations du serveur").queue();

        // // Whitelist
        // jda.addEventListener(new WhitelistCommand(main));
        // jda.upsertCommand("whitelist", "Commande whitelist du
        // serveur").addOption(OptionType.STRING,
        // "action", "add/remove/on/off",true).addOption(OptionType.STRING, "pseudo",
        // "Pseudo du joueur", false).queue();

        // // Request
        // jda.addEventListener(new RequestCommand(main));
        // jda.upsertCommand("request", "Demander l'accès au
        // serveur").addOption(OptionType.STRING, "pseudo", "Indiquer un pseudo",
        // true).queue();

        // // Deny
        // jda.addEventListener(new DenyCommand(main));
        // jda.upsertCommand("deny", "Refuser l'accès au serveur (réservé au
        // staff)").addOption(OptionType.MENTIONABLE,
        // "mention", "Mentionner un membre", true).queue();

    }

    public void disconnect() {
        if (jda != null)
            jda.shutdownNow();
    }
}
