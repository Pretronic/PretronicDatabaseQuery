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

import net.prematic.databasequery.core.Aggregation;
import net.prematic.databasequery.core.QueryOperator;
import net.prematic.databasequery.core.impl.query.AbstractFindQuery;
import net.prematic.databasequery.core.impl.query.QueryEntry;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.impl.query.result.SimpleQueryResult;
import net.prematic.databasequery.core.impl.query.result.SimpleQueryResultEntry;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.core.query.result.QueryResultEntry;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;
import net.prematic.databasequery.sql.mysql.MySqlUtils;
import net.prematic.libraries.utility.map.Pair;

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
            PreparedStatement preparedStatement = connection.prepareStatement(buildExecuteString());
            List<Integer> indexToPrepare = new ArrayList<>();
            for (QueryEntry queryEntry : getEntries()) {
                MySqlUtils.prepareQueryEntry(queryEntry, preparedStatement, new AtomicInteger(1), indexToPrepare);
            }
            int index = 0;
            for (Object value : values) {
                preparedStatement.setObject(indexToPrepare.get(index), value);
                index++;
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Map<String, Object> results = new LinkedHashMap<>();
                if(!this.fields.isEmpty()) {
                    for (String field : this.fields) {
                        results.put(field, resultSet.getObject(field));
                    }
                } else {
                    for(int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        results.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
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
        queryString.append("SELECT ")
        ;
        List<QueryEntry> queryEntries = getEntries();
        queryEntries.sort(Comparator.comparingInt(queryEntry -> MySqlUtils.getQueryOperatorPriority(queryEntry.getOperator())));
        boolean first = true;
        boolean returnValues = false;
        for (QueryEntry queryEntry : queryEntries) {
            if(queryEntry.getOperator() == QueryOperator.GET) {
                returnValues = true;
                if(queryEntry.containsData("fields")) {
                    for (String field : (String[]) queryEntry.getData("fields")) {
                        if(!first) queryString.append(",");
                        else first = false;
                        queryString.append("`").append(field).append("`");
                        fields.add(field);
                    }
                } else if(queryEntry.containsData("getBuilders")) {
                    for (GetBuilder getBuilder : (GetBuilder[]) queryEntry.getData("getBuilders")) {
                        StringBuilder subQueryString = new StringBuilder();
                        if(!first) queryString.append(",");
                        else first = false;
                        String alias = null;
                        for (GetBuilder.Entry entry : getBuilder.getEntries()) {
                            alias = buildGetBuilderEntry(entry, subQueryString);
                        }
                        if(alias != null) {
                            subQueryString.append(" AS `").append(alias).append("`");
                            fields.add(alias);
                        } else fields.add(subQueryString.toString());
                        queryString.append(subQueryString);
                    }
                }
            }
        }
        if(!returnValues) queryString.append("*");
        queryString.append(" FROM `").append(((MySqlDatabaseCollection)getCollection()).getDatabase().getName())
                .append("`.`").append(getCollection().getName()).append("`");
        MySqlUtils.buildSearchQuery(queryString, queryEntries);
        this.queryString = queryString.append(";").toString();
        return this.queryString;
    }

    private String buildGetBuilderEntry(GetBuilder.Entry entry, StringBuilder queryString) {
        switch (entry.getType()) {
            case FIELD: {
                queryString.append("`").append(entry.getValue()).append("`");
                return null;
            }
            case OPERATOR: {
                queryString.append(" ").append(entry.getValue()).append(" ");
                return null;
            }
            case AGGREGATION: {
                Pair<Aggregation, String> value = (Pair<Aggregation, String>) entry.getValue();
                queryString.append(value.getKey()).append("(").append(value.getValue()).append(")");
                return null;
            }
            case ALIAS: {
                return (String) entry.getValue();
            }
            case GET_BUILDER: {
                queryString.append("(");
                for (GetBuilder.Entry childEntry : ((GetBuilder) entry.getValue()).getEntries()) {
                    buildGetBuilderEntry(childEntry, queryString);
                }
                queryString.append(")");
            }
        }
        return null;
    }

    @Override
    public GetBuilder getGetBuilder() {
        return null;
    }
}