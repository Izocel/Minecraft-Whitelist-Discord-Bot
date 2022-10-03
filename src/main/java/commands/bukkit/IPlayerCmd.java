package commands.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface IPlayerCmd  {
    abstract void execute(CommandSender sender, Command cmd, String label, String[] args);
}
