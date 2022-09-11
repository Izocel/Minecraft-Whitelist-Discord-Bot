package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class UsersDao extends BaseDao {

    private Logger logger;

	public UsersDao () {
        super();
        this.tablename = "users";
        this.logger = Logger.getLogger("WJE:" + this.getClass().getName());
		this.creds = this.getCredentials();
	}

    public Integer SaveUser(JSONObject sqlProps) {

        final Integer id = sqlProps.getInt("id");
        String UUID = sqlProps.getString("mc_uuid");

        if(UUID.length() < 36) { this.logger.warning("User Minecraft UUID is not valid."); }

        try {
            String sql = "SELECT count(id) FROM " + this.tablename + " WHERE id = ?;";
            this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.setString(1, UUID);
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



        return null;
    }

    public JSONArray findAllowed() {

        JSONArray results = new JSONArray();

        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE checked = 1";
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

}
