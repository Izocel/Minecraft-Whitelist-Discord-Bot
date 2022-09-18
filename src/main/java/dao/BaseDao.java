package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.lang.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaseDao implements IDao {

    private Logger logger;
    protected String tablename;
    protected Connection connection;

    public BaseDao(Connection connection) {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getName());
        this.connection = connection;
    }

    public JSONObject find(Integer id) {

        if (id < 1) {
            return null;
        }

        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE id = ?;";
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.execute();

            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (results == null) {
            return null;
        }

        return (JSONObject) results.get(0);
    }

    public JSONArray findAll() {
        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tablename + ";";

            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.executeQuery();

            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public Integer save(JSONObject json) {
        throw new NotImplementedException();
    }
}
