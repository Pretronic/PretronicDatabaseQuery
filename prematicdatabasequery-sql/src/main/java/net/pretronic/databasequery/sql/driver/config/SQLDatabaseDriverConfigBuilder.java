/*
 * (C) Copyright 2020 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 06.01.20, 17:25
 *
 * The PrematicDatabaseQuery Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.pretronic.databasequery.sql.driver.config;

import net.prematic.libraries.utility.Validate;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.sql.dialect.Dialect;
import net.pretronic.databasequery.sql.driver.SQLDatabaseDriver;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class SQLDatabaseDriverConfigBuilder {

    private static int count = 1;

    private String name;
    private Dialect dialect;
    private String connectionString;
    private String connectionCatalog;
    private String connectionSchema;
    private boolean autoCommit;
    private boolean connectionReadOnly;
    private int connectionIsolationLevel;
    private int connectionNetworkTimeout;
    private String dataSourceClassName;
    private long dataSourceConnectionExpireAfterAccess;
    private long dataSourceConnectionExpire;
    private long dataSourceConnectionLoginTimeout;
    private int dataSourceMaximumPoolSize;
    private int dataSourceMinimumIdleConnectionPoolSize;

    //Local database driver
    private File location;

    //Remote database driver
    private InetAddress host;
    private int port;
    private InetSocketAddress address;
    private String username;
    private String password;

    public SQLDatabaseDriverConfigBuilder() {
        this.name = "SQL Pool-" + count++;
        this.autoCommit = true;
        this.connectionReadOnly = false;
        //datasource @Todo own datasource
        this.dataSourceConnectionExpireAfterAccess = TimeUnit.MINUTES.toMillis(10);
        this.dataSourceConnectionExpire = TimeUnit.MINUTES.toMillis(30);
        this.dataSourceConnectionLoginTimeout = TimeUnit.SECONDS.toMillis(30);
        this.dataSourceMaximumPoolSize = 10;
        this.dataSourceMinimumIdleConnectionPoolSize = 10;
        loadDriverClass();
    }

    private void loadDriverClass() {
        try {
            Class.forName(SQLDatabaseDriver.class.getName());
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public SQLDatabaseDriverConfigBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setDialect(Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setConnectionString(String connectionString) {
        this.connectionString = connectionString;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setConnectionCatalog(String connectionCatalog) {
        this.connectionCatalog = connectionCatalog;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setConnectionSchema(String connectionSchema) {
        this.connectionSchema = connectionSchema;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setConnectionReadOnly(boolean connectionReadOnly) {
        this.connectionReadOnly = connectionReadOnly;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setConnectionIsolationLevel(int connectionIsolationLevel) {
        this.connectionIsolationLevel = connectionIsolationLevel;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setConnectionNetworkTimeout(int connectionNetworkTimeout) {
        this.connectionNetworkTimeout = connectionNetworkTimeout;
        return this;
    }

    @SuppressWarnings("unchecked")
    public SQLDatabaseDriverConfigBuilder setDataSourceClassName(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setDataSourceConnectionExpireAfterAccess(long dataSourceConnectionExpireAfterAccess) {
        this.dataSourceConnectionExpireAfterAccess = dataSourceConnectionExpireAfterAccess;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setDataSourceConnectionExpire(long dataSourceConnectionExpire) {
        this.dataSourceConnectionExpire = dataSourceConnectionExpire;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setDataSourceConnectionLoginTimeout(long dataSourceConnectionLoginTimeout) {
        this.dataSourceConnectionLoginTimeout = dataSourceConnectionLoginTimeout;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setDataSourceMaximumPoolSize(int dataSourceMaximumPoolSize) {
        this.dataSourceMaximumPoolSize = dataSourceMaximumPoolSize;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setDataSourceMinimumIdleConnectionPoolSize(int dataSourceMinimumIdleConnectionPoolSize) {
        this.dataSourceMinimumIdleConnectionPoolSize = dataSourceMinimumIdleConnectionPoolSize;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setLocation(File location) {
        this.location = location;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setHost(InetAddress host) {
        this.host = host;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setAddress(InetSocketAddress address) {
        this.address = address;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public SQLDatabaseDriverConfigBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public SQLDatabaseDriverConfig<?> build() {
        Validate.notNull(dialect);
        if(dialect.getEnvironment() == DatabaseDriverEnvironment.LOCAL) {
            Validate.notNull(location);
            return new SQLLocalDatabaseDriverConfig(name, dialect, connectionString, connectionCatalog, connectionSchema, autoCommit,
                    connectionReadOnly, connectionIsolationLevel, connectionNetworkTimeout, dataSourceClassName,
                    dataSourceConnectionExpireAfterAccess, dataSourceConnectionExpire, dataSourceConnectionLoginTimeout,
                    dataSourceMaximumPoolSize, dataSourceMinimumIdleConnectionPoolSize, location);
        } else if(dialect.getEnvironment() == DatabaseDriverEnvironment.REMOTE) {
            Validate.isTrue((host != null && port > 0) || address != null);
            Validate.notNull(username);
            return new SQLRemoteDatabaseDriverConfig(name, dialect, connectionString, connectionCatalog, connectionSchema, autoCommit,
                    connectionReadOnly, connectionIsolationLevel, connectionNetworkTimeout, dataSourceClassName,
                    dataSourceConnectionExpireAfterAccess, dataSourceConnectionExpire, dataSourceConnectionLoginTimeout,
                    dataSourceMaximumPoolSize, dataSourceMinimumIdleConnectionPoolSize, host, port, address, username, password);
        } else {
            throw new IllegalArgumentException(String.format("Not available database driver environment (%s) for sql dialect %s", dialect.getEnvironment(), dialect.getName()));
        }
    }
}