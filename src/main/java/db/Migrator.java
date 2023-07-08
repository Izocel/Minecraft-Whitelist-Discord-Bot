package db;

import java.util.logging.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import dao.DaoManager;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistDmc;

public class Migrator {
    private WhitelistDmc plugin;
    private Flyway flyway;
    private Logger logger;

    /**
     * @param plugin
     */
    public Migrator(WhitelistDmc plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("Migrator");

        this.plugin = plugin;
        this.logger = Logger.getLogger("WDMC:" + getClass().getSimpleName());
        try {
            FluentConfiguration flywayConfigs = Flyway.configure()
                .dataSource(DaoManager.getDatasource())
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
