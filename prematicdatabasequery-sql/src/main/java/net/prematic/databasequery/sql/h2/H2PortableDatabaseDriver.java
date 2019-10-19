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

import net.prematic.databasequery.api.DatabaseDriver;
import net.prematic.databasequery.sql.SqlDatabaseDriverConfig;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver;
import net.prematic.libraries.logging.PrematicLogger;

import java.util.concurrent.ExecutorService;

public class H2PortableDatabaseDriver extends MySqlDatabaseDriver {

    static {
        DatabaseDriver.registerCreator(H2PortableDatabaseDriver.class, (name, config, logger, executorService, properties) ->
                new H2PortableDatabaseDriver(name, new SqlDatabaseDriverConfig(config.getOriginal()), logger, executorService));
    }

    private static final String TYPE = "H2-Portable";

    public H2PortableDatabaseDriver(String name, SqlDatabaseDriverConfig config, PrematicLogger logger, ExecutorService executorService) {
        super(name, createBaseJdbcUrl(config), config, logger, executorService);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public static String createBaseJdbcUrl(SqlDatabaseDriverConfig config) {
        return String.format("jdbc:h2:file:%s", config.getHost() == null || config.getHost().equals("") ? "./" : config.getHost()) + ";MODE=Mysql;";
    }
}