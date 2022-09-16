package functions;

import java.util.List;
import java.util.logging.Logger;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class GuildManager {

    private Logger logger;
    private Guild guild;

    public GuildManager(Guild guild) {
        this.guild = guild;
        this.logger = Logger.getLogger("WJE:" + this.getClass().getName());
    }

    public Role findRole(String roleId) {
        try {
            Role guildRole = this.guild.getRoleById(roleId);
            if(guildRole == null) {
                this.logger.warning("Role was not found in the guild");
            }
            return guildRole;
    
        } catch (Exception e) {
            this.logger.warning("Problem while looking for a role on a guild");
            e.printStackTrace();
            return null;
        }
    }

    public Member findMember(String memberId) {
        try {
            Member guildMember = this.guild.getMemberById(memberId);
            if(guildMember == null) {
                this.logger.warning("Member was not found in the guild");
            }
            return guildMember;
    
        } catch (Exception e) {
            this.logger.warning("Problem while looking for a member on a guild");
            e.printStackTrace();
            return null;
        }
    }

    public void setRole(String memberId, String roleId) {
        Role role = this.findRole(roleId);
        this.guild.addRoleToMember(memberId, role).queue();
    }

    public void removeRole(String roleId, String memberId) {
        Role role = this.findRole(roleId);
        this.guild.removeRoleFromMember(memberId, role).queue();
    }

    public boolean hasRole(String memberId, String roleId) {

        try {
            Member foundMember = this.findMember(memberId);
            if(foundMember == null) {
                this.logger.warning("Member was not found in the guild");
            }

            Role guildRole = this.findRole(roleId);
            if(guildRole == null) {
                this.logger.warning("Role was not found in the guild");
            }
    
            List<Role> memberRoles = foundMember.getRoles();
            for (Role role : memberRoles) {
                if(role.getId().equals(guildRole.getId())) {
                    return true;
                }
            }
        } catch (Exception e) {
            this.logger.warning("Problem while looking for a role on a guild member");
            e.printStackTrace();
            return false;
        }

        return false;
    }
}
