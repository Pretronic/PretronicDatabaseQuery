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
import net.prematic.databasequery.core.impl.DataTypeInformation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public abstract class SqlDatabaseDriver implements DatabaseDriver {

    private HikariDataSource dataSource;
    private final String name;
    private final HikariConfig config;
    private final Set<DataTypeInformation> dataTypeInformation;

    public SqlDatabaseDriver(String name, HikariConfig config) {
        this.name = name == null ? getType() : name;
        this.config = config;
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

    public DataTypeInformation getDataTypeInformation(DataType dataType) {
        for (DataTypeInformation dataTypeInformation : getDataTypeInformation()) {
            if(dataTypeInformation.getDataType() == dataType) return dataTypeInformation;
        }
        return new DataTypeInformation(dataType, dataType.toString());
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
    }

    @Override
    public void disconnect() {
        this.dataSource.close();
    }

    public void registerDataTypeInformation() {
        //@Todo specify default sizes
        this.dataTypeInformation.add(new DataTypeInformation(DataType.DOUBLE, "DOUBLE"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.DECIMAL, "DECIMAL"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.FLOAT, "FLOAT"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.INTEGER, "INTEGER"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.LONG, "BIGINT"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.CHAR, "CHAR"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.STRING, "VARCHAR", 255));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.LONG_TEXT, "LONGTEXT"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.DATE, "DATE"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.DATETIME, "DATETIME"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.TIMESTAMP, "TIMESTAMP"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.BINARY, "BINARY"));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.BLOB, "BLOB", false));
        this.dataTypeInformation.add(new DataTypeInformation(DataType.UUID, "BINARY", true,16));
    }
}