package events.discords;

import java.util.logging.Logger;

import org.jooq.tools.json.JSONObject;

import helpers.NotificationManager;
import io.sentry.ITransaction;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import models.NotificationData;
import models.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import services.sentry.SentryService;

public class OnGuildMemberJoin extends ListenerAdapter {
    private Logger logger;
    private WhitelistDmc plugin;

    public OnGuildMemberJoin(WhitelistDmc plugin) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        ITransaction process = plugin.getSentryService().createTx("OnGuildMemberJoin", "updateUsers");
        try {
            User.updateFromMember(event.getMember()).saveUser();
            sendNotification(event);
        } catch (Exception e) {
            process.setThrowable(e);
            SentryService.captureEx(e);
            process.finish(SpanStatus.INTERNAL_ERROR);
        }

        if (!process.isFinished())
            process.finish(SpanStatus.OK);
    }

    private void sendNotification(GuildMemberJoinEvent event) {
        final net.dv8tion.jda.api.entities.User user = event.getMember().getUser();
        final String title = user.getAsTag().concat(" has just join your Discord® guild.");
        final String msg = "\n\t`Discord-UserId`: " + user.getId() +
        "\n\t`Discord-Server`: " + event.getGuild().getName();

        final NotificationData notification = new NotificationData(title, msg);
        notification.topic = NotificationManager.guildJoinTopic;
        notification.markdown = true;
        notification.tags.add("robot");

        NotificationManager.postNotification(notification, false);
    }

}
