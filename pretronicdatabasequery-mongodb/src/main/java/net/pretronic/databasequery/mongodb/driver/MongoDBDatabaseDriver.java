package net.pretronic.databasequery.mongodb.driver;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;
import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.common.driver.AbstractDatabaseDriver;
import net.pretronic.databasequery.mongodb.MongoDBDatabase;
import net.pretronic.databasequery.mongodb.driver.config.MongoDBDatabaseDriverConfig;
import net.pretronic.libraries.logging.PretronicLogger;

import java.util.concurrent.ExecutorService;

public class MongoDBDatabaseDriver extends AbstractDatabaseDriver implements ServerMonitorListener {

    static {
        DatabaseDriverFactory.registerFactory(MongoDBDatabaseDriver.class, new MongoDBDatabaseDriverFactory());
    }

    private MongoClient client;

    public MongoDBDatabaseDriver(String name, MongoDBDatabaseDriverConfig config, PretronicLogger logger, ExecutorService executorService) {
        super(name, "MongoDB", config, logger, executorService);
    }

    @Override
    public Database getDatabase(String name) {
        return new MongoDBDatabase(name, this);
    }

    @Override
    public boolean isConnected() {
        return this.client != null;
    }

    @Override
    public void connect() {
        if(this.client != null) throw new IllegalArgumentException("Already connected");
        this.client = new MongoClient(getConfig().getMongoClientURI(this));
    }

    @Override
    public void disconnect() {
        this.client.close();
    }

    @Override
    public MongoDBDatabaseDriverConfig getConfig() {
        return (MongoDBDatabaseDriverConfig) super.getConfig();
    }

    @Override
    public void serverHearbeatStarted(ServerHeartbeatStartedEvent event) {
        System.out.println("started");
    }

    @Override
    public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent event) {
        System.out.println("success");
    }

    @Override
    public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
        System.out.println("failed");
    }

    public MongoClient getClient() {
        return client;
    }
}
