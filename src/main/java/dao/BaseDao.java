package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.lang.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import services.sentry.SentryService;

public class BaseDao implements IDao {

    private Logger logger;
    protected String tableName;
    protected ComboPooledDataSource datasource;
    protected Connection connection;

    public BaseDao(ComboPooledDataSource poolDs) {
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
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
                return this.connection;
            }
        } catch (Exception e) {
            logger.warning("Exception while getting a BD connection\nTrying again...");
            try {
                this.connection = this.datasource.getConnection();
                this.connection.setAutoCommit(true);
                return this.connection;
            } catch (Exception err) {
                logger.warning("Unable to get a BD connection");
                SentryService.captureEx(e);
                SentryService.captureEx(err);
                return this.connection;
            }
        }
        return this.connection;
    }

    public JSONObject find(Integer id) {

        if (id < 1) {
            return null;
        }
        
        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tableName + " WHERE BINARY id = ?;";
            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setInt(1, id);
            preStmt.execute();

            
            final ResultSet resultSet = preStmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);
            preStmt.close();
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
            String sql = "SELECT * FROM " + this.tableName + ";";

            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.executeQuery();

            
            final ResultSet resultSet = preStmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);
            preStmt.close();
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
            String sql = "DELETE FROM " + this.tableName + " WHERE BINARY id = ? LIMIT 1;";
            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setInt(1, id);
            preStmt.execute();
            
            id = preStmt.getUpdateCount() > 0 ? id : null;
            preStmt.close();
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
