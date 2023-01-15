package commands.discords;

import org.json.JSONArray;

import configs.ConfigManager;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import locals.Lang;
import locals.LocalManager;
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
        final LocalManager LOCAL = WhitelistJe.LOCALES;

        LOCAL.setNextLang(Lang.FR.value);
        final String frCmdName = LOCAL.translate("CMD_LOOKUP");
        LOCAL.setNextLang(Lang.EN.value);
        final String enCmdName = LOCAL.translate("CMD_LOOKUP");

        if (!event.getName().equals(frCmdName) && !event.getName().equals(enCmdName))
            return;

        ITransaction tx = Sentry.startTransaction("LookupMcPlayerCommand", "dig Mc® player json");
        String lookupMsg = "";

        try {
            final String type = event.getOption("type").getAsString();
            final String value = event.getOption("value").getAsString();

            if(value.length() < 3) {
                lookupMsg = "❌**Cette valeur de recherche n'est pas valide...\n Voici des examples: \n\t**" +
                "`UUID`: [0c003c29-8675-4856-914b-9641e4b6bac3, 00000000-0000-0000-0009-000006D1B380, 2535422189221140]\n\t`PSEUDO`: Alex";
                event.reply(lookupMsg).setEphemeral(true).submit(true);

                tx.setData("state", "invalid form");
                tx.finish(SpanStatus.OK);
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
                event.reply(lookupMsg).setEphemeral(true).submit(true);
                return;
            }

            lookupMsg = "------------------------------------------------------\n```json\n" + 
                json.toString(2) + "\n```" + "------------------------------------------------------";

            
            event.reply(lookupMsg).setEphemeral(true).submit(true);

            tx.setData("state", "response was sent");
            tx.finish(SpanStatus.OK);

        } catch (Exception e) {
            event.reply("❌**Désoler une erreur est survenu...**").setEphemeral(true).submit(true);

            tx.setThrowable(e);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
        }

    }
}
