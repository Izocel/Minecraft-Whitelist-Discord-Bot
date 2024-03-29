package main;

import java.net.http.WebSocket.Listener;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import bukkit.BukkitManager;
import configs.ConfigManager;
import dao.DaoManager;
import db.Migrator;
import discord.DiscordManager;
import discord.GuildManager;
import helpers.EconomyManager;
import helpers.NotificationManager;
import helpers.StatsManager;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import locals.LocalManager;
import models.BedrockData;
import models.JavaData;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import services.sentry.SentryService;

public final class WhitelistDmc extends JavaPlugin implements Listener {

    private Logger logger;
    private BukkitManager bukkitManager;
    private GuildManager guildManager;
    private ConfigManager configManager;
    private DiscordManager discordManager;
    private DaoManager daoManager;
    private NotificationManager notificationManager;
    private StatsManager statsManager;
    private EconomyManager economyManager;

    private JSONArray players = new JSONArray();
    private JSONArray playersAllowed = new JSONArray();
    private SentryService sentryService;
    private Migrator migrator;

    public static LocalManager LOCALES;

    public final String getfiglet() {
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

    public WhitelistDmc() {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        saveDefaultConfig();
    }

    public final String getPluginInfos(boolean toConsole) {
        final StringBuilder sb = new StringBuilder();
        final String devName = toConsole
                ? "@xXx-RaFuX#1345"
                : "<@272924120142970892>";

        sb.append(String.format(
                LOCALES.translate("PLUGIN_NAME"),
                this.getName() + "\n"));
        sb.append(String.format(
                LOCALES.translate("PLUGIN_VERSION"),
                this.getVersion() + "\n"));
        sb.append(String.format(
                LOCALES.translate("PLUGIN_DEVBY"),
                devName + "\n\n"));

        return sb.toString();
    }

    public String getVersion() {
        return this.configManager.get("pluginVersion");
    }

    @Override
    public final void onEnable() {
        ITransaction transaction = Sentry.startTransaction("onEnable", "configurePlugin");
        final StringBuilder sb = new StringBuilder();

        try {
            this.configManager = new ConfigManager();
            logger.info("LOADED: ConfigManager");

            LOCALES = new LocalManager(this);
            logger.info("LOADED: LocalManager");

            sentryService = new SentryService(this);
            transaction = sentryService.createTx("onEnable", "configurePlugin");
            logger.info("LOADED: SentryService");

            daoManager = new DaoManager(this);
            logger.info("LOADED: DaoManager");

            // migrator = new Migrator(this);
            // logger.info("LOADED: Migrator");

            discordManager = new DiscordManager(this);
            logger.info("LOADED: DiscordManager");

            guildManager = new GuildManager(this);
            logger.info("LOADED: GuildManager");

            bukkitManager = new BukkitManager(this);
            logger.info("LOADED: BukkitManager");

            notificationManager = new NotificationManager(this);
            logger.info("LOADED: NotificationManager");

            statsManager = new StatsManager(this);
            logger.info("LOADED: StatsManager");

            economyManager = new EconomyManager(this);
            logger.info("LOADED: EconomyManager *vaultApi");

            updateAllPlayers();
            logger.info("UPDATED PLAYERS CACHE");

            sb.append(String.format(
                    LOCALES.translate("PLUGIN_HELLO"),
                    this.getName(), LOCALES.translate("IS_ACTIVE")));

            sb.append(getPluginInfos(false));
            guildManager.getBotLogChannel()
                    .sendMessage(sb.toString()).submit(true);

            logger.info(this.getfiglet());

            WhitelistSpark.main(this, this.configManager);

        } catch (Exception e) {
            try {
                transaction.setThrowable(e);
                transaction.setStatus(SpanStatus.INTERNAL_ERROR);
                SentryService.captureEx(e);

                sb.delete(0, sb.length());

                sb.append(String.format(
                        LOCALES.translate("PLUGIN_HELLO_ERROR"),
                        this.getName(), LOCALES.translate("IS_INACTIVE")));

                sb.append(LOCALES.translate("CHECK_LOGS") + "\n\n");
                sb.append(getPluginInfos(false));

                guildManager.getBotLogChannel().sendMessage(sb.toString()).submit(true);

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
    public final void onDisable() {
        final StringBuilder sb = new StringBuilder();

        sb.append(String.format(
                LOCALES.translate("PLUGIN_GOODBYE"),
                this.getName(), LOCALES.translate("IS_INACTIVE")));
        sb.append(getPluginInfos(false));

        guildManager.getBotLogChannel()
                .sendMessage(sb.toString()).submit(true);
    }

    public final DiscordManager getDiscordManager() {
        return this.discordManager;
    }

    public final DaoManager getDaoManager() {
        return this.daoManager;
    }

    public final ConfigManager getConfigManager() {
        return this.configManager;
    }

    public final GuildManager getGuildManager() {
        return this.guildManager;
    }

    public final BukkitManager getBukkitManager() {
        return this.bukkitManager;
    }

    public final SentryService getSentryService() {
        return this.sentryService;
    }

    public final StatsManager getStatsManager() {
        return this.statsManager;
    }

    public final EconomyManager getEconomyManager() {
        return this.economyManager;
    }

    public final JSONArray updateAllPlayers() {
        ISpan process = getSentryService().findWithuniqueName("onEnable")
                .startChild("updateAllPlayers");

        this.players = new JSONArray();

        JSONArray java = DaoManager.getJavaDataDao().findAll();
        if (java != null)
            this.players.putAll(java);

        JSONArray bedrocks = DaoManager.getBedrockDataDao().findAll();
        if (bedrocks != null)
            this.players.putAll(bedrocks);

        updateAllowedPlayers();

        process.setStatus(SpanStatus.OK);
        process.finish();
        return this.players;
    }

    public final JSONArray updateAllowedPlayers() {
        ISpan process = getSentryService().findWithuniqueName("onEnable")
                .startChild("updateAllowedPlayers");

        this.playersAllowed = new JSONArray();

        JSONArray java = DaoManager.getJavaDataDao().findAllowed();
        if (java != null)
            this.playersAllowed.putAll(java);

        JSONArray bedrocks = DaoManager.getBedrockDataDao().findAllowed();
        if (bedrocks != null)
            this.playersAllowed.putAll(bedrocks);

        process.setStatus(SpanStatus.OK);
        process.finish();
        return this.playersAllowed;
    }

    public final Integer getPlayerId(UUID uuid) {
        Integer userId = -1;
        this.updateAllPlayers();

        if (this.players == null) {
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

    public final Integer playerIsAllowed(UUID uuid) {
        Integer allowedUserId = -1;
        this.updateAllowedPlayers();

        if (this.playersAllowed == null) {
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

    public final Integer playerIsConfirmed(UUID uuid) {
        Integer allowedUserId = -1;
        this.updateAllowedPlayers();

        if (this.playersAllowed == null) {
            return -1;
        }

        try {
            for (Object object : this.playersAllowed) {
                final JSONObject player = (JSONObject) object;
                final String uuidReccord = player.getString("uuid");

                if (uuidReccord.equals(uuid.toString())) {
                    final boolean confirmed = player.optString("confirmed").equals("1");
                    allowedUserId = confirmed ? player.getInt("id") : -1;
                    break;
                }
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return allowedUserId;
    }

    public final JSONObject getMinecraftDataJson(UUID uuid) {
        JSONObject data = null;
        this.updateAllPlayers();

        if (this.players == null) {
            return null;
        }

        try {
            for (Object object : this.players) {
                final JSONObject playerData = (JSONObject) object;
                final String uuidReccord = playerData.getString("uuid");

                if (uuidReccord.equals(uuid.toString())) {
                    data = playerData;
                    break;
                }
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return data;
    }

    public final boolean removePlayerRegistry(UUID UUID, String reason) {
        try {
            if (UUID == null) {
                return false;
            }

            final String uuid = UUID.toString();
            final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
            final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);

            if (javaData != null) {
                javaData.delete(DaoManager.getJavaDataDao());
            }

            else if (bedData != null) {
                bedData.delete(DaoManager.getBedrockDataDao());
            }

            return bukkitManager.kickPlayer(uuid, reason);

        } catch (Exception e) {
            SentryService.captureEx(e);
            return false;
        }
    }

    // You can test some features here in 'dev' mode only.
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (isProduction()) {
            return false;
        }

        try {
            final Player player = (Player) sender;
            final Server server = sender.getServer();
            final World world = player.getWorld();
            final String cmdName = command.getLabel().toString();
            final Location playerLoc = player.getLocation();

            if (cmdName.equals("w-test")) {
                
            }

            else {
                logger.warning("---- Job was not found ----");
                return false;
            }

        } catch (Exception e) {
            logger.warning("Job error !!!");
            SentryService.captureEx(e);
        }

        return false;
    }

    public boolean isProduction() {
        return configManager.isProduction();
    }
}
