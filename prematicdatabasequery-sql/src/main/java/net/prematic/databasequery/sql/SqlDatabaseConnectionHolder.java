/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 16.09.19, 22:23
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

import net.prematic.databasequery.core.exceptions.DatabaseQueryConnectException;
import net.prematic.libraries.logging.PrematicLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface SqlDatabaseConnectionHolder {

    Connection getConnection() throws SQLException;

    boolean isConnected();

    void connect();

    void disconnect();

    void addJdbcUrlExtra(String extra);

    class DataSource implements SqlDatabaseConnectionHolder {

        private javax.sql.DataSource dataSource;
        private final SqlDatabaseDriver driver;
        private final PrematicLogger logger;
        private String jdbcUrl;

        public DataSource(SqlDatabaseDriver driver, PrematicLogger logger, String jdbcUrl) {
            this.driver = driver;
            this.logger = logger;
            this.jdbcUrl = jdbcUrl;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return this.dataSource.getConnection();
        }

        @Override
        public boolean isConnected() {
            try {
                Connection connection = this.dataSource.getConnection();
                connection.close();
                return true;
            } catch (SQLException exception) {
                return false;
            }
        }

        @Override
        public void connect() {
            try {
                this.dataSource = SqlDatabaseDriver.DATA_SOURCE_CREATORS.get(Class.forName(driver.getConfig().getDataSourceConfig().getClassName())).apply(this.driver, this.jdbcUrl);
                Connection connection = this.dataSource.getConnection();
                this.logger.info("Connected to sql database at {}", this.driver.createBaseJdbcUrl());
                connection.close();
            } catch (SQLException | ClassNotFoundException exception) {
                this.logger.info("Failed to connect to sql database at {}", this.driver.createBaseJdbcUrl());
                throw new DatabaseQueryConnectException(exception.getMessage(), exception);
            }
        }

        @Override
        public void disconnect() {
            this.logger.info("Disconnected from sql database at {}", this.driver.createBaseJdbcUrl());
            if(this.dataSource != null && this.dataSource instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) this.dataSource).close();
                } catch (Exception ignored) {}
            }
        }

        @Override
        public void addJdbcUrlExtra(String extra) {
            if(this.jdbcUrl == null) this.jdbcUrl = this.driver.createBaseJdbcUrl();
            jdbcUrl+=extra;
        }
    }

    class SingleConnection implements SqlDatabaseConnectionHolder {

        private Connection connection;
        private final SqlDatabaseDriver driver;
        private final PrematicLogger logger;
        private String jdbcUrl;

        public SingleConnection(SqlDatabaseDriver driver, PrematicLogger logger, String jdbcUrl) {
            this.driver = driver;
            this.logger = logger;
            this.jdbcUrl = jdbcUrl;
        }

        @Override
        public Connection getConnection() throws SQLException {
            if(this.connection.isValid(1)) return this.connection;
            else this.logger.info("Connection is not valid. Trying to reconnect.");
            return null;
        }

        @Override
        public boolean isConnected() {
            try {
                return !this.connection.isClosed();
            } catch (SQLException ignored) {
                return false;
            }
        }

        @Override
        public void connect() {
            try {
                this.connection = DriverManager.getConnection(this.jdbcUrl != null ? this.jdbcUrl : this.driver.createBaseJdbcUrl());
                this.logger.info("Connected to sql database at {}", this.jdbcUrl != null ? this.jdbcUrl : this.driver.createBaseJdbcUrl());
            } catch (SQLException exception) {
                this.logger.info("Failed to connect to sql database at {}", this.jdbcUrl != null ? this.jdbcUrl : this.driver.createBaseJdbcUrl());
                throw new DatabaseQueryConnectException(exception.getMessage(), exception);
            }
        }

        @Override
        public void disconnect() {
            this.logger.info("Disconnected from sql database at {}", this.jdbcUrl != null ? this.jdbcUrl : this.driver.createBaseJdbcUrl());
            try {
                this.connection.close();
            } catch (SQLException ignored) {}
        }

        @Override
        public void addJdbcUrlExtra(String extra) {
            if(this.jdbcUrl == null) this.jdbcUrl = this.driver.createBaseJdbcUrl();
            this.jdbcUrl+=extra;
        }
    }
}