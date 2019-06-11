/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 25.05.19, 23:08
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

package net.prematic.databasequery.sql.mysql;

import com.zaxxer.hikari.HikariConfig;
import net.prematic.databasequery.core.Database;
import net.prematic.databasequery.core.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.sql.SqlDatabaseDriver;

import java.util.Collection;
import java.util.HashSet;

public class MySqlDatabaseDriver extends SqlDatabaseDriver {

    private static final String TYPE = "MySql";
    private final Collection<DataTypeAdapter> dataTypeAdapters;

    public MySqlDatabaseDriver(String name, HikariConfig config) {
        super(name, config);
        this.dataTypeAdapters = new HashSet<>();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Database getDatabase(String name) {
        return new MySqlDatabase(name, this);
    }

    @Override
    public void dropDatabase(String name) {

    }

    @Override
    public Collection<DataTypeAdapter> getDataTypeAdapters() {
        return this.dataTypeAdapters;
    }
}
