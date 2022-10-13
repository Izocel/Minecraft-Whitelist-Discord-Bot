package commands.bukkit;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import configs.ConfigManager;
import dao.DaoManager;
import helpers.Helper;
import services.sentry.SentryService;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class ConfirmLinkCmd extends PlayerBaseCmd {

  public static String acceptId = "linkAccept";
  public static String rejectId = "linkReject";

  public ConfirmLinkCmd(WhitelistJe plugin, String cmdName) {
    super(plugin, cmdName);
  }

  @Override
  public void execute(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      Player player = (Player) sender;
      final Integer foundPlayerId  = plugin.getPlayerId(player.getUniqueId());
      final User user  = DaoManager.getUsersDao().findUser(foundPlayerId);

      if(foundPlayerId > 0) {
        final Boolean isConfirmed  = plugin.playerIsConfirmed(player.getUniqueId()) > 0;
  
        if(!isConfirmed) {
          Member member = plugin.getGuildManager().findMember(user.getDiscordId());
          this.sendConfimationEmbeded(member, player);
          return;
        }
        else {
          final String msg = "Vos compte sont déja confirmées...\n Discord-Id: "+ user.getDiscordId();
          player.sendMessage(msg);
          return;
        }
      }

      // User not found
      final String msg = "Votre enregistrement n'a pas pu être retrouvé...\n" + 
      "Enregistrer vous sur le serveur Discord® :: Contactez un admin...";
      player.sendMessage(msg);
    } catch (Exception e) {
      SentryService.captureEx(e);
    }

  }

  private void sendConfimationEmbeded(Member member, Player player) {

    final String discordId = member.getId();
    final String mcPeudo = player.getName();
    final UUID mcUuid = player.getUniqueId();

    this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
      final String channel_id = channel.getId();
      final MessageEmbed msgEmbededs = Helper.jsonToMessageEmbed(this.confirmationEmbededs(channel_id, mcUuid.toString(), mcPeudo));
      final ArrayList<ActionRow> msgActions = Helper.getActionRowsfromJson(this.confirmationActions(channel_id));
      Helper.preparePrivateCustomMsg(channel, msgEmbededs, msgActions).queue();
    });

  }

  private String confirmationEmbededs(String channel_id, String uuid, String pseudo) {
    ConfigManager configs = plugin.getConfigManager();
    final  String embedUrl = configs.get("minecrafInfosLink", "https://www.fiverr.com/rvdprojects?up_rollout=true");

    return """
      {
        "embeds": [
          {
            "type": "rich",
            "title": "Confirmation de vos comptes",
            "url": '""" + embedUrl + "'," + """
            "description": "**Veuillez confirmer ou entrer la commande de confirmation dans la barre de discussion:**\n    /link [your code]",
            "color": "cc00ff",
            "fields": [
              {
                "name": "Minecraft pseudo: ",
                "value": """ + pseudo + "," + """
                "inline": true
              },
              {
                "name": "Minecraft uuid: ",
                "value": """ + uuid + "," + """
                "inline": true
              }
            ],
            "image": {
              "url": 'https://info.varonis.com/hubfs/Varonis_June2021/Images/data-security-hero-1200x401.png'
            },
            "author": {
              "name": "Whitelist-Je",
              "icon_url": 'https://incrypted.com/wp-content/uploads/2021/07/a4cf2df48e2218af11db8.jpeg'
            },
            "footer": {
              "text": "En cliquant sur 'OUI' vous confirmez que ces comptes seront reliés et que vous en êtes le détenteur.\nEn cliquant sur 'NON' les liens temporaires seront détruits et toutes activitées courrantes et futures seront suspendues."
            }
          }
        ]
      }
      """;
  }

  private String confirmationActions(String channel_id) {
    return """
      {
        "components": [
          {
            "type": 1,
            "components": [
              {
                "style": 4,
                "label": "Non, ce n'était pas moi",
                "custom_id": """ + rejectId + "," + """
                "disabled": false,
                "type": 2
              },
              {
                "style": 3,
                "label": "Oui, cétait bien moi",
                "custom_id": """ + acceptId + "," + """
                "disabled": false,
                "type": 2
              }
            ]
          }
        ]
      }
      """;
  }

}
