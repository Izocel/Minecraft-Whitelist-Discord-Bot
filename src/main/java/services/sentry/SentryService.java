package services.sentry;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import services.sentry.SentryService;
import io.sentry.Hint;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import main.WhitelistDmcNode;

public class SentryService {
  private static Logger logger;
  private static User user;
  private static WhitelistDmcNode plugin;
  private static String envType;

  HashMap<Integer, ITransaction> pendingTransactions = new HashMap<Integer, ITransaction>();

  public SentryService(WhitelistDmcNode plugin) {
    SentryService.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
    SentryService.plugin = plugin;
    SentryService.initUser();

    SentryService.envType = plugin.getConfigManager().get("misc.envType", "production");
    final String release = plugin.getConfigManager().get("pluginVersion", "?release?") + "@" + envType;

    Sentry.setLevel(SentryLevel.DEBUG);
    Sentry.init(options -> {
      options.setEnableAutoSessionTracking(true);
      options.setDsn(
          "https://80475ddc2c8d457ea52a1fbd0a8e22bb@o4503922553847808.ingest.sentry.io/4503922559156224");

      options.setDiagnosticLevel(SentryLevel.DEBUG);
      options.setTracesSampleRate(0.66);
      options.setRelease(release);
      options.setEnvironment(envType);
    });
  }

  @Override
  protected void finalize() throws Throwable {
    finalyzeAllTransaction();
  }

  private void finalyzeAllTransaction() {
    try {
      if (pendingTransactions != null && getTxSize() > 0)
        pendingTransactions.forEach((key, tx) -> {
          try {
            tx.setData("finalStatus", "with destruction");
            tx.finish();
          } catch (Exception e) {
            captureEx(e);
          }
        });
    } catch (Exception e) {
      captureEx(e);
    }

  }

  public ITransaction createTx(String name, String operation) {
    final Integer size = getTxSize();
    ITransaction tx = Sentry.startTransaction(name, operation);
    tx.setName(name);
    tx.setOperation(operation);
    tx.setData("name", name);
    tx.setData("operation", operation);

    pendingTransactions.put(
        size, tx);

    return pendingTransactions.get(size);
  }

  public ITransaction findWithuniqueName(String txName) {
    if (pendingTransactions == null || getTxSize() < 1) {
      return null;
    }

    for (int i = 0; i < getTxSize(); i++) {
      if (getTransactions().get(i).getData("name").equals(txName)) {
        return getTransactions().get(i);
      }
    }

    return Sentry.startTransaction(txName, txName);
  }

  public ITransaction putTx(ITransaction tx) {
    final Integer size = getTxSize();
    pendingTransactions.put(size, tx);

    return pendingTransactions.get(size);
  }

  public ITransaction findTx(Integer keyPos) {
    return pendingTransactions.get(keyPos);
  }

  public Integer getTxSize() {
    return pendingTransactions != null ? pendingTransactions.size() : null;
  }

  public HashMap<Integer, ITransaction> getTransactions() {
    if (this.pendingTransactions == null) {
      this.pendingTransactions = new HashMap<Integer, ITransaction>();
    }
    return this.pendingTransactions;
  }

  public static SentryId captureEx(Throwable error) {
    Logger.getLogger("WDMC-Sentry").warning("WARNING:");
    error.fillInStackTrace();
    error.printStackTrace();

    if (!envType.equals("production")) {
      return null;
    }

    Hint hint = new Hint();
    hint.set("user", userToString());
    return Sentry.captureException(error, hint);
  }

  public SentryId captureEvent(SentryEvent event) {
    return envType.equals("production")
        ? Sentry.captureEvent(event)
        : null;
  }

  public void changeUser(@Nullable User user) {
    Sentry.configureScope(scope -> {
      scope.setUser(user);
    });
  }

  private static String userToString() {
    return """
        User:
        \t Mc-Server: """ + user.getUsername() + """
        \n\t DiscordServer: """ + user.getId() + """
        \n\t DiscordOwnerId: """ + user.getName() + """
        \n\t Contact-Email: """ + user.getEmail() + """
        \n\t Ip-Address: """ + user.getIpAddress() + """
        """;
  }

  private static void initUser() {
    final String username = plugin.getServer().getName() + "-Node";
    final String ipAddress = plugin.getConfigManager().get("javaIp", "?javaIp?");
    final String id = plugin.getConfigManager().get("misc.discordServerId", "?discordServerId?");
    final String name = plugin.getConfigManager().get("misc.discordOwnerId", "?discordOwnerId?");
    final String email = plugin.getConfigManager().get("misc.serverContactEmail", "?serverContactEmail?");

    final User user = new User();
    user.setId(id);
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    user.setIpAddress(ipAddress);
    SentryService.user = user;
  }

}
