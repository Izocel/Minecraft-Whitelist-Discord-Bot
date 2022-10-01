package commands.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ConfirmLinkCmd extends PlayerBaseCmd {

  public ConfirmLinkCmd(Plugin plugin, String cmdName) {
    super(plugin, cmdName);
  }

  @Override
  public void execute(CommandSender sender, Command cmd, String label, String[] args) {
    Player player = (Player) sender;

    this.logger.info(player.getName());
    this.logger.info(this.plugin.getClass().getSimpleName());
  }

}
