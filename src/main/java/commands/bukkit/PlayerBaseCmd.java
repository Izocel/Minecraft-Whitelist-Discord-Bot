package commands.bukkit;

import java.util.logging.Logger;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.WhitelistJe;

public class PlayerBaseCmd implements IPlayerCmd, CommandExecutor {

  protected WhitelistJe plugin;
  protected String cmdName;
  protected Logger logger;
  

  public PlayerBaseCmd(WhitelistJe plugin, String cmdName) {
    this.plugin = plugin;
    this.cmdName = cmdName;
    this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName() + " </>:" + this.cmdName);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      if (cmd.getName().equalsIgnoreCase(this.cmdName)) {

          if (!(sender instanceof Player)) {
              sender.sendMessage("This command can only be run by a player instance.");
          } else {
              this.execute(sender, cmd, label, args);
          }
          return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public void execute(CommandSender sender, Command cmd, String label, String[] args) {
      throw new NotImplementedException();
  }

}
