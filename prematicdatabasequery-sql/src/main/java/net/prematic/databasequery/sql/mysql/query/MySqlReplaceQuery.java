/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 03.07.19, 18:34
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

import net.prematic.databasequery.core.aggregation.AggregationBuilder;
import net.prematic.databasequery.core.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.core.impl.query.AbstractInsertQuery;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.query.ReplaceQuery;
import net.prematic.databasequery.core.query.option.OrderOption;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.mysql.CommitOnExecute;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlReplaceQuery implements ReplaceQuery, QueryStringBuildAble, CommitOnExecute {

    private final MySqlDatabaseCollection databaseCollection;
    private final MySqlDeleteQuery deleteQuery;
    private final MySqlInsertQuery insertQuery;

    public MySqlReplaceQuery(MySqlDatabaseCollection databaseCollection) {
        this.databaseCollection = databaseCollection;
        this.deleteQuery = (MySqlDeleteQuery) databaseCollection.delete();
        this.insertQuery = (MySqlInsertQuery) databaseCollection.insert();
    }

    @Override
    public ReplaceQuery set(String field, Object value) {
        this.insertQuery.set(field, value);
        return this;
    }

    @Override
    public ReplaceQuery where(String field, Object value) {
        this.deleteQuery.where(field, value);
        return this;
    }

    @Override
    public ReplaceQuery whereLike(String field, String pattern) {
        this.deleteQuery.whereLike(field, pattern);
        return this;
    }

    @Override
    public ReplaceQuery where(String field, String operator, Object value) {
        this.deleteQuery.where(field, operator, value);
        return this;
    }

    @Override
    public ReplaceQuery where(Object first, String operator, Object second) {
        this.deleteQuery.where(first, operator, second);
        return this;
    }

    @Override
    public ReplaceQuery not(Consumer searchQuery) {
        this.deleteQuery.not(searchQuery);
        return this;
    }

    @Override
    public ReplaceQuery and(Consumer... searchQueries) {
        this.deleteQuery.and(searchQueries);
        return this;
    }

    @Override
    public ReplaceQuery or(Consumer... searchQueries) {
        this.deleteQuery.or(searchQueries);
        return this;
    }

    @Override
    public ReplaceQuery between(String field, Object value1, Object value2) {
        this.deleteQuery.between(field, value1, value2);
        return this;
    }

    @Override
    public ReplaceQuery limit(int limit, int offset) {
        this.deleteQuery.limit(limit, offset);
        return this;
    }

    @Override
    public ReplaceQuery orderBy(String field, OrderOption orderOption) {
        this.deleteQuery.orderBy(field, orderOption);
        return this;
    }

    @Override
    public ReplaceQuery orderBy(AggregationBuilder aggregationBuilder, OrderOption orderOption) {
        this.deleteQuery.orderBy(aggregationBuilder, orderOption);
        return this;
    }

    @Override
    public ReplaceQuery groupBy(String... fields) {
        this.deleteQuery.groupBy(fields);
        return this;
    }

    @Override
    public ReplaceQuery groupBy(AggregationBuilder... aggregationBuilders) {
        this.deleteQuery.groupBy(aggregationBuilders);
        return this;
    }

    @Override
    public ReplaceQuery min(Object first, String operator, Object second) {
        this.deleteQuery.min(first, operator, second);
        return this;
    }

    @Override
    public ReplaceQuery max(Object first, String operator, Object second) {
        this.deleteQuery.max(first, operator, second);
        return this;
    }

    @Override
    public ReplaceQuery count(Object first, String operator, Object second) {
        this.deleteQuery.count(first, operator, second);
        return this;
    }

    @Override
    public ReplaceQuery avg(Object first, String operator, Object second) {
        this.deleteQuery.avg(first, operator, second);
        return this;
    }

    @Override
    public ReplaceQuery sum(Object first, String operator, Object second) {
        this.deleteQuery.sum(first, operator, second);
        return this;
    }

    @Override
    public QueryResult execute(boolean commit, Object... values) {
        try(Connection connection = this.databaseCollection.getDatabase().getDriver().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(buildExecuteString());
            int index = 1;
            int valueGet = 0;
            for (Object value : this.deleteQuery.values) {
                if(value == null) {
                    value = values[valueGet];
                    valueGet++;
                }
                DataTypeAdapter adapter = this.databaseCollection.getDatabase().getDriver().getDataTypeAdapterByWriteClass(value.getClass());
                if(adapter != null) value = adapter.write(value);
                preparedStatement.setObject(index, value);
                index++;
            }
            for (int i = 0; i < insertQuery.getValuesPerField(); i++) {
                for (AbstractInsertQuery.Entry entry : insertQuery.getEntries()) {
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
            preparedStatement.executeUpdate();
            if(commit) connection.commit();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public QueryResult execute(Object... values) {
        return execute(true, values);
    }

    @Override
    public String buildExecuteString(Object... values) {
        return this.deleteQuery.buildExecuteString() +
                this.insertQuery.buildExecuteString();
    }
}