/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.12.19, 15:47
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

package net.pretronic.databasequery.sql.driver;

import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.sql.dialect.Dialect;
import net.pretronic.databasequery.sql.driver.config.SQLDatabaseDriverConfig;
import net.pretronic.databasequery.sql.driver.config.SQLLocalDatabaseDriverConfig;
import net.pretronic.databasequery.sql.driver.config.SQLRemoteDatabaseDriverConfig;

import java.util.concurrent.ExecutorService;

public class SQLDatabaseDriverFactory implements DatabaseDriverFactory {

    @Override
    public DatabaseDriver createDriver(String name, DatabaseDriverConfig<?> config, PretronicLogger logger, ExecutorService executorService) {
        Validate.notNull(name);
        Validate.isTrue(config instanceof SQLDatabaseDriverConfig<?>);
        return new SQLDatabaseDriver(name, config, logger, executorService);
    }

    @Override
    public DatabaseDriverConfig<?> createConfig(Document config) {
        Dialect dialect = Dialect.byName(config.getString("dialectName"));
        if(dialect.getEnvironment() == DatabaseDriverEnvironment.LOCAL) {
            return config.getAsObject(SQLLocalDatabaseDriverConfig.class);
        } else if(dialect.getEnvironment() == DatabaseDriverEnvironment.REMOTE) {
            return config.getAsObject(SQLRemoteDatabaseDriverConfig.class);
        }
        throw new IllegalArgumentException("Can't load config from document");
    }
}
