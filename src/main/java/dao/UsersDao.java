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
    private String tablename2;
    private String tablename3;

    public UsersDao(ComboPooledDataSource poolDs) {
        super(poolDs);
        this.tablename = "wje_users";
        this.tablename2 = "wje_java_data";
        this.tablename3 = "wje_bedrock_data";
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
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
        final String lang = sqlProps.optString("lang");

        try {
            int status = -1;

            // New user
            if (id < 1) {

                String sql = "INSERT INTO " + this.tablename + " (discord_id, discord_tag, lang) " +
                    "VALUES (?,?,?);";

                final PreparedStatement pstmt = this.getConnection().prepareStatement(sql, new String[] { "id" });
                pstmt.setString(1, discordId);
                pstmt.setString(2, discordTag);
                pstmt.setObject(3, lang.length() > 0 ? lang : null);
                status = pstmt.executeUpdate();
                ResultSet generatedKeys = pstmt.getGeneratedKeys();

                while (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                    break;
                }

                pstmt.close();
                this.closeConnection();
            }

            // Update User
            else if(this.findUser(id) != null){
                String sql = "UPDATE " + this.tablename + " SET " +
                        "discord_id = ?," +
                        "discord_tag = ?," +
                        "lang = ?," +
                        "updated_at = CURRENT_TIMESTAMP " +
                        "WHERE id = ?;";

                final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
                pstmt.setString(1, discordId);
                pstmt.setString(2, discordTag);
                pstmt.setObject(3, lang.length() > 0 ? lang : null);
                pstmt.setInt(4, id);
                status = pstmt.executeUpdate();
                id = pstmt.getUpdateCount() > 0 ? id : -1;
                pstmt.close();
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
            String sql = "SELECT * FROM " + this.tablename + " WHERE discord_id = ? LIMIT 1";
            final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
            pstmt.setString(1, discordId);
            pstmt.executeQuery();

            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);
            pstmt.close();
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
            String sql = "SELECT * FROM " + this.tablename + " WHERE discord_tag = ? LIMIT 1";
            final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
            pstmt.setString(1, discordTag);
            pstmt.executeQuery();

            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);
            pstmt.close();
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
