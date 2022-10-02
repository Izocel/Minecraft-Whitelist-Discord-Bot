package events.discords;

import java.util.logging.Logger;

import dao.UsersDao;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnUserConfirm extends ListenerAdapter {
    private Logger logger;
    private WhitelistJe main;
    static String acceptId = "acceptActionLink";
    static String rejectId = "rejectActionLink";

    public OnUserConfirm(WhitelistJe main) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.main = main;
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        try {
            if (!event.getChannel().getType().toString().equals("PRIVATE")) {
                return;
            }
    
            //TODO:
            final String componentId = event.getComponentId();
            //final String actionId = componentId.split(" ")[0];
            final String actionId = acceptId;
    
            this.logger.info("componentId:" + componentId);
    
            if (actionId.equals(acceptId)) {
                this.handleAccepted(event);
    
            } else if (actionId.equals(rejectId)) {
                this.handleRejected(event);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAccepted(ButtonClickEvent event) {
        final Member respMember = event.getMember();
        final String componentId = event.getComponentId();
        final String mcUuid = componentId.split(" ")[1];
        final String discordId = componentId.split(" ")[2];

        final UsersDao dao = this.main.getDaoManager().getUsersDao();
        final User user = dao.findByMcUUID(mcUuid);

        user.setAsConfirmed(true);
        user.save(dao);
    }

    private void handleRejected(ButtonClickEvent event) {
        final Member respMember = event.getMember();
        final String componentId = event.getComponentId();
        final String mcUuid = componentId.split(" ")[1];
        final String discordId = componentId.split(" ")[2];

        final UsersDao dao = this.main.getDaoManager().getUsersDao();
        final User user = dao.findByMcUUID(mcUuid);

        user.delete(dao);
    }
}
