package events.discords;

import java.util.logging.Logger;

import main.WhitelistJe;
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
        
        if (!event.getChannel().getType().equals("PrivateChannel")) {
            event.reply("Dommage vous n'avez pas les accès...¯\\_(ツ)_/¯")
                    .setEphemeral(true).queue();
            return;
        }

        final String componentId = event.getComponentId();
        final String actionId = componentId.split(" ")[0];

        if (actionId.equals(this.acceptId)) {
            this.handleAccepted(event);

        } else if (actionId.equals(this.rejectId)) {
            this.handleRejected(event);
        }


    }

    private void handleRejected(ButtonClickEvent event) {
        final Member respMember = event.getMember();
        final String componentId = event.getComponentId();
        final String pseudo = componentId.split(" ")[1];
        final String discordId = componentId.split(" ")[2];

    }

    private void handleAccepted(ButtonClickEvent event) {
        final Member respMember = event.getMember();
        final String componentId = event.getComponentId();
        final String pseudo = componentId.split(" ")[1];
        final String discordId = componentId.split(" ")[2];
    }
}
