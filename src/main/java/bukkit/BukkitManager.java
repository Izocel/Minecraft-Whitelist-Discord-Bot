package bukkit;

import org.bukkit.Bukkit;

import events.bukkit.OnPlayerLoggin;
import main.WhitelistJe;

public class BukkitManager {

    public BukkitManager(WhitelistJe main) {
        this.registerEvents(main);
    }

    private void registerEvents(WhitelistJe main) {
        Bukkit.getPluginManager().registerEvents(new OnPlayerLoggin(main), main);
    }

}
