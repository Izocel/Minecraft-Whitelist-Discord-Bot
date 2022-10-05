package helpers;
 
 
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import configs.ConfigManager;
 
public class DbPoolFactory {

    public static final PooledDatasource getMysqlPool(ConfigManager configs) throws Exception {

        // JDBC Driver Name & Database URL
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

        // JDBC Driver Name & Database URL
        final String JDBC_DB_URL = configs.get("mysqlJdbcUrl");
    
        // JDBC Database Credentials
        final String JDBC_USER = configs.get("mysqlUser");
        final String JDBC_PASS = configs.get("mysqlPass");
        final Integer JDBC_MAX_WAIT_TIME = 60000;
        final Integer JDBC_MAX_ACTIVE = Integer.parseInt(configs.get("mysqlMaxConnection", "5"));

        Class.forName(JDBC_DRIVER);
 
        // Creates an Instance of GenericObjectPool That Holds Our Pool of Connections Object!
        GenericObjectPool gPool = new GenericObjectPool();
        gPool.setMaxActive(JDBC_MAX_ACTIVE);
        gPool.setMaxIdle(JDBC_MAX_ACTIVE);
        gPool.setMaxWait(JDBC_MAX_WAIT_TIME);
        gPool.setTestWhileIdle(true);
 
        // Creates a ConnectionFactory Object Which Will Be Use by the Pool to Create the Connection Object!
        ConnectionFactory cf = new DriverManagerConnectionFactory(JDBC_DB_URL, JDBC_USER, JDBC_PASS);
 
        // Creates a PoolableConnectionFactory That Will Wraps the Connection Object Created by the ConnectionFactory to Add Object Pooling Functionality!
        new PoolableConnectionFactory(cf, gPool, null, null, false, true);

        final PooledDatasource pds = new PooledDatasource(gPool);

        return pds;
    }
}