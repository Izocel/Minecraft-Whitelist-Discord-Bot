package events.discords;

import java.util.logging.Logger;

import io.sentry.ITransaction;
import io.sentry.SpanStatus;
import main.WhitelistDMC;
import models.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import services.sentry.SentryService;

public class OnGuildMemberJoin extends ListenerAdapter {
    private Logger logger;
    private WhitelistDMC plugin;

    public OnGuildMemberJoin(WhitelistDMC plugin) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        ITransaction process = plugin.getSentryService().createTx("OnGuildMemberJoin", "updateUsers");
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
