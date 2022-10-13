package main;

import java.net.http.WebSocket.Listener;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import bukkit.BukkitManager;
import configs.ConfigManager;
import dao.DaoManager;
import discord.DiscordManager;
import functions.GuildManager;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import services.sentry.SentryService;

public final class WhitelistJe extends JavaPlugin implements Listener {

    private Logger logger;
    public WhitelistJe instance;
    private BukkitManager bukkitManager;
    private GuildManager guildManager;
    private ConfigManager configManager;
    private DiscordManager discordManager;
    private DaoManager daoManager;

    private JSONArray players = new JSONArray();
    private JSONArray playersAllowed = new JSONArray();
    private SentryService sentryService;

    public String getfiglet() {
        String figlet = """

                 __       __  __        __    __                __  __              __               _____
                /  |  _  /  |/  |      /  |  /  |              /  |/  |            /  |             /     |
                $$ | / \\ $$ |$$ |____  $$/  _$$ |_     ______  $$ |$$/   _______  _$$ |_            $$$$$ |  ______
                $$ |/$  \\$$ |$$      \\ /  |/ $$   |   /      \\ $$ |/  | /       |/ $$   |  ______      $$ | /      \\
                $$ /$$$  $$ |$$$$$$$  |$$ |$$$$$$/   /$$$$$$  |$$ |$$ |/$$$$$$$/ $$$$$$/  /      |__   $$ |/$$$$$$  |
                $$ $$/$$ $$ |$$ |  $$ |$$ |  $$ | __ $$    $$ |$$ |$$ |$$      \\   $$ | __$$$$$$//  |  $$ |$$    $$ |
                $$$$/  $$$$ |$$ |  $$ |$$ |  $$ |/  |$$$$$$$$/ $$ |$$ | $$$$$$  |  $$ |/  |      $$ \\__$$ |$$$$$$$$/
                $$$/    $$$ |$$ |  $$ |$$ |  $$  $$/ $$       |$$ |$$ |/     $$/   $$  $$/       $$    $$/ $$       |
                $$/      $$/ $$/   $$/ $$/    $$$$/   $$$$$$$/ $$/ $$/ $$$$$$$/     $$$$/         $$$$$$/   $$$$$$$/
                         ______     __   __   _____        ______   ______     ______       __     ______     ______     ______   ______
                        /\\  == \\   /\\ \\ / /  /\\  __-.     /\\  == \\ /\\  == \\   /\\  __ \\     /\\ \\   /\\  ___\\   /\\  ___\\   /\\__  _\\ /\\  ___\\
                        \\ \\  __<   \\ \\ \\'/   \\ \\ \\/\\ \\    \\ \\  _-/ \\ \\  __<   \\ \\ \\/\\ \\   _\\_\\ \\  \\ \\  __\\   \\ \\ \\____  \\/_/\\ \\/ \\ \\___  \\
                         \\ \\_\\ \\_\\  \\ \\__|    \\ \\____-     \\ \\_\\    \\ \\_\\ \\_\\  \\ \\_____\\ /\\_____\\  \\ \\_____\\  \\ \\_____\\    \\ \\_\\  \\/\\_____\\
                          \\/_/ /_/   \\/_/      \\/____/      \\/_/     \\/_/ /_/   \\/_____/ \\/_____/   \\/_____/   \\/_____/     \\/_/   \\/_____/
                """;

        figlet += "\n" + this.getPluginInfos(true);

        return figlet;
    }

    public WhitelistJe() {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
    }

    public String getPluginInfos(boolean toConsole) {

        final String devName = toConsole ? "@xXx-RaFuX#1345" : "<@272924120142970892>";

        return "Name: `" + this.getName() + "`\n" +
        "Version: `" + this.getVersion() + "`\n" +
        "Developped by: " + devName + "\n\n";
    }

    public String getVersion() {
        return this.configManager.get("pluginVersion", "2022.2");
    }

