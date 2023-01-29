package commands.discords;

import java.util.logging.Logger;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import locals.LocalManager;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import services.sentry.SentryService;

public abstract class BaseCmd extends ListenerAdapter {

    protected User user;
    protected Guild guild;
    protected Logger logger;
    protected Member member;
    protected ITransaction tx;
    protected WhitelistJe plugin;
    protected MessageChannel channel;
    protected SlashCommandEvent event;
    protected static LocalManager LOCAL = WhitelistJe.LOCALES;

    protected String childClassName;
    protected String cmdNameTradKey;
    protected String mainTransactionName;
    protected String mainOperationName;
    protected net.dv8tion.jda.api.entities.User eventUser;
    protected String cmdLang;

    abstract void execute();

    protected BaseCmd(WhitelistJe plugin,
        String childClassName, String cmdNameTradKey,
        String mainTransactionName, String mainOperationName) {
        this.plugin = plugin;
        this.childClassName = childClassName;
        this.logger = Logger.getLogger("WJE:" + childClassName);
        this.cmdNameTradKey = cmdNameTradKey;
        this.mainTransactionName = mainTransactionName;
        this.mainOperationName = mainOperationName;
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

    /**
     * Checks all langugae eventName to find the current language and
     * prevent multiple event calls.
     * @param SlashCommandEvent event 
     * @return Boolean
     */
    protected final boolean isValidToContinue(SlashCommandEvent event) {
        return !event.isAcknowledged()
                && LOCAL.setCheckEventLocal(event.getName(), cmdNameTradKey);
    }

    protected final boolean isValidButtonToContinue(ButtonClickEvent event) {
        return !event.isAcknowledged();
    }

    protected final void setWjeUser() {
        if (member != null)
            this.user = User.getFromMember(member);
    }

    protected final void sendMsgToUser(String msg) {
        if (eventUser == null) {
            logger.warning("Undefined User for cmd event");
            return;
        }

        this.plugin.getDiscordManager().jda.openPrivateChannelById(eventUser.getId()).queue(channel -> {
            channel.sendMessage(msg).submit(true);
        });
    }

    protected final String useTranslator(String key) {
        if(user != null) {
            return translateUser(key);
        }
        else if (cmdLang != null) {
            return translateCmd(key);
        }
        else {
            return translate(key);
        }
    }

    protected final String translateUser(String key) {
        if (user == null) {
            return translate(key);
        }

        return translateBy(key, this.user.getLang());
    }

    protected final String translateCmd(String key) {
        if (this.cmdLang == null) {
            return translate(key);
        }

        return translateBy(key, this.cmdLang);
    }

    protected final String translateBy(String key, String lang) {
        return LOCAL.translateBy(key, lang);
    }

    protected final String translate(String key) {
        return LOCAL.translate(key);
    }

    protected final void submitReply(String msg) {
        this.event.reply(msg).submit(true);
    }

    protected final Boolean getBoolParam(String paramTradKey) {
        final OptionMapping option = event.getOption(LOCAL.translate(paramTradKey));
        return option != null ? option.getAsBoolean() : null;
    }

    protected final String getStringParam(String paramTradKey) {
        final OptionMapping option = event.getOption(LOCAL.translate(paramTradKey));
        return option != null ? option.getAsString() : null;
    }

    protected final Long getLongParam(String paramTradKey) {
        final OptionMapping option = event.getOption(LOCAL.translate(paramTradKey));
        return option != null ? option.getAsLong() : null;
    }

    protected final Double getDoubleParam(String paramTradKey) {
        final OptionMapping option = event.getOption(LOCAL.translate(paramTradKey));
        return option != null ? option.getAsDouble() : null;
    }

}
