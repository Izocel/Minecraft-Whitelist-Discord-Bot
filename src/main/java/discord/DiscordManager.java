package discord;

import java.util.EnumSet;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import commands.discords.LookupMcPlayerCommand;
import commands.discords.RegisterCommand;
import commands.discords.ServerCommand;
import configs.ConfigManager;
import events.discords.OnUserConfirm;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import locals.Lang;
import locals.LocalManager;
import main.WhitelistJe;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import services.sentry.SentryService;

public class DiscordManager {
    public WhitelistJe plugin;
    public JDA jda;

    private Logger logger;
    private String servername = "Discord® Server";
    private boolean isPrivateBot = true;
    private String guildId;
    private String inviteUrl;
	private Guild guild;
    private String ownerId;
    static private ConfigManager Configs = new ConfigManager();

    public DiscordManager(WhitelistJe plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
        .startChild("DiscordManager");

        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
        this.connect();
        this.setupCommands();
        this.setupListener();

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public void connect() {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
        .startChild("DiscordManager.connect");
        try {
            jda = JDABuilder.create(Configs.get("discordBotToken", null),
                    EnumSet.allOf(GatewayIntent.class))
                    .build()
                    .awaitReady();

            if(this.isPrivateBot) {this.checkGuild();}
            this.plugin.getSentryService().setUsername(this.ownerId);
            this.plugin.getSentryService().setUserId(this.getGuild().getId());
            
            if (jda == null) {
                throw new LoginException("Cannot initialize JDA");
            }
            
        } catch (LoginException | InterruptedException e) {
            process.setThrowable(e);
            process.setStatus(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
            Bukkit.shutdown();
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    private String setInvite() {
        InviteAction invite = jda.getGuildById(this.guildId).getTextChannels().get(0).createInvite();
        return invite.setMaxAge(0)
        .complete().getUrl();
    }

    public String getServerName() {
        return this.servername;
    }

    private void checkGuild() {
        String guildId;
        try {
            if(jda.getGuilds().size() > 1) {
                this.logger.warning("Discord bot's tokken already in use on another DS !!!");
            }

            guildId = Configs.get("discordServerId", null);
            this.guild = jda.getGuildById(guildId);
            this.servername = this.guild.getName();
            this.guildId = this.guild.getId();
            this.inviteUrl = this.setInvite();
            this.ownerId = this.guild.getOwnerId();
        } catch (Exception e) {
            this.logger.warning("Discord bot's not authorized into this guild. (Check: " + Configs.getClass().getSimpleName() +")");
        }
    }

    private void setupListener() {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
        .startChild("DiscordManager.setupListener");

        jda.addEventListener(new OnUserConfirm(plugin));

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    private void setupCommands() {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
        .startChild("DiscordManager.setupCommands");

        final LocalManager LOCAL = WhitelistJe.LOCALES;

        try {
            // FRENCH COMMAND
            LOCAL.setNextLang(Lang.FR.value);

            String srvCmdName = LOCAL.translate("CMD_SERVER");
            String srvCmdDesc = LOCAL.translate("DESC_SERVER");
            
            String rgstrCmdName = LOCAL.translate("CMD_REGISTER");
            String rgstrCmdDesc = LOCAL.translate("DESC_REGISTR");
            String paramJ = LOCAL.translate("PARAM_PJAVA");
            String paramB = LOCAL.translate("PARAM_PBEDR");
            String paramLabelJ = LOCAL.translate("PARAM_REGISTR_LABELJ");
            String paramLabelB = LOCAL.translate("PARAM_REGISTR_LABELB");

            String lookCmdName = LOCAL.translate("CMD_LOOKUP");
            String lookDesc = LOCAL.translate("DESC_LOOKUP");
            String valueLabel = LOCAL.translate("PARAM_LOOKUP_LABEL");

            // Serveur
            jda.addEventListener(new ServerCommand(plugin));
            jda.upsertCommand(srvCmdName, srvCmdDesc)
            .submit(true);

            // Enregistrer
            jda.addEventListener(new RegisterCommand(plugin));
            jda.upsertCommand(rgstrCmdName, rgstrCmdDesc)
            .addOption(OptionType.STRING, paramJ, paramLabelJ, false)
            .addOption(OptionType.STRING, paramB, paramLabelB, false)
            .submit(true);

            // Recherche
            jda.addEventListener(new LookupMcPlayerCommand(plugin));
            jda.upsertCommand(lookCmdName, lookDesc)
                .addOption(OptionType.STRING, "type", "`UUID` || `PSEUDO`", true)
                .addOption(OptionType.STRING, "value", valueLabel, true)
                .submit(true);


            // ENGLISH COMMANDS
            LOCAL.setNextLang(Lang.EN.value);

            srvCmdName = LOCAL.translate("CMD_SERVER");
            srvCmdDesc = LOCAL.translate("DESC_SERVER");

            rgstrCmdName = LOCAL.translate("CMD_REGISTER");
            rgstrCmdDesc = LOCAL.translate("DESC_REGISTR");
            paramJ = LOCAL.translate("PARAM_PJAVA");
            paramB = LOCAL.translate("PARAM_PBEDR");
            paramLabelJ = LOCAL.translate("PARAM_REGISTR_LABELJ");
            paramLabelB = LOCAL.translate("PARAM_REGISTR_LABELB");

            lookCmdName = LOCAL.translate("CMD_LOOKUP");
            lookDesc = LOCAL.translate("DESC_LOOKUP");
            valueLabel = LOCAL.translate("PARAM_LOOKUP_LABEL");

            // Server
            jda.upsertCommand(srvCmdName, srvCmdDesc)
            .submit(true);

            // Register
            jda.upsertCommand(rgstrCmdName, rgstrCmdDesc)
            .addOption(OptionType.STRING, paramJ, paramLabelJ, false)
            .addOption(OptionType.STRING, paramB, paramLabelB, false)
            .submit(true);

            // Search
            jda.upsertCommand(lookCmdName, lookDesc)
                .addOption(OptionType.STRING, "type", "`UUID` || `PSEUDO`", true)
                .addOption(OptionType.STRING, "value", valueLabel, true)
                .submit(true);
            
        } catch (Exception e) {
            this.logger.warning("Failed to initialize DS commands correctly");
            SentryService.captureEx(e);
        }

        LOCAL.nextIsDefault();
        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public void disconnect() {
        if (jda != null)
            jda.shutdownNow();
    }

    public String getInviteUrl() {
        return this.inviteUrl;
    }

	public Guild getGuild() {
		return this.guild;
	}
}


// TEST BIDON