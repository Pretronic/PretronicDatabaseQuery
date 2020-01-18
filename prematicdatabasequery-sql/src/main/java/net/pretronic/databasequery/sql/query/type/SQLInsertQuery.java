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

import net.prematic.databasequery.api.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.libraries.utility.map.Pair;
import net.prematic.libraries.utility.reflect.Primitives;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.common.query.type.AbstractInsertQuery;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;

import java.util.List;

public class SQLInsertQuery extends AbstractInsertQuery<SQLDatabaseCollection> {

    public SQLInsertQuery(SQLDatabaseCollection collection) {
        super(collection);
    }

    @SuppressWarnings("unchecked")
    @Override
    public QueryResult executeAndGetGeneratedKeys(String[] keyColumns, Object... values) {
        Pair<String, List<Object>> data = this.collection.getDatabase().getDriver().getDialect()
                .newInsertQuery(this.collection, this.entries, values);
        Number[] keys = this.collection.getDatabase().executeUpdateQuery(data.getKey(), true, preparedStatement -> {
            for (int i = 1; i <= data.getValue().size(); i++) {
                Object value = data.getValue().get(i-1);
                if(value != null && !Primitives.isPrimitive(value)) {
                    DataTypeAdapter adapter = this.collection.getDatabase().getDriver().getDataTypeAdapter(value.getClass());
                    if(adapter != null) {
                        value = adapter.write(value);
                    } else {
                        value = value.toString();
                    }
                }
                preparedStatement.setObject(i, value);
            }
        }, keyColumns);
        DefaultQueryResult result = new DefaultQueryResult();
        for (int i = 0; i < keyColumns.length; i++) {
            result.addEntry(new DefaultQueryResultEntry(this.collection.getDatabase().getDriver()).addEntry(keyColumns[i], keys[i]));
        }
        return result;
    }
}
