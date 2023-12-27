package dao;

import java.util.logging.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import helpers.DbPoolFactory;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmc;
import services.sentry.SentryService;

public class DaoManager {
    protected static ComboPooledDataSource dataSource = null;
    protected static UsersDao usersDao = null;
    protected static BedrockDataDao bedrockDataDao = null;
    protected static JavaDataDao javaDataDao = null;
    protected static RewardsDAO rewardsDAO;
    private Logger logger;

    public DaoManager(WhitelistDmc plugin) {
        try {
            ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                    .startChild("DaoManager");

            this.logger = Logger.getLogger("WDMC:" + getClass().getSimpleName());
            dataSource = DbPoolFactory.getPoolConnection(plugin.getConfigManager());

            process.setStatus(SpanStatus.OK);
            process.finish();
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public static ComboPooledDataSource getDatasource() {
        return dataSource;
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

    public static RewardsDAO getRewardsDAO() {
        if (rewardsDAO == null) {
            rewardsDAO = new RewardsDAO(dataSource);
        }
        return rewardsDAO;
    }
}
