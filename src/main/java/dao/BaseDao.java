package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.lang.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONObject;

import helpers.PooledDatasource;
import services.sentry.SentryService;

public class BaseDao implements IDao {

    private Logger logger;
    protected String tablename;
    protected PooledDatasource datasource;
    protected Connection connection;

    public BaseDao(PooledDatasource poolDs) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.datasource = poolDs;
    }

    protected void closeConnection() {
        if(connection == null) {
            return;
        }
        try {
            this.connection.close();
            this.connection = null;
        } catch (SQLException e) {
            logger.warning("Unable to close the BD connection");
            SentryService.captureEx(e);
        }
    }

    protected Connection getConnection() {
        try {
            if(this.connection == null || this.connection.isClosed()) {
                this.connection = this.datasource.getConnection();
                this.connection.setAutoCommit(true);
                logger.info("Db Connection ++");
            }
        } catch (Exception e) {

            try {
                this.connection = this.datasource.getConnection();
                this.connection.setAutoCommit(true);
                logger.info("Db Connection ++");
            } catch (Exception err) {
                logger.warning("Unable to get a BD connection");
                SentryService.captureEx(err);
                return this.connection;
            }

            logger.warning("Unable to get a BD connection");
            SentryService.captureEx(e);
        }

        return this.connection;
    }

    public JSONObject find(Integer id) {

        if (id < 1) {
            return null;
        }
        
        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE BINARY id = ?;";
            final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.execute();

            
            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);
            this.closeConnection();

        } catch (SQLException e) {
            SentryService.captureEx(e);
        }
        
        if (results == null || results.length() < 1) {
            return null;
        }
        
        return (JSONObject) results.get(0);
    }

    public JSONArray findAll() {
        
        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tablename + ";";

            final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
            pstmt.executeQuery();

            
            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);
            this.closeConnection();

        } catch (SQLException e) {
            SentryService.captureEx(e);
        }

        if (results == null || results.length() < 1) {
            return null;
        }
        
        return results;
    }

    public Integer delete(Integer id) {
        if(id == null || id < 1) {
            return null;
        }

        try {
            String sql = "DELETE FROM " + this.tablename + " WHERE BINARY id = ? LIMIT 1;";
            final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.execute();
            
            id = pstmt.getUpdateCount() > 0 ? id : null;
            this.closeConnection();

        } catch (SQLException e) {
            SentryService.captureEx(e);
        }
        
        return id;
    }

    @Override
    public Integer save(JSONObject json) {
        throw new NotImplementedException();
    }
}
