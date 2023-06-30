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
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
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
    configs = plugin.getConfigManager();
  }

  @Override
  public void execute(CommandSender sender, Command cmd, String label, String[] args) {
    ITransaction tx = Sentry.startTransaction("Wje-Link", "comfim user account");

    Player player = (Player) sender;
    LocalManager LOCAL = WhitelistJe.LOCALES;

    String userLang = LOCAL.getNextLang();

    try {
      final JSONObject playerData = plugin.getMinecraftDataJson(player.getUniqueId());

      if (playerData == null) {
        final String msg = LOCAL.translate("NOTREGISTERED") + "\n" +
            LOCAL.translate("USERONLY_CMD") + "\n" +
            LOCAL.translate("DOREGISTER") + " :: " + LOCAL.translate("CONTACT_ADMNIN");
        player.sendMessage(msg);

        tx.setData("state", "player not yet registered");
        tx.finish(SpanStatus.OK);
        return;
      }

      final String uuid = playerData.getString("uuid");
      final Integer userId = playerData.getInt("user_id");
      final Boolean isConfirmed = playerData.optString("confirmed").equals("1");
      final String registrationDate = playerData.optString("created_at", null);

      final User user = DaoManager.getUsersDao().findUser(userId);
      userLang = user.getLang();

      if (isConfirmed) {
        final String msg = LOCAL.translateBy("MINECRAFT_ALREADYCONFIRMED", userLang) + "\n" +
            LOCAL.translateBy("LABEL_DISCORD_ID", userLang) + ": " + user.getDiscordId();
        player.sendMessage(msg);

        tx.setData("state", "already confirmed");
        tx.finish(SpanStatus.OK);
        return;
      }

      final Integer confirmHourDelay = Integer.valueOf(
        configs.get("hoursToConfirmMcAccount", "24"));

      final boolean canConfirm = Helper.isWithinXXHour(
          Helper.convertStringToTimestamp(registrationDate), confirmHourDelay);

      if (!canConfirm) {
        final String msg = getDisallowMsg(user.getDiscordTag(), uuid, userLang);
        player.setWhitelisted(false);
        player.kickPlayer(msg);
        plugin.deletePlayerRegistration(player.getUniqueId());

        tx.setData("state", "delay for confirmation was exceeded");
        tx.finish(SpanStatus.PERMISSION_DENIED);
        return;
      }
      
      Member member = plugin.getGuildManager().findMember(user.getDiscordId());
      this.sendConfimationEmbeded(member, player, userLang);
      final String msg = LOCAL.translateBy("MINECRAFT_CONFIRMATIONONITSWAY", userLang);
      player.sendMessage(msg);

    } catch (Exception e) {
      player.sendMessage(LOCAL.translateBy("CMD_ERROR", userLang));

      tx.setThrowable(e);
      tx.setData("state", "error");
      tx.finish(SpanStatus.INTERNAL_ERROR);
      SentryService.captureEx(e);
    }

    tx.setData("state", "confirm success");
    tx.finish(SpanStatus.OK);

  }

  public String getDisallowMsg(String tagDiscord, String mcUUID, String userLang) {
    LocalManager LOCAL = WhitelistJe.LOCALES;
    final String cmdName = ": /" + LOCAL.translateBy("CMD_REGISTER", userLang);

    return "\n\n§c§l" + LOCAL.translateBy("WARN_REGISTRATIONDELAY", userLang) +
        "\n§f" + LOCAL.translateBy("ACCOUNTSINFOS", userLang) +
        "\n§f" + LOCAL.translateBy("LABEL_DISCORD_TAG", userLang) + ": " + tagDiscord +
        "\n§f" + LOCAL.translateBy("LABEL_MINECRAFT_UUID", userLang) + ": " + mcUUID +
        "\n\n§l" + LOCAL.translateBy("INFO_TRYREGISTERAGAIN", userLang) +
        "\n§9" + LOCAL.translateBy("LABEL_USECMD", userLang) + cmdName +
        "\n\n§c" + LOCAL.translateBy("INFO_LEGITIMATE", userLang);
  }

  private void sendConfimationEmbeded(Member member, Player player, String lang) {
    final UUID uuid = player.getUniqueId();
    final String discordId = member.getUser().getId();
    final JSONObject pData = plugin.getMinecraftDataJson(uuid);

    this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
      final String channel_id = channel.getId();

      final MessageEmbed msgEmbededs = Helper.jsonToMessageEmbed(
        this.confirmationEmbededs(channel_id, uuid.toString(), pData.getString("pseudo"), lang)
      );
      final ArrayList<ActionRow> msgActions = Helper.getActionRowsfromJson(this.confirmationActions(channel_id, lang));

      Helper.preparePrivateCustomMsg(channel, msgEmbededs, msgActions).submit(true);
    });
  }

  private String confirmationEmbededs(String channel_id, String uuid, String pseudo, String lang) {
    LocalManager LOCAL = WhitelistJe.LOCALES;

    final String title = LOCAL.translateBy("TITLE_ACCOUNT_CONFIRM", lang);
    final String description = "**" + LOCAL.translateBy("EMBD_LINK_DESC", lang) + "**";
    final String mcNameLabel = LOCAL.translateBy("LABEL_LONG_MC", lang);
    final String mcUuidLabel = LOCAL.translateBy("LABEL_MINECRAFT_UUID", lang);
    final String policy = LOCAL.translateBy("EMBD_LINK_POLICY", lang);

    final String embedUrl = this.configs.get("minecraftInfosLink");

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

  private String confirmationActions(String channel_id, String lang) {
    LocalManager LOCAL = WhitelistJe.LOCALES;

    final String WORD_YES = '"' + LOCAL.translateBy("EMBD_LINK_YESME", lang) + '"';
    final String WORD_NO = '"' + LOCAL.translateBy("EMBD_LINK_NOTME", lang) + '"';

    final String jsonEmbeded = """
    {
      "components": [
        {
          "type": "1",
          "components": [
            {
              "style": "4",
              "label": """ + WORD_NO + "," + """
              "custom_id": '""" + rejectId + "'," + """
              "disabled": false,
              "type": "2"
            },
            {
              "style": "3",
              "label": """ + WORD_YES + "," + """
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
