/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 15.06.19, 21:58
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
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.query.SearchQuery;
import net.prematic.databasequery.core.query.option.OrderOption;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.mysql.CommitOnExecute;
import net.prematic.databasequery.sql.mysql.MySqlAggregationBuilder;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class MySqlSearchQueryHelper<T extends SearchQuery> implements SearchQuery<T>, QueryStringBuildAble, CommitOnExecute {

    protected final MySqlDatabaseCollection databaseCollection;
    protected final StringBuilder searchQueryBuilder, whereAggregationQueryBuilder, groupByQueryBuilder, orderByQueryBuilder;
    protected String limit;
    protected final List<Object> values;
    protected boolean where, first, negate;

    public MySqlSearchQueryHelper(MySqlDatabaseCollection databaseCollection) {
        this.databaseCollection = databaseCollection;
        this.searchQueryBuilder = new StringBuilder();
        this.whereAggregationQueryBuilder = new StringBuilder();
        this.orderByQueryBuilder = new StringBuilder();
        this.groupByQueryBuilder = new StringBuilder();
        this.values = new ArrayList<>();
        this.where = true;
        this.first = false;
        this.negate = false;
    }

    @Override
    public T where(String field, Object value) {
        this.values.add(value);
        if(!first) {
            if(where) {
                searchQueryBuilder.append(" WHERE ");
                this.where = false;
            }
            else searchQueryBuilder.append(" AND ");
        }
        if(negate) searchQueryBuilder.append("NOT ");
        searchQueryBuilder.append("`").append(field).append("`=?");
        return (T) this;
    }

    @Override
    public T whereLike(String field, String pattern) {
        this.values.add(pattern);
        if(!first) {
            if(where) {
                searchQueryBuilder.append(" WHERE ");
                this.where = false;
            }
            else searchQueryBuilder.append(" AND ");
        }
        if(negate) searchQueryBuilder.append("NOT ");
        searchQueryBuilder.append("`").append(field).append("` LIKE ?");
        return (T) this;
    }

    @Override
    public T where(String field, String operator, Object value) {
        this.values.add(value);
        if(!first) {
            if(where) {
                searchQueryBuilder.append(" WHERE ");
                this.where = false;
            }
            else searchQueryBuilder.append(" AND ");
        }
        if(negate) searchQueryBuilder.append("NOT ");
        searchQueryBuilder.append("`").append(field).append("` ").append(operator).append(" ?");
        return (T) this;
    }

    @Override
    public T where(Object first, String operator, Object second) {
        if(whereAggregationQueryBuilder.length() == 0) whereAggregationQueryBuilder.append(" HAVING ");
        else whereAggregationQueryBuilder.append(" AND ");
        buildWhereAggregation(first);
        whereAggregationQueryBuilder.append(" ").append(operator).append(" ");
        buildWhereAggregation(second);
        return (T) this;
    }

    private void buildWhereAggregation(Object value) {
        if(value instanceof AggregationBuilder) {
            whereAggregationQueryBuilder.append(((MySqlAggregationBuilder)value).getAggregationBuilder());
            this.values.addAll(((MySqlAggregationBuilder)value).getValues());
        } else if(!(value instanceof String)) {
            whereAggregationQueryBuilder.append("?");
            this.values.add(value);
        } else whereAggregationQueryBuilder.append("`").append(value).append("`");
    }

    @Override
    public T not(Consumer searchQuery) {
        SearchQuery resultQuery = this.databaseCollection.find();
        ((MySqlSearchQueryHelper)resultQuery).negate = true;
        if(this.searchQueryBuilder.length() != 0) {
            ((MySqlSearchQueryHelper)resultQuery).where = false;
        }
        searchQuery.accept(resultQuery);
        searchQueryBuilder.append(((MySqlFindQuery)resultQuery).searchQueryBuilder);
        this.values.addAll(((MySqlSearchQueryHelper)resultQuery).values);
        return (T) this;
    }

    @Override
    public T and(Consumer... searchQueries) {
        return andOr("AND", searchQueries);
    }

    @Override
    public T or(Consumer... searchQueries) {
        return andOr("OR", searchQueries);
    }

    private T andOr(String operator, Consumer... searchQueries) {
        SearchQuery[] resultQueries = new SearchQuery[searchQueries.length];
        boolean first = true;
        for (int i = 0; i < searchQueries.length; i++) {
            SearchQuery searchQuery = this.databaseCollection.find();
            ((MySqlSearchQueryHelper)searchQuery).first = first;
            ((MySqlSearchQueryHelper)searchQuery).where = false;
            searchQueries[i].accept(searchQuery);
            resultQueries[i] = searchQuery;
            first = false;
        }
        if(!first) {
            if(where) searchQueryBuilder.append(" WHERE ");
            else searchQueryBuilder.append(" ").append(operator).append(" ");
        }
        if(negate) searchQueryBuilder.append("NOT ");
        searchQueryBuilder.append("(");
        for (SearchQuery searchQuery : resultQueries) {
            searchQueryBuilder.append(((MySqlSearchQueryHelper)searchQuery).searchQueryBuilder);
            this.values.addAll(((MySqlSearchQueryHelper)searchQuery).values);
        }
        searchQueryBuilder.append(")");
        return (T) this;
    }

    @Override
    public T between(String field, Object value1, Object value2) {
        if(!first) {
            if(where) searchQueryBuilder.append(" WHERE ");
            else searchQueryBuilder.append(" AND ");
        }
        searchQueryBuilder.append("`").append(field).append("`");
        if(negate) searchQueryBuilder.append(" NOT");
        searchQueryBuilder.append(" BETWEEN ");

        if(value1 instanceof MySqlAggregationBuilder) {
            searchQueryBuilder.append(((MySqlAggregationBuilder)value1).getAggregationBuilder()).append(" AND ");
            this.values.addAll(((MySqlAggregationBuilder)value1).getValues());
        } else {
            searchQueryBuilder.append("? AND ");
            this.values.add(value1);
        }
        if(value2 instanceof MySqlAggregationBuilder) {
            searchQueryBuilder.append(((MySqlAggregationBuilder)value2).getAggregationBuilder());
            this.values.addAll(((MySqlAggregationBuilder)value2).getValues());
        } else {
            searchQueryBuilder.append("?");
            this.values.add(value2);
        }
        return (T) this;
    }

    @Override
    public T limit(int limit, int offset) {
        this.limit = " LIMIT ?,?";
        if(limit != -1) this.values.add(limit);
        if(offset != -1) this.values.add(offset);
        return (T) this;
    }

    @Override
    public T orderBy(String field, OrderOption orderOption) {
        if(this.orderByQueryBuilder.length() == 0) orderByQueryBuilder.append(" ORDER BY ");
        else orderByQueryBuilder.append(",");
        orderByQueryBuilder.append("`").append(field).append("`");
        if(orderOption != null) orderByQueryBuilder.append(" ").append(orderByQueryBuilder);
        return (T) this;
    }

    @Override
    public T orderBy(AggregationBuilder aggregationBuilder, OrderOption orderOption) {
        if(this.orderByQueryBuilder.length() == 0) orderByQueryBuilder.append(" ORDER BY ");
        else orderByQueryBuilder.append(",");
        orderByQueryBuilder.append(((MySqlAggregationBuilder)aggregationBuilder).getAggregationBuilder());
        this.values.addAll(((MySqlAggregationBuilder)aggregationBuilder).getValues());
        if(orderOption != null) orderByQueryBuilder.append(" ").append(orderByQueryBuilder);
        return (T) this;
    }

    @Override
    public T groupBy(String... fields) {
        if(this.groupByQueryBuilder.length() == 0) groupByQueryBuilder.append(" GROUP BY ");
        else groupByQueryBuilder.append(" AND ");
        this.first = true;
        for(String field : fields) {
            if(!first) groupByQueryBuilder.append(",");
            else first = false;
            groupByQueryBuilder.append("`").append(field).append("`");
        }
        return (T) this;
    }

    @Override
    public T groupBy(AggregationBuilder... aggregationBuilders) {
        if(this.groupByQueryBuilder.length() == 0) groupByQueryBuilder.append(" GROUP BY ");
        else groupByQueryBuilder.append(" AND ");
        this.first = true;
        for (AggregationBuilder aggregationBuilder : aggregationBuilders) {
            if(!first) groupByQueryBuilder.append(",");
            else first = false;
            groupByQueryBuilder.append(((MySqlAggregationBuilder)aggregationBuilder).getAggregationBuilder());
            this.values.addAll(((MySqlAggregationBuilder)aggregationBuilder).getValues());
        }
        return (T) this;
    }

    @Override
    public T min(Object first, String operator, Object second) {
        return aggregation("MIN", first, operator, second);
    }

    @Override
    public T max(Object first, String operator, Object second) {
        return aggregation("MAX", first, operator, second);
    }

    @Override
    public T count(Object first, String operator, Object second) {
        return aggregation("COUNT", first, operator, second);
    }

    @Override
    public T avg(Object first, String operator, Object second) {
        return aggregation("AVG", first, operator, second);
    }

    @Override
    public T sum(Object first, String operator, Object second) {
        return aggregation("SUM", first, operator, second);
    }

    private T aggregation(String aggregation, Object first, String operator, Object second) {
        searchQueryBuilder.append(aggregation).append("(");
        buildAggregationPart(first);
        if(operator != null) searchQueryBuilder.append(" ").append(operator).append(" ");
        buildAggregationPart(second);
        return (T) this;
    }

    private void buildAggregationPart(Object first) {
        if(first instanceof MySqlAggregationBuilder) {
            searchQueryBuilder.append(((MySqlAggregationBuilder)first).getAggregationBuilder());
            this.values.addAll(((MySqlAggregationBuilder)first).getValues());
        } else if(first instanceof String) {
            searchQueryBuilder.append("`").append(first).append("`");
        } else {
            searchQueryBuilder.append("?");
            this.values.add(first);
        }
    }

    @Override
    public QueryResult execute(boolean commit, Object... values) {
        try(Connection connection = this.databaseCollection.getDatabase().getDriver().getConnection()) {
            int index = 1;
            int valueGet = 0;
            PreparedStatement preparedStatement = connection.prepareStatement(buildExecuteString());
            for (Object value : this.values) {
                if(value == null) {
                    value = values[valueGet];
                    valueGet++;
                }
                DataTypeAdapter adapter = this.databaseCollection.getDatabase().getDriver().getDataTypeAdapterByWriteClass(value.getClass());
                if(adapter != null) value = adapter.write(value);
                preparedStatement.setObject(index, value);
                index++;
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
}