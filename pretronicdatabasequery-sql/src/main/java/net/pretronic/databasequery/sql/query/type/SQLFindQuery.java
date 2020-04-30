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
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.common.query.type.AbstractFindQuery;
import net.pretronic.databasequery.sql.SQLUtil;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;
import net.pretronic.databasequery.sql.query.CommitOnExecute;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.io.FileUtil;
import net.pretronic.libraries.utility.map.Pair;

import java.sql.Clob;
import java.util.List;

public class SQLFindQuery extends AbstractFindQuery<SQLDatabaseCollection> implements CommitOnExecute {

    public SQLFindQuery(SQLDatabaseCollection collection) {
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
                .newFindQuery(this.collection, this.getEntries, this.entries, values);
        return this.collection.getDatabase().executeResultQuery(data.getKey(), commit, SQLUtil.getSelectConsumer(collection, data),
                resultSet -> {
                    DefaultQueryResult result = new DefaultQueryResult();
                    while (resultSet.next()) {
                        DefaultQueryResultEntry resultEntry = new DefaultQueryResultEntry(this.collection.getDatabase().getDriver());
                        if(!this.getEntries.isEmpty()) {
                            for (GetEntry entry : this.getEntries) {
                                String getter = entry.getAggregation() == null ? entry.getField() : entry.getAggregation() + "(`" + entry.getField() + "`)";
                                Object value = resultSet.getObject(getter);
                                resultEntry.addEntry(getter, value);
                            }
                        } else {
                            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                                Object value = resultSet.getObject(i);
                                if(value instanceof Clob) {
                                    value = FileUtil.readContent(((Clob) value).getAsciiStream());
                                }
                                resultEntry.addEntry(resultSet.getMetaData().getColumnName(i), value);
                            }
                        }
                        result.addEntry(resultEntry);
                    }
                    return result;
                });
    }
}
