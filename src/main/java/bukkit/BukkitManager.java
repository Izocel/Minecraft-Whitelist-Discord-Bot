package bukkit;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import commands.bukkit.HyperLinksCmd;
import dao.DaoManager;
import events.bukkit.OnPlayerJoin;
import events.bukkit.OnPlayerLogin;
import events.bukkit.OnServerLoad;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import locals.LocalManager;
import main.WhitelistDmcNode;
import models.BedrockData;
import models.JavaData;
import services.api.PlayerDbApi;
import services.sentry.SentryService;

public class BukkitManager {
    private WhitelistDmcNode plugin;
    private Logger logger;

    public BukkitManager(WhitelistDmcNode plugin) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("BukkitManager");

        this.plugin = plugin;
        this.registerEvents(plugin);
        this.registerCommands(plugin);

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    public String getServerInfoString() {
        final String ip = Bukkit.getServer().getIp();
        final String name = Bukkit.getServer().getName();
        final String version = Bukkit.getServer().getVersion();
        final String description = Bukkit.getServer().getMotd();
        final GameMode gameMode = Bukkit.getServer().getDefaultGameMode();
        final boolean onlineMode = Bukkit.getServer().getOnlineMode();
        final boolean forceWhitelist = Bukkit.getServer().isWhitelistEnforced();

        final String portJ = this.plugin.getConfigManager().get("portJava", "???");
        final String portB = this.plugin.getConfigManager().get("portBedrock", "???");
        final String javaIp = this.plugin.getConfigManager().get("javaIp", "???");
        final String bedrockIp = this.plugin.getConfigManager().get("bedrockIp", "???");

        final LocalManager LOCAL = WhitelistDmcNode.LOCALES;
        final String nameField = LOCAL.translate("SERVER");
        final String portField = LOCAL.translate("PORT");
        final String versionField = LOCAL.translate("VERSION");
        final String onlineField = LOCAL.translate("ONLINE_MODE");
        final String whitelistField = LOCAL.translate("WHITELISTED");
        final String defaultGModefield = LOCAL.translate("DEFAULT_GAME_MODE");
        final String descField = LOCAL.translate("DESCRIPTION");
        final String WORD_YES = LOCAL.translate("WORD_YES");
        final String WORD_NO = LOCAL.translate("WORD_NO");

        final String onlineStr = onlineMode ? "`" + WORD_YES + "`" : "`" + WORD_NO + "`";
        final String fwStr = forceWhitelist ? "`" + WORD_YES + "`" : "`" + WORD_NO + "`";

        StringBuilder sb = new StringBuilder();
        sb.append("\n\t" + nameField + " : `" + name + "`");
        sb.append("\n\tJava Ip: `" + javaIp + "`");
        sb.append("\n\tBedrock Ip: `" + bedrockIp + "`");
        sb.append("\n\t" + portField + " Java: `" + portJ + "`");
        sb.append("\n\t" + portField + " Bedrock: `" + portB + "`");
        sb.append("\n\t" + versionField + " : `" + version + "`");
        sb.append("\n\t" + onlineField + " : `" + onlineStr + "`");
        sb.append("\n\t" + whitelistField + " : `" + fwStr + "`");
        sb.append("\n\t" + defaultGModefield + " : `" + gameMode.name() + "`");
        sb.append("\n\t" + descField + " : `" + description + "`");

        return sb.toString();
    }

