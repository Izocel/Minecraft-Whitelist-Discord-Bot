package helpers;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class EconomyManager {
    private static WhitelistDmc plugin;
    private static Server server;
    private static Economy vaultEconomy;
    private static Logger logger;

    public EconomyManager(WhitelistDmc plugin) {
        logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("EconomyManager");

        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            process.setStatus(SpanStatus.ABORTED);
            process.finish();
            return;
        }

        EconomyManager.plugin = plugin;
        EconomyManager.server = plugin.getServer();
        RegisteredServiceProvider<Economy> vault = server.getServicesManager().getRegistration(Economy.class);
        vaultEconomy = vault.getProvider();

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public static EconomyResponse depositPlayer(Player player, double amount) {
        logger.info(String.format("Vault --> Depositing: %1$d | Player: %2$s", amount, player.getName()));
        return vaultEconomy.depositPlayer(player, amount);
    }

    public static EconomyResponse withdrawPlayer(Player player, double amount) {
        logger.info(String.format("Vault --> Withdrawing: %1$d | Player: %2$s", amount, player.getName()));
        return vaultEconomy.withdrawPlayer(player, amount);
    }

    public static double getPlayerBalance(Player player) {
        final double balance = vaultEconomy.getBalance(player);
        logger.info(String.format("Vault --> Player: %2$s | Balance: %1$d", balance, player.getName()));
        return balance;
    }

}
