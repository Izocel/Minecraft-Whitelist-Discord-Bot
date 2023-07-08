package discord;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import configs.ConfigManager;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDMC;
import services.sentry.SentryService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuildManager {

    private Logger logger;
    private Guild guild;

    public String adminChannelId;
    public String botLogchannelId;
    public String javaLogChannelId;
    public String whitelistChannelId;
    private ConfigManager configManager;
    private String ownerId;
    private String adminRoleId;
    private String modoRoleId;
    private String devRoleId;
    private String helperRoleId;
    private String welcomeChannelId;
    private Locale locale;
    private String lang;

    public GuildManager(WhitelistDMC plugin) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("GuildManager");
        
        this.configManager = plugin.getConfigManager();
        this.guild = plugin.getDiscordManager().getGuild();
        this.setupLanguage();
        this.setupIds();

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    private void setupLanguage() {
        this.locale = this.guild.getLocale();
        this.lang = this.locale.getLanguage();

        if(this.lang.length() < 1) {
            this.lang = WhitelistDMC.LOCALES.getDefaultLang();
        }
    }

    private void setupIds() {
        this.welcomeChannelId = this.configManager.get("discordWelcomeChanelId", null);
        this.adminChannelId = this.configManager.get("discordAdminChanelId", null);
        this.whitelistChannelId = this.configManager.get("discordWhitelistChanelId", null);
        this.botLogchannelId = this.configManager.get("botLogChannelId", null);
        this.javaLogChannelId = this.configManager.get("javaLogChannelId", null);

        this.adminRoleId = this.configManager.get("discordAdminRoleId", null);
        this.modoRoleId = this.configManager.get("discordModeratorRoleId", null);
        this.devRoleId = this.configManager.get("discordDevRoleId", null);
        this.helperRoleId = this.configManager.get("discordHelperRoleId", null);

        this.ownerId = this.guild.getOwner().getId();
    }

    public Guild getGuild() {
        return this.guild;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public String getAdminRoleId() {
        return this.adminRoleId;
    }

    public Role findRole(String roleId) {
        try {
            Role guildRole = this.guild.getRoleById(roleId);
            if (guildRole == null) {
                this.logger.warning("Role was not found in the guild");
            }
            return guildRole;

        } catch (Exception e) {
            this.logger.warning("Problem while looking for a role on a guild");
            SentryService.captureEx(e);
            return null;
        }
    }

    public Member findMember(String userId) {
        try {
            Member guildMember = this.guild.getMemberById(userId);
            if (guildMember == null) {
                this.logger.warning("Member was not found in the guild");
            }
            return guildMember;

        } catch (Exception e) {
            this.logger.warning("Problem while looking for a member on a guild");
            SentryService.captureEx(e);
            return null;
        }
    }

    public void setRole(String memberId, String roleId) {
        Role role = this.findRole(roleId);
        this.guild.addRoleToMember(memberId, role).submit(true);
    }

    public void removeRole(String roleId, String memberId) {
        Role role = this.findRole(roleId);
        this.guild.removeRoleFromMember(memberId, role).submit(true);
    }

    public boolean verifyRole(String memberId, String roleId) {

        try {
            Member foundMember = this.findMember(memberId);
            if (foundMember == null) {
                this.logger.warning("Member was not found in the guild");
            }

            Role guildRole = this.findRole(roleId);
            if (guildRole == null) {
                this.logger.warning("Role was not found in the guild");
            }

            List<Role> memberRoles = foundMember.getRoles();
            for (Role role : memberRoles) {
                if (role.getId().equals(guildRole.getId())) {
                    return true;
                }
            }
        } catch (Exception e) {
            this.logger.warning("Problem while looking for a role on a guild member");
            SentryService.captureEx(e);
            return false;
        }

        return false;
    }

    public String getModoRoleId() {
        return this.modoRoleId;
    }

    public String getDevRoleId() {
        return this.devRoleId;
    }

    public String getHelperRoleId() {
        return this.helperRoleId;
    }

    public TextChannel getWelcomeChannel() {
        return this.guild.getTextChannelById(this.welcomeChannelId);
    }

    public TextChannel getAdminChannel() {
        return this.guild.getTextChannelById(this.adminChannelId);
    }

    public TextChannel getWListChannel() {
        return this.guild.getTextChannelById(this.whitelistChannelId);
    }

    public TextChannel getbotLogChannelId() {
        return this.guild.getTextChannelById(this.botLogchannelId);
    }

    public TextChannel getJavaLogChannel() {
        return this.guild.getTextChannelById(this.javaLogChannelId);
    }

    public boolean isOwner(String memberId) {
        return memberId.equals(this.getOwnerId());
    }

    public boolean isAdmin(String memberId) {
        return this.verifyRole(memberId, this.getAdminRoleId());
    }

    public boolean isModo(String memberId) {
        return this.verifyRole(memberId, this.getModoRoleId());
    }

    public boolean isDev(String memberId) {
        return this.verifyRole(memberId, this.getDevRoleId());
    }

    public boolean isHelper(String memberId) {
        return this.verifyRole(memberId, this.getDevRoleId());
    }
}
