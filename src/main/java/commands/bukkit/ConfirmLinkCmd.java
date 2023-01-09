package commands.bukkit;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;

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
    Player player = (Player) sender;

    try {
      final JSONObject playerData = plugin.getMinecraftDataJson(player.getUniqueId());

      if(playerData == null) {
        final String msg = "Cette commande est réservée aux utilisateurs enregistrés par Discord®.\n" +
        "Votre enregistrement n'a pas pu être retrouvé...\n" + 
        "Enregistrer vous sur le serveur Discord® :: Contactez un admin...";
        player.sendMessage(msg);
        return;
      }

      final String uuid = playerData.getString("uuid");
      final Integer userId = playerData.getInt("user_id");
      final Boolean isConfirmed  = playerData.optString("confirmed").equals("1");
      final String registrationDate  = playerData.optString("created_at", null);

      final User user = DaoManager.getUsersDao().findUser(userId);

      if(isConfirmed) {
        final String msg = "Vos compte sont déja confirmées...\nDiscord-Id: "+ user.getDiscordId();
        player.sendMessage(msg);
        return;
      }

      final Integer confirmHourDelay = Integer.valueOf(
        this.plugin.getConfigManager().get("hoursToConfirmMcAccount", "24"));

      final boolean canConfirm = Helper.isWithinXXHour(
        Helper.convertStringToTimestamp(registrationDate), confirmHourDelay);

      if(!canConfirm) {
        final String msg = getDisallowMsg(user.getDiscordTag(), uuid);
        player.setWhitelisted(false);
        player.kickPlayer(msg);
        plugin.deletePlayerRegistration(player.getUniqueId());
        return;
      }

      Member member = plugin.getGuildManager().findMember(user.getDiscordId());
      this.sendConfimationEmbeded(member, player);


    } catch (Exception e) {
      player.sendMessage("Oups... une erreur est survenu lors de cette demande !!!");
      SentryService.captureEx(e);
    }

  }

  public String getDisallowMsg(String tagDiscord, String mcUUID) {
    final String cmdName = this.plugin.getConfigManager().get("registerCmdName", "register");
    return "\n\n§c§lLe délai pour confirmer ce compte est dépassé..." +
            "\n§fLe compte " + tagDiscord + " Discord® a fait une demande pour relier ce compte Minecraft®." +
            "\n\n§lEssayez de refaire une demande sur discord, utiliser la commande:\n§9    /" + cmdName +
            "\n\n§cSi cette demande vous semble illégitime, contactez un administrateur!!!" +
            "\n§fIdentifiant de la demande: " + mcUUID + "\n \n";
}

  private void sendConfimationEmbeded(Member member, Player player) {

    final String discordId = member.getUser().getId();
    final UUID uuid = player.getUniqueId();

    final JSONObject pData = plugin.getMinecraftDataJson(uuid);

    this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
      final String channel_id = channel.getId();
      final MessageEmbed msgEmbededs = Helper.jsonToMessageEmbed(this.confirmationEmbededs(channel_id, uuid.toString(), pData.getString("pseudo")));
      final ArrayList<ActionRow> msgActions = Helper.getActionRowsfromJson(this.confirmationActions(channel_id));
      Helper.preparePrivateCustomMsg(channel, msgEmbededs, msgActions).submit(true);
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
