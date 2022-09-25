package bukkit;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

import events.bukkit.OnPlayerLoggin;
import events.bukkit.OnServerLoad;
import main.WhitelistJe;

public class BukkitManager {
    private WhitelistJe main;

    public BukkitManager(WhitelistJe main) {
        this.main = main;
        this.registerEvents(main);
    }

    public String getServerInfoString() {
        final String ip = Bukkit.getServer().getIp();
        final String version = Bukkit.getServer().getVersion();
        final boolean onlineMode = Bukkit.getServer().getOnlineMode();
        final boolean usingWhiteList = Bukkit.getServer().hasWhitelist();
        final boolean forceWhitelist = Bukkit.getServer().isWhitelistEnforced();
        final String onlineStr = onlineMode ? "`true`" : "`false`";
        final String fwStr = forceWhitelist ? "`true`" : "`false`";

        final String protJ = this.main.getConfigManager().get("portJava", "???");
        final String portB = this.main.getConfigManager().get("portBedrock", "???");
        final String paperMcIp = this.main.getConfigManager().get("paperMcIp", "???");

        Logger.getLogger("test").info(ip);

        StringBuilder sb = new StringBuilder();
        sb.append("**Détails:**");
        sb.append("\n\t**Ip: **" + paperMcIp);
        sb.append("\n\t**Port Java: **" + protJ);
        sb.append("\n\t**Port Bedrock: **" + portB);
        sb.append("\n\t**Version: **" + version);
        sb.append("\n\t**Online Mode: **" + onlineStr);
        sb.append("\n\t**Whitelisted: **" + fwStr);

        return sb.toString();
    }

    private void registerEvents(WhitelistJe main) {
        Bukkit.getPluginManager().registerEvents(new OnPlayerLoggin(main), main);
        Bukkit.getPluginManager().registerEvents(new OnServerLoad(main), main);
    }

}
