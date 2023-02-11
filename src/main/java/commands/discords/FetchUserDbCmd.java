package commands.discords;

import org.json.JSONArray;

import discord.GuildManager;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import services.sentry.SentryService;

public class FetchUserDbCmd extends WjeUserOnlyCmd {

    private final static String KEY_CMD_NAME = "CMD_FETCHDB_USERS";
    private final static String KEY_CMD_DESC = "DESC_FETCHDB_USERS";
    private final static String KEY_PARAM_USER = "PARAM_MEMBER";
    private final static String KEY_PARAM_USER_LABEL = "PARAM_MEMBER_LABEL";

    public static void REGISTER_CMD(JDA jda, WhitelistJe plugin) {
        String cmdName = LOCAL.translate(KEY_CMD_NAME);
        String cmdDesc = LOCAL.translate(KEY_CMD_DESC);
        final String localUserParam = LOCAL.translate(KEY_PARAM_USER);
        final String localUserLabel = LOCAL.translate(KEY_PARAM_USER_LABEL);

        // Traduction
        jda.addEventListener(new FetchUserDbCmd(plugin));
        jda.upsertCommand(cmdName, cmdDesc)
                .addOption(OptionType.USER, localUserParam, localUserLabel, false)
                .submit(true);
    }

    public FetchUserDbCmd(WhitelistJe plugin) {
        super(plugin,
                "FetchDbCmd",
                "CMD_FETCHDB_USERS",
                "Fetch DB",
                "Fetch user(s)");
    }

    @Override
    protected final void execute() {
        try {
            final GuildManager gManager = this.plugin.getGuildManager();
            final boolean isAuthorized = gManager.isOwner(member.getId())
                    || gManager.isAdmin(member.getId())
                    || gManager.isModo(member.getId())
                    || gManager.isDev(member.getId())
                    || gManager.isHelper(member.getId());

            if (!isAuthorized) {
                submitReplyEphemeral(LOCAL.useDefault("ROLE_NOT_ALLOWED"));
                tx.setData("state", "not-allowed");
                tx.finish(SpanStatus.PERMISSION_DENIED);
                return;

            }
            
            final StringBuilder sb = new StringBuilder();
            final OptionMapping userParam = event.getOption(LOCAL.translate(KEY_PARAM_USER));
            // All users
            if(userParam == null) {
                final JSONArray data = plugin.updateAllPlayers();

                sb.append("Here's all the Minecraft users data:\n");
                sendMsgToUser(sb.toString());

                for (int i = 0; i < data.length(); i++) {
                    sendMsgToUser("```json\n" +  data.getJSONObject(i).toString(1) + "\n```");
                }
                
                submitReplyEphemeral("Infos's been sent to your private messages");
                tx.setData("state", "success-multiple");
                tx.finish(SpanStatus.OK);
                return;
            }
            
            final Member lookUpMember = userParam.getAsMember();
            final User lookupBdUser =  User.getFromMember(lookUpMember);

            // Single user NOT-FOUND
            if(lookupBdUser == null) {
                sb.append("User's not registered (no data to show)...");

                submitReplyEphemeral(sb.toString());
                tx.setData("state", "notFound");
                tx.finish(SpanStatus.NOT_FOUND);
                return;
            }

            // Single user registered
            sb.append("User is registered here's the info:\n");
            sb.append("```json\n" + lookupBdUser.toJson().toString(1) + "\n```");

            submitReplyEphemeral(sb.toString());
            tx.setData("state", "success-single");
            tx.finish(SpanStatus.OK);
            return;

        } catch (Exception e) {
            final String reply = useTranslator("CMD_ERROR") + ": " + useTranslator("CONTACT_ADMNIN");

            submitReplyEphemeral(reply);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
        }

    }

}