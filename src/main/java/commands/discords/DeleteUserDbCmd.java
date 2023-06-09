package commands.discords;

import org.json.JSONArray;

import dao.DaoManager;
import discord.GuildManager;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import services.sentry.SentryService;

/**
 * Will delete all users data, remove all account from whitelist
 * Also kick from Minecraft and Discord
 */
public class DeleteUserDbCmd extends WjeUserOnlyCmd {

    private final static String KEY_CMD_NAME = "CMD_REMOVEDB_USERS";
    private final static String KEY_CMD_DESC = "DESC_REMOVEDB_USERS";
    private final static String KEY_PARAM_USER = "PARAM_MEMBER";
    private final static String KEY_PARAM_USER_LABEL = "PARAM_MEMBER_LABEL";

    public static void REGISTER_CMD(JDA jda, WhitelistJe plugin) {
        String cmdName = LOCAL.translate(KEY_CMD_NAME);
        String cmdDesc = LOCAL.translate(KEY_CMD_DESC);
        final String localUserParam = LOCAL.translate(KEY_PARAM_USER);
        final String localUserLabel = LOCAL.translate(KEY_PARAM_USER_LABEL);

        // Traduction
        jda.addEventListener(new DeleteUserDbCmd(plugin));
        jda.upsertCommand(cmdName, cmdDesc)
                .addOption(OptionType.USER, localUserParam, localUserLabel, true)
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
            
            final Member lookUpMember = userParam.getAsMember();
            final User lookupBdUser =  User.getFromMember(lookUpMember);

            // Single user NOT-FOUND
            if(lookupBdUser == null) {
                sb.append("User's not registered...");

                submitReplyEphemeral(sb.toString());
                tx.setData("state", "notFound");
                tx.finish(SpanStatus.NOT_FOUND);
                return;
            }

            try {
                lookUpMember.kick();
            } catch (Exception e) {
                submitReplyEphemeral("❌ " + e.getMessage());
                tx.setData("error-state", "error");
                tx.finish(SpanStatus.INTERNAL_ERROR);
                SentryService.captureEx(e);
            }
            
            int i = 0;
            JSONArray bedData = lookupBdUser.toJson().optJSONArray("bedData");
            sb.append("User is registered (now deleted) here's the info:\n");
            sb.append("```json\n" + lookupBdUser.toJson().toString(1) + "\n```");

            if(bedData != null) {
                for (i = 0; i < bedData.length(); i++) {
                    plugin.getBukkitManager().banPlayer(bedData.getJSONObject(i).getString("uuid"));
                }
                sb.append("Deleted`" + i + "` Java® accounts\n");
            }

            JSONArray javaData = lookupBdUser.toJson().optJSONArray("javaData") ;
            if(javaData != null) {
                for (i = 0; i < javaData.length(); i++) {
                    plugin.getBukkitManager().banPlayer(javaData.getJSONObject(i).getString("uuid"));
                }
                sb.append("Deleted`" + i + "` Becrock® accounts\n");
            }
            
            lookupBdUser.delete(DaoManager.getUsersDao());
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