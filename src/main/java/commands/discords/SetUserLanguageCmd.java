package commands.discords;

import main.WhitelistJe;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SetUserLanguageCmd extends WjeUserOnlyCmd {

    private final static String KEY_CMD_NAME = "CMD_SETLOCAL";
    private final static String KEY_CMD_DESC = "DESC_SETLOCAL";
    private final static String KEY_PARAM_LANG = "PARAM_LOCAL_SETLOCAL";
    private final static String KEY_PARAM_LANG_LABEL = "PARAM_LOCAL_LABEL";

    public static void REGISTER_CMD(JDA jda, WhitelistJe plugin) {
        String cmdName = LOCAL.translate(KEY_CMD_NAME);
        String cmdDesc = LOCAL.translate(KEY_CMD_DESC);
        final String localLangParam = LOCAL.translate(KEY_PARAM_LANG);
        final String localLangLabel = LOCAL.translate(KEY_PARAM_LANG_LABEL);

        // Traduction
        jda.addEventListener(new SetUserLanguageCmd(plugin));
        jda.upsertCommand(cmdName, cmdDesc)
        .addOption(OptionType.STRING, localLangParam, localLangLabel, false)
            .submit(true);
    }

    public SetUserLanguageCmd(WhitelistJe plugin) {
        super(plugin,
            "SetUserLanguageCmd",
            "CMD_SETLOCAL",
            "SetUserLanguage",
            "Change local"
        );
    }

    @Override
    protected final void execute() {
        final String oldLang = user.getLang();
        final String lang = this.getStringParam(KEY_PARAM_LANG);

        final StringBuilder sb = new StringBuilder();
        sb.append(useTranslator("LANG_CURRENT") + ": ");
        sb.append("`" + user.getLang() + "`");

        if(lang == null || oldLang.equals(lang.toUpperCase())) {
            this.submitReplyEphemeral(sb.toString());
            return;
        }

        if(lang != null) {
            user.setLang(lang);
            user.saveUser();
        }

        sb.delete(0, sb.length());
        sb.append(translateBy("LANG_CHANGED", user.getLang()) + ": ");
        sb.append("`" + oldLang + "` --> `" + user.getLang() + "`");
        
        this.submitReplyEphemeral(sb.toString());
    }

}
