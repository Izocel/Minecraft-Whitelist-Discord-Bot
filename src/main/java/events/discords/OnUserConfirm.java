package events.discords;

import java.util.List;
import java.util.logging.Logger;

import commands.bukkit.ConfirmLinkCmd;
import dao.UsersDao;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnUserConfirm extends ListenerAdapter {
    private Logger logger;
    private WhitelistJe main;
    private String acceptId = ConfirmLinkCmd.acceptId;
    private String rejectId = ConfirmLinkCmd.rejectId;

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

            final String componentId = event.getComponentId();    
            if (componentId.equals(acceptId)) {
                this.handleAccepted(event);
    
            } else if (componentId.equals(rejectId)) {
                this.handleRejected(event);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAccepted(ButtonClickEvent event) {
        try {
            final List<Field> fields = event.getMessage().getEmbeds().get(0).getFields();
            final String mcUuid = fields.get(1).getValue();
            final UsersDao dao = this.main.getDaoManager().getUsersDao();
            final User user = dao.findByMcUUID(mcUuid);

            user.setAsConfirmed(true);
            user.save(dao);

            event.reply("✔️ Vos compte sont maintenant reliés.").queue();
        } catch (Exception e) {
            event.reply("❌ Vos compte non pas pu être reliés. Contactez un admin!").queue();
            e.printStackTrace();
        }
    }

    private void handleRejected(ButtonClickEvent event) {
        try {
            final List<Field> fields = event.getMessage().getEmbeds().get(0).getFields();
            final String mcUuid = fields.get(1).getValue();
            final UsersDao dao = this.main.getDaoManager().getUsersDao();
            final User user = dao.findByMcUUID(mcUuid);
    
            user.delete(dao);
            event.reply("✔️ La demande a bien été rejetée.").queue();
        } catch (Exception e) {
            event.reply("❌ Cette demande a rencontrée des problèmes. Contactez un admin!").queue();
            e.printStackTrace();
        }
    }
}
