package commands.bukkit;

import java.util.logging.Logger;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.json.JSONObject;

import dao.DaoManager;
import services.sentry.SentryService;
import main.WhitelistDmc;
import models.User;

public class UserBaseCmd extends PlayerBaseCmd {

  protected WhitelistDmc plugin;
  protected String cmdName;
  protected Logger logger;
  protected boolean confirmedOnly;
  protected User user;

  public UserBaseCmd(WhitelistDmc plugin, String cmdName, boolean confirmedOnly) {
    super(plugin, cmdName);
    this.confirmedOnly = confirmedOnly;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      if (!cmd.getName().equalsIgnoreCase(this.cmdName)) {
        return true;
      }

      final JSONObject userPlayerData = plugin.getMinecraftDataJson(player.getUniqueId());
      if (userPlayerData == null) {
        final String msg = LOCAL.translate("NOTREGISTERED") + "\n" +
            LOCAL.translate("USER_ONLY_CMD") + "\n" +
            LOCAL.translate("DO_REGISTER") + " :: " + LOCAL.translate("CONTACT_ADMIN");

        sender.sendMessage(msg);
        return true;
      }

      this.user = DaoManager.getUsersDao().findUser(userPlayerData.getInt("user_id"));
      LOCAL.setNextLang(user.getLang().toLowerCase());

      if (confirmedOnly && user.isConfirmed(player.getUniqueId().toString())) {
        final String msg = String.format(LOCAL.translate("INFO_MUST_CONFIRM_ACCOUNT"), "Minecraft");

        sender.sendMessage(msg);
        return true;
      }

      this.execute(sender, cmd, label, args);
      return true;
    } catch (Exception e) {
      SentryService.captureEx(e);
    }
    return false;
  }

  @Override
  public void execute(CommandSender sender, Command cmd, String label, String[] args) {
    throw new NotImplementedException();
  }

}
