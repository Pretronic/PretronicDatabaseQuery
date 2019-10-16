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

import net.prematic.databasequery.core.Database;
import net.prematic.databasequery.core.DatabaseDriver;
import net.prematic.databasequery.core.datatype.DataType;
import net.prematic.databasequery.core.query.option.CreateOption;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.core.query.result.QueryResultEntry;
import net.prematic.databasequery.sql.h2.H2PortableDatabaseDriver;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriverConfig;
import net.prematic.libraries.logging.LoggingUncaughtExceptionHandler;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.logging.SimplePrematicLogger;
import net.prematic.libraries.logging.bridge.slf4j.SLF4JStaticBridge;
import net.prematic.libraries.logging.level.LogLevel;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MySqlDatabaseConnection {

    @Test
    public void testConnection() throws InterruptedException {
        PrematicLogger logger = new SimplePrematicLogger();
        LoggingUncaughtExceptionHandler.hook(logger);
        logger.setLevel(LogLevel.DEBUG);
        SLF4JStaticBridge.setLogger(logger);

        MySqlDatabaseDriverConfig config = (MySqlDatabaseDriverConfig) new MySqlDatabaseDriverConfig(H2PortableDatabaseDriver.class).setHost("127.0.0.1")
                .setPort(3306).setUsername("root")
                .useDataSource().setClassName("com.zaxxer.hikari.HikariDataSource")
                .out();
        config.setMultipleDatabaseConnectionsAble(true);

        DatabaseDriver driver = new MySqlDatabaseDriver("Production", config, logger);
        driver.connect();
        Database database = driver.getDatabase("production");

        database.createCollection("user")
                .attribute("id", DataType.INTEGER, CreateOption.PRIMARY_KEY, CreateOption.AUTO_INCREMENT)
                .attribute("name", DataType.LONG_TEXT, CreateOption.NOT_NULL)
                .attribute("number", DataType.INTEGER, CreateOption.NOT_NULL)
                .create().thenAccept(user -> {
            for (int i = 0; i < 1; i++) {
                CompletableFuture<QueryResult> future = user.insert().set("name", "peter").set("number", i).executeAndGetGeneratedKeys("id");
                future.thenAccept(result -> {
                    for (QueryResultEntry resultEntry : result) {
                        logger.info("----------");
                        logger.info("Generated Keys:");
                        for (Map.Entry<String, Object> entry : resultEntry) {
                            logger.info(entry.getKey() + " | " + entry.getValue());
                        }
                    }
                    logger.info("----------");
                });
            }

            user.find().execute().thenAccept(result -> {
                for (QueryResultEntry resultEntry : result) {
                    logger.info("----------");
                    logger.info("Entry:");
                    for (Map.Entry<String, Object> entry : resultEntry) {
                        logger.info(entry.getKey() + " | " + entry.getValue());
                    }
                }
                logger.info("----------");
            });
        });
        Thread.sleep(3000);
        driver.disconnect();
    }
}