package functions;

import java.util.logging.Logger;

import org.json.JSONArray;

import dao.DaoManager;
import discord.GuildManager;
import main.WhitelistJe;

public class MyFunctions {
    
    private WhitelistJe plugin;
    private Logger logger;

    public MyFunctions(WhitelistJe pluin) {
        this.plugin = pluin;
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
    }

    public void dumpDbDatas(Boolean toAdminChannel) {
        JSONArray x = DaoManager.getUsersDao().findAll();
        JSONArray y = DaoManager.getBedrockDataDao().findAll();
        JSONArray z = DaoManager.getJavaDataDao().findAll();

        if(toAdminChannel) {
            final GuildManager manager = plugin.getGuildManager();
            if(x != null && x.length() > 0)
                manager.getAdminChannel().sendMessage("```json\n" + x.toString() + "\n```").submit(true);

            if(y != null && y.length() > 0)
                manager.getAdminChannel().sendMessage("```json\n" + y.toString() + "\n```").submit(true);

            if(z != null && z.length() > 0)
                manager.getAdminChannel().sendMessage("```json\n" + z.toString() + "\n```").submit(true);
        }

        if(x != null && x.length() > 0)
            this.logger.info("" + x.toString());

        if(y != null && y.length() > 0)
            this.logger.info("" + y.toString());

        if(z != null && z.length() > 0)
            this.logger.info("" + z.toString());

    }
}
