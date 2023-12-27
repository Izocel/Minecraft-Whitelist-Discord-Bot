package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import services.sentry.SentryService;

public class RewardsDAO extends BaseDao {
    private Logger logger;
    protected String userTable;

    public RewardsDAO(ComboPooledDataSource poolDs) {
        super(poolDs);
        this.userTable = "wdmc_users";
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
    }

    public JSONArray findWithUserKey(String userKey) {
        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tableName + " WHERE user_key = ?";
            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setString(1, userKey);
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

    public JSONArray findWithName(String calendarName) {
        JSONArray results = new JSONArray();
        try {
            String sql = "SELECT * FROM " + this.tableName + " WHERE calendar_name = ?";
            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setString(1, calendarName);
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

    public JSONArray findWithType(String calendarType) {
        JSONArray results = new JSONArray();
        try {
            String sql = "SELECT * FROM " + this.tableName + " WHERE calendar_type = ?";
            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setString(1, calendarType);
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

    public Integer wipeClaimedByType(String calendarType, String stopDate) {
        int status = -1;

        try {
            String sql = "DELETE FROM " + this.tableName + " WHERE calendar_type = ? and created_at < ?;";

            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setString(1, calendarType);
            preStmt.setString(2, stopDate);
            preStmt.execute();

            status = preStmt.getUpdateCount();
            preStmt.close();
            this.closeConnection();

            preStmt.close();
            this.closeConnection();
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return status;
    }

    public boolean wipeClaimedByName(String calendarName, String stopDate) {
        boolean status = false;

        try {
            String sql = "DELETE FROM " + this.tableName + " WHERE calendar_name = ? and created_at < ?;";

            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setString(1, calendarName);
            preStmt.setString(2, stopDate);
            preStmt.execute();

            status = preStmt.getUpdateCount() >= 0;
            preStmt.close();
            this.closeConnection();

            preStmt.close();
            this.closeConnection();
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return status;
    }

    public Integer wipeClaimedByDate(String stopDate) {
        int status = -1;

        try {
            String sql = "DELETE FROM " + this.tableName + " WHERE created_at < ?;";

            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setString(1, stopDate);
            preStmt.execute();

            status = preStmt.getUpdateCount();
            preStmt.close();
            this.closeConnection();

            preStmt.close();
            this.closeConnection();
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return status;
    }

    public boolean wipeAll() {
        boolean status = false;

        try {
            String sql = "DELETE FROM " + this.tableName + ";";
            
            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.execute();

            status = preStmt.getUpdateCount() >= 0;
            preStmt.close();
            this.closeConnection();

            preStmt.close();
            this.closeConnection();
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        return status;
    }

    @Override
    public Integer save(JSONObject sqlProps) {
        int id = sqlProps.optInt("id");
        final String userKey = sqlProps.optString("user_key");
        final String calendarName = sqlProps.optString("calendar_name");
        final String calendarType = sqlProps.optString("calendar_type");
        final String claimedAt = sqlProps.optString("claimed_at");
        final String data = sqlProps.optString("data");

        try {
            int status = -1;

            // Insert
            if (id < 1) {

                String sql = "INSERT INTO " + this.tableName + " (user_key, calendar_name, calendar_type, data) " +
                        "VALUES (?,?,?,?);";

                final PreparedStatement preStmt = this.getConnection().prepareStatement(sql, new String[] { "id" });
                preStmt.setString(1, userKey);
                preStmt.setString(2, calendarName);
                preStmt.setString(3, calendarType);
                preStmt.setString(4, data);
                status = preStmt.executeUpdate();
                ResultSet generatedKeys = preStmt.getGeneratedKeys();

                while (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                    break;
                }

                preStmt.close();
                this.closeConnection();
            }

            // Update
            else if (this.find(id) != null) {
                String sql = "UPDATE " + this.tableName + " SET " +
                        "data = ?," +
                        "claimed_at = ?," +
                        "updated_at = CURRENT_TIMESTAMP " +
                        "WHERE id = ?;";

                final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
                preStmt.setString(1, data);
                preStmt.setObject(2, claimedAt.length() > 0 ? claimedAt : null);
                preStmt.setInt(3, id);
                status = preStmt.executeUpdate();
                id = preStmt.getUpdateCount() > 0 ? id : -1;
                preStmt.close();
                this.closeConnection();
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return id;
    }
}
