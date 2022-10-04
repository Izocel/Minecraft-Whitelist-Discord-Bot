package dao;

import java.util.logging.Logger;

import configs.ConfigManager;
import helpers.DbPoolFactory;
import helpers.PooledDatasource;
import services.sentry.SentryService;

public class DaoManager {
    protected PooledDatasource dataSource = null;

    protected UsersDao usersDao = null;
    private Logger logger;

    public DaoManager(ConfigManager configs) {
        this.logger = Logger.getLogger("WJE:" + getClass().getSimpleName());
        try {
            this.dataSource = DbPoolFactory.getMysqlPool(configs);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public UsersDao getUsersDao() {
        if (this.usersDao == null) {
            this.usersDao = new UsersDao(dataSource);
        }
        return this.usersDao;
    }
}
