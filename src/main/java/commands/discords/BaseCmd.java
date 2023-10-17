package commands.discords;

import java.util.logging.Logger;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import locals.LocalManager;
import main.WhitelistDmc;
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
    protected WhitelistDmc plugin;
    protected MessageChannel channel;
    protected SlashCommandEvent event;
    protected static LocalManager LOCAL = WhitelistDmc.LOCALES;

    protected String childClassName;
    protected String cmdNameTradKey;
    protected String mainTransactionName;
    protected String mainOperationName;
    protected net.dv8tion.jda.api.entities.User eventUser;
    protected String cmdLang;

    abstract void execute();

    protected BaseCmd(WhitelistDmc plugin,
            String childClassName, String cmdNameTradKey,
            String mainTransactionName, String mainOperationName) {
        this.plugin = plugin;
        this.childClassName = childClassName;
        this.logger = Logger.getLogger("WDMC:" + childClassName);
        this.cmdNameTradKey = cmdNameTradKey;
        this.mainTransactionName = mainTransactionName;
        this.mainOperationName = mainOperationName;

        this.logger.info("Registered ----> /" + LOCAL.translate(this.cmdNameTradKey));
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!this.isValidToContinue(event)) {
            return;
        }

        this.event = event;
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.eventUser = event.getUser();
        this.channel = event.getChannel();
        this.setWdmcUser();
        this.setCommandLang();

        ITransaction trx = Sentry.startTransaction(this.mainTransactionName, this.mainOperationName);
        trx.setData("CommandClass", childClassName);
        this.tx = trx;

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

    /**
     * Checks all language eventName to find the current language and
     * prevent multiple event calls.
     * 
     * @param event event
     * @return Boolean
     */
    protected final boolean isValidToContinue(SlashCommandEvent event) {
        return !event.isAcknowledged()
                && LOCAL.setCheckEventLocal(event.getName(), cmdNameTradKey);
    }

    protected final boolean isValidButtonToContinue(ButtonClickEvent event) {
        return !event.isAcknowledged();
    }

    protected final void setWdmcUser() {
        if (member != null)
            this.user = User.getFromMember(member);
        else
            this.user = null;
    }

    protected final void setCommandLang() {
        if (user != null) {
            this.cmdLang = user.getLang();
        } else {
            this.cmdLang = LOCAL.getNextLang();
        }
    }

    protected final void sendMsgToUser(String msg) {
        if (eventUser == null) {
            logger.warning("Undefined User for cmd event");
            return;
        }

        if (msg.length() >= 2000) {
            sendMsgToUser(msg, "message.txt");
            return;
        }

        this.plugin.getDiscordManager().jda.openPrivateChannelById(eventUser.getId()).queue(channel -> {
            channel.sendMessage(msg).submit(true).isDone();
        });
    }

    protected final void sendMsgToUser(String msg, String fileName) {
        if (eventUser == null) {
            logger.warning("Undefined User for cmd event");
            return;
        }

        // Allows 6.25 Millions characters
        this.plugin.getDiscordManager().jda.openPrivateChannelById(eventUser.getId()).queue(channel -> {
            channel.sendFile(msg.getBytes(), fileName).submit(true).isDone();
        });
    }

    protected final String useTranslator(String key) {
        return translateBy(key, this.cmdLang);
    }

    protected final String translateBy(String key, String lang) {
        return LOCAL.translateBy(key, lang);
    }

    protected final void submitReply(String msg) {
        this.event.reply(msg).setEphemeral(false).submit(true);
    }

    protected final void submitReplyEphemeral(String msg) {
        this.event.reply(msg).setEphemeral(true).submit(true);
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
