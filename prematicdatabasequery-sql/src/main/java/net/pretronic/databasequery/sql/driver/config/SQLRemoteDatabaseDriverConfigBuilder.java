/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.12.19, 16:55
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

import net.prematic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.sql.dialect.Dialect;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class SQLRemoteDatabaseDriverConfigBuilder {

    private String name;
    private Class<? extends DatabaseDriver> driverClass;
    private Dialect dialect;
    private String connectionString;
    private String connectionCatalog;
    private String connectionSchema;
    private boolean autoCommit = true;
    private boolean connectionReadOnly = false;
    private int connectionIsolationLevel;
    private int connectionNetworkTimeout;
    private Class<? extends DataSource> dataSourceClass;
    private long dataSourceConnectionExpireAfterAccess;
    private long dataSourceConnectionExpire;
    private long dataSourceConnectionLoginTimeout;
    private int dataSourceMaximumPoolSize;
    private int dataSourceMinimumIdleConnectionPoolSize;
    private InetAddress host;
    private int port = 3306;
    private InetSocketAddress address;
    private String username = "root";
    private String password;

    public SQLRemoteDatabaseDriverConfigBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setDriverClass(Class<? extends DatabaseDriver> driverClass) {
        this.driverClass = driverClass;
        try {
            Class.forName(driverClass.getName());
        } catch (ClassNotFoundException ignored) {
            throw new IllegalArgumentException("Can't load driver class");
        }
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setDialect(Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setConnectionString(String connectionString) {
        this.connectionString = connectionString;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setConnectionCatalog(String connectionCatalog) {
        this.connectionCatalog = connectionCatalog;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setConnectionSchema(String connectionSchema) {
        this.connectionSchema = connectionSchema;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setConnectionReadOnly(boolean connectionReadOnly) {
        this.connectionReadOnly = connectionReadOnly;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setConnectionIsolationLevel(int connectionIsolationLevel) {
        this.connectionIsolationLevel = connectionIsolationLevel;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setConnectionNetworkTimeout(int connectionNetworkTimeout) {
        this.connectionNetworkTimeout = connectionNetworkTimeout;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setDataSourceClass(Class<? extends DataSource> dataSourceClass) {
        this.dataSourceClass = dataSourceClass;
        try {
            Class.forName(dataSourceClass.getName());
        } catch (ClassNotFoundException ignored) {
            throw new IllegalArgumentException("Can't load driver class");
        }
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setDataSourceConnectionExpireAfterAccess(long dataSourceConnectionExpireAfterAccess) {
        this.dataSourceConnectionExpireAfterAccess = dataSourceConnectionExpireAfterAccess;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setDataSourceConnectionExpire(long dataSourceConnectionExpire) {
        this.dataSourceConnectionExpire = dataSourceConnectionExpire;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setDataSourceConnectionLoginTimeout(long dataSourceConnectionLoginTimeout) {
        this.dataSourceConnectionLoginTimeout = dataSourceConnectionLoginTimeout;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setDataSourceMaximumPoolSize(int dataSourceMaximumPoolSize) {
        this.dataSourceMaximumPoolSize = dataSourceMaximumPoolSize;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setDataSourceMinimumIdleConnectionPoolSize(int dataSourceMinimumIdleConnectionPoolSize) {
        this.dataSourceMinimumIdleConnectionPoolSize = dataSourceMinimumIdleConnectionPoolSize;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setHost(InetAddress host) {
        this.host = host;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setHost(String host) {
        try {
            this.host = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(String.format("Host %s not found", host));
        }
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setAddress(InetSocketAddress address) {
        this.address = address;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public SQLRemoteDatabaseDriverConfigBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public SQLRemoteDatabaseDriverConfig createSQLRemoteDatabaseDriverConfig() {
        return new SQLRemoteDatabaseDriverConfig(name, driverClass, dialect, connectionString, connectionCatalog, connectionSchema, autoCommit, connectionReadOnly, connectionIsolationLevel, connectionNetworkTimeout, dataSourceClass, dataSourceConnectionExpireAfterAccess, dataSourceConnectionExpire, dataSourceConnectionLoginTimeout, dataSourceMaximumPoolSize, dataSourceMinimumIdleConnectionPoolSize, host, port, address, username, password);
    }
}