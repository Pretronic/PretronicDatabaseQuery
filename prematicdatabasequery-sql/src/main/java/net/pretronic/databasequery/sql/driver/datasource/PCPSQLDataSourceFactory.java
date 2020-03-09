/*
 * (C) Copyright 2020 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.01.20, 12:33
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

package net.pretronic.databasequery.sql.driver.datasource;

import net.prematic.connectionpool.PrematicDataSourceBuilder;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.sql.driver.SQLDatabaseDriver;
import net.pretronic.databasequery.sql.driver.config.SQLDatabaseDriverConfig;
import net.pretronic.databasequery.sql.driver.config.SQLLocalDatabaseDriverConfig;
import net.pretronic.databasequery.sql.driver.config.SQLRemoteDatabaseDriverConfig;

import javax.sql.DataSource;

public class PCPSQLDataSourceFactory implements SQLDataSourceFactory {

    @Override
    public DataSource createDataSource(SQLDatabaseDriver driver, String database) {
        SQLDatabaseDriverConfig<?> config = driver.getConfig();
        PrematicDataSourceBuilder builder = new PrematicDataSourceBuilder();
        if(driver.getDialect().getEnvironment() == DatabaseDriverEnvironment.LOCAL) {
            String jdbcUrl = config.getConnectionString();
            if(jdbcUrl == null) {
                jdbcUrl = driver.getDialect().createConnectionString(null, ((SQLLocalDatabaseDriverConfig)config).getLocation());
            }
            builder.jdbcUrl(String.format(jdbcUrl, database));
        } else {
            String jdbcUrl = config.getConnectionString();
            if(jdbcUrl == null) {
                jdbcUrl = driver.getDialect().createConnectionString(null, ((SQLRemoteDatabaseDriverConfig)config).getAddress());
            }
            builder.jdbcUrl(String.format(jdbcUrl, database));
        }
        if(config instanceof SQLRemoteDatabaseDriverConfig) {
            SQLRemoteDatabaseDriverConfig remoteConfig = (SQLRemoteDatabaseDriverConfig) config;
            if(remoteConfig.getUsername() != null) builder.username(remoteConfig.getUsername());
            if(remoteConfig.getPassword() != null) builder.password(remoteConfig.getPassword());
        }
        if(config.getConnectionCatalog() != null) builder.connectionCatalog(config.getConnectionCatalog());
        if(config.getConnectionSchema() != null) builder.connectionSchema(config.getConnectionSchema());
        builder.autoCommit(false);
        builder.connectionReadOnly(config.isConnectionReadOnly());
        if(config.getDataSourceConnectionExpire() != 0) builder.connectionExpire(config.getDataSourceConnectionExpire());
        if(config.getDataSourceConnectionExpireAfterAccess() != 0) builder.connectionExpireAfterAccess(config.getDataSourceConnectionExpireAfterAccess());
        if(config.getDataSourceConnectionLoginTimeout() != 0) builder.connectionLoginTimeout(config.getDataSourceConnectionLoginTimeout());
        if(config.getDataSourceMaximumPoolSize() != 0) builder.maximumPoolSize(config.getDataSourceMaximumPoolSize());
        if(config.getDataSourceMinimumIdleConnectionPoolSize() != 0) builder.minimumIdleConnectionPoolSize(config.getDataSourceMinimumIdleConnectionPoolSize());

        if(config.getConnectionIsolationLevel() != 0) builder.connectionIsolationLevel(config.getConnectionIsolationLevel());
        return builder.build();
    }
}
