package events.discords;

import java.util.logging.Logger;

import io.sentry.ITransaction;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import models.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import services.sentry.SentryService;

public class OnGuildMemberRemove extends ListenerAdapter {
    private Logger logger;
    private WhitelistDmc plugin;

    public OnGuildMemberRemove(WhitelistDmc plugin) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        ITransaction process = plugin.getSentryService().createTx("OnGuildMemberRemove", "deleteUser");
        try {
            User.updateFromMember(event.getMember()).deleteUser();
        } catch (Exception e) {
            process.setThrowable(e);
            SentryService.captureEx(e);
            process.finish(SpanStatus.INTERNAL_ERROR);
        }

        if(!process.isFinished())
            process.finish(SpanStatus.OK);
    }

}
