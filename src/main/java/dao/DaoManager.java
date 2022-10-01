package dao;

import java.util.logging.Logger;

import helpers.PooledDatasource;

public class DaoManager {
    protected PooledDatasource dataSource = null;

    protected UsersDao usersDao = null;
    private Logger logger;

    public DaoManager(PooledDatasource dataSource) {
        this.logger = Logger.getLogger("WJE:" + getClass().getSimpleName());
        this.dataSource = dataSource;
    }

    public UsersDao getUsersDao() {
        if (this.usersDao == null) {
            this.usersDao = new UsersDao(dataSource);
        }
        return this.usersDao;
    }
}
