package commands.discords;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.json.JSONArray;

import configs.ConfigManager;
import main.WhitelistJe;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import services.api.PlayerDbApi;
import services.sentry.SentryService;

public class LookupMcPlayerCommand extends ListenerAdapter {
    private WhitelistJe main;
    private ConfigManager configs;

    public LookupMcPlayerCommand(WhitelistJe main) {
        this.main = main;
        this.configs = this.main.getConfigManager();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        final String cmdName = this.main.getConfigManager().get("lookupMcPlayerCmdName", "lookupMcPlayerCmdName");
        if (!event.getName().equals(cmdName))
            return;

        String lookupMsg = "";

        try {
            
            final String type = event.getOption("type").getAsString();
            final String value = event.getOption("value").getAsString();

            if(value.length() < 3) {
                lookupMsg = "❌**Cette valeur de recherche n'est pas valide...\n Voici des examples: \n\t**" +
                "`UUID`: [0c003c29-8675-4856-914b-9641e4b6bac3, 00000000-0000-0000-0009-000006D1B380, 2535422189221140]\n\t`PSEUDO`: Alex";
                event.reply(lookupMsg).setEphemeral(true).queue();
                return;
            }

            JSONArray json = null;
            if(type.toLowerCase().equals("uuid")) {
                json = PlayerDbApi.fetchInfosWithUuid(value);
            }
            else if(type.toLowerCase().equals("pseudo")) {
                json = PlayerDbApi.fetchInfosWithPseudo(value);
            }
            else {
                lookupMsg = "❌**Vous devez choisir un type valide [`UUID`, `PSEUDO`]**";
                event.reply(lookupMsg).setEphemeral(true).queue();
                return;
            }

            lookupMsg = "------------------------------------------------------\n```json\n" + 
                json.toString(2) + "\n```" + "------------------------------------------------------";

        } catch (Exception e) {
            SentryService.captureEx(e);
            lookupMsg = "❌**Désoler une erreur est survenu...**";
            event.reply(lookupMsg).setEphemeral(true).queue();
        }


        event.reply(lookupMsg).setEphemeral(true).queue();
    }
}
