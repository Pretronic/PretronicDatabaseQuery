/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 20:52
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

package net.pretronic.databasequery.sql.query.type;

import net.prematic.databasequery.api.collection.DatabaseCollection;
import net.prematic.libraries.utility.map.Pair;
import net.pretronic.databasequery.common.query.type.AbstractCreateQuery;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;

import java.util.List;

public class SQLCreateQuery extends AbstractCreateQuery<SQLDatabase> {

    public SQLCreateQuery(String name, SQLDatabase database) {
        super(name, database);
    }

    @Override
    public DatabaseCollection create() {
        Pair<String, List<Object>> data = this.database.getDriver().getDialect().newCreateQuery(this.database, this.entries, this.name, this.engine, this.type, this.includingQuery, EMPTY_OBJECT_ARRAY);
        this.database.executeUpdateQuery(data.getKey(), true, preparedStatement -> {
            for (int i = 1; i <= data.getValue().size(); i++) {
                preparedStatement.setObject(i, data.getValue().get(i-1));
            }
        });
        return new SQLDatabaseCollection(this.name, this.database, this.type);
    }
}