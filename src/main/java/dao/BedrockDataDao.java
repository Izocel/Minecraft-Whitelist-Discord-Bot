package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import models.BedrockData;
import services.sentry.SentryService;

public class BedrockDataDao extends BaseDao {

    private Logger logger;

    public BedrockDataDao(ComboPooledDataSource poolDs) {
        super(poolDs);
        this.tableName = "`wdmc_bedrock_data`";
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
    }

    public JSONArray findWithUser(Integer userId) {

        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tableName + " WHERE user_id = ?";
            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setInt(1, userId);
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

    @Override
    public Integer save(JSONObject sqlProps) {
        int id = sqlProps.optInt("id");
        int userId = sqlProps.optInt("user_id");
        final String pseudo = sqlProps.optString("pseudo");
        final String uuid = sqlProps.optString("uuid");
        final String msgId = sqlProps.optString("msg_id");
        final String avatarUrl = sqlProps.optString("avatar_url", null);

        final Object isConfirmed = sqlProps.opt("confirmed");
        final Object isAllowed = sqlProps.opt("allowed");

        final int allowed = isAllowed.equals(true) ? 1 : 0;
        final int confirmed = isConfirmed.equals(true) ? 1 : 0;

        String acceptedBy = sqlProps.optString("accepted_by");
        String revokedBy = sqlProps.optString("revoked_by");
        acceptedBy = acceptedBy.length() > 0 ? acceptedBy : null;
        revokedBy = revokedBy.length() > 0 ? revokedBy : null;

        if (userId < 1 || pseudo.equals("") || uuid.equals("") || msgId.equals("")) {
            this.logger.warning("Missing informations to save user entity");
            return -1;
        }

        if (acceptedBy == null && revokedBy == null) {
            this.logger.warning("Missing informations to save user entity");
            return -1;
        }

        try {
            int status = -1;
            BedrockData found = this.findWithUuid(uuid);

            // New user
            if (found == null) {
                id = -1;

                String sql = "INSERT INTO " + this.tableName + " (user_id, pseudo, uuid, accepted_by, " +
                        "revoked_by, msg_id, confirmed, allowed, avatar_url, created_at, updated_at) " +
                        "VALUES (?,?,?,?,?,?,?,?,?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";

                final PreparedStatement preStmt = this.getConnection().prepareStatement(sql, new String[] { "id" });
                preStmt.setInt(1, userId);
                preStmt.setString(2, pseudo);
                preStmt.setString(3, uuid);
                preStmt.setObject(4, acceptedBy);
                preStmt.setObject(5, revokedBy);
                preStmt.setString(6, msgId);
                preStmt.setInt(7, confirmed);
                preStmt.setInt(8, allowed);
                preStmt.setObject(9, avatarUrl);
                status = preStmt.executeUpdate();
                ResultSet generatedKeys = preStmt.getGeneratedKeys();

                while (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                    break;
                }

                preStmt.close();
                this.closeConnection();
            }

            // Update User
            else {
                String sql = "UPDATE " + this.tableName + " SET " +
                        "user_id = ?," +
                        "pseudo = ?," +
                        "uuid = ?," +
                        "accepted_by = ?," +
                        "revoked_by = ?," +
                        "msg_id = ?," +
                        "confirmed = ?," +
                        "allowed = ?," +
                        "avatar_url = ?," +
                        "updated_at = CURRENT_TIMESTAMP " +
                        "WHERE id = ?;";

                final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
                preStmt.setInt(1, userId);
                preStmt.setString(2, pseudo);
                preStmt.setString(3, uuid);
                preStmt.setObject(4, acceptedBy);
                preStmt.setObject(5, revokedBy);
                preStmt.setString(6, msgId);
                preStmt.setInt(7, confirmed);
                preStmt.setInt(8, allowed);
                preStmt.setObject(9, avatarUrl);
                preStmt.setObject(10, id);
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

    public JSONArray findAllowed() {

        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tableName + " WHERE allowed = 1";
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

    public void setPlayerUUID(Integer userId, UUID UUID, boolean tempConfirmed) {
        try {
            String sql = "UPDATE " + this.tableName + " SET mc_uuid = ? WHERE user_id = ?;";
            if (tempConfirmed) {
                sql = "UPDATE " + this.tableName + " SET mc_uuid = ?, confirmed = 1 WHERE user_id = ?;";
            }

            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setString(1, UUID.toString());
            preStmt.setInt(2, userId);
            preStmt.executeUpdate();
            preStmt.close();
            this.closeConnection();

        } catch (SQLException e) {
            SentryService.captureEx(e);
        }
    }

    public BedrockData findWithUuid(String uuid) {

        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tableName + " WHERE uuid = ?";
            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setString(1, uuid);
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

        return new BedrockData(results.getJSONObject(0));
    }

}
