package commands.bukkit;

import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.json.JSONObject;

import configs.ConfigManager;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import locals.LocalManager;
import main.WhitelistDmcNode;
import services.sentry.SentryService;

public class HyperLinksCmd extends UserBaseCmd {

  private final ConfigManager configs;
  private LinkedHashMap<String, Object> linkConfigs;

  public HyperLinksCmd(WhitelistDmcNode plugin, String cmdName) {
    super(plugin, cmdName, true);
    this.configs = plugin.getConfigManager();
  }

  @Override
  public void execute(CommandSender sender, Command cmd, String label, String[] args) {
    ITransaction tx = Sentry.startTransaction("Wje-HyperLink", "Custom link command");

    try {
      this.parseLinksConfgis(args);
      this.sendMcLink();

    } catch (Exception e) {
      player.sendMessage(LOCAL.translate("CMD_ERROR"));

      tx.setThrowable(e);
      tx.setData("state", "link was not delivered");
      tx.finish(SpanStatus.INTERNAL_ERROR);
      SentryService.captureEx(e);
    }

    tx.setData("state", "link was delivered");
    tx.finish(SpanStatus.OK);
  }

  private void parseLinksConfgis(String[] args) {
    final String argumentName = args[0].toString();
    final LinkedHashMap<String, Object> linksMap = configs.getAsMap("linksCommands");
    final LinkedHashMap<String, Object> cmdConfigs = (LinkedHashMap<String, Object>) linksMap.getOrDefault(argumentName,
        null);

    if (linksMap == null || linksMap.isEmpty()) {
      this.logger.info("hyperlinks: no custom links are configured");
      player.sendMessage("Sorry this cmd is not available at the moment...");
      return;
    }

    if (cmdConfigs == null || cmdConfigs.isEmpty()) {
      this.logger.info("hyperlinks: configuration not found for: " + argumentName);
      player.sendMessage("Sorry this cmd is not available at the moment...");
      return;
    }

    boolean doInit = true;
    final String doInitConf = cmdConfigs.getOrDefault("useMinecraft", "true").toString();
    if (doInitConf.equals("false")) {
      doInit = false;
    }

    if (doInit != true) {
      final String name = cmdConfigs.get("aliasTradKey").toString();
      this.logger.info("hyperlinks: " + name + " is deactivated for minecraft");
      player.sendMessage("Sorry this cmd is not available at the moment...");
      return;
    }

    this.linkConfigs = cmdConfigs;
  }

  private void sendMcLink() {
    final LocalManager LOCAL = WhitelistDmcNode.LOCALES;

    final String textKey = linkConfigs.get("minecraftTextTradKey").toString();
    final String linkKey = linkConfigs.get("linkTradKey").toString();

    final String msg = LOCAL.translate(textKey);
    final String link = LOCAL.translate(linkKey);

    final JSONObject clickEvent = new JSONObject();
    clickEvent.put("action", "open_url");
    clickEvent.put("value", link);

    final JSONObject obj = new JSONObject();
    obj.put("text", msg);
    obj.put("clickEvent", clickEvent);

    boolean isPublic = false;
    final String publicConf = linkConfigs.getOrDefault("isPublicMsg", "false").toString();
    if (publicConf.equals("true")) {
      isPublic = true;
    }

    final String targetEntity = isPublic ? "@a" : player.getName();
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + targetEntity + " " + obj.toString());
  }
}