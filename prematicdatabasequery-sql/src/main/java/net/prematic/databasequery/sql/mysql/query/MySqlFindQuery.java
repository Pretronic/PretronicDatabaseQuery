/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 27.05.19, 18:33
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
import net.prematic.databasequery.core.exceptions.DatabaseQueryExecuteFailedException;
import net.prematic.databasequery.core.impl.query.result.SimpleQueryResult;
import net.prematic.databasequery.core.impl.query.result.SimpleQueryResultEntry;
import net.prematic.databasequery.core.query.FindQuery;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.core.query.result.QueryResultEntry;
import net.prematic.databasequery.sql.mysql.MySqlAggregationBuilder;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MySqlFindQuery extends MySqlSearchQueryHelper<FindQuery> implements FindQuery {

    private final List<String> fields;
    private final boolean first;
    private final StringBuilder getQueryBuilder;

    public MySqlFindQuery(MySqlDatabaseCollection databaseCollection) {
        super(databaseCollection);
        this.fields = new ArrayList<>();
        this.first = true;
        this.getQueryBuilder = new StringBuilder();
    }

    @Override
    public FindQuery get(String... fields) {
        for (String field : fields) {
            if(!this.first) this.getQueryBuilder.append(",");
            this.getQueryBuilder.append("`").append(field).append("`");
            this.fields.add(field);
        }
        return this;
    }

    @Override
    public FindQuery get(AggregationBuilder... aggregationBuilders) {
        for (AggregationBuilder aggregationBuilder : aggregationBuilders) {
            if(!this.first) this.getQueryBuilder.append(",");
            this.getQueryBuilder.append(((MySqlAggregationBuilder) aggregationBuilder).getAggregationBuilder());
            this.fields.add(((MySqlAggregationBuilder) aggregationBuilder).getAlias());
        }
        return this;
    }

    @Override
    public FindQuery get(AggregationBuilder.Consumer... aggregationBuilders) {
        AggregationBuilder[] results = new AggregationBuilder[aggregationBuilders.length];
        for (int i = 0; i < aggregationBuilders.length; i++) {
            AggregationBuilder aggregationBuilder = this.databaseCollection.getDatabase().newAggregationBuilder(true);
            aggregationBuilders[i].accept(aggregationBuilder);
            results[i] = aggregationBuilder;
        }
        return get(results);
    }

    @Override
    public QueryResult execute(boolean commit, Object... values) {
        List<QueryResultEntry> resultEntries = new ArrayList<>();
        try(Connection connection = this.databaseCollection.getDatabase().getDriver().getConnection()) {
            int index = 1;
            int valueGet = 0;
            String query = buildExecuteString(values);
            System.out.println(query);
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            for (Object value : this.values) {
                if(value == null) {
                    value = values[valueGet];
                    valueGet++;
                }
                preparedStatement.setObject(index, value);
                index++;
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if(commit) connection.commit();
            if(this.databaseCollection.getLogger().isDebugging()) this.databaseCollection.getLogger().debug("Executed sql query: {}", query);
            while (resultSet.next()) {

                Map<String, Object> results = new LinkedHashMap<>();
                if(!this.fields.isEmpty()) {
                    for (String field : this.fields) {
                        Object value = resultSet.getObject(field);
                        DataTypeAdapter dataTypeAdapter = this.databaseCollection.getDatabase().getDriver().getDataTypeAdapterByReadClass(value.getClass());
                        results.put(field, dataTypeAdapter != null ? dataTypeAdapter.read(value) : value);
                    }
                } else {
                    for(int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        Object value = resultSet.getObject(i);
                        DataTypeAdapter dataTypeAdapter = this.databaseCollection.getDatabase().getDriver().getDataTypeAdapterByReadClass(value.getClass());
                        results.put(resultSet.getMetaData().getColumnName(i), dataTypeAdapter != null ? dataTypeAdapter.read(value) : value);
                    }
                }
                resultEntries.add(new SimpleQueryResultEntry(results));
            }
        } catch (SQLException exception) {
            throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
        }
        return new SimpleQueryResult(resultEntries);
    }

    @Override
    public String buildExecuteString(Object... values) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT ");
        if(this.fields.isEmpty()) queryString.append("*");
        else queryString.append(this.getQueryBuilder);
        return queryString.append(" FROM `")
                .append(this.databaseCollection.getDatabase().getName())
                .append("`.`").append(this.databaseCollection.getName())
                .append("` ").append(this.queryBuilder).append(";").toString();
    }
}