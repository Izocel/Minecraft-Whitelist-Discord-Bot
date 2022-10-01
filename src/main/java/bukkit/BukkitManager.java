package bukkit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;

import commands.bukkit.ConfirmLinkCmd;
import events.bukkit.OnPlayerJoin;
import events.bukkit.OnPlayerLoggin;
import events.bukkit.OnServerLoad;
import main.WhitelistJe;

public class BukkitManager {
    private WhitelistJe main;
    private Server server;

    public BukkitManager(WhitelistJe main) {
        this.main = main;
        this.registerEvents(main);
        this.registerCommands(main);
        this.server = Bukkit.getServer();
    }

    public Server getServer() {
        return this.server;
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

        final String protJ = this.main.getConfigManager().get("portJava", "???");
        final String portB = this.main.getConfigManager().get("portBedrock", "???");
        final String paperMcIp = this.main.getConfigManager().get("paperMcIp", "???");

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

    private void registerEvents(WhitelistJe main) {
        try {
            Bukkit.getPluginManager().registerEvents(new OnPlayerLoggin(main), main);
            Bukkit.getPluginManager().registerEvents(new OnPlayerJoin(main), main);
            Bukkit.getPluginManager().registerEvents(new OnServerLoad(main), main);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerCommands(WhitelistJe main) {
        final String linkCmd = this.main.getConfigManager().get("confirmLinkCmdName", "wje-link");

        try {
            this.main.getCommand(linkCmd).setExecutor(new ConfirmLinkCmd(this.main, linkCmd));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
