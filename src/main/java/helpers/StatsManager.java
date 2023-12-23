package helpers;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import services.sentry.SentryService;

public class StatsManager {
    private static WhitelistDmc plugin;
    private static Server server;
    private static Logger logger;

    public StatsManager(WhitelistDmc plugin) {
        logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("StatsManager");

        StatsManager.plugin = plugin;
        StatsManager.server = plugin.getServer();

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public static void giveXp(Player player, int amount) {
        player.giveExp(amount);
        logger.info(String.format("XP was given: %1$s | Player: %2$s", amount, player.getName()));

    }

    public static boolean dropExpOrbs(Location loc, int orbsCount, int orbsXp) {
        if (orbsCount < 1 || orbsXp < 1) {
            return true;
        }

        try {
            final BukkitTask task = new BukkitRunnable() {
                int i = -1;

                @Override
                public void run() {
                    try {
                        if (i++ >= orbsCount) {
                            cancel();
                            return;
                        }

                        ExperienceOrb orb = (ExperienceOrb) loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
                        orb.setVelocity(new Vector(0D, 0D, 0D));
                        orb.setCustomNameVisible(true);
                        orb.setCustomName("XPOrb");
                        orb.addScoreboardTag("XPOrbs");
                        orb.setExperience(orbsXp);
                    } catch (Exception e) {
                        SentryService.captureEx(e);
                    }

                }
            }.runTaskTimer(plugin, 0L, 10L);

            return task.getTaskId() > 0;
        } catch (Exception e) {
            SentryService.captureEx(e);
            return false;
        }

    }

}
