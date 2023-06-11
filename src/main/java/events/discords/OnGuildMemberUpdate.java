package events.discords;

import java.util.logging.Logger;

import io.sentry.ITransaction;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import services.sentry.SentryService;

public class OnGuildMemberUpdate extends ListenerAdapter {
    private Logger logger;
    private WhitelistJe plugin;

    public OnGuildMemberUpdate(WhitelistJe plugin) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @Override
    public void onGuildMemberUpdate(GuildMemberUpdateEvent event) {
        ITransaction process = plugin.getSentryService().createTx("OnGuildMemberUpdateEvent", "updateUser");
        try {
            User.updateFromMember(event.getMember()).saveUser();
        } catch (Exception e) {
            process.setThrowable(e);
            SentryService.captureEx(e);
            process.finish(SpanStatus.INTERNAL_ERROR);
        }

        if(!process.isFinished())
            process.finish(SpanStatus.OK);
    }

}
