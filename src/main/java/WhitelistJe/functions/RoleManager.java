package WhitelistJe.functions;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class RoleManager {
    public void setRole(Guild guild, String roleId, Member member) {
        Role role = guild.getRoleById(roleId);
        guild.addRoleToMember(member, role).queue();
    }

    public void setRole(Guild guild, String roleId, String id) {
        Role role = guild.getRoleById(roleId);
        guild.addRoleToMember(id, role).queue();
    }

    public void removeRole(Guild guild, String roleId, Member member) {
        Role role = guild.getRoleById(roleId);
        guild.removeRoleFromMember(member, role).queue();
    }

    public void removeRole(Guild guild, String roleId, String id) {
        Role role = guild.getRoleById(roleId);
        guild.removeRoleFromMember(id, role).queue();
    }

    public boolean hasRole(Member member, String id) {
        Guild guild = member.getGuild();
        Role role = guild.getRoleById(id);
        if(member.getRoles().contains(role)) {
            return true;
        } else {
            return false;
        }
    }
}
