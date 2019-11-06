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

import net.prematic.databasequery.api.Database;
import net.prematic.databasequery.api.DatabaseCollection;
import net.prematic.databasequery.api.DatabaseDriver;
import net.prematic.databasequery.api.datatype.DataType;
import net.prematic.databasequery.api.query.Query;
import net.prematic.databasequery.api.query.option.CreateOption;
import net.prematic.databasequery.api.query.result.QueryResultEntry;
import net.prematic.databasequery.sql.SqlDatabaseDriverConfig;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver;
import net.prematic.libraries.logging.LoggingUncaughtExceptionHandler;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.logging.SimplePrematicLogger;
import net.prematic.libraries.logging.bridge.slf4j.SLF4JStaticBridge;
import net.prematic.libraries.logging.level.LogLevel;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MySqlDatabaseConnection {

    @Test
    public void test() throws InterruptedException {
        PrematicLogger logger = new SimplePrematicLogger();
        LoggingUncaughtExceptionHandler.hook(logger);
        logger.setLevel(LogLevel.DEBUG);
        SLF4JStaticBridge.setLogger(logger);

        SqlDatabaseDriverConfig config = new SqlDatabaseDriverConfig(MySqlDatabaseDriver.class)
                .setHost("127.0.0.1")
                .setPort(3306).setUsername("root")
                .getDataSourceConfig().setClassName("com.zaxxer.hikari.HikariDataSource")
                .out();
        config.setMultipleDatabaseConnectionsAble(true);

        DatabaseDriver driver = config.createDatabaseDriver("production", logger);
        driver.connect();
        driver.registerDefaultAdapters();

        Database database = driver.getDatabase("production");

        DatabaseCollection user = database.createCollection("user")
                .attribute("id", DataType.INTEGER, CreateOption.PRIMARY_KEY, CreateOption.AUTO_INCREMENT)
                .attribute("name", DataType.LONG_TEXT, CreateOption.NOT_NULL)
                .attribute("number", DataType.INTEGER)
                .create();
        for (int i = 0; i < 10; i++) {
            logger.info("----------");
            logger.info("Generated Key:");
            int id = user.insert().set("name", "peter").set("number", Query.NULL).executeAndGetGeneratedKeyAsInt("id");
            logger.info("id" + " | " + id);
        }
        logger.info("...");
        for (QueryResultEntry resultEntry : user.find().execute()) {
            logger.info("----------");
            logger.info("Entry:");
            for (Map.Entry<String, Object> entry : resultEntry) {
                logger.info(entry.getKey() + " | " + entry.getValue());
            }
        }
        logger.info("----------");
        logger.info("Set column number to null and select all null entries");
        logger.info("----------");
        user.update().set("number", Query.NULL).execute();
        for (QueryResultEntry resultEntry : user.find().whereNull("number").execute()) {
            logger.info("----------");
            logger.info("Entry:");
            for (Map.Entry<String, Object> entry : resultEntry) {
                logger.info(entry.getKey() + " | " + entry.getValue());
            }
        }
        logger.info("----------");
        logger.info("Set column number where id = 1 to 110 and select all not null entries");
        logger.info("----------");
        user.update().set("number", "110").whereIn("id", 5, 6, 7).execute();
        for (QueryResultEntry resultEntry : user.find().not(query -> query.whereNull("number")).execute()) {
            logger.info("----------");
            logger.info("Entry:");
            for (Map.Entry<String, Object> entry : resultEntry) {
                logger.info(entry.getKey() + " | " + entry.getValue());
            }
        }
        Thread.sleep(3000);
        driver.disconnect();
    }
}