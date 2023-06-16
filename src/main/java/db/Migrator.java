package db;

import java.util.logging.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import configs.ConfigManager;
import dao.DaoManager;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import services.sentry.SentryService;

public class Migrator {
    private WhitelistJe plugin;
    private Flyway flyway;
    private Logger logger;

    /**
     * @param plugin
     */
    public Migrator(WhitelistJe plugin) {
        try {
            ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                    .startChild("Migrator");

            this.plugin = plugin;
            this.logger = Logger.getLogger("WJE:" + getClass().getSimpleName());

            final ConfigManager configs = this.plugin.getConfigManager();

            final String JDBC_USER = configs.get("dbUser");
            final String JDBC_PASS = configs.get("dbPass");
            final String JDBC_URL = configs.get("dbJdbcUrl");

            try {
                FluentConfiguration flywayConfigs = Flyway.configure()
                .dataSource(DaoManager.getDatasource())
                .locations("classpath:db/migration");

                this.flyway = new Flyway(flywayConfigs);
                flyway.migrate();

            } catch (Exception e) {
                this.logger.warning(e.getMessage());
                e.printStackTrace();
            }

            process.setStatus(SpanStatus.OK);
            process.finish();
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }
}