    @Override
    public void onEnable() {
        ITransaction transaction = Sentry.startTransaction("onEnable", "configurePlugin");

        try {
            instance = this;
            configManager = new ConfigManager();
            sentryService = new SentryService(this);
            transaction = sentryService.createTx("onEnable", "configurePlugin");

            daoManager = new DaoManager(configManager, this);
            discordManager = new DiscordManager(this);
            guildManager = new GuildManager(discordManager.getGuild(), this);
            bukkitManager = new BukkitManager(this);
    
            updateAllPlayers();
            updateAllowedPlayers();

            Logger.getLogger("WhiteList-Je").info(this.getfiglet());
            guildManager.getAdminChannel().sendMessage("**Le plugin `" + this.getName() + "` est loader**\n\n" + getPluginInfos(false)).queue();
        } catch (Exception e) {
            try {
                transaction.setThrowable(e);
                transaction.setStatus(SpanStatus.INTERNAL_ERROR);
                SentryService.captureEx(e);
                guildManager.getAdminChannel().sendMessage("**`OUPS`, Le plugin `" + this.getName() + "`" +
                 " a rencontré des `problèmes` à l'initialisation**\n" + 
                 "**Regarder les fichers de `log` !!!!**\n\n" + 
                 getPluginInfos(false)).queue();

            } catch (Exception err) {
                transaction.setThrowable(err);
                transaction.setStatus(SpanStatus.INTERNAL_ERROR);
                SentryService.captureEx(err);
            }
        }

        transaction.setStatus(SpanStatus.OK);
        transaction.finish();
    }

    @Override
    public void onDisable() {
        guildManager.getAdminChannel().sendMessage("**Le plugin `" + this.getName() + "` est unloader**\n\n" + getPluginInfos(false)).queue();
    }

    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }

    public DaoManager getDaoManager() {
        return this.daoManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public GuildManager getGuildManager() {
        return this.guildManager;
    }

    public BukkitManager getBukkitManager() {
        return this.bukkitManager;
    }

    public SentryService getSentryService() {
        return this.sentryService;
    }

    public JSONArray updateAllPlayers() {
        ISpan process = getSentryService().findWithuniqueName("onEnable")
        .startChild("updateAllPlayers");
        
        this.players = new JSONArray();

        JSONArray java = DaoManager.getJavaDataDao().findAll();
        if(java != null)
            this.players.putAll(java);

        JSONArray bedrocks = DaoManager.getBedrockDataDao().findAll();
        if(bedrocks != null)
            this.players.putAll(bedrocks);

        updateAllowedPlayers();

        process.setStatus(SpanStatus.OK);
        process.finish();
        return this.players;
    }

    public JSONArray updateAllowedPlayers() {
        ISpan process = getSentryService().findWithuniqueName("onEnable")
        .startChild("updateAllowedPlayers");

        this.playersAllowed = new JSONArray();

        JSONArray java = DaoManager.getJavaDataDao().findAllowed();
        if(java != null)
            this.players.putAll(java);

        JSONArray bedrocks = DaoManager.getBedrockDataDao().findAllowed();
        if(bedrocks != null)
            this.players.putAll(bedrocks);

        process.setStatus(SpanStatus.OK);
        process.finish();
        return this.playersAllowed;
    }

    public Integer getPlayerId(UUID uuid) {
        Integer userId = -1;
        this.updateAllPlayers();

        if(this.players == null) {
            return -1;
        }

        try {
            for (Object object : this.players) {
                final JSONObject player = (JSONObject) object;
                final String uuidReccord = player.optString("uuid");
    
                if (uuidReccord.equals(uuid.toString())) {
                    userId = player.getInt("id");
                    break;
                }
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return userId;
    }

    public Integer playerIsAllowed(UUID uuid) {
        Integer allowedUserId = -1;
        this.updateAllowedPlayers();

        if(this.playersAllowed == null) {
            return -1;
        }

        try {
            for (Object object : this.playersAllowed) {
                final JSONObject player = (JSONObject) object;
                final String uuidReccord = player.optString("uuid");
    
                if (uuidReccord.equals(uuid.toString())) {
                    allowedUserId = player.getInt("id");
                    break;
                }
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return allowedUserId;
    }

    public Integer playerIsConfirmed(UUID uuid) {
        Integer allowedUserId = -1;
        this.updateAllowedPlayers();

        if(this.players == null) {
            return -1;
        }

        try {
            for (Object object : this.players) {
                final JSONObject player = (JSONObject) object;
                final String uuidReccord = player.optString("uuid");
    
                if (uuidReccord.equals(uuid.toString())) {
                    final boolean confirmed = player.optBoolean("confirmed");
                    allowedUserId = confirmed ? player.getInt("id") : -1;
                    break;
                }
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return allowedUserId;
    }
}
