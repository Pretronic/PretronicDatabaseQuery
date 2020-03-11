/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 20:52
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

package net.pretronic.databasequery.sql.query.type;

import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.map.Pair;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.common.query.type.AbstractInsertQuery;
import net.pretronic.databasequery.sql.SQLUtil;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;
import net.pretronic.databasequery.sql.query.CommitOnExecute;

import java.util.List;

public class SQLInsertQuery extends AbstractInsertQuery<SQLDatabaseCollection> implements CommitOnExecute {

    public SQLInsertQuery(SQLDatabaseCollection collection) {
        super(collection);
    }

    @Override
    public QueryResult executeAndGetGeneratedKeys(String[] keyColumns, Object... values) {
        return executeAndGetGeneratedKeys(true, keyColumns, values);
    }

    @Internal
    public QueryResult executeAndGetGeneratedKeys(boolean commit, String[] keyColumns, Object... values) {
        Pair<String, List<Object>> data = this.collection.getDatabase().getDriver().getDialect()
                .newInsertQuery(this.collection, this.entries, values);
        Number[] keys = this.collection.getDatabase().executeUpdateQuery(data.getKey(), commit, SQLUtil.getSelectConsumer(collection, data),
                keyColumns);
        DefaultQueryResult result = new DefaultQueryResult();
        for (int i = 0; i < keyColumns.length; i++) {
            result.addEntry(new DefaultQueryResultEntry(this.collection.getDatabase().getDriver()).addEntry(keyColumns[i], keys[i]));
        }
        return result;
    }

    @Override
    public QueryResult execute(boolean commit, Object... values) {
        return executeAndGetGeneratedKeys(commit, EMPTY_STRING_ARRAY, values);
    }
}
