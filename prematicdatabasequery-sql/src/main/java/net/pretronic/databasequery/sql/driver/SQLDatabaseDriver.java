/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 16.12.19, 15:12
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

package net.pretronic.databasequery.sql.driver;

import net.prematic.databasequery.api.Database;
import net.prematic.databasequery.api.datatype.DataType;
import net.prematic.databasequery.api.driver.DatabaseDriverFactory;
import net.prematic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.prematic.databasequery.api.exceptions.DatabaseQueryConnectException;
import net.prematic.databasequery.api.exceptions.DatabaseQueryException;
import net.prematic.libraries.document.DocumentRegistry;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.utility.Iterators;
import net.prematic.libraries.utility.annonations.Internal;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.common.driver.AbstractDatabaseDriver;
import net.pretronic.databasequery.sql.DataTypeInfo;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.dialect.Dialect;
import net.pretronic.databasequery.sql.dialect.DialectDocumentAdapter;
import net.pretronic.databasequery.sql.driver.config.SQLDatabaseDriverConfig;
import net.pretronic.databasequery.sql.driver.datasource.HikariSQLDataSourceFactory;
import net.pretronic.databasequery.sql.driver.datasource.PCPSQLDataSourceFactory;
import net.pretronic.databasequery.sql.driver.datasource.SQLDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

public class SQLDatabaseDriver extends AbstractDatabaseDriver {

    static {
        DocumentRegistry.getDefaultContext().registerHierarchyAdapter(Dialect.class, new DialectDocumentAdapter());
        SQLDataSourceFactory.registerFactory("com.zaxxer.hikari.HikariDataSource", new HikariSQLDataSourceFactory());
        SQLDataSourceFactory.registerFactory("net.prematic.sqlconnectionpool.PrematicDataSource", new PCPSQLDataSourceFactory());
        DatabaseDriverFactory.registerFactory(SQLDatabaseDriver.class, new SQLDatabaseDriverFactory());
    }

    private DataSource dataSource;
    private final Collection<SQLDatabase> databases;
    private final Collection<DataTypeInfo> dataTypeInfos;

    public SQLDatabaseDriver(String name, DatabaseDriverConfig<?> config, PrematicLogger logger, ExecutorService executorService) {
        super(name, "SQL", config, logger, executorService);
        this.databases = new ArrayList<>();
        this.dataTypeInfos = new ArrayList<>();
        registerDataTypeInfos();
    }

    @Override
    public boolean isConnected() {
        if(getConfig().getDialect().getEnvironment() == DatabaseDriverEnvironment.REMOTE) {
            if(this.dataSource == null) return false;
            try(Connection ignored = this.dataSource.getConnection()) {
                return true;
            } catch (SQLException exception) {
                return false;
            }
        } else {
            if(databases.size() > 0) {
                for (SQLDatabase database : this.databases) {
                    return database.isLocalConnected();
                }
            }
            return false;
        }
    }

    @Override
    public void connect() {
        getDialect().loadDriver();
        if(getDialect().getEnvironment() == DatabaseDriverEnvironment.REMOTE && this.dataSource == null) {
            this.dataSource = SQLDataSourceFactory.create(this, null);
            try (Connection ignored = this.dataSource.getConnection()) {
                getLogger().info("Connected to remote {} database at {}", getConfig().getDialect().getName(), getConfig().getConnectionString());
            } catch (SQLException exception) {
                getLogger().info("Failed to connect to sql database at {}", getConfig().getConnectionString());
                throw new DatabaseQueryConnectException(exception.getMessage(), exception);
            }
        } else if(getConfig().getDialect().getEnvironment() == DatabaseDriverEnvironment.LOCAL) {
            getLogger().info("Connected to local {} database at {}", getConfig().getDialect().getName(), getConfig().getConnectionString());
        } else if(isConnected()) {
            getLogger().info("DatabaseDriver {} already connected", getName());
        } else {
            throw new DatabaseQueryConnectException("Error by connecting to database at " + getConfig().getConnectionString());
        }
    }

    @Override
    public void disconnect() {
        getLogger().info("Disconnected from sql database at {}", getConfig().getConnectionString());
        if(this.getDialect().getEnvironment() == DatabaseDriverEnvironment.LOCAL) {
            for (SQLDatabase database : this.databases) {
                if(database.getDataSource() != null && database.getDataSource() instanceof AutoCloseable) {
                    try(AutoCloseable ignored = (AutoCloseable) database.getDataSource()) {} catch (Exception ignored) {}
                }
            }
        } else if(this.dataSource != null && this.dataSource instanceof AutoCloseable) {
            try(AutoCloseable ignored = (AutoCloseable) this.dataSource) {} catch (Exception ignored) {}
        }
    }

    @Override
    public SQLDatabaseDriverConfig<?> getConfig() {
        return (SQLDatabaseDriverConfig<?>) super.getConfig();
    }

    @Override
    public Database getDatabase(String name) {
        SQLDatabase database = Iterators.findOne(this.databases, sqlDatabase -> sqlDatabase.getName().equalsIgnoreCase(name));
        if(database == null) {
            DataSource dataSource = this.dataSource;
            if(getDialect().getEnvironment() == DatabaseDriverEnvironment.LOCAL) {
                dataSource = SQLDataSourceFactory.create(this, name);
            }else if(dataSource == null) {
                throw new DatabaseQueryException(String.format("DatabaseDriver %s not connected", getName()));
            }
            database = new SQLDatabase(name, this, dataSource);
            this.databases.add(database);
        }
        return database;
    }

    @Internal
    public Dialect getDialect() {
        return getConfig().getDialect();
    }

    public DataTypeInfo getDataTypeInfo(DataType dataType) {
        return Iterators.findOne(this.dataTypeInfos, dataTypeInfo -> dataTypeInfo.getDataType() == dataType);
    }
    
    private void registerDataTypeInfos() {
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.DOUBLE).names("DOUBLE"));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.DECIMAL).names("DECIMAL"));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.FLOAT).names("FLOAT"));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.INTEGER).names("INTEGER", "INT"));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.LONG).names("BIGINT").defaultSize(8));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.CHAR).names("CHAR").defaultSize(1));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.STRING).names("VARCHAR").defaultSize(255));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.LONG_TEXT).names("LONGTEXT").sizeAble(false));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.DATE).names("DATE"));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.DATETIME).names("DATETIME"));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.TIMESTAMP).names("TIMESTAMP"));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.BINARY).names("BINARY"));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.UUID).names("BINARY").defaultSize(16));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.BOOLEAN).names("BIT").defaultSize(1));
    }
}
