/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 20:22
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
import net.prematic.databasequery.api.driver.config.RemoteDatabaseDriverConfig;
import net.prematic.databasequery.api.exceptions.DatabaseQueryException;
import net.pretronic.databasequery.sql.dialect.Dialect;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SQLRemoteDatabaseDriverConfig extends SQLDatabaseDriverConfig<SQLRemoteDatabaseDriverConfig> implements RemoteDatabaseDriverConfig {

    private final InetAddress host;
    private final int port;
    private final InetSocketAddress address;
    private final String username;
    private final String password;

    protected SQLRemoteDatabaseDriverConfig(String name, Class<? extends DatabaseDriver> driverClass, Dialect dialect, String connectionString, String connectionCatalog, String connectionSchema, boolean autoCommit, boolean connectionReadOnly, int connectionIsolationLevel, int connectionNetworkTimeout, Class<? extends DataSource> dataSourceClass, long dataSourceConnectionExpireAfterAccess, long dataSourceConnectionExpire, long dataSourceConnectionLoginTimeout, int dataSourceMaximumPoolSize, int dataSourceMinimumIdleConnectionPoolSize, InetAddress host, int port, InetSocketAddress address, String username, String password) {
        super(name, driverClass, dialect, setConnectionString(name, host, port, dialect, connectionString), connectionCatalog, connectionSchema, autoCommit, connectionReadOnly, connectionIsolationLevel, connectionNetworkTimeout, dataSourceClass, dataSourceConnectionExpireAfterAccess, dataSourceConnectionExpire, dataSourceConnectionLoginTimeout, dataSourceMaximumPoolSize, dataSourceMinimumIdleConnectionPoolSize);
        this.host = host;
        this.port = port;
        this.address = address == null ? new InetSocketAddress(host, port) : address;
        this.username = username;
        this.password = password;
    }


    @Override
    public InetAddress getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    private static String setConnectionString(String name, InetAddress host, int port, Dialect dialect, String connectionString) {
        if(connectionString != null) {
            return connectionString;
        } else if(host != null && port != 0) {
            return String.format("jdbc:%s://%s:%s", dialect.getProtocol(),
                    host.getHostAddress(), port);
        } else {
            throw new DatabaseQueryException("Can't match jdbc url for driver " + name);
        }
    }
}