/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 16.10.19, 18:49
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

import net.prematic.databasequery.core.DatabaseDriver;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriverConfig;
import net.prematic.libraries.document.Document;
import net.prematic.libraries.logging.PrematicLogger;

public class H2PortableDatabaseDriverConfig extends MySqlDatabaseDriverConfig {

    public H2PortableDatabaseDriverConfig(Class<? extends DatabaseDriver> driverClass) {
        super(driverClass);
    }

    public H2PortableDatabaseDriverConfig(Document original) {
        super(original);
    }

    @Override
    public DatabaseDriver createDatabaseDriver(String name, PrematicLogger logger) {
        return new H2PortableDatabaseDriver(name, this, logger);
    }
}
