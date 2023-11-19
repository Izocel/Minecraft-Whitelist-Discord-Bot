package commands.bukkit;

import java.util.logging.Logger;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import locals.LocalManager;
import services.sentry.SentryService;
import main.WhitelistDmcNode;

public class PlayerBaseCmd implements IPlayerCmd, CommandExecutor {

  protected WhitelistDmcNode plugin;
  protected LocalManager LOCAL;
  protected String cmdName;
  protected Logger logger;
  protected Player player;

  public PlayerBaseCmd(WhitelistDmcNode plugin, String cmdName) {
    this.plugin = plugin;
    this.cmdName = cmdName;
    this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName() + " </> " + this.cmdName);
    this.LOCAL = WhitelistDmcNode.LOCALES;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      if (!cmd.getName().equalsIgnoreCase(this.cmdName)) {
        return true;
      }

      if (!(sender instanceof Player)) {
        sender.sendMessage("This command can only be run by a player instance.");
      } else {
        this.player = (Player) sender;
        this.execute(sender, cmd, label, args);
      }
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
