/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 10.04.20, 19:18
 * @web %web%
 *
 * The PretronicDatabaseQuery Project is under the Apache License, version 2.0 (the "License");
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

package net.pretronic.databasequery.mongodb.driver;

import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.databasequery.mongodb.driver.config.MongoDBDatabaseDriverConfig;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.utility.Validate;

import java.util.concurrent.ExecutorService;

public class MongoDBDatabaseDriverFactory extends DatabaseDriverFactory {

    @Override
    public DatabaseDriver createDriver(String name, DatabaseDriverConfig<?> config, PretronicLogger logger, ExecutorService executorService) {
        Validate.notNull(name);
        Validate.isTrue(config instanceof MongoDBDatabaseDriverConfig);
        return new MongoDBDatabaseDriver(name, (MongoDBDatabaseDriverConfig) config, logger, executorService);
    }

    @Override
    public DatabaseDriverConfig<?> createConfig(Document config) {
        return config.getAsObject(MongoDBDatabaseDriverConfig.class);
    }
}
