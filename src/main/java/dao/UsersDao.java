package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import models.User;

public class UsersDao extends BaseDao {

    private Logger logger;

	public UsersDao () {
        super();
        this.tablename = "users";
        this.logger = Logger.getLogger("WJE:" + this.getClass().getName());
		this.creds = this.getCredentials();
	}

    public Integer saveUser(JSONObject sqlProps) {

        final int id = sqlProps.optInt("id");
        final String mcName = sqlProps.optString("mc_name");
        final String discordTag = sqlProps.optString("discord_tag");
        final int acceptedBy = sqlProps.optInt("accepted_by");
        final int revokedBy = sqlProps.optInt("revoked_by");
        final String mcUUID = sqlProps.optString("mc_uuid");
        final String msgId = sqlProps.optString("msg_id");
        String createdAt = sqlProps.optString("created_at");
        final boolean confirmed = sqlProps.optBoolean("confirmed");
        final boolean alllowed = sqlProps.optBoolean("alllowed");

        try {
            User found = this.find(id);

            // New user
            if(found.getId() < 1) {

                if(discordTag.equals("") || msgId.equals("")) {
                    this.logger.warning("Missing informations to save user entity");
                    return -1;
                }

                String sql = "INSERT(mc_name, discord_tag, accepted_by, " +
                "revoked_by, mc_uuid, msg_id, created_at, confirmed, alllowed) " + 
                "INTO " + this.tablename + " " +
                "VALUES(?,?,?,?,?,?,?,?,?);";

                this.open();
                final PreparedStatement pstmt = this.connection.prepareStatement(sql);
                pstmt.setString(1, mcName);
                pstmt.setString(2, discordTag);
                pstmt.setInt(3, acceptedBy);
                pstmt.setInt(4, revokedBy);
                pstmt.setString(5, mcUUID);
                pstmt.setString(6, msgId);
                pstmt.setString(7, createdAt);
                pstmt.setBoolean(8, confirmed);
                pstmt.setBoolean(9, alllowed);
                pstmt.execute();
                
                if(pstmt.getUpdateCount() < 1) {
                    return -1;
                }

                JSONArray result = this.toJsonArray(pstmt.getResultSet());
                return new User(result.getJSONObject(0)).getId();
            }

            // Update User
            else {
                if(createdAt.equals("") || discordTag.equals("") || msgId.equals("")) {
                    this.logger.warning("Missing informations to save user entity");
                    return -1;
                }

                String sql = "UPDATE " + this.tablename + " " +
                "SET mc_name = ? " + 
                "SET discord_tag = ? " + 
                "SET accepted_by = ? " + 
                "SET revoked_by = ? " + 
                "SET mc_uuid = ? " + 
                "SET msg_id = ? " + 
                "SET created_at = ? " + 
                "SET confirmed = ? " + 
                "SET alllowed = ? " + 
                "WHERE id = ?;";

                this.open();
                final PreparedStatement pstmt = this.connection.prepareStatement(sql);
                pstmt.setString(1, mcName);
                pstmt.setString(2, discordTag);
                pstmt.setInt(3, acceptedBy);
                pstmt.setInt(4, revokedBy);
                pstmt.setString(5, mcUUID);
                pstmt.setString(6, msgId);
                pstmt.setString(7, createdAt);
                pstmt.setBoolean(8, confirmed);
                pstmt.setBoolean(9, alllowed);
                pstmt.setInt(10, id);
                pstmt.executeUpdate();
                return pstmt.getUpdateCount() > 0 ? id : -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            this.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }

    public JSONArray findAllowed() {

        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE allowed = 1";
            this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.executeQuery();

            final ResultSet resultSet = pstmt.getResultSet();
            results = this.toJsonArray(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            this.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public void setPlayerUUID(Integer id, UUID UUID) {
        try {
            String sql = "UPDATE " + this.tablename + " SET mc_uuid = ? WHERE id = ?;";
            this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.setString(1, UUID.toString());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            this.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User findByMcName(String pseudo) {
        JSONArray results = new JSONArray();
        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE mc_name = ? LIMIT 1";
            this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.setString(1, pseudo);
            pstmt.executeQuery();

            final ResultSet resultSet = pstmt.getResultSet();
            results = this.toJsonArray(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            this.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new User(results.getJSONObject(0));
    }

    public User findByDisccordTag(String discordTag) {
        JSONArray results = new JSONArray();
        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE discord_tag = ? LIMIT 1";
            this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.setString(1, discordTag);
            pstmt.executeQuery();

            final ResultSet resultSet = pstmt.getResultSet();
            results = this.toJsonArray(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            this.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new User(results.getJSONObject(0));
    }

}
