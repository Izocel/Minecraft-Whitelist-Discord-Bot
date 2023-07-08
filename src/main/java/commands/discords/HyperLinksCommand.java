package commands.discords;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import configs.ConfigManager;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import net.dv8tion.jda.api.JDA;

public class HyperLinksCommand extends BaseCmd {
    private static String KEY_CMD_NAME = null;
    private static String KEY_CMD_DESC = null;
    private LinkedHashMap<String, Object> cmdConfigs;
    private ConfigManager configs;

    public static void REGISTER_CMD(JDA jda, WhitelistDmc plugin, LinkedHashMap<String, Object> cmdConfigs) {
        HyperLinksCommand.KEY_CMD_NAME = cmdConfigs.get("aliasTradKey").toString();
        HyperLinksCommand.KEY_CMD_DESC = cmdConfigs.get("descriptionTradKey").toString();

        String cmdName = LOCAL.translate(KEY_CMD_NAME);
        String cmdDesc = LOCAL.translate(KEY_CMD_DESC);

        jda.addEventListener(new HyperLinksCommand(plugin, cmdConfigs));
        jda.upsertCommand(cmdName, cmdDesc).submit(true);
    }

    public HyperLinksCommand(WhitelistDmc plugin, LinkedHashMap<String, Object> cmdConfigs) {
        super(plugin,
                "LinksCommand",
                KEY_CMD_NAME,
                "LinksCommand",
                "Show a dynamic link");
        this.configs = this.plugin.getConfigManager();
        this.cmdConfigs = cmdConfigs;
    }

    @Override
    protected final void execute() {

        if (this.member == null) {
            final String reply = useTranslator("GUILDONLY_CMD");
            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "guild reserved");
            tx.finish(SpanStatus.UNAVAILABLE);
            return;
        }

        final Integer msgDelaySec = 120;
        final String textKey = cmdConfigs.get("discordTextTradKey").toString();
        final String reply = useTranslator(textKey);

        event.reply(reply)
                .setEphemeral(false)
                .queue((message) -> message.deleteOriginal().queueAfter(msgDelaySec, TimeUnit.SECONDS));

        tx.setData("state", "link was delivered");
        tx.finish(SpanStatus.OK);
    }
}
