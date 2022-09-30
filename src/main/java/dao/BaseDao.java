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

public class BaseDao implements IDao {

    private Logger logger;
    protected String tablename;
    protected PooledDatasource datasource;
    protected Connection connection;

    public BaseDao(PooledDatasource poolDs) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getName());
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
            e.printStackTrace();
        }
    }

    protected Connection getConnection() {
        if(connection == null) {
            try {
                this.connection = this.datasource.getConnection();
                logger.info("Db Connection ++");
            } catch (SQLException e) {
                logger.warning("Unable to get a BD connection");
                e.printStackTrace();
            }    
        }
        try {
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    protected Connection getConnectionTx() {
        if(connection == null) {
            try {
                logger.info("Db TX Connection ++");
                this.connection = this.datasource.getConnection();
            } catch (SQLException e) {
                logger.warning("Unable to get a BD TX connection");
                e.printStackTrace();
            }    
        }
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }

        if (results == null || results.length() < 1) {
            return null;
        }
        
        return results;
    }

    @Override
    public Integer save(JSONObject json) {
        throw new NotImplementedException();
    }
}
