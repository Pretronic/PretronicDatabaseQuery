/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 11:21
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
import net.prematic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.prematic.libraries.document.Document;
import net.pretronic.databasequery.sql.dialect.Dialect;

import javax.sql.DataSource;

public abstract class SQLDatabaseDriverConfig<T extends SQLDatabaseDriverConfig<T>> implements DatabaseDriverConfig<T> {

    private final String name;
    private final Class<? extends DatabaseDriver> driverClass;
    private final Dialect dialect;
    private final String connectionString;
    private final String connectionCatalog;
    private final String connectionSchema;
    private final boolean autoCommit;
    private final boolean connectionReadOnly;
    private final int connectionIsolationLevel;
    private final int connectionNetworkTimeout;
    private final Class<? extends DataSource> dataSourceClass;
    private final long dataSourceConnectionExpireAfterAccess;
    private final long dataSourceConnectionExpire;
    private final long dataSourceConnectionLoginTimeout;
    private final int dataSourceMaximumPoolSize;
    private final int dataSourceMinimumIdleConnectionPoolSize;

    protected SQLDatabaseDriverConfig(String name, Class<? extends DatabaseDriver> driverClass, Dialect dialect, String connectionString, String connectionCatalog, String connectionSchema, boolean autoCommit, boolean connectionReadOnly, int connectionIsolationLevel, int connectionNetworkTimeout, Class<? extends DataSource> dataSourceClass, long dataSourceConnectionExpireAfterAccess, long dataSourceConnectionExpire, long dataSourceConnectionLoginTimeout, int dataSourceMaximumPoolSize, int dataSourceMinimumIdleConnectionPoolSize) {
        this.name = name;
        this.driverClass = driverClass;
        this.dialect = dialect;
        this.connectionString = connectionString;
        this.connectionCatalog = connectionCatalog;
        this.connectionSchema = connectionSchema;
        this.autoCommit = autoCommit;
        this.connectionReadOnly = connectionReadOnly;
        this.connectionIsolationLevel = connectionIsolationLevel;
        this.connectionNetworkTimeout = connectionNetworkTimeout;
        this.dataSourceClass = dataSourceClass;
        this.dataSourceConnectionExpireAfterAccess = dataSourceConnectionExpireAfterAccess;
        this.dataSourceConnectionExpire = dataSourceConnectionExpire;
        this.dataSourceConnectionLoginTimeout = dataSourceConnectionLoginTimeout;
        this.dataSourceMaximumPoolSize = dataSourceMaximumPoolSize;
        this.dataSourceMinimumIdleConnectionPoolSize = dataSourceMinimumIdleConnectionPoolSize;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<? extends DatabaseDriver> getDriverClass() {
        return this.driverClass;
    }

    @Override
    public String getConnectionString() {
        return this.connectionString;
    }

    @Override
    public Document toDocument() {
        return Document.newDocument(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends T> N getAs(Class<N> aClass) {
        return (N) this;
    }

    public Dialect getDialect() {
        return this.dialect;
    }

    public String getConnectionCatalog() {
        return this.connectionCatalog;
    }

    public String getConnectionSchema() {
        return this.connectionSchema;
    }

    public boolean isAutoCommit() {
        return this.autoCommit;
    }

    public boolean isConnectionReadOnly() {
        return this.connectionReadOnly;
    }

    public int getConnectionIsolationLevel() {
        return this.connectionIsolationLevel;
    }

    public int getConnectionNetworkTimeout() {
        return this.connectionNetworkTimeout;
    }

    public Class<? extends DataSource> getDataSourceClass() {
        return this.dataSourceClass;
    }

    public long getDataSourceConnectionExpireAfterAccess() {
        return this.dataSourceConnectionExpireAfterAccess;
    }

    public long getDataSourceConnectionExpire() {
        return this.dataSourceConnectionExpire;
    }

    public long getDataSourceConnectionLoginTimeout() {
        return this.dataSourceConnectionLoginTimeout;
    }

    public int getDataSourceMaximumPoolSize() {
        return this.dataSourceMaximumPoolSize;
    }

    public int getDataSourceMinimumIdleConnectionPoolSize() {
        return this.dataSourceMinimumIdleConnectionPoolSize;
    }
}
