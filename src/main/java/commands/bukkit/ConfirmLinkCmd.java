package commands.bukkit;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import helpers.Helper;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class ConfirmLinkCmd extends PlayerBaseCmd {

  public ConfirmLinkCmd(WhitelistJe plugin, String cmdName) {
    super(plugin, cmdName);
  }

  @Override
  public void execute(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      Player player = (Player) sender;
      User user = this.plugin.getDaoManager().getUsersDao().findByMcUUID(player.getUniqueId().toString());
      Member member = this.plugin.getGuildManager().findMember(user.getDiscordId());

      this.sendConfimationEmbeded(member, player);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void sendConfimationEmbeded(Member member, Player player) {

    final String discordId = member.getId();
    final String discordTag = member.getUser().getAsTag();
    final String mcPeudo = player.getName();

    this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
      final String channel_id = channel.getId();
      final MessageEmbed msgEmbededs = Helper.jsonToMessageEmbed(this.confirmationEmbededs(channel_id));
      final ArrayList<ActionRow> msgActions = Helper.getActionRowsfromJson(this.confirmationActions(channel_id));
      Helper.preparePrivateCustomMsg(channel, msgEmbededs, msgActions).queue();
    });

  }


  private String confirmationEmbededs(String channel_id) {
    return """
      {
        "channel_id": "+ channel_id +",
        "content": "",
        "tts": false,
        "embeds": [
          {
            "type": "rich",
            "title": "Confirmation de vos comptes",
            "url": 'https://www.youtube.com/c/Tumeniaises',
            "description": "**Veuillez confirmer ou entrer la commande de confirmation dans la barre de discussion:**\n    /link [your code]",
            "color": "cc00ff",
            "fields": [
              {
                "name": "Minecraft pseudo: ",
                "value": "Izocel",
                "inline": true
              },
              {
                "name": "Minecraft uuid: ",
                "value": "123456789",
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
              "text": "Clicking 'YES' will confirm he link between the accounts.\nClicking 'NO' will delete the link between the accounts and suspend all current and futher activities."
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
                "label": "No, this was not me",
                "custom_id": "OnUserConfirm.rejectId",
                "disabled": false,
                "type": 2
              },
              {
                "style": 3,
                "label": "Yes, is was me",
                "custom_id": "OnUserConfirm.acceptId",
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
