/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 17.09.19, 13:37
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

package net.prematic.databasequery.sql;

import net.prematic.databasequery.api.DatabaseDriver;
import net.prematic.databasequery.api.config.DatabaseDriverConfig;
import net.prematic.libraries.document.Document;
import net.prematic.libraries.document.DocumentEntry;

public class SqlDatabaseDriverConfig extends DatabaseDriverConfig<SqlDatabaseDriverConfig> {

    static {
        registerDriverConfig("net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver", SqlDatabaseDriverConfig.class);
        registerDriverConfig("net.prematic.databasequery.sql.h2.H2PortableDatabaseDriver", SqlDatabaseDriverConfig.class);
    }

    private DataSourceConfig dataSourceConfig;

    public SqlDatabaseDriverConfig(Class<? extends DatabaseDriver> driverClass) {
        super(driverClass);
        this.dataSourceConfig = new DataSourceConfig();
    }

    public SqlDatabaseDriverConfig(Document original) {
        super(original);
        this.dataSourceConfig = new DataSourceConfig();
    }

    public String getJdbcUrl() {
        return getString("jdbcUrl");
    }

    public String getUsername() {
        return getString("username");
    }

    public String getPassword() {
        return getString("password");
    }

    public String getDriverClassName() {
        return getString("driverClassName");
    }

    public String getConnectionCatalog() {
        return getString("connectionCatalog");
    }

    public String getConnectionSchema() {
        return getString("connectionSchema");
    }

    public boolean isAutoCommit() {
        return !contains("autoCommit") || getBoolean("autoCommit");
    }

    public boolean isConnectionReadOnly() {
        return contains("connectionReadOnly") && getBoolean("connectionReadOnly");
    }

    public boolean isMultipleDatabaseConnectionsAble() {
        return !contains("multipleDatabaseConnectionsAble") || getBoolean("multipleDatabaseConnectionsAble");
    }

    public int getConnectionIsolationLevel() {
        return getInt("connectionIsolationLevel");
    }

    public int getConnectionNetworkTimeout() {
        return getInt("connectionNetworkTimeout");
    }

    public DataSourceConfig getDataSourceConfig() {
        return this.dataSourceConfig;
    }

    public SqlDatabaseDriverConfig setJdbcUrl(String jdbcUrl) {
        set("jdbcUrl", jdbcUrl);
        return this;
    }

    public SqlDatabaseDriverConfig setUsername(String username) {
        set("username", username);
        return this;
    }

    public SqlDatabaseDriverConfig setPassword(String password) {
        set("password", password);
        return this;
    }

    public SqlDatabaseDriverConfig setDriverClassName(String driverClassName) {
        set("driverClassName", driverClassName);
        return this;
    }

    public SqlDatabaseDriverConfig setConnectionCatalog(String connectionCatalog) {
        set("connectionCatalog", connectionCatalog);
        return this;
    }

    public SqlDatabaseDriverConfig setConnectionSchema(String connectionSchema) {
        set("connectionSchema", connectionSchema);
        return this;
    }

    public SqlDatabaseDriverConfig setAutoCommit(boolean autoCommit) {
        set("autoCommit", autoCommit);
        return this;
    }

    public SqlDatabaseDriverConfig setConnectionReadOnly(boolean connectionReadOnly) {
        set("connectionReadOnly", connectionReadOnly);
        return this;
    }

    public SqlDatabaseDriverConfig setMultipleDatabaseConnectionsAble(boolean multipleDatabaseConnectionsAble) {
        set("multipleDatabaseConnectionsAble", multipleDatabaseConnectionsAble);
        return this;
    }

    public SqlDatabaseDriverConfig setConnectionIsolationLevel(int connectionIsolationLevel) {
        set("connectionIsolationLevel", connectionIsolationLevel);
        return this;
    }

    public class DataSourceConfig {

        public String getClassName() {
            for (DocumentEntry entry : iterate("datasource")) {
                if(entry.getKey().equals("className")) return entry.toPrimitive().getAsString();
            }
            return getString("datasource.className");
        }

        public long getConnectionExpireAfterAccess() {
            return getLong("datasource.connectionExpireAfterAccess");
        }

        public long getConnectionExpire() {
            return getLong("datasource.connectionExpire");
        }

        public long getConnectionLoginTimeout() {
            return getLong("datasource.connectionLoginTimeout");
        }

        public int getMaximumPoolSize() {
            return getInt("datasource.maximumPoolSize");
        }

        public int getMinimumIdleConnectionPoolSize() {
            return getInt("datasource.minimumIdleConnectionPoolSize");
        }

        public DataSourceConfig setClassName(String className) {
            set("datasource.className", className);
            return this;
        }

        public DataSourceConfig setConnectionExpireAfterAccess(long connectionExpireAfterAccess) {
            set("datasource.connectionExpireAfterAccess", connectionExpireAfterAccess);
            return this;
        }

        public DataSourceConfig setConnectionExpire(long connectionExpire) {
            set("datasource.connectionExpire", connectionExpire);
            return this;
        }

        public DataSourceConfig setConnectionLoginTimeout(long connectionLoginTimeout) {
            set("datasource.connectionLoginTimeout", connectionLoginTimeout);
            return this;
        }

        public DataSourceConfig setMaximumPoolSize(int maximumPoolSize) {
            set("datasource.maximumPoolSize", maximumPoolSize);
            return this;
        }

        public DataSourceConfig setMinimumIdleConnectionPoolSize(int minimumIdleConnectionPoolSize) {
            set("datasource.minimumIdleConnectionPoolSize", minimumIdleConnectionPoolSize);
            return this;
        }

        public SqlDatabaseDriverConfig out() {
            return SqlDatabaseDriverConfig.this;
        }
    }
}
