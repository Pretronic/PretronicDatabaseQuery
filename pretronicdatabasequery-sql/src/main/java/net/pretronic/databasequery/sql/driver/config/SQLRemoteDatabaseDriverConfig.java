/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 20:22
 *
 * The PretronicDatabaseQuery Project is under the Apache License, version 2.0 (the "License");
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

import net.pretronic.databasequery.api.driver.config.RemoteDatabaseDriverConfig;
import net.pretronic.databasequery.sql.dialect.Dialect;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SQLRemoteDatabaseDriverConfig extends SQLDatabaseDriverConfig<SQLRemoteDatabaseDriverConfig> implements RemoteDatabaseDriverConfig {

    private final InetSocketAddress address;
    private final String username;
    private final String password;

    protected SQLRemoteDatabaseDriverConfig(String name, Dialect dialect, String connectionString, String connectionCatalog
            , String connectionSchema, boolean connectionReadOnly, int connectionIsolationLevel
            , int connectionNetworkTimeout, String dataSourceClassName, long dataSourceConnectionExpireAfterAccess
            , long dataSourceConnectionExpire, long dataSourceConnectionLoginTimeout, int dataSourceMaximumPoolSize
            , int dataSourceMinimumIdleConnectionPoolSize,  InetSocketAddress address, String username, String password) {
        super(name, dialect, connectionString, connectionCatalog, connectionSchema
                , connectionReadOnly, connectionIsolationLevel, connectionNetworkTimeout, dataSourceClassName
                , dataSourceConnectionExpireAfterAccess, dataSourceConnectionExpire, dataSourceConnectionLoginTimeout
                , dataSourceMaximumPoolSize, dataSourceMinimumIdleConnectionPoolSize);
        this.address = address;
        this.username = username;
        this.password = password;
    }


    @Override
    public InetAddress getHost() {
        return address.getAddress();
    }

    @Override
    public int getPort() {
        return address.getPort();
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
}
