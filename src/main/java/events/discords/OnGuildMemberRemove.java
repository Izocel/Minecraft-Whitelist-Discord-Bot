package events.discords;

import java.util.logging.Logger;

import dao.DaoManager;
import dao.UsersDao;
import io.sentry.ITransaction;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import services.sentry.SentryService;

public class OnGuildMemberRemove extends ListenerAdapter {
    private Logger logger;
    private WhitelistJe plugin;

    public OnGuildMemberRemove(WhitelistJe plugin) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        ITransaction process = plugin.getSentryService().createTx("OnGuildMemberRemove", "deleteUser");
        try {
            final net.dv8tion.jda.api.entities.User user = event.getMember().getUser();
            final String userId = user.getId();
            final String userTag = user.getAsTag();
            
            plugin.getDaoManager();
            UsersDao dao = DaoManager.getUsersDao(); 
            User user2 = dao.findByDisccordTag(userTag);

            if(user2 == null || user2.getId() < 1) {
                return;
            }
            
            user2.delete(dao);
            
        } catch (Exception e) {
            process.setThrowable(e);
            SentryService.captureEx(e);
            process.finish(SpanStatus.INTERNAL_ERROR);
        }

        if(!process.isFinished())
            process.finish(SpanStatus.OK);
    }

}
