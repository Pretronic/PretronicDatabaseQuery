/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 25.05.19, 23:08
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

package net.prematic.databasequery.sql.mysql;

import net.prematic.databasequery.core.Database;
import net.prematic.databasequery.core.DatabaseDriver;
import net.prematic.databasequery.core.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.sql.SqlDatabaseConnectionHolder;
import net.prematic.databasequery.sql.SqlDatabaseDriver;
import net.prematic.databasequery.sql.SqlDatabaseDriverConfig;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.utility.StringUtil;
import net.prematic.libraries.utility.Validate;

import java.util.Collection;
import java.util.HashSet;

public class MySqlDatabaseDriver extends SqlDatabaseDriver {

    static {
        DatabaseDriver.registerCreator(MySqlDatabaseDriver.class, (name, config, logger, properties) ->
                new MySqlDatabaseDriver(name, new MySqlDatabaseDriverConfig(config), logger));
    }

    private static final String TYPE = "MySql";
    private final Collection<DataTypeAdapter> dataTypeAdapters;
    protected SqlDatabaseConnectionHolder connectionHolder;

    public MySqlDatabaseDriver(String name, MySqlDatabaseDriverConfig config, PrematicLogger logger) {
        super(name, config, logger);
        this.dataTypeAdapters = new HashSet<>();
        if(getConfig().isMultipleDatabaseConnectionsAble()) {
            this.connectionHolder = createConnectionHolder(this, logger, null);
        }
    }


    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Database getDatabase(String name) {
        return new MySqlDatabase(name, this) {
            @Override
            public SqlDatabaseConnectionHolder getConnectionHolder() {
                if(getConfig().isMultipleDatabaseConnectionsAble() && this.getDriver().connectionHolder != null  && this.getDriver().connectionHolder.isConnected()) {
                    return this.getDriver().connectionHolder;
                }
                String jdbcUrl = this.driver.createBaseJdbcUrl();
                String[] splitted = StringUtil.splitAndKeep(jdbcUrl, "[&;]");
                String newJdbcUrl = "";
                Validate.isTrue(splitted[0].contains("jdbc"), "Not valid jdbc url {}", jdbcUrl);
                if(splitted[0].contains("jdbc")) {
                    if(splitted[0].endsWith("/")) newJdbcUrl+=splitted[0]+name;
                    else newJdbcUrl+="/"+splitted[0]+name;
                }
                for (int i = 1; i < splitted.length; i++) {
                    newJdbcUrl+=splitted[i];
                }
                SqlDatabaseConnectionHolder connectionHolder = createConnectionHolder(this.getDriver(), getLogger(), newJdbcUrl);
                connectionHolder.connect();
                return connectionHolder;
            }
        };
    }

    @Override
    public void dropDatabase(String name) {
        ((MySqlDatabase)getDatabase(name)).executeSimpleUpdateQuery("DROP DATABASE IF EXISTS `" + name + "`", true);
    }

    @Override
    public Collection<DataTypeAdapter> getDataTypeAdapters() {
        return this.dataTypeAdapters;
    }

    @Override
    public boolean isConnected() {
        return this.connectionHolder.isConnected();
    }

    @Override
    public void connect() {
        if(this.connectionHolder != null) this.connectionHolder.connect();
    }

    @Override
    public void disconnect() {
        if(this.connectionHolder != null) this.connectionHolder.disconnect();
    }

    @Override
    public String createBaseJdbcUrl() {
        return getConfig().getJdbcUrl() != null ? getConfig().getJdbcUrl() : String.format("jdbc:mysql://%s:%s", getConfig().getHost(), getConfig().getPort() == 0 ? 3306 : getConfig().getPort());
    }

    private static SqlDatabaseConnectionHolder createConnectionHolder(SqlDatabaseDriver driver, PrematicLogger logger, String jdbcUrl) {
        SqlDatabaseDriverConfig config = driver.getConfig();
        if(config.isMultipleDatabaseConnectionsAble()) {
            return config.getDataSourceConfig() == null ? new SqlDatabaseConnectionHolder.SingleConnection(driver, logger, jdbcUrl) : new SqlDatabaseConnectionHolder.DataSource(driver, logger, jdbcUrl);
        } else {
            return config.getDataSourceConfig() == null ? new SqlDatabaseConnectionHolder.SingleConnection(driver, logger, jdbcUrl) : new SqlDatabaseConnectionHolder.DataSource(driver, logger, jdbcUrl);
        }
    }
}