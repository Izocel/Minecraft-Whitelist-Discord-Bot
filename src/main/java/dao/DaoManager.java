package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import main.PooledDatasource;

public class DaoManager {
    protected PooledDatasource dataSource = null;

    protected UsersDao usersDao = null;
    private Logger logger;

    public DaoManager(PooledDatasource dataSource) {
        this.logger = Logger.getLogger("WJE:" + getClass().getName());
        this.dataSource = dataSource;
    }

    public UsersDao getUsersDao() {
        if (this.usersDao == null) {
            this.usersDao = new UsersDao(dataSource);
        }
        return this.usersDao;
    }
}
