package main;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

import configs.ConfigManager;
import oracle.jdbc.pool.OracleDataSource;

public class DataSourceFactory {

	public static DataSource getMySQLDataSource(ConfigManager configs) {
		MysqlDataSource mysqlDS = null;
		mysqlDS = new MysqlDataSource();
		mysqlDS.setURL(configs.get("mysqlJdbcUrl"));
		mysqlDS.setUser(configs.get("mysqlUser"));
		mysqlDS.setPassword(configs.get("mysqlPass"));
		return mysqlDS;
	}
	
	public static DataSource getOracleDataSource(ConfigManager configs){
		OracleDataSource oracleDS = null;
		try {
			oracleDS = new OracleDataSource();
			oracleDS.setURL(configs.get("ORACLE_DB_URL"));
			oracleDS.setUser(configs.get("ORACLE_DB_USERNAME"));
			oracleDS.setPassword(configs.get("ORACLE_DB_PASSWORD"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return oracleDS;
	}
		
}