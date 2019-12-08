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

import net.prematic.databasequery.api.Database;
import net.prematic.databasequery.api.driver.DatabaseDriver;
import net.prematic.databasequery.sql.SqlDatabaseDriver;
import net.prematic.databasequery.sql.SqlDatabaseDriverConfig;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.utility.StringUtil;
import net.prematic.libraries.utility.Validate;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;

public class MySqlDatabaseDriver extends SqlDatabaseDriver {

    static {
        DatabaseDriver.registerCreator(MySqlDatabaseDriver.class, (name, config, logger, executorService, properties) ->
                new MySqlDatabaseDriver(name, new SqlDatabaseDriverConfig(config), logger, executorService));
    }

    private static final String TYPE = "MySql";

    protected DataSource dataSource;

    public MySqlDatabaseDriver(String name, String baseJdbcUrl, SqlDatabaseDriverConfig config, PrematicLogger logger, ExecutorService executorService) {
        super(name, baseJdbcUrl, config, logger, executorService);
        if(getConfig().isMultipleDatabaseConnectionsAble()) {
            this.dataSource = SqlDatabaseDriver.getDataSourceCreator(this).apply(this, null);
        }
    }

    public MySqlDatabaseDriver(String name, SqlDatabaseDriverConfig config, PrematicLogger logger, ExecutorService executorService) {
        this(name, createBaseJdbcUrl(config), config, logger, executorService);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Database getDatabase(String name) {
        return new MySqlDatabase(name, this) {
            @Override
            public DataSource getDataSource() {
                if(getConfig().isMultipleDatabaseConnectionsAble() && this.getDriver().dataSource != null) {
                    return this.getDriver().dataSource;
                }
                String jdbcUrl = this.driver.getBaseJdbcUrl();
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
                return SqlDatabaseDriver.getDataSourceCreator(this.getDriver()).apply(this.getDriver(), newJdbcUrl);
            }
        };
    }

    @Override
    public void dropDatabase(String name) {
        ((MySqlDatabase)getDatabase(name)).executeSimpleUpdateQuery("DROP DATABASE IF EXISTS `" + name + "`", true);
    }

    private static String createBaseJdbcUrl(SqlDatabaseDriverConfig config) {
        return config.getJdbcUrl() != null ? config.getJdbcUrl() : String.format("jdbc:mysql://%s:%s", config.getHost(), config.getPort() == 0 ? 3306 : config.getPort());
    }
}