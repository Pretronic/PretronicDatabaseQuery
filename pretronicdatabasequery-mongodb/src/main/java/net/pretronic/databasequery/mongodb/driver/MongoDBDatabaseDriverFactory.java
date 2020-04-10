package net.pretronic.databasequery.mongodb.driver;

import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.databasequery.mongodb.driver.config.MongoDBDatabaseDriverConfig;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.utility.Validate;

import java.util.concurrent.ExecutorService;

public class MongoDBDatabaseDriverFactory extends DatabaseDriverFactory {

    @Override
    public DatabaseDriver createDriver(String name, DatabaseDriverConfig<?> config, PretronicLogger logger, ExecutorService executorService) {
        Validate.notNull(name);
        Validate.isTrue(config instanceof MongoDBDatabaseDriverConfig);
        return new MongoDBDatabaseDriver(name, (MongoDBDatabaseDriverConfig) config, logger, executorService);
    }

    @Override
    public DatabaseDriverConfig<?> createConfig(Document config) {
        return config.getAsObject(MongoDBDatabaseDriverConfig.class);
    }
}
