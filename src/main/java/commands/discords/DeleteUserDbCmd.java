package commands.discords;

import org.json.JSONArray;

import dao.DaoManager;
import discord.GuildManager;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import models.BedrockData;
import models.JavaData;
import models.User;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import services.sentry.SentryService;
import com.google.gson.JsonArray;

/**
 * Will delete all users data, remove all account from whitelist
 * Also kick from Minecraft and Discord
 */
public class DeleteUserDbCmd extends WjeUserOnlyCmd {

    private final static String KEY_CMD_NAME = "CMD_REMOVEDB_USERS";
    private final static String KEY_CMD_DESC = "DESC_REMOVEDB_USERS";
    private final static String KEY_PARAM_USER = "PARAM_MEMBER";
    private final static String KEY_PARAM_USER_LABEL = "PARAM_MEMBER_LABEL";
    private final static String KEY_PARAM_UUID = "PARAM_UUID";
    private final static String KEY_PARAM_UUID_LABEL = "PARAM_UUID_LABEL";

    public static void REGISTER_CMD(JDA jda, WhitelistJe plugin) {
        String cmdName = LOCAL.translate(KEY_CMD_NAME);
        String cmdDesc = LOCAL.translate(KEY_CMD_DESC);
        final String localUserParam = LOCAL.translate(KEY_PARAM_USER);
        final String localUserLabel = LOCAL.translate(KEY_PARAM_USER_LABEL);
        final String localUuidParam = LOCAL.translate(KEY_PARAM_UUID);
        final String localUuidLabel = LOCAL.translate(KEY_PARAM_UUID_LABEL);

        // Traduction
        jda.addEventListener(new DeleteUserDbCmd(plugin));
        jda.upsertCommand(cmdName, cmdDesc)
                .addOption(OptionType.USER, localUserParam, localUserLabel, false)
                .addOption(OptionType.STRING, localUuidParam, localUuidLabel, false)
                .submit(true);
    }

    public DeleteUserDbCmd(WhitelistJe plugin) {
        super(plugin,
                "DeleteUserDbCmd",
                "CMD_REMOVEDB_USERS",
                "Delete DB user",
                "Delete user");
    }

    @Override
    protected final void execute() {
        try {
            final GuildManager gManager = this.plugin.getGuildManager();
            final boolean isAuthorized = gManager.isOwner(member.getId())
                    || gManager.isAdmin(member.getId())
                    || gManager.isModo(member.getId())
                    || gManager.isDev(member.getId());

            if (!isAuthorized) {
                submitReplyEphemeral(LOCAL.useDefault("ROLE_NOT_ALLOWED"));
                tx.setData("state", "not-allowed");
                tx.finish(SpanStatus.PERMISSION_DENIED);
                return;
            }

            final StringBuilder sb = new StringBuilder();
            final OptionMapping userParam = event.getOption(LOCAL.translate(KEY_PARAM_USER));
            final OptionMapping uuidParam = event.getOption(LOCAL.translate(KEY_PARAM_UUID));

            if (userParam == null && uuidParam == null || (userParam != null && uuidParam != null)) {
                sb.append("❌ Bad parameters...");

                submitReplyEphemeral(sb.toString());
                tx.setData("state", "notFound (no params)");
                tx.finish(SpanStatus.NOT_FOUND);
                return;
            }

            String lookUpUuid = null;
            User lookupBdUser = null;
            Member lookUpMember = null;
            JSONArray bedData = new JSONArray(0);
            JSONArray javaData = new JSONArray(0);

            if (userParam != null) {
                lookUpMember = userParam.getAsMember();
                lookupBdUser = User.getFromMember(lookUpMember);
                bedData = lookupBdUser.toJson().optJSONArray("bedData");
                javaData = lookupBdUser.toJson().optJSONArray("javaData");
            }

            if (uuidParam != null) {
                lookUpUuid = uuidParam.getAsString();
                final BedrockData bedSingeleData = DaoManager.getBedrockDataDao().findWithUuid(lookUpUuid);
                final JavaData javaSingeleData = DaoManager.getJavaDataDao().findWithUuid(lookUpUuid);

                final Integer userId = bedSingeleData != null
                        ? bedSingeleData.getUserId()
                        : javaSingeleData.getUserId();

                lookupBdUser = DaoManager.getUsersDao().findUser(userId);
                lookUpMember = plugin.getGuildManager().findMember(lookupBdUser.getDiscordId());

                if (bedSingeleData != null) {
                    bedData.put(bedSingeleData.toJson());
                }

                if (javaSingeleData != null) {
                    javaData.put(javaSingeleData.toJson());
                }
            }

            // User NOT-FOUND
            if (lookupBdUser == null) {
                sb.append("❌ User's not registered...");

                submitReplyEphemeral(sb.toString());
                tx.setData("state", "notFound");
                tx.finish(SpanStatus.NOT_FOUND);
                return;
            }

            try {
                lookUpMember.kick();
            } catch (Exception e) {
                sb.append("❌ " + e.getMessage() + "\n");
                tx.setData("error-state", "error diccord-kick unauthorized");
                SentryService.captureEx(e);
            }

            final String olduserData = lookupBdUser.toJson().toString(1);
            sb.append("User is registered here's the old user infos:\n");
            sb.append("```json\n" + olduserData +"\n```");

            int i = 0;
            final JsonArray j_ids = new JsonArray();
            final JsonArray b_ids = new JsonArray();

            for (i = 0; i < bedData.length(); i++) {
                b_ids.add(bedData.getJSONObject(i).getString("uuid"));
                plugin.getBukkitManager().banPlayer(b_ids.get(i).getAsString());
            }
            sb.append("Deleted `" + i + "` Becrock® accounts --> `" + b_ids.toString() + "`\n");

            for (i = 0; i < javaData.length(); i++) {
                j_ids.add(javaData.getJSONObject(i).getString("uuid"));
                plugin.getBukkitManager().banPlayer(j_ids.get(i).getAsString());
            }
            sb.append("Deleted `" + i + "`  Java® accounts --> `"+ j_ids.toString() + "`\n");

            if(userParam != null) {
                lookupBdUser.delete(DaoManager.getUsersDao());
            }

            submitReplyEphemeral(sb.toString());
            tx.setData("state", "success");
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