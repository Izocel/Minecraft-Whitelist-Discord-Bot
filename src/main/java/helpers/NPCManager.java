package helpers;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Server;

import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.SimpleNPCDataStore;
import net.citizensnpcs.api.util.NBTStorage;
import services.sentry.SentryService;

public class NPCManager {
    private static WhitelistDmc plugin;
    private static Server server;
    private static Logger logger;
    public static NPCRegistry apiRegistry;
    public static NPCRegistry pluginRegistry;

    public NPCManager(WhitelistDmc plugin) {
        logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable").startChild("NPCManager");
        new File(FileHelper.writeToPluginDir("npc/configs.yml", "", false));

        NPCManager.plugin = plugin;
        NPCManager.server = plugin.getServer();
        NPCManager.apiRegistry = CitizensAPI.getNPCRegistry();
        pluginRegistry = CitizensAPI.getNamedNPCRegistry(plugin.getName());

        if (pluginRegistry == null) {
            pluginRegistry = createNBTStorage(plugin.getName(), "npc/citizens.dat");
        }

        logger.info(String.format("Found citizens NBT Storage: name: %s, path: %s", plugin.getName(), "npc/citizens.dat"));
        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    private static NPCRegistry createNBTStorage(String name, String filepath) {
        final File newFile = new File(FileHelper.writeToPluginDir(filepath, "", false));
        final NBTStorage nbtStorage = new NBTStorage(newFile);
        final NPCDataStore nbtDataStore = SimpleNPCDataStore.create(nbtStorage);
        final NPCRegistry registry = CitizensAPI.createNamedNPCRegistry(name, nbtDataStore);
        registry.saveToStore();

        logger.warning(String.format("Created citizens NBT Storage: name: %s, path: %s", name, filepath));
        return registry;
    }

}
