package WhitelistJe.dao;

import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import WhitelistJe.dao.BaseDao;

public class UsersDAO extends BaseDao {

    protected String tablename = "users";

	public UsersDAO () {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getName());
		this.creds = this.getCredentials();
	}

    public findAllowed() {
        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE checked = 1;";
            final Connection con = this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);

            pstmt.executeQuery();
            final ResultSet resultset = pstmt.getResultSet(); con.close();

            this.logger.info(resultset);
            return resultset;

        } catch (SQLException e) {
            e.printStackTrace();
            con.close();
        }
    }

}
