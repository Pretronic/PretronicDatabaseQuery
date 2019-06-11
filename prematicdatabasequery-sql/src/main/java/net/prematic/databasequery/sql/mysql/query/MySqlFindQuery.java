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
import net.prematic.databasequery.core.impl.QueryOperator;
import net.prematic.databasequery.core.impl.query.AbstractFindQuery;
import net.prematic.databasequery.core.impl.query.QueryEntry;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.impl.query.result.SimpleQueryResult;
import net.prematic.databasequery.core.impl.query.result.SimpleQueryResultEntry;
import net.prematic.databasequery.core.query.FindQuery;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.core.query.result.QueryResultEntry;
import net.prematic.databasequery.sql.mysql.MySqlAggregationBuilder;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;
import net.prematic.databasequery.sql.mysql.MySqlUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MySqlFindQuery extends AbstractFindQuery implements QueryStringBuildAble {

    private String queryString;
    private final List<String> fields;

    public MySqlFindQuery(MySqlDatabaseCollection collection) {
        super(collection);
        this.fields = new ArrayList<>();
    }

    @Override
    public QueryResult execute(Object... values) {
        List<QueryResultEntry> resultEntries = new ArrayList<>();
        try(Connection connection = ((MySqlDatabaseCollection)getCollection()).getDatabase().getDriver().getConnection()) {
            AtomicInteger index = new AtomicInteger(0);
            for (QueryEntry queryEntry : getEntries()) {
                queryEntry.getValues().clear();
                MySqlUtils.prepareQueryEntry(queryEntry, index, values);
            }
            System.out.println(buildExecuteString());
            PreparedStatement preparedStatement = connection.prepareStatement(buildExecuteString());
            List<QueryEntry> queryEntries = new ArrayList<>(getEntries());
            queryEntries.sort(Comparator.comparingInt(queryEntry -> MySqlUtils.getQueryOperatorPriority(queryEntry.getOperator())));
            index.set(1);
            for (QueryEntry queryEntry : queryEntries) {
                for (Object value : queryEntry.getValuesDeep()) {
                    preparedStatement.setObject(index.getAndIncrement(), value);
                }
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                Map<String, Object> results = new LinkedHashMap<>();
                if(!this.fields.isEmpty()) {
                    for (String field : this.fields) {
                        Object value = resultSet.getObject(field);
                        DataTypeAdapter dataTypeAdapter = ((MySqlDatabaseCollection) getCollection()).getDatabase().getDriver().getDataTypeAdapterByReadClass(value.getClass());
                        results.put(field, dataTypeAdapter != null ? dataTypeAdapter.read(value) : value);
                    }
                } else {
                    for(int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        Object value = resultSet.getObject(i);
                        DataTypeAdapter dataTypeAdapter = ((MySqlDatabaseCollection) getCollection()).getDatabase().getDriver().getDataTypeAdapterByReadClass(value.getClass());
                        results.put(resultSet.getMetaData().getColumnName(i), dataTypeAdapter != null ? dataTypeAdapter.read(value) : value);
                    }
                }
                resultEntries.add(new SimpleQueryResultEntry(results));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new SimpleQueryResult(resultEntries);
    }

    @Override
    public String buildExecuteString(boolean rebuild) {
        if(!rebuild && this.queryString != null) return this.queryString;
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT ");
        List<QueryEntry> queryEntries = new ArrayList<>(getEntries());
        queryEntries.sort(Comparator.comparingInt(queryEntry -> MySqlUtils.getQueryOperatorPriority(queryEntry.getOperator())));
        boolean first = true;
        boolean returnValue = false;
        for (QueryEntry queryEntry : queryEntries) {
            if(queryEntry.getOperator() == QueryOperator.GET) {
                returnValue = true;
                if(queryEntry.containsData("fields")) {
                    for (String field : (String[]) queryEntry.getData("fields")) {
                        if(!first) queryString.append(",");
                        else first = false;
                        queryString.append("`").append(field).append("`");
                        fields.add(field);
                    }
                } else if(queryEntry.containsData("aggregationBuilders")) {
                    for (AggregationBuilder aggregationBuilder : (AggregationBuilder[]) queryEntry.getData("aggregationBuilders")) {
                        if(!first) queryString.append(",");
                        else first = false;
                        queryString.append(((MySqlAggregationBuilder) aggregationBuilder).buildExecuteString());
                        fields.add(((MySqlAggregationBuilder) aggregationBuilder).getAlias());
                    }
                }
            }
        }
        if(!returnValue) queryString.append("*");
        queryString.append(" FROM `").append(((MySqlDatabaseCollection)getCollection()).getDatabase().getName())
                .append("`.`").append(getCollection().getName()).append("`");
        MySqlUtils.buildSearchQuery(queryString, queryEntries);
        this.queryString = queryString.append(";").toString();
        return this.queryString;
    }

    @Override
    public FindQuery get(AggregationBuilder.Consumer... aggregationBuilders) {
        AggregationBuilder[] results = new AggregationBuilder[aggregationBuilders.length];
        for (int i = 0; i < aggregationBuilders.length; i++) {
            AggregationBuilder aggregationBuilder = ((MySqlDatabaseCollection)getCollection()).getDatabase().newAggregationBuilder(true);
            aggregationBuilders[i].accept(aggregationBuilder);
            results[i] = aggregationBuilder;
        }
        return get(results);
    }
}