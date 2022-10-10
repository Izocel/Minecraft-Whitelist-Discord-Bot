package events.discords;

import java.util.logging.Logger;

import dao.DaoManager;
import dao.UsersDao;
import io.sentry.ITransaction;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import services.sentry.SentryService;

public class OnGuildMemberJoin extends ListenerAdapter {
    private Logger logger;
    private WhitelistJe plugin;

    public OnGuildMemberJoin(WhitelistJe plugin) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        ITransaction process = plugin.getSentryService().createTx("OnGuildMemberJoin", "updateUsers");
        try {
            final net.dv8tion.jda.api.entities.User user = event.getMember().getUser();
            final String userId = user.getId();
            final String userTag = user.getAsTag();
            
            plugin.getDaoManager();
            UsersDao dao = DaoManager.getUsersDao(); 
            User user2 = dao.findByDisccordTag(userTag);

            if(user2 == null || user2.getId() < 1) {
                user2 = new User();
            }

            user2.setDiscordId(userId);
            user2.setDiscordTag(userTag);
            user2.save(dao);
            
        } catch (Exception e) {
            process.setThrowable(e);
            SentryService.captureEx(e);
            process.finish(SpanStatus.INTERNAL_ERROR);
        }

        if(!process.isFinished())
            process.finish(SpanStatus.OK);
    }

}
