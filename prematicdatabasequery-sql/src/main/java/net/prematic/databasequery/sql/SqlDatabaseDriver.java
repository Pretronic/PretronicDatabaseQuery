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
import net.prematic.databasequery.core.Database;
import net.prematic.databasequery.core.DatabaseDriver;

import javax.print.DocFlavor;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class SqlDatabaseDriver implements DatabaseDriver {

    private HikariDataSource dataSource;
    private final String name;
    private final HikariConfig config;

    public SqlDatabaseDriver(String name, HikariConfig config) {
        this.name = name == null ? getType() : name;
        this.config = config;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public Connection getConnection() {
        try {
            if(getDataSource().isRunning()) return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isConnected() {
        return this.dataSource.isRunning();
    }

    @Override
    public void connect() {
        this.dataSource = new HikariDataSource(this.config);
    }

    @Override
    public void disconnect() {
        this.dataSource.close();
    }
}