package Whitelist_Je.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import Whitelist_Je.WhitelistJe;
import Whitelist_Je.functions.Alphanumeric;
import Whitelist_Je.functions.WhitelistManager;

public class WhitelistCommand extends ListenerAdapter
{
    public WhitelistJe main;
    public WhitelistCommand(WhitelistJe main) {
        this.main = main;
    }
    private Alphanumeric alphanumeric = new Alphanumeric();
    private WhitelistManager whitelistManager;

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if(event.getName().equals("whitelist")) {
            if(!event.getMember().hasPermission(Permission.BAN_MEMBERS)) return;
            String action = event.getOption("action").getAsString();
            if(action == null) return;
            switch (action) {
                /*case "add": @TODO Poubelle
                    if (!alphanumeric.isAlphanumeric(event.getOption("pseudo").getAsString())) {
                        event.reply("Ce pseudo ne doit pas comporter de caractères spéciaux à part les underscores _ et tirets -").setEphemeral(true).queue();
                        return;
                    }
                    OfflinePlayer player = Bukkit.getOfflinePlayer(event.getOption("pseudo").getAsString());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                    if (Bukkit.getServer().getWhitelistedPlayers().contains(player)) {
                        event.reply("Ce joueur est déjà sur la whitelist").setEphemeral(true).queue();
                        return;
                    }
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + event.getOption("pseudo").getAsString());
                            event.reply("Le joueur " + event.getOption("pseudo").getAsString() + " a été ajouté à la whitelist").setEphemeral(true).queue();
                        }
                    }.runTask(main);
                    break;
                case "remove":
                    if (!alphanumeric.isAlphanumeric(event.getOption("pseudo").getAsString())) {
                        event.reply("Ce pseudo ne doit pas comporter de caractères spéciaux à part les underscores _ et tirets -").setEphemeral(true).queue();
                        return;
                    }
                    OfflinePlayer playerr = Bukkit.getOfflinePlayer(event.getOption("pseudo").getAsString());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                    if (!Bukkit.getServer().getWhitelistedPlayers().contains(playerr)) {
                        event.reply("Ce joueur n'est pas sur la whitelist").setEphemeral(true).queue();
                        return;
                    }
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "whitelist remove " + event.getOption("pseudo").getAsString());
                            event.reply("Le joueur " + event.getOption("pseudo").getAsString() + " a été supprimé de la whitelist").setEphemeral(true).queue();}
                    }.runTask(main);
                    break;*/
                case "on":
                    if(event.getMember().getId().equals("258071819108614144") ||
                            event.getMember().getId().equals("99187278613581824")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "whitelist on");
                                event.reply("Mise à jour de la whitelist sur ON").setEphemeral(true).queue();
                            }
                        }.runTask(main);
                    } else {
                        event.reply("Vous n'avez pas accès à cette commande !").setEphemeral(true).queue();
                    }
                    break;
                case "list" :
                    event.reply(getPlayersOnline()).setEphemeral(true).queue();
                break;
                case "off":
                    if(event.getMember().getId().equals("258071819108614144") ||
                            event.getMember().getId().equals("99187278613581824")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "whitelist off");
                                event.reply("Mise à jour de la whitelist sur OFF").setEphemeral(true).queue();
                            }
                        }.runTask(main);
                    } else {
                        event.reply("Vous n'avez pas accès à cette commande !").setEphemeral(true).queue();
                        return;
                    }
                    break;
                case " ":
                default:
                    event.reply("Érreur syntaxe de la commande").setEphemeral(true).queue();
                    return;
            }
            return;
        }
    }

    public String getPlayersOnline() {
        whitelistManager = main.getWhitelistManager();
        return whitelistManager.getPlayersAllowed().size() + " joueurs sur la whitelist`\n" + whitelistManager.getPlayersAllowed().toString().replace("[", "").replace("]", "").replace("_", "⎽");
    }
}