    private void registerEvents(WhitelistDmcNode plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("BukkitManager.registerEvents");
        try {
            Bukkit.getPluginManager().registerEvents(new OnPlayerLogin(plugin), plugin);
            Bukkit.getPluginManager().registerEvents(new OnPlayerJoin(plugin), plugin);
            Bukkit.getPluginManager().registerEvents(new OnServerLoad(plugin), plugin);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    private void registerCommands(WhitelistDmcNode plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("BukkitManager.registerCommands");

        try {
            this.plugin.getCommand("w-hyperlinks").setExecutor(new HyperLinksCmd(this.plugin, "w-hyperlinks"));
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public boolean kickPlayer(String uuid, String reason) {
        try {
            final UUID UUID = java.util.UUID.fromString(uuid);
            OfflinePlayer player = getServer().getOfflinePlayer(UUID);
            Player onlinePlayer = getServer().getPlayer(UUID);
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        if (onlinePlayer != null) {
                            onlinePlayer.kickPlayer(reason);
                        }
                        player.setWhitelisted(false);
                    } catch (Exception e) {
                        SentryService.captureEx(e);
                    }
                }
            }.runTask(plugin);

            return getServer().getPlayer(UUID) == null;

        } catch (Exception e) {
            SentryService.captureEx(e);
            return false;
        }
    }

    public boolean setPlayerAsAllowed(Integer userId, String msgId, boolean allowed, String moderatorId, String uuid,
            boolean confirmed, String pseudo) {
        final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
        final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);

        if (javaData != null && javaData.isAllowed()) {
            javaData.setAsAllowed(msgId, allowed, moderatorId);
            return true;
        }

        else if (bedData != null && bedData.isAllowed()) {
            bedData.setAsAllowed(msgId, allowed, moderatorId);
            return true;
        }

        else {
            final String foundJava = PlayerDbApi.getMinecraftUUID(pseudo);
            final String foundXbox = PlayerDbApi.getXboxUUID(pseudo);

            if (foundJava != null && foundJava.equals(uuid)) {
                JavaData data = new JavaData();
                data.setMcName(pseudo);
                if (allowed)
                    data.setAcceptedBy(moderatorId);
                else
                    data.setRevokedBy(moderatorId);

                data.setUUID(uuid);
                data.setUserId(userId);
                data.setAsAllowed(msgId, allowed, moderatorId);
                data.setAsConfirmed(confirmed);
                data.save(DaoManager.getJavaDataDao());
                return true;
            }

            else if (foundXbox != null && foundXbox.equals(uuid)) {
                BedrockData data = new BedrockData();
                data.setMcName(pseudo);
                if (allowed)
                    data.setAcceptedBy(moderatorId);
                else
                    data.setRevokedBy(moderatorId);

                data.setUUID(uuid);
                data.setUserId(userId);
                data.setAsAllowed(msgId, allowed, moderatorId);
                data.setAsConfirmed(confirmed);
                data.save(DaoManager.getBedrockDataDao());
                return true;
            }
        }

        logger.warning("Could not find any allowed player with UUID: " + uuid);
        return false;
    }

    public void setPlayerAsConfirmed(String uuid) {
        try {
            final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
            final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);

            if (javaData != null && javaData.isAllowed()) {
                javaData.setAsConfirmed(true);
                javaData.save(DaoManager.getJavaDataDao());
            }

            else if (bedData != null && bedData.isAllowed()) {
                bedData.setAsConfirmed(true);
                bedData.save(DaoManager.getBedrockDataDao());
            }

            else {
                logger.warning("Could not find any allowed player with UUID: " + uuid);
            }

        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public Object getPlayerData(String uuid) {
        final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
        final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);
        return javaData != null ? javaData : bedData != null ? bedData : null;
    }

    public String getAvatarUrl(String uuid, String pxSize) {
        try {
            final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
            final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);

            String type = null;
            if (javaData != null) {
                type = "Java";
            }

            else if (bedData != null) {
                type = "Bedrock";
            }

            return type == "Bedrock"
                    ? "https://api.tydiumcraft.net/v1/players/skin?uuid=" + uuid + "&size=" + pxSize
                    : type == "Java"
                            ? "https://mc-heads.net/body/" + uuid + "/" + pxSize
                            : "https://mc-heads.net/body/08673fd1-1196-43be-bc8b-e93fd2dee36d/" + pxSize;

        } catch (Exception e) {
            SentryService.captureEx(e);
            return "https://mc-heads.net/body/08673fd1-1196-43be-bc8b-e93fd2dee36d/";
        }
    }
}