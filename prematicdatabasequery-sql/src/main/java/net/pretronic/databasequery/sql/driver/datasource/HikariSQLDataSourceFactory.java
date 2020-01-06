/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.12.19, 15:57
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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.IsolationLevel;
import net.prematic.libraries.utility.Validate;
import net.pretronic.databasequery.sql.driver.SQLDatabaseDriver;
import net.pretronic.databasequery.sql.driver.config.SQLRemoteDatabaseDriverConfig;

import javax.sql.DataSource;

public class HikariSQLDataSourceFactory implements SQLDataSourceFactory {

    @Override
    public DataSource createDataSource(SQLDatabaseDriver driver) {
        Validate.isTrue(driver.getConfig() instanceof SQLRemoteDatabaseDriverConfig);
        SQLRemoteDatabaseDriverConfig config = (SQLRemoteDatabaseDriverConfig) driver.getConfig();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(driver.getName());
        hikariConfig.setJdbcUrl(config.getConnectionString());
        if(config.getUsername() != null) hikariConfig.setUsername(config.getUsername());
        if(config.getPassword() != null) hikariConfig.setPassword(config.getPassword());
        if(config.getConnectionCatalog() != null) hikariConfig.setCatalog(config.getConnectionCatalog());
        if(config.getConnectionSchema() != null) hikariConfig.setSchema(config.getConnectionSchema());
        hikariConfig.setAutoCommit(config.isAutoCommit());
        hikariConfig.setReadOnly(config.isConnectionReadOnly());
        if(config.getDataSourceConnectionExpire() != 0) hikariConfig.setMaxLifetime(config.getDataSourceConnectionExpire());
        if(config.getDataSourceConnectionExpireAfterAccess() != 0) hikariConfig.setIdleTimeout(config.getDataSourceConnectionExpireAfterAccess());
        if(config.getDataSourceConnectionLoginTimeout() != 0) hikariConfig.setConnectionTimeout(config.getDataSourceConnectionLoginTimeout());
        if(config.getDataSourceMaximumPoolSize() != 0) hikariConfig.setMaximumPoolSize(config.getDataSourceMaximumPoolSize());
        if(config.getDataSourceMinimumIdleConnectionPoolSize() != 0) hikariConfig.setMinimumIdle(config.getDataSourceMinimumIdleConnectionPoolSize());

        if(config.getConnectionIsolationLevel() != 0) hikariConfig.setTransactionIsolation(convertToHikariIsolationLevel(config.getConnectionIsolationLevel()));
        return new HikariDataSource(hikariConfig);
    }

    private static String convertToHikariIsolationLevel(int level) {
        for (IsolationLevel value : IsolationLevel.values()) {
            if(value.getLevelId() == level) return value.name();
        }
        return null;
    }
}
