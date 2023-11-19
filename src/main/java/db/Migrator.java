package db;

import java.util.logging.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import dao.DaoManager;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmcNode;

public class Migrator {
    private WhitelistDmcNode plugin;
    private Flyway flyway;
    private Logger logger;

    /**
     * @param plugin
     */
    public Migrator(WhitelistDmcNode plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("Migrator");

        this.plugin = plugin;
        this.logger = Logger.getLogger("WDMC:" + getClass().getSimpleName());

        // final String JDBC_USER = plugin.getConfigManager().get("dbUser");
        // final String JDBC_PASS = plugin.getConfigManager().get("dbPass");
        // final String JDBC_URL = plugin.getConfigManager().get("dbJdbcUrl");

        try {
            FluentConfiguration flywayConfigs = Flyway.configure()
                .dataSource(DaoManager.getDatasource())
                //.dataSource(JDBC_URL, JDBC_USER, JDBC_PASS)
                .locations("classpath:db/migration");

            this.flyway = new Flyway(flywayConfigs);
            flyway.migrate();

        } catch (Exception e) {
            this.logger.warning(e.getMessage());
            e.printStackTrace();
            //SentryService.captureEx(e);
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }
}
