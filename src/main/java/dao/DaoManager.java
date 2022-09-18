package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class DaoManager {
    protected DataSource dataSource = null;
    protected Connection connection = null;

    protected UsersDao usersDao = null;
    private Logger logger;

    public DaoManager(DataSource dataSource) {
        this.logger = Logger.getLogger("WJE:" + getClass().getName());
        this.dataSource = dataSource;
    }

    public UsersDao getUsersDaoTx() {
        if (this.usersDao == null) {
            this.usersDao = new UsersDao(getTxConnection());
        }
        return this.usersDao;
    }

    public UsersDao getUsersDao() {
        if (this.usersDao == null) {
            this.usersDao = new UsersDao(getConnection());
        }
        return this.usersDao;
    }

    protected Connection getConnection() {
        if (this.connection == null) {
            try {
                this.logger.info("New DB connection");
                this.connection = dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.connection;
    }

    protected Connection getTxConnection() {
        this.getConnection();
        try {
            this.logger.info("New DB TX connection");
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    public Object transaction(DaoCommand command) throws Exception {
        try {
            Object returnValue = command.execute(this);
            getConnection().commit();
            return returnValue;
        } catch (Exception e) {
            try {
                getConnection().rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e; // or wrap it before rethrowing it
        } finally {
            getConnection().setAutoCommit(true);
        }
    }

    public Object executeAndClose(DaoCommand command) {
        try {
            return command.execute(this);
        } finally {
            try {
                getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Object transactionAndClose(DaoCommand command) {

        return executeAndClose(new DaoCommand() {

            public Object execute(DaoManager manager) {
                try {
                    return manager.transaction(command);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }
}
