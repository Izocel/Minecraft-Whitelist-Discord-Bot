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
import locals.LocalManager;
import services.sentry.SentryService;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class ConfirmLinkCmd extends PlayerBaseCmd {

  public static String acceptId = "linkAccept";
  public static String rejectId = "linkReject";
  private ConfigManager configs;

  public ConfirmLinkCmd(WhitelistJe plugin, String cmdName) {
    super(plugin, cmdName);
  }

  @Override
  public void execute(CommandSender sender, Command cmd, String label, String[] args) {
    Player player = (Player) sender;
    LocalManager LOCAL = WhitelistJe.LOCALES;

    try {
      final JSONObject playerData = plugin.getMinecraftDataJson(player.getUniqueId());

      if (playerData == null) {
        final String msg = LOCAL.translate("NOTREGISTERED") + "\n" +
            LOCAL.translate("USERONLY_CMD") + "\n" +
            LOCAL.translate("DOREGISTER") + " :: " + LOCAL.translate("CONTACT_ADMNIN");
        player.sendMessage(msg);
        return;
      }

      final String uuid = playerData.getString("uuid");
      final Integer userId = playerData.getInt("user_id");
      final Boolean isConfirmed = playerData.optString("confirmed").equals("1");
      final String registrationDate = playerData.optString("created_at", null);

      final User user = DaoManager.getUsersDao().findUser(userId);

      if (isConfirmed) {
        final String msg = LOCAL.translate("MINECRAFT_ALREADYREGISTERED") + "\n" +
            LOCAL.translate("LABEL_DISCORD_ID") + ": " + user.getDiscordId();
        player.sendMessage(msg);
        return;
      }

      final Integer confirmHourDelay = Integer.valueOf(
          plugin.getConfigManager().get("hoursToConfirmMcAccount", "24"));

      final boolean canConfirm = Helper.isWithinXXHour(
          Helper.convertStringToTimestamp(registrationDate), confirmHourDelay);

      if (!canConfirm) {
        final String msg = getDisallowMsg(user.getDiscordTag(), uuid);
        player.setWhitelisted(false);
        player.kickPlayer(msg);
        plugin.deletePlayerRegistration(player.getUniqueId());
        return;
      }

      Member member = plugin.getGuildManager().findMember(user.getDiscordId());
      this.sendConfimationEmbeded(member, player);

    } catch (Exception e) {
      player.sendMessage(LOCAL.translate("CMD_ERROR"));
      SentryService.captureEx(e);
    }

  }

  public String getDisallowMsg(String tagDiscord, String mcUUID) {
    LocalManager LOCAL = WhitelistJe.LOCALES;
    final String cmdName = ": \\" + LOCAL.translate("CMD_LINK");

    return "\n\n§c§l" + LOCAL.translate("WARN_REGISTRATIONDELAY") +
        "\n§f" + LOCAL.translate("ACCOUNTSINFOS") +
        "\n§f" + LOCAL.translate("LABEL_DISCORD_TAG") + ": " + tagDiscord +
        "\n§f" + LOCAL.translate("LABEL_MIBECRAFT_UUID") + ": " + mcUUID +
        "\n\n§l" + LOCAL.translate("INFO_TRYREGISTERAGAIN") +
        "\n§9" + LOCAL.translate("LABEL_USECMD") + cmdName +
        "\n\n§c" + LOCAL.translate("INFO_LEGITIMATE");
  }

  private void sendConfimationEmbeded(Member member, Player player) {
    final UUID uuid = player.getUniqueId();
    final String discordId = member.getUser().getId();
    final JSONObject pData = plugin.getMinecraftDataJson(uuid);

    this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
      final String channel_id = channel.getId();

      final MessageEmbed msgEmbededs = Helper.jsonToMessageEmbed(this.confirmationEmbededs(channel_id, uuid.toString(), pData.getString("pseudo")));
      final ArrayList<ActionRow> msgActions = Helper.getActionRowsfromJson(this.confirmationActions(channel_id));

      Helper.preparePrivateCustomMsg(channel, msgEmbededs, msgActions).submit(true);
    });
  }

  private String confirmationEmbededs(String channel_id, String uuid, String pseudo) {
    LocalManager LOCAL = WhitelistJe.LOCALES;
    ConfigManager configs = plugin.getConfigManager();

    final String title = LOCAL.translate("TITLE_ACCOUNT_CONFIRM");
    final String description = "**" + LOCAL.translate("EMBD_LINK_DESC") + "**";
    final String mcNameLabel = LOCAL.translate("LABEL_LONG_MC");
    final String mcUuidLabel = LOCAL.translate("LABEL_MIBECRAFT_UUID");
    final String policy = LOCAL.translate("EMBD_LINK_POLICY");

    final String embedUrl = this.configs.get("minecrafInfosLink", "https://www.fiverr.com/rvdprojects?up_rollout=true");

    final String jsonEmbeded = """
    {
      "embeds": [
        {
          "type": "rich",
          "title": '""" + title + "'," + """
          "url": '""" + embedUrl + "'," + """
          "description": '""" + description + "'," + """
          "color": "cc00ff",
          "fields": [
            {
            "name": '""" + mcNameLabel + "'," + """
            "value": '""" + pseudo + "'," + """
            "inline": true
            },
            {
              "name": '""" + mcUuidLabel + "'," + """
              "value": '""" + uuid + "'," + """
              "inline": true
            }
          ],
          "image": {
            "url": 'https://info.varonis.com/hubfs/Varonis_June2021/Images/data-security-hero-1200x401.png'
          },
          "author": {
            "name": '""" + this.plugin.getName() + "'," + """
            "icon_url": 'https://incrypted.com/wp-content/uploads/2021/07/a4cf2df48e2218af11db8.jpeg'
          },
          "footer": {
            "text": '""" + policy + "'" + """
          }
        }
      ]
    }""";

    return jsonEmbeded;
  }

  private String confirmationActions(String channel_id) {
    LocalManager LOCAL = WhitelistJe.LOCALES;

    final String YES = '"' + LOCAL.translate("EMBD_LINK_YESME") + '"';
    final String NO = '"' + LOCAL.translate("EMBD_LINK_NOTME") + '"';

    final String jsonEmbeded = """
    {
      "components": [

        {
          "type": "1",
          "components": [
            {
              "style": "4",
              "label": """ + NO + "," + """
              "custom_id": '""" + rejectId + "'," + """
              "disabled": false,
              "type": "2"
            },
            {
              "style": "3",
              "label": """ + YES + "," + """
              "custom_id": '""" + acceptId + "'," + """
              "disabled": false,
              "type": "2"
            }
          ]
        }
      ]
    }""";

    return jsonEmbeded;
  }

}
