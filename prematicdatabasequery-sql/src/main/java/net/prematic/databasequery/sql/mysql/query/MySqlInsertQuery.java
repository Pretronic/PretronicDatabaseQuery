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

import net.prematic.databasequery.api.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.databasequery.api.query.result.QueryResultEntry;
import net.prematic.databasequery.common.query.AbstractInsertQuery;
import net.prematic.databasequery.common.query.QueryStringBuildAble;
import net.prematic.databasequery.common.query.result.SimpleQueryResult;
import net.prematic.databasequery.common.query.result.SimpleQueryResultEntry;
import net.prematic.databasequery.sql.SqlQuery;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class MySqlInsertQuery extends AbstractInsertQuery implements QueryStringBuildAble, SqlQuery {

    private final MySqlDatabaseCollection databaseCollection;
    private int valuesPerField;

    public MySqlInsertQuery(MySqlDatabaseCollection databaseCollection) {
        this.databaseCollection = databaseCollection;
        this.valuesPerField = 0;
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.databaseCollection.getDatabase().getDriver().getExecutorService();
    }

    protected int getValuesPerField() {
        return valuesPerField;
    }

    private QueryResult executeAndGetGeneratedKeys(boolean commit, String[] keyColumns, Object... values) {
        String query = buildExecuteString(values);
        Number[] generatedKeys = this.databaseCollection.getDatabase().executeUpdateQuery(query, commit
                , preparedStatement -> handlePreparedStatement(preparedStatement, values), keyColumns);
        List<QueryResultEntry> resultEntries = new ArrayList<>();
        for (int i = 0; i < generatedKeys.length; i++) {
            Map<String, Object> results = new HashMap<>();
            results.put(keyColumns[i], generatedKeys[i]);
            resultEntries.add(new SimpleQueryResultEntry(results));
        }
        return new SimpleQueryResult(resultEntries);
    }

    @Override
    public QueryResult executeAndGetGeneratedKeys(String[] keyColumns, Object... values) {
        return executeAndGetGeneratedKeys(true, keyColumns, values);
    }

    @Override
    public CompletableFuture<QueryResult> executeAsyncAndGetGeneratedKeys(String[] keyColumns, Object... values) {
        CompletableFuture<QueryResult> future = new CompletableFuture<>();
        getExecutorService().execute(()-> future.complete(executeAndGetGeneratedKeys(true, keyColumns, values)));
        return future;
    }

    @Override
    public QueryResult execute(boolean commit, Object... values) {
        return executeAndGetGeneratedKeys(commit, new String[0], values);
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

    private void handlePreparedStatement(PreparedStatement preparedStatement, Object... values) throws SQLException {
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
                if(value == Option.NULL) {
                    value = null;
                } else {
                    DataTypeAdapter adapter = this.databaseCollection.getDatabase().getDriver().getDataTypeAdapterByWriteClass(value.getClass());
                    if(adapter != null) value = adapter.write(value);
                }
                preparedStatement.setObject(index, value);
                index++;
            }
        }
    }
}