/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.12.19, 15:47
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

package net.pretronic.databasequery.sql.driver;

import net.prematic.databasequery.api.driver.DatabaseDriver;
import net.prematic.databasequery.api.driver.DatabaseDriverFactory;
import net.prematic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.prematic.libraries.document.Document;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.utility.Validate;
import net.pretronic.databasequery.sql.driver.config.SQLDatabaseDriverConfig;

import java.util.concurrent.ExecutorService;

public class SQLDatabaseDriverFactory implements DatabaseDriverFactory {

    @Override
    public DatabaseDriver createDriver(String name, DatabaseDriverConfig<?> config, PrematicLogger logger, ExecutorService executorService) {
        Validate.notNull(name);
        Validate.isTrue(config instanceof SQLDatabaseDriverConfig<?>);
        return new SQLDatabaseDriver(name, config, logger, executorService);
    }

    @Override
    public DatabaseDriverConfig<?> createConfig(Document config) {
        return null;
    }
}
