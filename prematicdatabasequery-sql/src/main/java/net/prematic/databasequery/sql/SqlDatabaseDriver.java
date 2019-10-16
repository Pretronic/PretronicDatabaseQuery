/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 12.05.19, 18:28
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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.IsolationLevel;
import net.prematic.databasequery.core.DatabaseDriver;
import net.prematic.databasequery.core.datatype.DataType;
import net.prematic.databasequery.core.exceptions.DatabaseQueryExecuteFailedException;
import net.prematic.databasequery.core.impl.DataTypeInformation;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.utility.annonations.Internal;
import net.prematic.sqlconnectionpool.PrematicDataSourceBuilder;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public abstract class SqlDatabaseDriver implements DatabaseDriver {

    public static Map<Class<?>, BiFunction<SqlDatabaseDriver, String, DataSource>> DATA_SOURCE_CREATORS = new HashMap<>();

    //Initialize default data source creators
    static {
        try {
            Class<?> dataSourceClass = Class.forName("net.prematic.sqlconnectionpool.PrematicDataSource");
            registerDataSourceCreator(dataSourceClass, (driver, jdbcUrl) -> {
                PrematicDataSourceBuilder builder = new PrematicDataSourceBuilder();
                SqlDatabaseDriverConfig config = driver.getConfig();
                //Todo executor service
                return builder.jdbcUrl(jdbcUrl == null ? driver.createBaseJdbcUrl() : jdbcUrl)
                        .username(config.getUsername())
                        .password(config.getPassword())
                        .driverClassName(config.getDriverClassName())
                        .connectionCatalog(config.getConnectionCatalog())
                        .connectionSchema(config.getConnectionSchema())
                        .logger(driver.getLogger())
                        .autoCommit(config.isAutoCommit())
                        .connectionReadOnly(config.isConnectionReadOnly())
                        .connectionExpireAfterAccess(config.getDataSourceConfig().getConnectionExpireAfterAccess())
                        .connectionExpire(config.getDataSourceConfig().getConnectionExpire())
                        .connectionLoginTimeout(config.getDataSourceConfig().getConnectionLoginTimeout())
                        .maximumPoolSize(config.getDataSourceConfig().getMaximumPoolSize())
                        .minimumIdleConnectionPoolSize(config.getDataSourceConfig().getMinimumIdleConnectionPoolSize())
                        .connectionIsolationLevel(config.getConnectionIsolationLevel())
                        .connectionNetworkTimeout(config.getConnectionNetworkTimeout())
                        .build();
            });
        } catch (ClassNotFoundException ignored) {}

        //@Todo logger
        try {
            Class<?> dataSourceClass = Class.forName("com.zaxxer.hikari.HikariDataSource");
            registerDataSourceCreator(dataSourceClass, (driver, jdbcUrl) -> {
                SqlDatabaseDriverConfig config = driver.getConfig();
                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setJdbcUrl(driver.createBaseJdbcUrl());
                if(config.getUsername() != null) hikariConfig.setUsername(config.getUsername());
                if(config.getPassword() != null) hikariConfig.setPassword(config.getPassword());
                if(config.getDriverClassName() != null) hikariConfig.setDriverClassName(config.getDriverClassName());
                if(config.getConnectionCatalog() != null) hikariConfig.setCatalog(config.getConnectionCatalog());
                if(config.getConnectionSchema() != null) hikariConfig.setSchema(config.getConnectionSchema());
                hikariConfig.setAutoCommit(config.isAutoCommit());
                hikariConfig.setReadOnly(config.isConnectionReadOnly());
                if(config.getDataSourceConfig() != null) {
                    if(config.getDataSourceConfig().getConnectionExpire() != 0) hikariConfig.setMaxLifetime(config.getDataSourceConfig().getConnectionExpire());
                    if(config.getDataSourceConfig().getConnectionExpireAfterAccess() != 0) hikariConfig.setIdleTimeout(config.getDataSourceConfig().getConnectionExpireAfterAccess());
                    if(config.getDataSourceConfig().getConnectionLoginTimeout() != 0) hikariConfig.setConnectionTimeout(config.getDataSourceConfig().getConnectionLoginTimeout());
                    if(config.getDataSourceConfig().getMaximumPoolSize() != 0) hikariConfig.setMaximumPoolSize(config.getDataSourceConfig().getMaximumPoolSize());
                    if(config.getDataSourceConfig().getMinimumIdleConnectionPoolSize() != 0) hikariConfig.setMinimumIdle(config.getDataSourceConfig().getMinimumIdleConnectionPoolSize());
                }
                if(config.getConnectionIsolationLevel() != 0) hikariConfig.setTransactionIsolation(convertToHikariIsolationLevel(config.getConnectionIsolationLevel()));
                return new HikariDataSource(hikariConfig);
            });
        } catch (ClassNotFoundException ignored) {}
    }

    private final String name;
    private final Set<DataTypeInformation> dataTypeInformation;
    private final PrematicLogger logger;
    private final SqlDatabaseDriverConfig config;

    public SqlDatabaseDriver(String name, SqlDatabaseDriverConfig config, PrematicLogger logger) {
        this.name = name == null ? getType() : name;
        this.logger = logger;
        this.config = config;
        this.dataTypeInformation = new HashSet<>();
        registerDataTypeInformation();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Internal
    public boolean useDataSource() {
        return this.config.getDataSourceConfig() != null;
    }

    @Internal
    public Set<DataTypeInformation> getDataTypeInformation() {
        return dataTypeInformation;
    }

    @Internal
    public DataTypeInformation getDataTypeInformationByDataType(DataType dataType) {
        for (DataTypeInformation dataTypeInformation : getDataTypeInformation()) {
            if(dataTypeInformation.getDataType() == dataType) return dataTypeInformation;
        }
        return DataTypeInformation.from().dataType(dataType).names(dataType.toString());
    }

    @Internal
    public DataTypeInformation getDataTypeInformationByName(String name) {
        for (DataTypeInformation dataTypeInformation : getDataTypeInformation()) {
            for (String dataTypeName : dataTypeInformation.getNames()) {
                if(dataTypeName.equalsIgnoreCase(name)) return dataTypeInformation;
            }
        }
        return null;
    }

    @Override
    public abstract boolean isConnected();

    @Override
    public abstract void connect();

    @Override
    public abstract void disconnect();

    @Override
    public PrematicLogger getLogger() {
        return this.logger;
    }

    public SqlDatabaseDriverConfig getConfig() {
        return config;
    }

    public abstract String createBaseJdbcUrl();

    private void registerDataTypeInformation() {
        //@Todo specify default sizes
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.DOUBLE).names("DOUBLE"));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.DECIMAL).names("DECIMAL"));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.FLOAT).names("FLOAT"));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.INTEGER).names("INTEGER", "INT"));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.LONG).names("BIGINT").defaultSize(8));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.CHAR).names("CHAR").defaultSize(1));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.STRING).names("VARCHAR").defaultSize(255));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.LONG_TEXT).names("LONGTEXT").sizeAble(false));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.DATE).names("DATE"));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.DATETIME).names("DATETIME"));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.TIMESTAMP).names("TIMESTAMP"));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.BINARY).names("BINARY"));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.BLOB).names("BLOB").sizeAble(false));
        this.dataTypeInformation.add(DataTypeInformation.from().dataType(DataType.UUID).names("BINARY").defaultSize(16));
    }

    @Internal
    public void handleDatabaseQueryExecuteFailedException(SQLException exception, String query) {
        getLogger().info("Error executing sql query: {}", query);
        throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
    }

    public static void registerDataSourceCreator(Class<?> dataSourceClassName, BiFunction<SqlDatabaseDriver, String, DataSource> creator) {
        DATA_SOURCE_CREATORS.put(dataSourceClassName, creator);
    }

    private static String convertToHikariIsolationLevel(int level) {
        for (IsolationLevel value : IsolationLevel.values()) {
            if(value.getLevelId() == level) return value.name();
        }
        return null;
    }
}