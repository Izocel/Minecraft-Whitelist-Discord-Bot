package services.sentry;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import services.sentry.SentryService;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import main.WhitelistJe;

public class SentryService {

  private boolean enabled = true;
  private String debugMode = "debug";
  private WhitelistJe plugin;
  private static User user;
  HashMap<Integer, ITransaction>  pendingTransactions = new HashMap<Integer, ITransaction>();
  private Logger logger;
  private static WhitelistJe main;

  public SentryService(WhitelistJe plugin) {
    this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());

    this.plugin = plugin;
    main = this.plugin;
    
    final User user = new User();
    final String id = this.plugin.getConfigManager().get("discordServerId", "discordServerId");
    final String username = this.plugin.getConfigManager().get("discordOwnerId", "discordOwnerId");
    final String ipAddress = this.plugin.getConfigManager().get("paperMcIp", "paperMcIp");
    final String envType = this.plugin.getConfigManager().get("envType", "?envType?");
    final String release = this.plugin.getConfigManager().get("pluginVersion", "?release?") + "@" + envType;
    

    user.setId(id);
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

  public static @NotNull SentryId captureEx(Throwable error) {
    Exception err = new Exception(error.getMessage());
    Exception ex = new Exception(error.getMessage() + userToString());
    Logger.getLogger("WJE: SentryService").warning("WARNING: ");
    err.printStackTrace();

    if(!main.getConfigManager().get("envType", "production").equals("dev"))
      return Sentry.captureException(ex);

    return null;
  }

  public @NotNull SentryId captureEvent(SentryEvent event) {
    return Sentry.captureEvent(event);
  }

  public void changeUser(@Nullable User user) {
    Sentry.configureScope(scope -> {
      scope.setUser(user);
    });
  }

  public void setUsername(String username) {
    user.setUsername(username);
  }

  public void setUserEmail(String email) {
    user.setEmail(email);
  }

  public void setUserIpAdress(String ip) {
    user.setIpAddress(ip);
  }

  public void setUserId(String id) {
    user.setId(id);
  }

}
