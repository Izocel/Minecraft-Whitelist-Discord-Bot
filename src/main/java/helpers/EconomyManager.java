package helpers;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

import main.WhitelistDmc;
import net.milkbowl.vault.economy.Economy;

public class EconomyManager {
    Economy coreEcon;

    public EconomyManager(WhitelistDmc plugin) {
        final Server srv = plugin.getServer();
        if (srv.getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Economy> core = srv.getServicesManager().getRegistration(Economy.class);
        this.coreEcon = core.getProvider();
    }

}
