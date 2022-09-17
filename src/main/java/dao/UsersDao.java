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

    
    public User findUser(Integer id) {
        JSONObject res = this.find(id);
        if(res == null) {
            return null;
        }
        return new User(res);
    }

    public Integer saveUser(JSONObject sqlProps) {

        int id = sqlProps.optInt("id");
        final String mcName = sqlProps.optString("mc_name");
        final String discordTag = sqlProps.optString("discord_tag");
        final String mcUUID = sqlProps.optString("mc_uuid");
        final String msgId = sqlProps.optString("msg_id");
        final String createdAt = sqlProps.optString("created_at");
        final boolean confirmed = sqlProps.optBoolean("confirmed");
        final boolean allowed = sqlProps.optBoolean("allowed");

        Integer acceptedBy = sqlProps.optInt("accepted_by");
        Integer revokedBy = sqlProps.optInt("revoked_by");
        acceptedBy = acceptedBy > 0 ? acceptedBy : null;
        revokedBy = revokedBy > 0 ? revokedBy : null;

        if(mcName.equals("") || discordTag.equals("") || msgId.equals("") || createdAt.equals("")) {
            this.logger.warning("Missing informations to save user entity");
            return -1;
        }
        
        try {
            int status = -1;
            User found = this.findUser(id);

            // New user
            if(found == null) {
                id = -1;

                String sql = "INSERT INTO " + this.tablename + " (mc_name, discord_tag, accepted_by, " +
                "revoked_by, mc_uuid, msg_id, created_at, confirmed, allowed, updated_at) " + 
                "VALUES (?,?,?,?,?,?,?,?,?, CURRENT_TIMESTAMP);";

                this.open();
                final PreparedStatement pstmt = this.connection.prepareStatement(sql, new String [] { "id" } );
                pstmt.closeOnCompletion();
                pstmt.setString(1, mcName);
                pstmt.setString(2, discordTag);
                pstmt.setObject(3, acceptedBy);
                pstmt.setObject(4, revokedBy);
                pstmt.setString(5, mcUUID);
                pstmt.setString(6, msgId);
                pstmt.setString(7, createdAt);
                pstmt.setBoolean(8, confirmed);
                pstmt.setBoolean(9, allowed);
                status = pstmt.executeUpdate();

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                while(generatedKeys.next())
               {
                    id = generatedKeys.getInt(1); 
                    break;
               }
            }

            // Update User
            else {

                if(msgId.equals("")) {
                    this.logger.warning("Missing informations to save user entity");
                    return -1;
                }

                if(acceptedBy == null && revokedBy == null) {
                    this.logger.warning("Missing informations to save user entity");
                    return -1;
                }

                String sql = "UPDATE " + this.tablename + " SET " +
                "mc_name = ?," + 
                "discord_tag = ?," + 
                "accepted_by = ?," + 
                "revoked_by = ?," + 
                "mc_uuid = ?," + 
                "msg_id = ?," + 
                "created_at = ?," + 
                "confirmed = ?," + 
                "allowed = ? ," + 
                "updated_at = CURRENT_TIMESTAMP " + 
                "WHERE id = ?;";

                this.open();
                final PreparedStatement pstmt = this.connection.prepareStatement(sql);
                pstmt.closeOnCompletion();
                pstmt.setString(1, mcName);
                pstmt.setString(2, discordTag);
                pstmt.setObject(3, acceptedBy);
                pstmt.setObject(4, revokedBy);
                pstmt.setString(5, mcUUID);
                pstmt.setString(6, msgId);
                pstmt.setString(7, createdAt);
                pstmt.setBoolean(8, confirmed);
                pstmt.setBoolean(9, allowed);
                pstmt.setObject(10, id);
                status = pstmt.executeUpdate();
                id = pstmt.getUpdateCount() > 0 ? id : -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return id;
    }

    public JSONArray findAllowed() {

        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE allowed = 1";
            this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.closeOnCompletion();
            pstmt.executeQuery();

            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);

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
            pstmt.closeOnCompletion();
            pstmt.setString(1, UUID.toString());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

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
            pstmt.closeOnCompletion();
            pstmt.setString(1, pseudo);
            pstmt.executeQuery();

            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(results == null) {
            return null;
        }

        return new User(results.getJSONObject(0));
    }

    public User findByDisccordTag(String discordTag) {
        JSONArray results = new JSONArray();
        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE discord_tag = ? LIMIT 1";
            this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.closeOnCompletion();
            pstmt.setString(1, discordTag);
            pstmt.executeQuery();

            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(results == null) {
            return null;
        }

        return new User(results.getJSONObject(0));
    }

}
