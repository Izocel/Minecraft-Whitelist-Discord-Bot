package commands;

import main.WhitelistJe;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class testAssyncAwaitCmd extends ListenerAdapter {
    private Logger logger;
    private WhitelistJe main;

    public testAssyncAwaitCmd(WhitelistJe main) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.main = main;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("test"))
            return;

        TestAsync th = new TestAsync(main, event);


        JDA jda = this.main.getDiscordManager().jda;
        final net.dv8tion.jda.api.entities.User user = event.getUser();


        Object atRun = th.response;
        jda.openPrivateChannelById(user.getId()).queue(channel -> {
            channel.sendMessage(this.main.getBukkitManager().getPlayerUUID("Izocel"))
            .queue();
        });

        th.start();

        Object atFinish = th.response;
        jda.openPrivateChannelById(user.getId()).queue(channel -> {
            channel.sendMessage(atFinish == null ? "atFinish == null" : atFinish.toString())
            .queue();
        });


        for (int i = 0; i < 10; i++) {
            jda.openPrivateChannelById(user.getId()).queue(channel -> {
                channel.sendMessage("I'M counting")
                .queue();
            });
        }

        event.reply("Ok I'll do that for you please wait...").queue();
    }


    // .Start() = Async ||| .Run() = Awaited Start
    public class TestAsync extends Thread {
        
        private SlashCommandEvent event;
        public Object response = null;
        private Logger logger;
        private WhitelistJe main;

        public TestAsync(WhitelistJe main, SlashCommandEvent event) {
            super();
            this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
            this.main = main;
            this.event = event;
        }

        @Override
        public void run() {

            JDA jda = this.main.getDiscordManager().jda;
            final net.dv8tion.jda.api.entities.User user = event.getUser();
            
            int i = 0;
            while(true) {
                final int j = i;
                jda.openPrivateChannelById(user.getId()).queue(channel -> {
                    channel.sendMessage("**Test #`.**" + String.valueOf(j))
                    .queue();
                });

                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    SentryService.captureEx(e);
                }
                
                i++;
                if(i > 9) {
                    break;
                } 
            }
        }
    }

}