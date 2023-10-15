package helpers;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import main.WhitelistDmc;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class EconomyManager {
    private Economy coreEcon;
    private WhitelistDmc plugin;
    private Server server;

    public EconomyManager(WhitelistDmc plugin) {
        final Server srv = plugin.getServer();
        if (srv.getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Economy> core = srv.getServicesManager().getRegistration(Economy.class);
        this.coreEcon = core.getProvider();
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    public EconomyResponse depositPlayer(Player player, double amount) {
        return coreEcon.depositPlayer(player, amount);
    }

    public EconomyResponse withdrawPlayer(Player player, double amount) {
        return coreEcon.withdrawPlayer(player, amount);
    }

    public double getPlayerBalance(Player player) {
        return coreEcon.getBalance(player);
    }

}
