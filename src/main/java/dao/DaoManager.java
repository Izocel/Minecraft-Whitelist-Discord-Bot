package dao;

import java.util.logging.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import helpers.DbPoolFactory;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import services.sentry.SentryService;

public class DaoManager {
    protected static ComboPooledDataSource dataSource = null;
    protected static UsersDao usersDao = null;
    protected static BedrockDataDao bedrockDataDao = null;
    protected static JavaDataDao javaDataDao = null;
    private Logger logger;

    public DaoManager(WhitelistJe plugin) {
        try {
            ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("DaoManager");

            this.logger = Logger.getLogger("WJE:" + getClass().getSimpleName());
            dataSource = DbPoolFactory.getMysqlPool(plugin.getConfigManager());

            process.setStatus(SpanStatus.OK);
            process.finish();
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public static UsersDao getUsersDao() {
        if (usersDao == null) {
            usersDao = new UsersDao(dataSource);
        }
        return usersDao;
    }

    public static BedrockDataDao getBedrockDataDao() {
        if (bedrockDataDao == null) {
            bedrockDataDao = new BedrockDataDao(dataSource);
        }
        return bedrockDataDao;
    }

    public static JavaDataDao getJavaDataDao() {
        if (javaDataDao == null) {
            javaDataDao = new JavaDataDao(dataSource);
        }
        return javaDataDao;
    }
}
