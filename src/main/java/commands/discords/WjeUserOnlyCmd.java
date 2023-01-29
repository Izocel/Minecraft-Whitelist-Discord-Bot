package commands.discords;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import services.sentry.SentryService;

public abstract class WjeUserOnlyCmd extends BaseCmd {

    protected WjeUserOnlyCmd(WhitelistJe plugin, String childClassName, String cmdNameTradKey, String mainTransactionName, String mainOperationName) {
        super(plugin, childClassName, cmdNameTradKey, mainTransactionName, mainOperationName);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!this.isValidToContinue(event)) {
            return;
        }
        
        this.user = null;
        this.event = event;
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.eventUser = event.getUser();
        this.channel = event.getChannel();
        this.cmdLang = LOCAL.getNextLang();
        this.setWjeUser();

        ITransaction trx = Sentry.startTransaction(this.mainTransactionName, this.mainOperationName);
        trx.setData("CommandClass", childClassName);
        this.tx = trx;

        if (this.member == null) {
            final String reply = useTranslator("MEMBERONLY_CMD");
            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "guild reserved");
            tx.finish(SpanStatus.UNAVAILABLE);
            return;
        }

        if (this.user == null) {
            final String reply = useTranslator("USERONLY_CMD");
            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "unregistered");
            tx.finish(SpanStatus.UNAUTHENTICATED);
            return;
        }

        try {
            this.execute();
        } catch (Exception e) {
            final String reply = useTranslator("CMD_ERROR") + ": " + useTranslator("CONTACT_ADMNIN");

            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
        }

        if (!tx.isFinished()) {
            tx.setData("finalState", "unkwown");
            tx.finish(SpanStatus.UNKNOWN);
        }
    }

}
