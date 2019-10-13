/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 25.08.19, 16:07
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

package net.prematic.databasequery.sql.h2;

import net.prematic.databasequery.core.Database;
import net.prematic.databasequery.sql.SqlDatabaseDriverConfig;
import net.prematic.databasequery.sql.mysql.MySqlDatabase;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver;
import net.prematic.libraries.logging.PrematicLogger;

public class H2PortableDatabaseDriver extends MySqlDatabaseDriver {

    private static final String TYPE = "H2-Portable";
    private static final String JDBC_URL_EXTRA = ";MODE=Mysql;";

    public H2PortableDatabaseDriver(String name, SqlDatabaseDriverConfig config, PrematicLogger logger) {
        super(name, config, logger);
        if(this.connectionHolder != null && config.isMultipleDatabaseConnectionsAble()) this.connectionHolder.addJdbcUrlExtra(JDBC_URL_EXTRA);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Database getDatabase(String name) {
        MySqlDatabase database = ((MySqlDatabase)super.getDatabase(name));
        //database.getConnectionHolder().addJdbcUrlExtra(name);
        if(!getConfig().isMultipleDatabaseConnectionsAble()) {
            database.getConnectionHolder().addJdbcUrlExtra(JDBC_URL_EXTRA);
        }
        return database;
    }

    @Override
    public String createBaseJdbcUrl() {
        return String.format("jdbc:h2:file:%s", getConfig().getHost() == null || getConfig().getHost().equals("") ? "./" : getConfig().getHost());
    }
}