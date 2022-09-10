package WhitelistJe.dao;

import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbCredentials;
import configs.ConfigManager;

public class BaseDao {

	static private ConfigManager Configs = new ConfigManager();
	private Connection connection;
	private DbCredentials creds;
    protected String tablename;

	public BaseDao () {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getName());
		this.creds = this.getCredentials();
	}

	private DbCredentials getCredentials() {
		return new DbCredentials(
			Configs.get("mysqlHost", "127.0.0.1"),
			Configs.get("mysqlUser", "root"),
			Configs.get("mysqlPass", "mysql"),
			Configs.get("mysqlDb", "whitelist_je"),
			Integer.parseInt(Configs.get("mysqlPort", "3306"))
		);
	}

	private Connection open() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            this.connection = DriverManager.getConnection(
                this.creds.toURI(), 
                this.creds.getUser(),
                this.creds.getPass()
            );
            
            this.logger.info("Connecté à la base de donnée !");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            this.logger.warning("Connexion impossible à la base de donnée !");
        }

    }

    private void close() throws SQLException {
        try {
            this.connection.close();
        } catch (SQLException  e) {
            e.printStackTrace();
            this.logger.warning("Déconnexion impossible à la base de donnée !");
        }
    }

    protected ResultSet find(Integer id) {
        try {
            String sql = "SELECT * FROM " + this.tablename + " WHERE id = ?";
            final Connection con = this.open();
            final PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.setInt(1, id);

            pstmt.executeQuery();
            final ResultSet resultset = pstmt.getResultSet(); con.close();

            this.logger.info(resultset);
            return resultset;

        } catch (SQLException e) {
            e.printStackTrace();
            con.close();
        }
    }

    protected ResultSet findAll() {
        try {
            String sql = "SELECT * FROM " + this.tablename;
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
