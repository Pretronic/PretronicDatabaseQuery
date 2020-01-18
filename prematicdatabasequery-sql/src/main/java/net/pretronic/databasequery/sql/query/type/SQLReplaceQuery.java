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

import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.libraries.utility.annonations.Internal;
import net.prematic.libraries.utility.map.Pair;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.type.AbstractReplaceQuery;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;
import net.pretronic.databasequery.sql.query.CommitOnExecute;

import java.util.List;

public class SQLReplaceQuery extends AbstractReplaceQuery<SQLDatabaseCollection> implements CommitOnExecute {

    public SQLReplaceQuery(SQLDatabaseCollection collection) {
        super(collection);
    }

    @Override
    public QueryResult execute(Object... values) {
        return execute(true, values);
    }

    @Internal
    @Override
    public QueryResult execute(boolean commit, Object... values) {
        Pair<String, List<Object>> data = this.collection.getDatabase().getDriver().getDialect()
                .newReplaceQuery(this.collection, this.entries, values);
        this.collection.getDatabase().executeUpdateQuery(data.getKey(), commit, preparedStatement -> {
            for (int i = 1; i <= data.getValue().size(); i++) {
                preparedStatement.setObject(i, data.getValue().get(i-1));
            }
        });
        return DefaultQueryResult.EMPTY;
    }
}
