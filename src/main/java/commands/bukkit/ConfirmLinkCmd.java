package commands.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import helpers.Helper;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

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
      final MessageEmbed msgEmbeded = this.confirmationEmbeded(channel_id);

      if(!msgEmbeded.isSendable()) {
        try {
          throw new Exception("Embeded is not sendable");
        } catch (Exception e) {
          e.printStackTrace();
        }
        return;
      }
      channel.sendMessage(msgEmbeded).queue();
  });

}

  private MessageEmbed confirmationEmbeded(String channel_id) {
    String msg = """
        {
        "channel_id": "+ channel_id +",
        "content": "",
        "tts": false,
        "components": [
          {
            "type": 1,
            "components": [
              {
                "style": 4,
                "label": `No, this was not me`,
                "custom_id": `OnUserConfirm.rejectId`,
                "disabled": false,
                "type": 2
              },
              {
                "style": 3,
                "label": `Yes, is was me`,
                "custom_id": `OnUserConfirm.acceptId`,
                "disabled": false,
                "type": 2
              }
            ]
          }
        ],
        "embeds": [
          {
            "type": "rich",
            "title": `Confirmation de vos comptes`,
            "description": `**Veullez comfirmer ou entrer la commande de confirmation dans la barre de discussion:**\n    /link [your code]`,
            "color": 0xcc00ff,
            "fields": [
              {
                "name": `Minecraft pseudo: `,
                "value": `Izocel`,
                "inline": true
              },
              {
                "name": `Minecraft uuid: `,
                "value": `123456789`
              }
            ],
            "image": {
              "url": `https://info.varonis.com/hubfs/Varonis_June2021/Images/data-security-hero-1200x401.png`,
              "height": 0,
              "width": 0
            },
            "author": {
              "name": `Whitelist-Je`,
              "icon_url": `https://incrypted.com/wp-content/uploads/2021/07/a4cf2df48e2218af11db8.jpeg`
            },
            "footer": {
              "text": `Clicking 'YES' will confirm he link between the accounts.\nClicking 'NO' will delete the link between the accounts and suspend all current and futher activities.`
            },
            "url": `https://www.youtube.com/c/Tumeniaises`
          }
        ]
        }
          """;

      JsonObject embededJson = new Gson().fromJson(msg, JsonObject.class);
      return Helper.jsonToEmbed(embededJson);
  }

}
