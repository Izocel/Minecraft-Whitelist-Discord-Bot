package helpers;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import configs.ConfigManager;

public class DbPoolFactory {

    public static final ComboPooledDataSource getMysqlPool(ConfigManager configs) throws Exception {

        // JDBC Driver Name & Database URL
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

        // JDBC Driver Name & Database URL
        final String JDBC_URL = configs.get("mysqlJdbcUrl") + 
            "?connectionAttributes=program_name:WhiteList-Je";

        final Integer JDBC_MAX_ACTIVE = Integer.parseInt(configs.get("mysqlMaxConnection", "15"));
        final Integer JDBC_MAX_IDLE = Integer.parseInt(configs.get("mysqlMaxConnectionIDLE", "5"));

        final String JDBC_USER = configs.get("mysqlUser");
        final String JDBC_PASS = configs.get("mysqlPass");

        Class.forName(JDBC_DRIVER);

        // Creates an Instance of GenericObjectPool That Holds Our Pool of Connections
        // Object!
        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass(JDBC_DRIVER);
        ds.setJdbcUrl(JDBC_URL);
        ds.setUser(JDBC_USER);
        ds.setPassword(JDBC_PASS);
        ds.setMinPoolSize(JDBC_MAX_IDLE);
        ds.setMaxPoolSize(JDBC_MAX_ACTIVE);
        ds.setTestConnectionOnCheckin(true);
        ds.setTestConnectionOnCheckout(true);
        ds.setDescription("Pool for Wje Mc®");
        ds.setDataSourceName("Wje-plugin");


        return ds;
    }
}