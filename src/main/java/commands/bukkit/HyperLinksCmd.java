package commands.bukkit;

import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import configs.ConfigManager;
import dao.DaoManager;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import locals.LocalManager;
import main.WhitelistDmc;
import services.sentry.SentryService;
import models.User;
import net.dv8tion.jda.api.entities.Member;

public class HyperLinksCmd extends PlayerBaseCmd {

  private final ConfigManager configs;

  public HyperLinksCmd(WhitelistDmc plugin, String cmdName) {
    super(plugin, cmdName);
    this.configs = plugin.getConfigManager();
  }

  @Override
  public void execute(CommandSender sender, Command cmd, String label, String[] args) {
    ITransaction tx = Sentry.startTransaction("Wje-Vote", "custom vote command");
    final Player player = (Player) sender;

    final LinkedHashMap<String, Object> linksMap = configs.getAsMap("linksCommands");
    if (linksMap == null || linksMap.isEmpty()) {
      this.logger.info("hyperlinks: no custom links are configured");
      player.sendMessage("Sorry this cmd is not available at the moment...");
      return;
    }

    final String argumentName = args[0].toString();

    final LinkedHashMap<String, Object> cmdConfigs = (LinkedHashMap<String, Object>) linksMap.getOrDefault(argumentName, null);

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

    
    final LocalManager LOCAL = WhitelistDmc.LOCALES;
    String usedLang = LOCAL.getNextLang();

    try {
      final JSONObject playerData = plugin.getMinecraftDataJson(player.getUniqueId());

      if (playerData == null) {
        final String msg = LOCAL.translate("NOTREGISTERED") + "\n" +
            LOCAL.translate("USER_ONLY_CMD") + "\n" +
            LOCAL.translate("DO_REGISTER") + " :: " + LOCAL.translate("CONTACT_ADMIN");

        tx.setData("state", "Player must be registered");
        tx.finish(SpanStatus.OK);
        return;
      }

      final Integer userId = playerData.getInt("user_id");
      final User user = DaoManager.getUsersDao().findUser(userId);
      final Member member = plugin.getGuildManager().findMember(user.getDiscordId());
      usedLang = user.getLang().toLowerCase();

      this.sendMcLink(member, player, usedLang, cmdConfigs);

    } catch (Exception e) {
      player.sendMessage(LOCAL.translateBy("CMD_ERROR", usedLang));

      tx.setThrowable(e);
      tx.setData("state", "link was not delivered");
      tx.finish(SpanStatus.INTERNAL_ERROR);
      SentryService.captureEx(e);
    }

    tx.setData("state", "link was delivered");
    tx.finish(SpanStatus.OK);
  }

  private void sendMcLink(Member member, Player player, String usedLang, LinkedHashMap<String, Object> cmdConfigs) {
    final LocalManager LOCAL = WhitelistDmc.LOCALES;

    final String textKey = cmdConfigs.get("minecraftTextTradKey").toString();
    final String linkKey = cmdConfigs.get("linkTradKey").toString();

    final String msg = LOCAL.translateBy(textKey, usedLang);
    final String link = LOCAL.translateBy(linkKey, usedLang);

    final JSONObject clickEvent = new JSONObject();
    clickEvent.put("action", "open_url");
    clickEvent.put("value", link);

    final JSONObject obj = new JSONObject();
    obj.put("text", msg);
    obj.put("clickEvent", clickEvent);

    boolean isPublic = false;
    final String publicConf = cmdConfigs.getOrDefault("isPublicMsg", "false").toString();
    if (publicConf.equals("true")) {
      isPublic = true;
    }
    
    final String targetEntity = isPublic ? "@a" : player.getName();
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + targetEntity + " " + obj.toString());
  }
}