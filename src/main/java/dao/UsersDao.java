package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import services.sentry.SentryService;
import models.User;

public class UsersDao extends BaseDao {

    private Logger logger;
    private String tableName2;
    private String tableName3;

    public UsersDao(ComboPooledDataSource poolDs) {
        super(poolDs);
        this.tableName = "wdmc_users";
        this.tableName2 = "wdmc_java_data";
        this.tableName3 = "wdmc_bedrock_data";
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
    }

    public User findUser(Integer id) {
        JSONObject res = this.find(id);
        return res == null ? null : new User(res);
    }

    @Override
    public Integer save(JSONObject sqlProps) {
        int id = sqlProps.optInt("id");
        final String discordId = sqlProps.optString("discord_id");
        final String discordTag = sqlProps.optString("discord_tag");
        final String avatarUrl = sqlProps.optString("avatar_url");
        final String lang = sqlProps.optString("lang");

        try {
            int status = -1;

            // New user
            if (id < 1) {

                String sql = "INSERT INTO " + this.tableName + " (discord_id, discord_tag, avatar_url, lang) " +
                        "VALUES (?,?,?,?);";

                final PreparedStatement preStmt = this.getConnection().prepareStatement(sql, new String[] { "id" });
                preStmt.setString(1, discordId);
                preStmt.setString(2, discordTag);
                preStmt.setString(3, avatarUrl.length() > 0 ? avatarUrl : null);
                preStmt.setObject(4, lang.length() > 0 ? lang : null);
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
            else if (this.findUser(id) != null) {
                String sql = "UPDATE " + this.tableName + " SET " +
                        "discord_id = ?," +
                        "discord_tag = ?," +
                        "avatar_url = ?," +
                        "lang = ?," +
                        "updated_at = CURRENT_TIMESTAMP " +
                        "WHERE id = ?;";

                final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
                preStmt.setString(1, discordId);
                preStmt.setString(2, discordTag);
                preStmt.setString(3, avatarUrl.length() > 0 ? avatarUrl : null);
                preStmt.setObject(4, lang.length() > 0 ? lang : null);
                preStmt.setInt(5, id);
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

    public User findByDiscordId(String discordId) {

        JSONArray results = new JSONArray();
        try {
            String sql = "SELECT * FROM " + this.tableName + " WHERE discord_id = ? LIMIT 1";
            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setString(1, discordId);
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

        return new User(results.getJSONObject(0));
    }

    public User findByDiscordTag(String discordTag) {

        JSONArray results = new JSONArray();
        try {
            String sql = "SELECT * FROM " + this.tableName + " WHERE discord_tag = ? LIMIT 1";
            final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
            preStmt.setString(1, discordTag);
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

        return new User(results.getJSONObject(0));
    }

}
