/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 26.05.19, 16:05
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

package net.prematic.databasequery.sql.mysql;

import net.prematic.databasequery.core.QueryOperator;
import net.prematic.databasequery.core.datatype.DataType;
import net.prematic.databasequery.core.impl.DataTypeInformation;
import net.prematic.databasequery.core.impl.query.QueryEntry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MySqlUtils {

    public static final Map<QueryOperator, Integer> QUERY_OPERATOR_PRIORITY = new HashMap<QueryOperator, Integer>() {{
        //Find Query
        put(QueryOperator.GET, 10);
        put(QueryOperator.WHERE, 20);
        put(QueryOperator.NOT, 20);
        put(QueryOperator.BETWEEN, 20);
        put(QueryOperator.AND, 20);
        put(QueryOperator.OR, 20);
        put(QueryOperator.HAVING, 20);
        put(QueryOperator.GROUP_BY, 20);
        put(QueryOperator.MIN, 20);
        put(QueryOperator.MAX, 20);
        put(QueryOperator.COUNT, 20);
        put(QueryOperator.AVG, 20);
        put(QueryOperator.SUM, 20);
        put(QueryOperator.LIMIT, 50);
        put(QueryOperator.ORDER_BY, 50);
        //Change Query
        put(QueryOperator.SET, 0);
        //Create Query
        put(QueryOperator.CREATE, 0);
        put(QueryOperator.ENGINE, 10);
        put(QueryOperator.COLLECTION_TYPE, -1);
    }};

    public static final List<DataTypeInformation> DATA_TYPE_INFORMATION = new ArrayList<DataTypeInformation>() {{
        add(new DataTypeInformation(DataType.DOUBLE, "DOUBLE"));
        add(new DataTypeInformation(DataType.DECIMAL, "DECIMAL"));
        add(new DataTypeInformation(DataType.FLOAT, "FLOAT"));
        add(new DataTypeInformation(DataType.INTEGER, "INTEGER"));
        add(new DataTypeInformation(DataType.LONG, "BIGINT"));
        add(new DataTypeInformation(DataType.CHAR, "CHAR"));
        add(new DataTypeInformation(DataType.STRING, "VARCHAR"));
        add(new DataTypeInformation(DataType.LONG_TEXT, "LONGTEXT"));
        add(new DataTypeInformation(DataType.DATE, "DATE"));
        add(new DataTypeInformation(DataType.DATETIME, "DATETIME"));
        add(new DataTypeInformation(DataType.TIMESTAMP, "TIMESTAMP"));
        add(new DataTypeInformation(DataType.BINARY, "BINARY"));
        add(new DataTypeInformation(DataType.BLOB, "BLOB", false));
        add(new DataTypeInformation(DataType.UUID, "BINARY", true,16));
    }};

    public static int getQueryOperatorPriority(QueryOperator queryOperator) {
        return QUERY_OPERATOR_PRIORITY.getOrDefault(queryOperator, 20);
    }

    public static DataTypeInformation getDataTypeInformation(DataType dataType) {
        for (DataTypeInformation dataTypeInformation : DATA_TYPE_INFORMATION) {
            if(dataTypeInformation.getDataType() == dataType) return dataTypeInformation;
        }
        return null;
    }

    public static void buildSearchQuery(StringBuilder queryString, List<QueryEntry> queryEntries) {
        List<QueryEntry> orderByQueryEntries = new ArrayList<>();
        int limit = -1;
        for (QueryEntry queryEntry : queryEntries) {
            switch (queryEntry.getOperator()) {
                case ORDER_BY: {
                    orderByQueryEntries.add(queryEntry);
                    continue;
                }
                case LIMIT: {
                    limit = (int) queryEntry.getData("limit");
                    continue;
                }
                default: {
                    buildChildQueryEntry(queryString, queryEntry, false, true, false);
                    continue;
                }
            }
        }
        if(!orderByQueryEntries.isEmpty()) {
            queryString.append(" ORDER BY ");
            boolean first = true;
            for (QueryEntry queryEntry : orderByQueryEntries) {
                if(!first) queryString.append(",");
                else first = false;
                queryString.append(queryEntry.getData("field"));
                if(queryEntry.hasData("orderOption")) queryString.append(" ").append(queryEntry.getData("orderOption"));
            }
        }
        if(limit != -1) queryString.append(" LIMIT ?");
    }

    private static void buildChildQueryEntry(StringBuilder queryString, QueryEntry queryEntry, boolean negate, boolean where, boolean first) {
        switch (queryEntry.getOperator()) {
            case WHERE: {
                if(!first) {
                    if(where) queryString.append(" WHERE ");
                    else queryString.append(" AND ");
                }
                if(negate) queryString.append("NOT ");
                queryString.append("`").append(queryEntry.getData("field")).append("`=?");
                return;
            }
            case WHERE_PATTERN: {
                if(!first) {
                    if(where) queryString.append(" WHERE");
                    else queryString.append(" AND");
                }
                if(negate) queryString.append(" NOT");
                queryString.append("`").append(queryEntry.getData("field")).append("` LIKE ?");
                return;
            }
            case WHERE_AGGREGATION: {
                if(!first) {
                    if(where) queryString.append(" WHERE");
                    else queryString.append(" AND");
                }
                if(negate) queryString.append(" NOT");
                queryString.append("`").append(queryEntry.getData("field")).append("` ").append(queryEntry.getData("operator")).append(" ?");
                return;
            }
            case NOT: {
                for (QueryEntry childQueryEntry : queryEntry.getEntries()) {
                    buildChildQueryEntry(queryString, childQueryEntry, true, where, first);
                }
                return;
            }
            case AND: case OR: {
                queryString.append(" ").append(queryEntry.getOperator().toString()).append(" ");
                if(negate) queryString.append("NOT ");
                queryString.append("(");
                first = true;
                for(QueryEntry childQueryEntry : queryEntry.getEntries()) {
                    buildChildQueryEntry(queryString, childQueryEntry, false, false, first);
                    first = false;
                }
                queryString.append(")");
                return;
            }
            case BETWEEN: {
                if(!first) {
                    if(where) queryString.append(" WHERE ");
                    else queryString.append(" AND ");
                }
                queryString.append("`").append(queryEntry.getData("field")).append("`");
                if(negate) queryString.append(" NOT");
                queryString.append(" BETWEEN ? AND ?");
                return;
            }
            case GROUP_BY: {

            }
            case HAVING: {

            }
            case MIN: {

            }
            case MAX: {

            }
            case COUNT: {

            }
            case AVG: {

            }
            case SUM: {

            }
        }
    }

    public static void prepareQueryEntry(QueryEntry queryEntry, PreparedStatement preparedStatement, AtomicInteger index, List<Integer> indexToPrepare) throws SQLException {
        switch (queryEntry.getOperator()) {
            case WHERE: case WHERE_AGGREGATION: {
                if(queryEntry.hasData("value")) preparedStatement.setObject(index.getAndIncrement(), queryEntry.getData("value"));
                else indexToPrepare.add(index.getAndIncrement());
                return;
            }
            case WHERE_PATTERN: {
                if(queryEntry.hasData("pattern")) preparedStatement.setObject(index.getAndIncrement(), queryEntry.getData("pattern"));
                else indexToPrepare.add(index.getAndIncrement());
                return;
            }
            case NOT: {
                for (QueryEntry childQueryEntry : queryEntry.getEntries()) {
                    prepareQueryEntry(childQueryEntry, preparedStatement, index, indexToPrepare);
                }
                return;
            }
            case AND: case OR: {
                List<QueryEntry> queryEntries = queryEntry.getEntries();
                for (QueryEntry childQueryEntry : queryEntries) {
                    prepareQueryEntry(childQueryEntry, preparedStatement, index, indexToPrepare);
                }
                return;
            }
            case BETWEEN: {

            }
            case LIMIT: {

            }
            case ORDER_BY: {

            }
            case GROUP_BY: {

            }
            case HAVING: {

            }
            case MIN: {

            }
            case MAX: {

            }
            case COUNT: {

            }
            case AVG: {

            }
            case SUM: {

            }
        }
    }
}