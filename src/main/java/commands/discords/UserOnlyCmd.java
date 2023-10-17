package commands.discords;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import services.sentry.SentryService;

public abstract class UserOnlyCmd extends BaseCmd {

    protected UserOnlyCmd(WhitelistDmc plugin, String childClassName, String cmdNameTradKey, String mainTransactionName, String mainOperationName) {
        super(plugin, childClassName, cmdNameTradKey, mainTransactionName, mainOperationName);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!this.isValidToContinue(event)) {
            return;
        }
        
        this.event = event;
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.bot = event.getGuild().getSelfMember();
        this.eventUser = event.getUser();
        this.channel = event.getChannel();
        this.setWdmcUser();
        this.setCommandLang();

        ITransaction trx = Sentry.startTransaction(this.mainTransactionName, this.mainOperationName);
        trx.setData("CommandClass", childClassName);
        this.tx = trx;

        if (this.member == null) {
            final String reply = useTranslator("GUILDONLY_CMD");
            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "guild reserved");
            tx.finish(SpanStatus.UNAVAILABLE);
            return;
        }

        if (this.user == null) {
            final String reply = useTranslator("USER_ONLY_CMD");
            event.reply(reply).setEphemeral(true).submit(true);
            tx.setData("error-state", "unregistered");
            tx.finish(SpanStatus.UNAUTHENTICATED);
            return;
        }

        try {
            this.execute();
        } catch (Exception e) {
            final String reply = useTranslator("CMD_ERROR") + ": " + useTranslator("CONTACT_ADMIN");

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
