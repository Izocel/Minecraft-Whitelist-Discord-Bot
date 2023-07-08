package commands.discords;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import configs.ConfigManager;
import helpers.Helper;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class HyperLinksCommand extends BaseCmd {
    private static String KEY_CMD_NAME = null;
    private static String KEY_CMD_DESC = null;
    private final LinkedHashMap<String, Object> cmdConfigs;
    private final ConfigManager configs;
    private final String doRun;

    public static void REGISTER_CMD(JDA jda, WhitelistDmc plugin, LinkedHashMap<String, Object> cmdConfigs) {
        final String doInit = cmdConfigs.getOrDefault("useDiscord", "true").toString();
        if( doInit != "true") {
            return;
        }

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
        this.doRun = cmdConfigs.getOrDefault("useDiscord", "true").toString();
    }

    @Override
    protected final void execute() {
        if( doRun != "true") {
            final String name = cmdConfigs.get("aliasTradKey").toString();
            this.logger.info("hyperlink: " + name + " is deactivated for discord");
            return;
        }

        if (this.member == null) {
            final String reply = useTranslator("GUILDONLY_CMD");
            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "guild reserved");
            tx.finish(SpanStatus.UNAVAILABLE);
            return;
        }

        final String textKey = cmdConfigs.get("discordTextTradKey").toString();
        final String linkKey = cmdConfigs.get("linkTradKey").toString();

        boolean isPublic = true;
        if(cmdConfigs.getOrDefault("isPublicMsg", "true") != "true") {
            isPublic = false;
        }

        final Integer msgDelaySec = 120;
        final String msg = useTranslator(textKey);
        final String link = useTranslator(linkKey);

        final MessageEmbed msgEmbedded = Helper.jsonToMessageEmbed(buildLinkEmbed(msg, link));
        event.replyEmbeds(msgEmbedded)
                .setEphemeral(!isPublic)
                .queue((message) -> message.deleteOriginal().queueAfter(msgDelaySec, TimeUnit.SECONDS));

        tx.setData("state", "link was delivered");
        tx.finish(SpanStatus.OK);
    }

    public static String buildLinkEmbed(String msg, String url) {

        final String jsonEmbedded = """
            {
              "content": "",
              "tts": true,
              "embeds": [
                {
                  "type": "rich",
                  "title": '""" + msg + "'," + """
            "description": "",
            "color": "cc00ff",
            "url": '""" + url + "'" + """
                }
              ]
            }""";
    
        return jsonEmbedded;
      }
}
