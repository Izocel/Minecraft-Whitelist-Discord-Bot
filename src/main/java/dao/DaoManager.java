package dao;

import java.util.logging.Logger;

import configs.ConfigManager;
import helpers.DbPoolFactory;
import helpers.PooledDatasource;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import services.sentry.SentryService;

public class DaoManager {
    protected PooledDatasource dataSource = null;
    protected UsersDao usersDao = null;
    private Logger logger;

    public DaoManager(ConfigManager configs, WhitelistJe plugin) {
        try {
            ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("DaoManager");

            this.logger = Logger.getLogger("WJE:" + getClass().getSimpleName());
            this.dataSource = DbPoolFactory.getMysqlPool(configs);

            process.setStatus(SpanStatus.OK);
            process.finish();
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
