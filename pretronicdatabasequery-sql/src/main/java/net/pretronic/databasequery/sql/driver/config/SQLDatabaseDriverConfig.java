/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 11:21
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

import com.zaxxer.hikari.HikariDataSource;
import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.databasequery.sql.dialect.Dialect;
import net.pretronic.databasequery.sql.driver.SQLDatabaseDriver;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.annotations.DocumentIgnoreBooleanValue;
import net.pretronic.libraries.document.annotations.DocumentIgnoreZeroValue;
import net.pretronic.libraries.document.annotations.DocumentKey;

import java.util.concurrent.TimeUnit;

public abstract class SQLDatabaseDriverConfig<T extends SQLDatabaseDriverConfig<T>> implements DatabaseDriverConfig<T> {

    private final Class<?> driver = SQLDatabaseDriver.class;

    @DocumentKey("name")
    protected final String name;

    @DocumentKey("dialectName")
    protected final Dialect dialect;

    @DocumentKey("connectionString")
    protected final String connectionString;

    @DocumentKey("useSSL")
    protected final boolean useSSL;

    @DocumentKey("connection.options.catalog")
    protected final String connectionCatalog;

    @DocumentKey("connection.options.schema")
    protected final String connectionSchema;

    @DocumentIgnoreBooleanValue(ignore = false)
    @DocumentKey("connection.options.readOnly")
    protected final boolean connectionReadOnly;

    @DocumentIgnoreZeroValue
    @DocumentKey("connection.options.isolationLevel")
    protected final int connectionIsolationLevel;

    @DocumentIgnoreZeroValue
    @DocumentKey("connection.options.networkTimeout")
    protected final int connectionNetworkTimeout;

    @DocumentKey("datasource.className")
    protected String dataSourceClassName;

    @DocumentIgnoreZeroValue
    @DocumentKey("datasource.connectionExpireAfterAccess")
    protected long dataSourceConnectionExpireAfterAccess;

    @DocumentIgnoreZeroValue
    @DocumentKey("datasource.connectionExpire")
    protected long dataSourceConnectionExpire;

    @DocumentIgnoreZeroValue
    @DocumentKey("datasource.connectionLoginTimeout")
    protected long dataSourceConnectionLoginTimeout;

    @DocumentIgnoreZeroValue
    @DocumentKey("datasource.maximumPoolSize")
    protected int dataSourceMaximumPoolSize;

    @DocumentIgnoreZeroValue
    @DocumentKey("datasource.minimumIdleConnectionPoolSize")
    protected int dataSourceMinimumIdleConnectionPoolSize;

    protected SQLDatabaseDriverConfig(String name, Dialect dialect, String connectionString, boolean useSSL, String connectionCatalog, String connectionSchema, boolean connectionReadOnly, int connectionIsolationLevel, int connectionNetworkTimeout, String dataSourceClassName, long dataSourceConnectionExpireAfterAccess, long dataSourceConnectionExpire, long dataSourceConnectionLoginTimeout, int dataSourceMaximumPoolSize, int dataSourceMinimumIdleConnectionPoolSize) {
        this.name = name;
        this.dialect = dialect;
        this.connectionString = connectionString;
        this.useSSL = useSSL;
        this.connectionCatalog = connectionCatalog;
        this.connectionSchema = connectionSchema;
        this.connectionReadOnly = connectionReadOnly;
        this.connectionIsolationLevel = connectionIsolationLevel;
        this.connectionNetworkTimeout = connectionNetworkTimeout;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<? extends DatabaseDriver> getDriverClass() {
        return (Class<? extends DatabaseDriver>) this.driver;
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

    public boolean isUseSSL() {
        return useSSL;
    }

    public String getConnectionCatalog() {
        return this.connectionCatalog;
    }

    public String getConnectionSchema() {
        return this.connectionSchema;
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

    public String getDataSourceClass() {
        if(this.dataSourceClassName == null) {
            this.dataSourceClassName = HikariDataSource.class.getName();
        }
        return this.dataSourceClassName;
    }

    public long getDataSourceConnectionExpireAfterAccess() {
        if(this.dataSourceConnectionExpireAfterAccess == 0) {
            this.dataSourceConnectionExpireAfterAccess = TimeUnit.MINUTES.toMillis(10);
        }
        return this.dataSourceConnectionExpireAfterAccess;
    }

    public long getDataSourceConnectionExpire() {
        if(this.dataSourceConnectionExpire == 0) {
            this.dataSourceConnectionExpire = TimeUnit.MINUTES.toMillis(30);
        }
        return this.dataSourceConnectionExpire;
    }

    public long getDataSourceConnectionLoginTimeout() {
        if(this.dataSourceConnectionLoginTimeout == 0) {
            this.dataSourceConnectionLoginTimeout = TimeUnit.SECONDS.toMillis(30);
        }
        return this.dataSourceConnectionLoginTimeout;
    }

    public int getDataSourceMaximumPoolSize() {
        if(this.dataSourceMaximumPoolSize == 0) {
            this.dataSourceMaximumPoolSize = 10;
        }
        return this.dataSourceMaximumPoolSize;
    }

    public int getDataSourceMinimumIdleConnectionPoolSize() {
        if(this.dataSourceMinimumIdleConnectionPoolSize == 0) {
            this.dataSourceMinimumIdleConnectionPoolSize = 1;
        }
        return this.dataSourceMinimumIdleConnectionPoolSize;
    }
}
