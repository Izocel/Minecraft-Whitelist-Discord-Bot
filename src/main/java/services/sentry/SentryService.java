package services.sentry;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import services.sentry.SentryService;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import main.WhitelistJe;

public class SentryService {

  private String debugMode = "debug";
  private boolean enabled = true;
  private WhitelistJe plugin;
  private static User user;

  public SentryService(WhitelistJe main) {
    this.plugin = main;

    final User user = new User();
    final String id = this.plugin.getConfigManager().get("discordServerId", "discordServerId");
    final String username = this.plugin.getConfigManager().get("discordOwnerId", "discordOwnerId");
    final String ipAddress = this.plugin.getConfigManager().get("paperMcIp", "paperMcIp");

    user.setId(id);
    user.setUsername(username);
    user.setIpAddress(ipAddress);

    SentryService.user = user;
    Sentry.setLevel(SentryLevel.DEBUG);
    Sentry.init(options -> {
      options.setDsn(
          "https://80475ddc2c8d457ea52a1fbd0a8e22bb@o4503922553847808.ingest.sentry.io/4503922559156224");
      // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance
      // monitoring.
      // We recommend adjusting this value in production.
      options.setDiagnosticLevel(SentryLevel.DEBUG);
      options.setTracesSampleRate(1.0);
    });
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
    Exception ex = new Exception(error.getMessage() + userToString());
    ex.printStackTrace();
    return Sentry.captureException(ex);
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
