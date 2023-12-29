package commands.discords;

import main.WhitelistDmc;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class ReferralCmd extends UserOnlyCmd {
    public static void REGISTER_CMD(JDA jda, WhitelistDmc plugin) {
        String cmdName = LOCAL.translate("CMD_REFERRAL");
        String cmdDesc = LOCAL.translate("DESC_REFERRAL");
        final String userKey = LOCAL.translate("PARAM_MEMBER");
        final String userLabel = LOCAL.translate("PARAM_MEMBER_DESC");
        final String userCode = LOCAL.translate("PARAM_REF_CODE");
        final String userCodeLabel = LOCAL.translate("PARAM_REF_CODE_DESC");

        jda.addEventListener(new ReferralCmd(plugin));
        jda.upsertCommand(cmdName, cmdDesc)
                .addOption(OptionType.USER, userKey, userLabel, false)
                .addOption(OptionType.STRING, userCode, userCodeLabel, false)
                .submit(true);
    }

    public ReferralCmd(WhitelistDmc plugin) {
        super(plugin,
                "ReferralCmd",
                "CMD_REFERRAL",
                "ReferralCmd",
                "Inputs reference to user");
    }

    @Override
    protected final void execute() {
        final StringBuilder sb = new StringBuilder();
        
        final Member referredMember = this.getMemberParam("PARAM_MEMBER");
        final String code = this.getStringParam("PARAM_REF_CODE");

        // Insert if not exist
        // Fetch referral user DAO, find both entry line as:
        // [0] currentUser , [1] referencedUser

        this.submitReplyEphemeral(sb.toString());
    }
}
