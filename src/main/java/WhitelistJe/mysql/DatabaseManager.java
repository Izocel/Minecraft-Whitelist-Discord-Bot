package WhitelistJe.mysql;

import java.sql.SQLException;

import WhitelistJe.configs.ConfigManager;

public class DatabaseManager {
	private ConfigManager env = new ConfigManager(null, null);
	private dbConnection userinfo;

	public DatabaseManager() {

		this.userinfo = new dbConnection(new dbCredentials(
				this.env.getConfig("mysqlHost", "127.0.0.1"),
				this.env.getConfig("mysqlUser", "root"),
				this.env.getConfig("mysqlPass", "mysql"),
				this.env.getConfig("mysqlDb", "whitelist_je"),
				Integer.parseInt(this.env.getConfig("mysqlPort", "3306"))));
	}

	public dbConnection getUserinfo() {
		return userinfo;
	}

	public void close() {
		try {
			this.userinfo.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
