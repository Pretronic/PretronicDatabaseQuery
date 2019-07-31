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
import net.prematic.databasequery.core.DatabaseDriver;
import net.prematic.databasequery.core.datatype.DataType;
import net.prematic.databasequery.core.exceptions.DatabaseQueryConnectException;
import net.prematic.databasequery.core.exceptions.DatabaseQueryExecuteFailedException;
import net.prematic.databasequery.core.impl.DataTypeInformation;
import net.prematic.libraries.logging.PrematicLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class SqlDatabaseDriver implements DatabaseDriver {

    private HikariDataSource dataSource;
    private final String name;
    private final HikariConfig config;
    private final Set<DataTypeInformation> dataTypeInformation;
    private final PrematicLogger logger;

    public SqlDatabaseDriver(String name, HikariConfig config, PrematicLogger logger) {
        this.name = name == null ? getType() : name;
        this.config = config;
        this.logger = logger;
        this.dataTypeInformation = new HashSet<>();
        registerDataTypeInformation();
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Set<DataTypeInformation> getDataTypeInformation() {
        return dataTypeInformation;
    }

    public DataTypeInformation getDataTypeInformationByDataType(DataType dataType) {
        for (DataTypeInformation dataTypeInformation : getDataTypeInformation()) {
            if(dataTypeInformation.getDataType() == dataType) return dataTypeInformation;
        }
        return DataTypeInformation.from().dataType(dataType).names(dataType.toString());
    }

    public DataTypeInformation getDataTypeInformationByName(String name) {
        for (DataTypeInformation dataTypeInformation : getDataTypeInformation()) {
            for (String dataTypeName : dataTypeInformation.getNames()) {
                if(dataTypeName.equalsIgnoreCase(name)) return dataTypeInformation;
            }
        }
        return null;
    }

    public Connection getConnection() throws SQLException {
        return getDataSource() == null ? null : getDataSource().getConnection();
    }

    @Override
    public boolean isConnected() {
        return this.dataSource.isRunning();
    }

    @Override
    public void connect() {
        this.config.setAutoCommit(false);
        this.dataSource = new HikariDataSource(this.config);
        try {
            this.dataSource.getConnection();
            getLogger().info("Connected to sql database at {}", this.dataSource.getJdbcUrl());
        } catch (SQLException exception) {
            getLogger().info("Failed to connect to sql database at {}", this.dataSource.getJdbcUrl());
            throw new DatabaseQueryConnectException(exception.getMessage(), exception);
        }
    }

    @Override
    public void disconnect() {
        this.dataSource.close();
        getLogger().info("Disconnected from sql database at {}", this.dataSource.getJdbcUrl());
    }

    @Override
    public PrematicLogger getLogger() {
        return this.logger;
    }

    public void executeSimpleUpdateQuery(String sql) {
        executeUpdateQuery(sql, ignored -> {});
    }

    public void executeResultQuery(String sql, Consumer<PreparedStatement> preparedStatementConsumer, Consumer<ResultSet> resultSetConsumer) {
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatementConsumer.accept(preparedStatement);
            resultSetConsumer.accept(preparedStatement.executeQuery());
            connection.commit();
        } catch (SQLException exception) {
            throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
        }
    }

    public void executeUpdateQuery(String sql, Consumer<PreparedStatement> preparedStatementConsumer) {
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatementConsumer.accept(preparedStatement);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException exception) {
            throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
        }
    }

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
}