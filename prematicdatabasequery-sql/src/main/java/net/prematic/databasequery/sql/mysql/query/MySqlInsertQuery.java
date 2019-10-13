/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.06.19, 23:33
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

package net.prematic.databasequery.sql.mysql.query;

import net.prematic.databasequery.core.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.core.impl.query.AbstractInsertQuery;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.impl.query.result.SimpleQueryResult;
import net.prematic.databasequery.core.impl.query.result.SimpleQueryResultEntry;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.core.query.result.QueryResultEntry;
import net.prematic.databasequery.sql.CommitOnExecute;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MySqlInsertQuery extends AbstractInsertQuery implements QueryStringBuildAble, CommitOnExecute {

    private final MySqlDatabaseCollection databaseCollection;
    private int valuesPerField;

    public MySqlInsertQuery(MySqlDatabaseCollection databaseCollection) {
        this.databaseCollection = databaseCollection;
        this.valuesPerField = 0;
    }

    public int getValuesPerField() {
        return valuesPerField;
    }

    public CompletableFuture<QueryResult> executeAndGetGeneratedKeys(boolean commit, String[] keyColumns, Object... values) {
        CompletableFuture<QueryResult> completableFuture = new CompletableFuture<>();

        String query = buildExecuteString(values);
        Number[] generatedKeys = this.databaseCollection.getDatabase().executeUpdateQuery(query, commit, preparedStatement -> {
            try {
                int index = 1;
                int valueGet = 0;
                for (int i = 0; i < this.valuesPerField; i++) {
                    for (Entry entry : getEntries()) {
                        Object value;
                        if(entry.getValues().size() > i) {
                            value = entry.getValues().get(i);
                        } else {
                            value = values[valueGet];
                            valueGet++;
                        }
                        DataTypeAdapter adapter = this.databaseCollection.getDatabase().getDriver().getDataTypeAdapterByWriteClass(value.getClass());
                        if(adapter != null) value = adapter.write(value);
                        preparedStatement.setObject(index, value);
                        index++;
                    }
                }
            } catch (SQLException exception) {
                this.databaseCollection.getDatabase().getDriver().handleDatabaseQueryExecuteFailedException(exception, query);
            }
        }, keyColumns);
        List<QueryResultEntry> resultEntries = new ArrayList<>();
        for (int i = 0; i < generatedKeys.length; i++) {
            Map<String, Object> results = new HashMap<>();
            results.put(keyColumns[i], generatedKeys[i]);
            resultEntries.add(new SimpleQueryResultEntry(results));
        }
        QueryResult result = new SimpleQueryResult(resultEntries);
        completableFuture.complete(result);
        return completableFuture;
    }

    @Override
    public CompletableFuture<QueryResult> executeAndGetGeneratedKeys(String[] keyColumns, Object... values) {
        return executeAndGetGeneratedKeys(true, keyColumns, values);
    }

    @Override
    public CompletableFuture<QueryResult> execute(boolean commit, Object... values) {
        return executeAndGetGeneratedKeys(commit, new String[0], values);
    }

    @Override
    public CompletableFuture<QueryResult> execute(Object... values) {
        return execute(true, values);
    }

    @Override
    public String buildExecuteString(Object... values) {
        StringBuilder fieldQueryBuilder = new StringBuilder();
        StringBuilder valueQueryBuilder = new StringBuilder();

        int fieldCount = getEntries().size();
        int valueCount = values.length;

        for (Entry entry : getEntries()) {
            if(fieldQueryBuilder.length() == 0) fieldQueryBuilder.append("(");
            else fieldQueryBuilder.append(",");
            fieldQueryBuilder.append("`").append(entry.getField()).append("`");
            valueCount+=entry.getValues().size();
        }

        this.valuesPerField = valueCount/fieldCount;

        for (int i = 0; i < valuesPerField; i++) {
            if(valueQueryBuilder.length() != 0) valueQueryBuilder.append(",(");
            else valueQueryBuilder.append(" VALUES (");
            for (int i1 = 0; i1 < fieldCount; i1++) {
                if(i1 != 0) valueQueryBuilder.append(",");
                valueQueryBuilder.append("?");
            }
            valueQueryBuilder.append(")");
        }
        String query = "INSERT INTO `";
        if(databaseCollection.getDatabase().getDriver().getConfig().isMultipleDatabaseConnectionsAble()) {
            query+=this.databaseCollection.getDatabase().getName() + "`.`";
        }
        query+=this.databaseCollection.getName() + "` " + fieldQueryBuilder + ")" + valueQueryBuilder + ";";
        return query;
    }
}