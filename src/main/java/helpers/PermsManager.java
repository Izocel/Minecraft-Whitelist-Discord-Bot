package helpers;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import services.sentry.SentryService;

public class PermsManager {
    private static WhitelistDmc plugin;
    private static Server server;
    private static LuckPerms luckPerms;
    private static Logger logger;
    private static @NonNull UserManager userManager;

    public PermsManager(WhitelistDmc plugin) {
        logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("PermsManager");

        luckPerms = LuckPermsProvider.get();
        userManager = luckPerms.getUserManager();

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public static boolean hasPermission(User user, String permission) {
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    public static String hasPermissions(Player player, Collection<String> permissions) {
        for (String perm : permissions) {
            if (player.hasPermission(perm)) {
                return perm;
            }
        }
        return null;
    }

    // example "group.xyz"
    public static void addPermission(UUID userUuid, String permission) {
        try {
            User user = userManager.getUser(userUuid);
            user.data().add(Node.builder(permission).build());
            userManager.saveUser(user);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public static void addToGroup(UUID userUuid, String group) {
        try {
            User user = userManager.getUser(userUuid);
            user.data().add(Node.builder("group." + group).build());
            userManager.saveUser(user);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public static void removeFromGroup(UUID userUuid, String group) {
        try {
            User user = userManager.getUser(userUuid);
            user.data().remove(Node.builder("group." + group).build());
            userManager.saveUser(user);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public static void addToPluginGroup(UUID userUuid) {
        try {
            addToGroup(userUuid, "wdmc");
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public static void removeFromPluginGroup(UUID userUuid) {
        try {
            removeFromGroup(userUuid, "wdmc");
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

}
