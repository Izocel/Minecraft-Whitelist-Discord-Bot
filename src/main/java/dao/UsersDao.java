package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import helpers.PooledDatasource;
import models.User;

public class UsersDao extends BaseDao {

    private Logger logger;

	public UsersDao (PooledDatasource poolDs) {
        super(poolDs);
        this.tablename = "users";
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
	}

    
    public User findUser(Integer id) {
        JSONObject res = this.find(id);
        return res == null ? null : new User(res);
    }

    @Override
    public Integer save(JSONObject sqlProps) {
        int id = sqlProps.optInt("id");
        final String mcName = sqlProps.optString("mc_name");
        final String discordId = sqlProps.optString("discord_id");
        final String mcUUID = sqlProps.optString("mc_uuid");
        final String msgId = sqlProps.optString("msg_id");
        final String createdAt = sqlProps.optString("created_at");

        final Object isConfirmed = sqlProps.opt("confirmed");
        final Object isAllowed = sqlProps.opt("allowed");

        final int allowed = isAllowed.equals(true) ? 1 : 0;
        final int confirmed = isConfirmed.equals(true) ? 1 : 0;

        String acceptedBy = sqlProps.optString("accepted_by");
        String revokedBy = sqlProps.optString("revoked_by");
        acceptedBy = acceptedBy.length() > 0 ? acceptedBy : null;
        revokedBy = revokedBy.length() > 0 ? revokedBy : null;

        if(mcName.equals("") || discordId.equals("") || msgId.equals("") || createdAt.equals("")) {
            this.logger.warning("Missing informations to save user entity");
            return -1;
        }
        
        try {
            int status = -1;
            User found = this.findUser(id);

            // New user
            if(found == null) {
                id = -1;

                String sql = "INSERT INTO " + this.tablename + " (mc_name, discord_id, accepted_by, " +
                "revoked_by, mc_uuid, msg_id, created_at, confirmed, allowed, updated_at) " + 
                "VALUES (?,?,?,?,?,?,?,?,?, CURRENT_TIMESTAMP);";

                final PreparedStatement pstmt = this.getConnection().prepareStatement(sql, new String [] { "id" } );
                pstmt.setString(1, mcName);
                pstmt.setString(2, discordId);
                pstmt.setObject(3, acceptedBy);
                pstmt.setObject(4, revokedBy);
                pstmt.setString(5, mcUUID);
                pstmt.setString(6, msgId);
                pstmt.setString(7, createdAt);
                pstmt.setInt(8, confirmed);
                pstmt.setInt(9, allowed);
                status = pstmt.executeUpdate();
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                
                while(generatedKeys.next())
               {
                    id = generatedKeys.getInt(1); 
                    break;
               }

               this.closeConnection();
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
                "discord_id = ?," + 
                "accepted_by = ?," + 
                "revoked_by = ?," + 
                "mc_uuid = ?," + 
                "msg_id = ?," + 
                "created_at = ?," + 
                "confirmed = ?," + 
                "allowed = ? ," + 
                "updated_at = CURRENT_TIMESTAMP " + 
                "WHERE id = ?;";

                final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
                pstmt.setString(1, mcName);
                pstmt.setString(2, discordId);
                pstmt.setObject(3, acceptedBy);
                pstmt.setObject(4, revokedBy);
                pstmt.setString(5, mcUUID);
                pstmt.setString(6, msgId);
                pstmt.setString(7, createdAt);
                pstmt.setInt(8, confirmed);
                pstmt.setInt(9, allowed);
                pstmt.setObject(10, id);
                status = pstmt.executeUpdate();
                id = pstmt.getUpdateCount() > 0 ? id : -1;
                this.closeConnection();
                
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
            final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
            pstmt.executeQuery();

            
            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);
            this.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(results == null || results.length() < 1) {
            return null;
        }


        return results;
    }

    public void setPlayerUUID(Integer id, UUID UUID, boolean tempConfirmed) {
        try {
            String sql = "UPDATE " + this.tablename + " SET mc_uuid = ? WHERE id = ?;";
            if(tempConfirmed) {
                sql = "UPDATE " + this.tablename + " SET mc_uuid = ?, confirmed = 1 WHERE id = ?;";
            }
            
            final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
            pstmt.setString(1, UUID.toString());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            this.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User findByMcName(String pseudo) {
        
        JSONArray results = new JSONArray();
        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE mc_name = ? LIMIT 1";
            final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
            pstmt.setString(1, pseudo);
            pstmt.executeQuery();
            
            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);
            this.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(results == null || results.length() < 1) {
            return null;
        }

        return new User(results.getJSONObject(0));
    }

    public User findByMcUUID(String uuid) {
        
        JSONArray results = new JSONArray();
        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE mc_uuid = ? LIMIT 1";
            final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
            pstmt.setString(1, uuid);
            pstmt.executeQuery();
            
            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);
            this.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(results == null || results.length() < 1) {
            return null;
        }

        return new User(results.getJSONObject(0));
    }

    public User findByDisccordId(String discordId) {
        
        JSONArray results = new JSONArray();
        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE discord_id = ? LIMIT 1";
            final PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
            pstmt.setString(1, discordId);
            pstmt.executeQuery();
            
            final ResultSet resultSet = pstmt.getResultSet();
            results = resultSet == null ? null : this.toJsonArray(resultSet);
            this.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(results == null || results.length() < 1) {
            return null;
        }

        return new User(results.getJSONObject(0));
    }

}
