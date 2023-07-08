package helpers;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import configs.ConfigManager;

public class DbPoolFactory {

    public static final ComboPooledDataSource getPoolConnection(ConfigManager configs) throws Exception {
        final String dbType = configs.get("dbType", "mysql");
        switch (dbType.toLowerCase()) {
            case "mysql":
                return getMysqlPool(configs);
            case "mariadb":
                return getMariaPool(configs);
            default:
                return null;
        }
    }

    public static final ComboPooledDataSource getMysqlPool(ConfigManager configs) throws Exception {
        // JDBC Driver Name & Database URL
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

        // JDBC Driver Name & Database URL
        final String JDBC_URL = configs.get("dbJdbcUrl") +
                "?connectionAttributes=program_name:Whitelist-DMC";

        final Integer JDBC_MAX_ACTIVE = Integer.parseInt(configs.get("dbMaxConnection", "15"));
        final Integer JDBC_MAX_IDLE = Integer.parseInt(configs.get("dbMaxConnectionIDLE", "5"));

        final String JDBC_USER = configs.get("dbUser");
        final String JDBC_PASS = configs.get("dbPass");

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
        ds.setDescription("Pool for Wdmc Mc®");
        ds.setDataSourceName("Wdmc-plugin");

        return ds;
    }

    public static final ComboPooledDataSource getMariaPool(ConfigManager configs) throws Exception {
        // JDBC Driver Name & Database URL
        final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";

        // JDBC Driver Name & Database URL
        final String JDBC_URL = configs.get("dbJdbcUrl") +
                "?connectionAttributes=program_name:Whitelist-DMC";

        final Integer JDBC_MAX_ACTIVE = Integer.parseInt(configs.get("dbMaxConnection", "15"));
        final Integer JDBC_MAX_IDLE = Integer.parseInt(configs.get("dbMaxConnectionIDLE", "5"));

        final String JDBC_USER = configs.get("dbUser");
        final String JDBC_PASS = configs.get("dbPass");

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
        ds.setDescription("Pool for Wdmc Mc®");
        ds.setDataSourceName("Wdmc-plugin");

        return ds;
    }
}