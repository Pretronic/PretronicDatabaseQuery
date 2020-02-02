/*
 * (C) Copyright 2020 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 02.02.20, 13:29
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

package net.pretronic.databasequery.sql.dialect.defaults;

import net.prematic.databasequery.api.exceptions.DatabaseQueryException;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;

import java.net.InetSocketAddress;

public class MariaDBDialect extends AbstractDialect {

    public MariaDBDialect() {
        super("MariaDB", "org.mariadb.jdbc.Driver", "mariadb", DatabaseDriverEnvironment.REMOTE);
    }

    @Override
    public String createConnectionString(String connectionString, Object host) {
        if(connectionString != null) {
            return connectionString;
        } else if(host instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) host;
            return String.format("jdbc:mariadb://%s:%s", address.getHostName(), address.getPort());
        }
        throw new DatabaseQueryException("Can't match jdbc url for dialect " + getName());
    }
}
