package bukkit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import commands.bukkit.ConfirmLinkCmd;
import dao.UsersDao;
import events.bukkit.OnPlayerJoin;
import events.bukkit.OnPlayerLoggin;
import events.bukkit.OnServerLoad;
import main.WhitelistJe;
import models.User;

public class BukkitManager {
    private WhitelistJe plugin;

    public BukkitManager(WhitelistJe plugin) {
        this.plugin = plugin;
        this.registerEvents(plugin);
        this.registerCommands(plugin);
    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    public String getServerInfoString() {
        final String ip = Bukkit.getServer().getIp();
        final String version = Bukkit.getServer().getVersion();
        final String description = Bukkit.getServer().getMotd();
        final GameMode gameMode = Bukkit.getServer().getDefaultGameMode();
        final boolean onlineMode = Bukkit.getServer().getOnlineMode();
        final boolean usingWhiteList = Bukkit.getServer().hasWhitelist();
        final boolean forceWhitelist = Bukkit.getServer().isWhitelistEnforced();
        final String onlineStr = onlineMode ? "`true`" : "`false`";
        final String fwStr = forceWhitelist ? "`true`" : "`false`";

        final String protJ = this.plugin.getConfigManager().get("portJava", "???");
        final String portB = this.plugin.getConfigManager().get("portBedrock", "???");
        final String paperMcIp = this.plugin.getConfigManager().get("paperMcIp", "???");

        StringBuilder sb = new StringBuilder();
        sb.append("\n\tIp: `" + paperMcIp + "`");
        sb.append("\n\tPort Java: `" + protJ + "`");
        sb.append("\n\tPort Bedrock: `" + portB + "`");
        sb.append("\n\tVersion: `" + version + "`");
        sb.append("\n\tOnline Mode: `" + onlineStr + "`");
        sb.append("\n\tWhitelisted: `" + fwStr + "`");
        sb.append("\n\tDefault Gamemode: `" + gameMode.name() + "`");
        sb.append("\n\tDescription: `" + description + "`");

        return sb.toString();
    }

    private void registerEvents(WhitelistJe plugin) {
        try {
            Bukkit.getPluginManager().registerEvents(new OnPlayerLoggin(plugin), plugin);
            Bukkit.getPluginManager().registerEvents(new OnPlayerJoin(plugin), plugin);
            Bukkit.getPluginManager().registerEvents(new OnServerLoad(plugin), plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerCommands(WhitelistJe plugin) {
        final String linkCmd = this.plugin.getConfigManager().get("confirmLinkCmdName", "wje-link");

        try {
            this.plugin.getCommand(linkCmd).setExecutor(new ConfirmLinkCmd(this.plugin, linkCmd));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sanitizeAnKickPlayer(UUID mcUuid) {
        try {

            OfflinePlayer player = Bukkit.getOfflinePlayer(mcUuid);
            if (player != null)
                player.setWhitelisted(false);

            final UsersDao dao = plugin.getDaoManager().getUsersDao();
            final User user = dao.findByMcUUID(mcUuid.toString());
            if (user != null)
                user.delete(dao);

            Player onlinePlayer = getServer().getPlayer(mcUuid);
            if (onlinePlayer != null) {
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    public void run() {
                        if (onlinePlayer != null) {
                            onlinePlayer.kickPlayer(
                                    "§lVous ne faites plus partie de l'aventure...\n§c§lCe compte n'est pas confirmé.");
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
