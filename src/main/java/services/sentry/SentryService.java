package services.sentry;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import services.sentry.SentryService;
import io.sentry.Hint;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import main.WhitelistDmc;

public class SentryService {
  
  private Logger logger;
  private static User user;
  private WhitelistDmc plugin;
  private static WhitelistDmc main;
  private boolean enabled = true;
  private String debugMode = "debug";
  
  HashMap<Integer, ITransaction>  pendingTransactions = new HashMap<Integer, ITransaction>();
  private static String envType;

  public SentryService(WhitelistDmc plugin) {
    this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());

    this.plugin = plugin;
    SentryService.main = this.plugin;
    
    final User user = new User();
    final String username = "?discordOwnerId?";
    final String id = this.plugin.getConfigManager().get("discordServerId", "?discordServerId?");
    final String email = this.plugin.getConfigManager().get("serverContactEmail", "?serverContactEmail?");
    final String ipAddress = this.plugin.getConfigManager().get("javaIp", "?javaIp?");
    SentryService.envType = this.plugin.getConfigManager().get("envType", "?envType?");
    final String release = this.plugin.getConfigManager().get("pluginVersion", "?release?") + "@" + envType;
    

    user.setId(id);
    user.setEmail(email);
    user.setUsername(username);
    user.setIpAddress(ipAddress);

    SentryService.user = user;
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
      if(pendingTransactions != null &&  getTxSize() > 0)
      pendingTransactions.forEach((key,tx) -> {
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
      size, tx
    );

    return pendingTransactions.get(size);
  }

  public ITransaction findWithuniqueName(String txName) {
    if(pendingTransactions == null || getTxSize() < 1) {
      return null;
    }

    for (int i = 0; i < getTxSize(); i++) {
      if(getTransactions().get(i).getData("name").equals(txName)) {
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
    if(this.pendingTransactions == null) {
      this.pendingTransactions = new HashMap<Integer, ITransaction>();
    }
    return this.pendingTransactions;
  }

  private static String userToString() {
    return """
        \n User: 
        \t Id: """ + user.getId() + """
        \n\t Username: """ + user.getUsername() + """
        \n\t IpAdress: """ + user.getIpAddress() + """
        \n\t Email: """ + user.getEmail() + """
        """;
  }

  public static SentryId captureEx(Throwable error) {
    Logger.getLogger("WDMC-Sentry").warning("WARNING:");
    error.fillInStackTrace();
    error.printStackTrace();
    
    if(!envType.equals("production")) {
      return null;
    }

    Hint hint = new Hint();
    hint.set("user", userToString());
    return Sentry.captureException(error, hint);
  }

  public @NotNull SentryId captureEvent(SentryEvent event) {
    return envType.equals("production")
      ? Sentry.captureEvent(event)
      : null;
  }

  public void changeUser(@Nullable User user) {
    Sentry.configureScope(scope -> {
      scope.setUser(user);
    });
  }

  public void setUsername(String username) {
    user.setUsername(username);
    changeUser(user);
  }

  public void setUserEmail(String email) {
    user.setEmail(email);
    changeUser(user);
  }

  public void setUserIpAdress(String ip) {
    user.setIpAddress(ip);
    changeUser(user);
  }

  public void setUserId(String id) {
    user.setId(id);
    changeUser(user);
  }

}
