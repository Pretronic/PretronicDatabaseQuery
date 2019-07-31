/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.07.19, 17:35
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

package net.prematic.databasequery.example;

import com.zaxxer.hikari.HikariConfig;
import net.prematic.databasequery.core.DatabaseDriver;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.logging.SimplePrematicLogger;
import net.prematic.libraries.logging.bridge.slf4j.SLF4JStaticBridge;

public class MySqlDatabaseConnection {

    public static void main(String[] args) {

        //Create a new prematic logger and set it as SLF4J logger
        PrematicLogger logger = new SimplePrematicLogger();
        SLF4JStaticBridge.setLogger(logger);

        //Simple hikari config. For more show on github of hikaricp
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306");
        config.setUsername("root");
        config.setPassword("your_password");

        DatabaseDriver databaseDriver = new MySqlDatabaseDriver("Name of driver", config, logger);
        databaseDriver.connect();
        databaseDriver.registerDefaultAdapters();
    }
}