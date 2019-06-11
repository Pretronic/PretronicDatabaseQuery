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

import net.prematic.databasequery.core.aggregation.AggregationBuilder;
import net.prematic.databasequery.core.datatype.DataType;
import net.prematic.databasequery.core.impl.DataTypeInformation;
import net.prematic.databasequery.core.impl.QueryOperator;
import net.prematic.databasequery.core.impl.query.QueryEntry;

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
        put(QueryOperator.HAVING, 30);
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
        List<QueryEntry> havingQueryEntries = new ArrayList<>();
        List<QueryEntry> groupByQueryEntries = new ArrayList<>();
        int limit = -1;
        boolean where = true;
        boolean currentWhere = false;
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
                case HAVING: {
                    havingQueryEntries.add(queryEntry);
                    continue;
                }
                case GROUP_BY: {
                    groupByQueryEntries.add(queryEntry);
                    continue;
                }
                case WHERE: case WHERE_COMPARE: case WHERE_PATTERN: case BETWEEN: currentWhere = true;
                default: {
                    buildChildQueryEntry(queryString, queryEntry, false, where, false);
                    if(currentWhere) where = false;
                }

            }
        }
        if(!groupByQueryEntries.isEmpty()) {
            queryString.append(" GROUP BY ");
            boolean first = true;
            for (QueryEntry queryEntry : groupByQueryEntries) {
                Object value = queryEntry.getData("value");
                if(value instanceof AggregationBuilder[]) {
                    for (AggregationBuilder aggregationBuilder : (AggregationBuilder[]) value) {
                        if(!first) queryString.append(",");
                        else first = false;
                        queryString.append(((MySqlAggregationBuilder)aggregationBuilder).buildExecuteString());
                    }
                } else {
                    for(String field : (String[]) value) {
                        if(!first) queryString.append(",");
                        else first = false;
                        queryString.append("`").append(field).append("`");
                    }
                }
            }
        }
        if(!havingQueryEntries.isEmpty()) {
            queryString.append(" HAVING ");
            boolean first = true;
            for (QueryEntry queryEntry : havingQueryEntries) {
                if(!first) queryString.append(" AND ");
                else first = false;
                Object value1 = queryEntry.getData("first");
                if(value1 instanceof AggregationBuilder) {
                    queryString.append(((MySqlAggregationBuilder)value1).buildExecuteString());
                } else if(!(value1 instanceof String)) {
                    queryString.append("?");
                } else  queryString.append("`").append(value1).append("`");

                queryString.append(" ").append(queryEntry.getData("operator")).append(" ");
                Object value2 = queryEntry.getData("second");
                if(value2 instanceof AggregationBuilder) {
                    queryString.append(((MySqlAggregationBuilder)value2).buildExecuteString());
                } else if(!(value2 instanceof String)) {
                    queryString.append("?");
                } else queryString.append("`").append(value2).append("`");
            }
        }
        if(!orderByQueryEntries.isEmpty()) {
            queryString.append(" ORDER BY ");
            boolean first = true;
            for (QueryEntry queryEntry : orderByQueryEntries) {
                if(!first) queryString.append(",");
                else first = false;
                Object value = queryEntry.getData("value");
                if(value instanceof MySqlAggregationBuilder) {
                    queryString.append(((MySqlAggregationBuilder)value).buildExecuteString());
                } else {
                    queryString.append("`").append(queryEntry.getData("field")).append("`");
                }
                if(queryEntry.hasData("orderOption")) queryString.append(" ").append(queryEntry.getData("orderOption"));
            }
        }
        if(limit != -1) queryString.append(" LIMIT ?,?");
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
                    if(where) queryString.append(" WHERE ");
                    else queryString.append(" AND ");
                }
                if(negate) queryString.append("NOT ");
                queryString.append("`").append(queryEntry.getData("field")).append("` LIKE ?");
                return;
            }
            case WHERE_COMPARE: {
                if(!first) {
                    if(where) queryString.append(" WHERE ");
                    else queryString.append(" AND ");
                }
                if(negate) queryString.append("NOT ");
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
                if(!first) {
                    if(where) queryString.append(" WHERE ");
                    else queryString.append(" ").append(queryEntry.getOperator().toString()).append(" ");
                }
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
                queryString.append(" BETWEEN ");

                Object value1 = queryEntry.getData("value1");
                Object value2 = queryEntry.getData("value2");

                if(value1 instanceof MySqlAggregationBuilder) {
                    queryString.append(((MySqlAggregationBuilder)value1).buildExecuteString()).append(" AND ");
                } else {
                    queryString.append("? AND ");
                }
                if(value2 instanceof MySqlAggregationBuilder) {
                    queryString.append(((MySqlAggregationBuilder)value2).buildExecuteString());
                } else {
                    queryString.append("?");
                }
                return;
            }
            case MIN: case MAX: case COUNT: case AVG: case SUM: {
                queryString.append(queryEntry.getOperator()).append("(");
                Object value1 = queryEntry.getData("first");
                if(value1 instanceof MySqlAggregationBuilder) {
                    queryString.append(((MySqlAggregationBuilder)value1).buildExecuteString());
                } else if(value1 instanceof String) {
                    queryString.append("`").append(value1).append("`");
                } else queryString.append("?");
                Object value2 = queryEntry.getData("second");
                if(value2 instanceof MySqlAggregationBuilder) {
                    queryString.append(((MySqlAggregationBuilder)value2).buildExecuteString());
                } else if(value2 instanceof String) {
                    queryString.append("`").append(value2).append("`");
                } else queryString.append("?");
            }
        }
    }

    public static void prepareQueryEntry(QueryEntry queryEntry, AtomicInteger index, Object[] values) throws SQLException {
        switch (queryEntry.getOperator()) {
            case WHERE: case SET: case WHERE_PATTERN: case WHERE_COMPARE: {
                if(queryEntry.hasData("value")) {
                    queryEntry.addValue(queryEntry.getData("value"));
                } else {
                    queryEntry.addValue(values[index.get()]);
                }
                index.incrementAndGet();
                return;
            }
            case NOT: {
                for (QueryEntry childQueryEntry : queryEntry.getEntries()) {
                    prepareQueryEntry(childQueryEntry, index, values);
                }
                return;
            }
            case AND: case OR: {
                List<QueryEntry> queryEntries = queryEntry.getEntries();
                for (QueryEntry childQueryEntry : queryEntries) {
                    prepareQueryEntry(childQueryEntry, index, values);
                }
                return;
            }
            case BETWEEN: {
                if(queryEntry.hasData("value1")) {
                    Object value1 = queryEntry.getData("value1");
                    if(value1 instanceof MySqlAggregationBuilder) {
                        prepareAggregationBuilder((MySqlAggregationBuilder)value1, index, values);
                    } else {
                        queryEntry.addValue(value1);
                        index.incrementAndGet();
                    }
                } else {
                    queryEntry.addValue(values[index.getAndIncrement()]);
                }
                if(queryEntry.hasData("value2")) {
                    Object value2 = queryEntry.getData("value2");
                    if(value2 instanceof MySqlAggregationBuilder) {
                        prepareAggregationBuilder((MySqlAggregationBuilder)value2, index, values);
                    } else {
                        queryEntry.addValue(value2);
                        index.incrementAndGet();
                    }
                } else {
                    queryEntry.addValue(values[index.getAndIncrement()]);
                }
                return;
            }

            case GET: {
                if(queryEntry.hasData("aggregationBuilders")) {
                    AggregationBuilder[] aggregationBuilders = (AggregationBuilder[]) queryEntry.getData("aggregationBuilders");
                    for(AggregationBuilder aggregationBuilder : aggregationBuilders) {
                        prepareAggregationBuilder((MySqlAggregationBuilder)aggregationBuilder, index, values);
                    }
                }
                return;
            }
            case LIMIT: {
                if(queryEntry.hasData("offset")) {
                    int offset = (int) queryEntry.getData("offset");
                    if(offset == -1) {
                        queryEntry.addValue(values[index.getAndIncrement()]);
                    } else {
                        queryEntry.addValue(offset);
                        index.incrementAndGet();
                    }
                }
                if(queryEntry.hasData("limit")) {
                    int limit = (int) queryEntry.getData("limit");
                    if(limit == -1) {
                        queryEntry.addValue(values[index.getAndIncrement()]);
                    } else {
                        queryEntry.addValue(limit);
                        index.incrementAndGet();
                    }
                }
                return;
            }
            case ORDER_BY: {
                if(queryEntry.hasData("aggregationBuilder")) {
                    MySqlAggregationBuilder aggregationBuilder = (MySqlAggregationBuilder) queryEntry.getData("aggregationBuilder");
                    prepareAggregationBuilder(aggregationBuilder, index, values);
                }
                return;
            }
            case GROUP_BY: {
                if(queryEntry.hasData("value")) {
                    Object value = queryEntry.getData("value");
                    if(value instanceof AggregationBuilder[]) {
                        for(AggregationBuilder aggregationBuilder : (AggregationBuilder[]) value) {
                            prepareAggregationBuilder((MySqlAggregationBuilder) aggregationBuilder, index, values);
                        }
                    }
                }
                return;
            }
            case HAVING: {
                if(queryEntry.hasData("first")) {
                    Object first = queryEntry.getData("first");
                    if(first instanceof AggregationBuilder) {
                        prepareAggregationBuilder((MySqlAggregationBuilder) first, index, values);
                    } else if(!(first instanceof String)) {
                        queryEntry.addValue(first);
                        index.incrementAndGet();
                    }
                }
                if(queryEntry.hasData("second")) {
                    Object second = queryEntry.getData("second");
                    if(second instanceof AggregationBuilder) {
                        prepareAggregationBuilder((MySqlAggregationBuilder) second, index, values);
                    } else if(!(second instanceof String)) {
                        queryEntry.addValue(second);
                        index.incrementAndGet();
                    }
                }
                return;
            }
            case MIN: case MAX: case COUNT: case AVG: case SUM: {
                if(queryEntry.hasData("first")) {
                    Object first = queryEntry.getData("first");
                    if(first instanceof MySqlAggregationBuilder) {
                        prepareAggregationBuilder((MySqlAggregationBuilder) first, index, values);
                    } else if(!(first instanceof String)) {
                        queryEntry.addValue(first);
                        index.incrementAndGet();
                    }
                }
                if(queryEntry.hasData("second")) {
                    Object second = queryEntry.getData("second");
                    if(second instanceof MySqlAggregationBuilder) {
                        prepareAggregationBuilder((MySqlAggregationBuilder) second, index, values);
                    } else if(!(second instanceof String)) {
                        queryEntry.addValue(second);
                        index.incrementAndGet();
                    }
                }
                return;
            }
        }
    }

    private static void prepareAggregationBuilder(MySqlAggregationBuilder aggregationBuilder, AtomicInteger index, Object[] values) throws SQLException {
        for (AggregationBuilder.Entry entry : aggregationBuilder.getEntries()) {
            if(entry.getType() == AggregationBuilder.Entry.Type.VALUE) {
                if(entry.getValue() != null) {
                    aggregationBuilder.addValue(entry.getValue());
                    index.incrementAndGet();
                } else {
                    aggregationBuilder.addValue(values[index.getAndIncrement()]);
                }
            } else if(entry.getType() == AggregationBuilder.Entry.Type.BUILDER) {
                prepareAggregationBuilder((MySqlAggregationBuilder) entry.getValue(), index, values);
            }
        }
    }
}