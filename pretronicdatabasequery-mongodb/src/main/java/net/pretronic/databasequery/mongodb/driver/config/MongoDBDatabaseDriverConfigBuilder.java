package net.pretronic.databasequery.mongodb.driver.config;

import net.pretronic.databasequery.mongodb.driver.MongoDBDatabaseDriver;
import net.pretronic.libraries.utility.Validate;

import java.net.InetSocketAddress;

public class MongoDBDatabaseDriverConfigBuilder {

    private static int COUNT = 1;

    private InetSocketAddress address;
    private String name;
    private String connectionString;
    private String user;
    private String password;
    private String authenticationDatabase;
    private boolean srv;
    private Boolean ssl;

    public MongoDBDatabaseDriverConfigBuilder() {
        this.name = "MongoDB-" + COUNT++;
        loadDriverClass();
    }

    private void loadDriverClass() {
        try {
            Class.forName(MongoDBDatabaseDriver.class.getName());
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public MongoDBDatabaseDriverConfigBuilder setAddress(InetSocketAddress address) {
        this.address = address;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setConnectionString(String connectionString) {
        this.connectionString = connectionString;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setAuthenticationDatabase(String authenticationDatabase) {
        this.authenticationDatabase = authenticationDatabase;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setSrv(boolean srv) {
        this.srv = srv;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setSsl(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    public MongoDBDatabaseDriverConfig build() {
        Validate.isTrue(address != null || connectionString != null);
        return new MongoDBDatabaseDriverConfig(address, name, connectionString, user, password, authenticationDatabase, srv, ssl);
    }
}