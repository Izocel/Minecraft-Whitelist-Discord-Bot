package commands.discords;

import org.json.JSONArray;

import io.sentry.SpanStatus;
import main.WhitelistJe;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import services.api.PlayerDbApi;

public class LookupMcPlayerCommand extends BaseCmd {

    private final static String KEY_CMD_NAME = "CMD_LOOKUP";
    private final static String KEY_CMD_DESC = "DESC_LOOKUP";
    private final static String KEY_PARAM_VALUE_LABEL = "PARAM_LOOKUP_LABEL";

    public static void REGISTER_CMD(JDA jda, WhitelistJe plugin) {
        String cmdName = LOCAL.translate(KEY_CMD_NAME);
        String cmdDesc = LOCAL.translate(KEY_CMD_DESC);
        final String valueLabel = LOCAL.translate(KEY_PARAM_VALUE_LABEL);

        jda.addEventListener(new LookupMcPlayerCommand(plugin));
        jda.upsertCommand(cmdName, cmdDesc)
            .addOption(OptionType.STRING, "type", "`UUID` || `PSEUDO`", true)
            .addOption(OptionType.STRING, "value", valueLabel, true)
            .submit(true);
}

    public LookupMcPlayerCommand(WhitelistJe plugin) {
        super(plugin,
            "LookupMcPlayerCommand",
            "CMD_LOOKUP",
            "LookupMcPlayerCommand",
            "Dig Mc® player json");
    }

    @Override
    protected final void execute() {

        if (!event.getChannel().getType().toString().equals("TEXTONLY_CMD")) {
            final String reply = LOCAL.translate("GUILDONLY_CMD");
            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "textual reserved");
            tx.finish(SpanStatus.UNAVAILABLE);
            return;
        }

        String lookupMsg = "";
        final String type = event.getOption("type").getAsString();
        final String value = event.getOption("value").getAsString();

        if (value.length() < 3) {
            lookupMsg = "❌**Cette valeur de recherche n'est pas valide...\n Voici des examples: \n\t**" +
                    "`UUID`: [0c003c29-8675-4856-914b-9641e4b6bac3, 00000000-0000-0000-0009-000006D1B380, 2535422189221140]\n\t`PSEUDO`: Alex";
            event.reply(lookupMsg).setEphemeral(true).submit(true);

            tx.setData("state", "invalid form");
            tx.finish(SpanStatus.OK);
            return;
        }

        JSONArray json = null;
        if (type.toLowerCase().equals("uuid")) {
            json = PlayerDbApi.fetchInfosWithUuid(value);
        } else if (type.toLowerCase().equals("pseudo")) {
            json = PlayerDbApi.fetchInfosWithPseudo(value);
        } else {
            lookupMsg = "❌**Vous devez choisir un type valide [`UUID`, `PSEUDO`]**";
            event.reply(lookupMsg).setEphemeral(true).submit(true);
            return;
        }

        lookupMsg = "------------------------------------------------------\n```json\n" +
                json.toString(2) + "\n```" + "------------------------------------------------------";

        event.reply(lookupMsg).setEphemeral(true).submit(true);

        tx.setData("state", "response was sent");
        tx.finish(SpanStatus.OK);

    }

}
