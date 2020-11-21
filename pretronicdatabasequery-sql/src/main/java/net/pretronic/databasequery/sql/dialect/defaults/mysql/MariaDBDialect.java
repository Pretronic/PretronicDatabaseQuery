/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.05.20, 15:17
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

package net.pretronic.databasequery.sql.dialect.defaults.mysql;

import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.sql.dialect.DialectDefaultSettings;

import java.util.concurrent.TimeUnit;

public class MariaDBDialect extends MySQLDialect {

    public MariaDBDialect() {
        super("MariaDB", "org.mariadb.jdbc.Driver", "mariadb",
                new DialectDefaultSettings(3306, TimeUnit.MINUTES.toMillis(5)), DatabaseDriverEnvironment.REMOTE,
                true, "`", "`");
    }
}
