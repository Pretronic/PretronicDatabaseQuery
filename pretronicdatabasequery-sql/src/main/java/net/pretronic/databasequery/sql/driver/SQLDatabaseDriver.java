/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 16.12.19, 15:12
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

package net.pretronic.databasequery.sql.driver;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.databasequery.api.exceptions.DatabaseQueryConnectException;
import net.pretronic.databasequery.api.exceptions.DatabaseQueryException;
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
import net.pretronic.libraries.document.DocumentRegistry;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.annonations.Internal;

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
        SQLDataSourceFactory.registerFactory("net.pretronic.sqlconnectionpool.PretronicDataSource", new PCPSQLDataSourceFactory());
        DatabaseDriverFactory.registerFactory(SQLDatabaseDriver.class, new SQLDatabaseDriverFactory());
    }

    private DataSource dataSource;
    private final Collection<SQLDatabase> databases;
    private final Collection<DataTypeInfo> dataTypeInfos;

    public SQLDatabaseDriver(String name, DatabaseDriverConfig<?> config, PretronicLogger logger, ExecutorService executorService) {
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
                getLogger().info("{} Connected to remote {} database at {}", getName(),getConfig().getDialect().getName(), getConfig().getConnectionString());
            } catch (SQLException exception) {
                getLogger().info("{} Failed to connect to sql database at {}", getName(),getConfig().getConnectionString());
                throw new DatabaseQueryConnectException(exception.getMessage(), exception);
            }
        } else if(getConfig().getDialect().getEnvironment() == DatabaseDriverEnvironment.LOCAL) {
            getLogger().info("{} Connected to local {} database at {}",getName(), getConfig().getDialect().getName(), getConfig().getConnectionString());
        } else if(isConnected()) {
            getLogger().info("{} Driver already connected", getName());
        } else {
            throw new DatabaseQueryConnectException("Error by connecting to database at " + getConfig().getConnectionString());
        }
    }

    @Override
    public void disconnect() {
        getLogger().info("{} Disconnected from sql database at {}",getName(), getConfig().getConnectionString());
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

    //@Todo move data type infos to dialect
    private void registerDataTypeInfos() {
        if(this.getDialect().equals(Dialect.POSTGRESQL)) {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.DOUBLE).names("DOUBLE PRECISION"));
        } else {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.DOUBLE).names("DOUBLE"));
        }
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.DECIMAL).names("DECIMAL"));
        if(this.getDialect().equals(Dialect.POSTGRESQL)) {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.FLOAT).names("FLOAT"));
        } else {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.FLOAT).names("REAL"));
        }

        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.INTEGER).names("INTEGER", "INT"));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.LONG).names("BIGINT").defaultSize(8));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.CHAR).names("CHAR").defaultSize(1));
        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.STRING).names("VARCHAR").defaultSize(255));

        if(this.getDialect().equals(Dialect.POSTGRESQL)) {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.LONG_TEXT).names("TEXT").sizeAble(false));
        } else {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.LONG_TEXT).names("LONGTEXT").sizeAble(false));
        }

        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.DATE).names("DATE"));

        if(this.getDialect().equals(Dialect.POSTGRESQL)) {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.DATETIME).names("TIMESTAMP"));
        } else {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.DATETIME).names("DATETIME"));
        }

        this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.TIMESTAMP).names("TIMESTAMP"));

        if(this.getDialect().equals(Dialect.POSTGRESQL)) {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.BINARY).names("BYTEA"));
        } else {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.BINARY).names("BINARY"));
        }

        if(this.getDialect().equals(Dialect.POSTGRESQL)) {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.UUID).names("BYTEA").defaultSize(16));
        } else {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.UUID).names("BINARY").defaultSize(16));
        }

        if(this.getDialect().equals(Dialect.POSTGRESQL)) {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.BOOLEAN).names("BOOLEAN").sizeAble(false));
        } else {
            this.dataTypeInfos.add(new DataTypeInfo().dataType(DataType.BOOLEAN).names("BIT").defaultSize(1));
        }
    }
}
