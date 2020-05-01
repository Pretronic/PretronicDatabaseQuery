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

import net.pretronic.databasequery.api.driver.config.LocalDatabaseDriverConfig;
import net.pretronic.databasequery.sql.dialect.Dialect;

import java.io.File;

public class SQLLocalDatabaseDriverConfig extends SQLDatabaseDriverConfig<SQLLocalDatabaseDriverConfig> implements LocalDatabaseDriverConfig<SQLLocalDatabaseDriverConfig> {

    private final File location;

    protected SQLLocalDatabaseDriverConfig(String name, Dialect dialect, String connectionString, boolean useSSL, String connectionCatalog,
                                           String connectionSchema, boolean connectionReadOnly, int connectionIsolationLevel,
                                           int connectionNetworkTimeout, String dataSourceClassName, long dataSourceConnectionExpireAfterAccess,
                                           long dataSourceConnectionExpire, long dataSourceConnectionLoginTimeout, int dataSourceMaximumPoolSize,
                                           int dataSourceMinimumIdleConnectionPoolSize, File location) {
        super(name, dialect, connectionString, useSSL, connectionCatalog, connectionSchema, connectionReadOnly, connectionIsolationLevel,
                connectionNetworkTimeout, dataSourceClassName, dataSourceConnectionExpireAfterAccess, dataSourceConnectionExpire,
                dataSourceConnectionLoginTimeout, dataSourceMaximumPoolSize, dataSourceMinimumIdleConnectionPoolSize);
        this.location = location;
    }


    @Override
    public File getLocation() {
        return this.location;
    }

    @Override
    public SQLLocalDatabaseDriverConfig copy() {
        return new SQLLocalDatabaseDriverConfig(this.name, this.dialect, this.connectionString, this.useSSL, this.connectionCatalog
                , this.connectionSchema, this.connectionReadOnly, this.connectionIsolationLevel, this.connectionNetworkTimeout
                , this.dataSourceClassName, this.dataSourceConnectionExpireAfterAccess, this.dataSourceConnectionExpire
                , this.dataSourceConnectionLoginTimeout, this.dataSourceMaximumPoolSize, this.dataSourceMinimumIdleConnectionPoolSize
                , this.location);
    }
}
