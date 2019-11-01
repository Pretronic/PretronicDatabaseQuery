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

import net.prematic.databasequery.api.aggregation.AggregationBuilder;
import net.prematic.databasequery.api.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.api.query.SearchQuery;
import net.prematic.databasequery.api.query.option.OrderOption;
import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.databasequery.common.query.QueryStringBuildAble;
import net.prematic.databasequery.common.query.result.SimpleQueryResult;
import net.prematic.databasequery.sql.SqlQuery;
import net.prematic.databasequery.sql.mysql.MySqlAggregationBuilder;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;
import net.prematic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class MySqlSearchQueryHelper<T extends SearchQuery> implements SearchQuery<T>, QueryStringBuildAble, SqlQuery {

    protected final MySqlDatabaseCollection databaseCollection;
    protected final StringBuilder queryBuilder;
    protected final List<Object> values;
    protected boolean where, operator, negate, whereAggregation, orderBy, groupBy;

    public MySqlSearchQueryHelper(MySqlDatabaseCollection databaseCollection) {
        this.databaseCollection = databaseCollection;
        this.queryBuilder = new StringBuilder();
        this.values = new ArrayList<>();
        this.where = true;
        this.operator = true;
        this.negate = false;
        this.whereAggregation = true;
        this.orderBy = true;
        this.groupBy = true;
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.databaseCollection.getDatabase().getDriver().getExecutorService();
    }

    @Override
    public T where(String field, Object value) {
        Validate.notNull(field, "Field can't be null.");
        Validate.notNull(value, "Value can't be null.");
        this.values.add(value);
        if(this.operator) {
            if(this.where) {
                this.queryBuilder.append(" WHERE ");
                this.where = false;
            }
            else this.queryBuilder.append(" AND ");
        }
        if(negate) this.queryBuilder.append("NOT ");
        this.queryBuilder.append("`").append(field).append("`=?");
        return (T) this;
    }

    @Override
    public T whereLike(String field, String pattern) {
        Validate.notNull(field, "Field can't be null.");
        Validate.notNull(pattern, "Pattern can't be null.");
        this.values.add(pattern);
        if(this.operator) {
            if(where) {
                this.queryBuilder.append(" WHERE ");
                this.where = false;
            }
            else this.queryBuilder.append(" AND ");
        }
        if(negate) this.queryBuilder.append("NOT ");
        this.queryBuilder.append("`").append(field).append("` LIKE ?");
        return (T) this;
    }

    @Override
    public T where(String field, String operator, Object value) {
        Validate.notNull(field, "Field can't be null.");
        Validate.notNull(operator, "Operator can't be null.");
        Validate.notNull(value, "Value can't be null.");
        this.values.add(value);
        if(this.operator) {
            if(where) {
                this.queryBuilder.append(" WHERE ");
                this.where = false;
            }
            else this.queryBuilder.append(" AND ");
        }
        if(negate) this.queryBuilder.append("NOT ");
        this.queryBuilder.append("`").append(field).append("` ").append(operator).append(" ?");
        return (T) this;
    }

    @Override
    public T where(Object first, String operator, Object second) {
        Validate.notNull(first, "First can't be null.");
        Validate.notNull(operator, "Operator can't be null.");
        Validate.notNull(second, "Second can't be null.");
        if(this.whereAggregation) {
            this.queryBuilder.append(" HAVING ");
            this.whereAggregation = false;
        }
        else this.queryBuilder.append(" AND ");
        buildWhereAggregation(first);
        this.queryBuilder.append(" ").append(operator).append(" ");
        buildWhereAggregation(second);
        return (T) this;
    }

    @Override
    public T whereNull(String field) {
        Validate.notNull(field, "Field can't be null.");
        if(this.operator) {
            if(this.where) {
                this.queryBuilder.append(" WHERE ");
                this.where = false;
            }
            else this.queryBuilder.append(" AND ");
        }
        this.queryBuilder.append("`").append(field).append("` IS ");
        if(negate) this.queryBuilder.append("NOT ");
        this.queryBuilder.append("NULL");
        return (T) this;
    }

    private void buildWhereAggregation(Object value) {
        if(value instanceof AggregationBuilder) {
            this.queryBuilder.append(((MySqlAggregationBuilder)value).getAggregationBuilder());
            this.values.addAll(((MySqlAggregationBuilder)value).getValues());
        } else if(!(value instanceof String)) {
            this.queryBuilder.append("?");
            this.values.add(value);
        } else this.queryBuilder.append("`").append(value).append("`");
    }

    @Override
    public T not(Consumer searchQuery) {
        Validate.notNull(searchQuery, "SearchQuery can't be null.");
        SearchQuery resultQuery = this.databaseCollection.find();
        ((MySqlSearchQueryHelper)resultQuery).negate = true;
        ((MySqlSearchQueryHelper)resultQuery).where = where;
        searchQuery.accept(resultQuery);
        this.queryBuilder.append(((MySqlFindQuery)resultQuery).queryBuilder);
        this.values.addAll(((MySqlSearchQueryHelper)resultQuery).values);
        this.where = ((MySqlSearchQueryHelper)resultQuery).where;
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
        Validate.notNull(operator, "Operator can't be null.");
        Validate.notNull(searchQueries, "SearchQueries can't be null.");
        SearchQuery[] resultQueries = new SearchQuery[searchQueries.length];
        boolean withOperator = false;
        for (int i = 0; i < searchQueries.length; i++) {
            SearchQuery searchQuery = this.databaseCollection.find();
            ((MySqlSearchQueryHelper)searchQuery).operator = withOperator;
            ((MySqlSearchQueryHelper)searchQuery).where = false;
            searchQueries[i].accept(searchQuery);
            resultQueries[i] = searchQuery;
            withOperator = true;
        }

        if(this.operator) {
            if(this.where) {
                this.queryBuilder.append(" WHERE ");
                this.where = false;
            }
            else this.queryBuilder.append(" ").append(operator).append(" ");
        }

        if(negate) this.queryBuilder.append("NOT ");
        this.queryBuilder.append("(");
        for (SearchQuery searchQuery : resultQueries) {
            this.queryBuilder.append(((MySqlSearchQueryHelper)searchQuery).queryBuilder);
            this.values.addAll(((MySqlSearchQueryHelper)searchQuery).values);
        }
        this.queryBuilder.append(")");
        return (T) this;
    }

    @Override
    public T between(String field, Object value1, Object value2) {
        Validate.notNull(field, "Field can't be null.");
        Validate.notNull(value1, "Value1 can't be null.");
        Validate.notNull(value2, "Value2 can't be null.");
        if(this.operator) {
            if(where) this.queryBuilder.append(" WHERE ");
            else this.queryBuilder.append(" AND ");
        }
        this.queryBuilder.append("`").append(field).append("`");
        if(negate) this.queryBuilder.append(" NOT");
        this.queryBuilder.append(" BETWEEN ");

        if(value1 instanceof MySqlAggregationBuilder) {
            this.queryBuilder.append(((MySqlAggregationBuilder)value1).getAggregationBuilder()).append(" AND ");
            this.values.addAll(((MySqlAggregationBuilder)value1).getValues());
        } else {
            this.queryBuilder.append("? AND ");
            this.values.add(value1);
        }
        if(value2 instanceof MySqlAggregationBuilder) {
            this.queryBuilder.append(((MySqlAggregationBuilder)value2).getAggregationBuilder());
            this.values.addAll(((MySqlAggregationBuilder)value2).getValues());
        } else {
            this.queryBuilder.append("?");
            this.values.add(value2);
        }
        return (T) this;
    }

    @Override
    public T limit(int limit, int offset) {
        this.queryBuilder.append(" LIMIT ?,?");
        if(limit != -1) this.values.add(limit);
        if(offset != -1) this.values.add(offset);
        return (T) this;
    }

    @Override
    public T orderBy(String field, OrderOption orderOption) {
        Validate.notNull(field, "Field can't be null.");
        if(this.orderBy) {
            this.queryBuilder.append(" ORDER BY ");
            this.orderBy = false;
        }
        else this.queryBuilder.append(",");
        this.queryBuilder.append("`").append(field).append("`");
        if(orderOption != null) this.queryBuilder.append(" ").append(orderOption);
        return (T) this;
    }

    @Override
    public T orderBy(AggregationBuilder aggregationBuilder, OrderOption orderOption) {
        Validate.notNull(aggregationBuilder, "AggregationBuilder can't be null.");
        if(this.orderBy) {
            this.queryBuilder.append(" ORDER BY ");
            this.orderBy = false;
        }
        else this.queryBuilder.append(",");
        this.queryBuilder.append(((MySqlAggregationBuilder)aggregationBuilder).getAggregationBuilder());
        this.values.addAll(((MySqlAggregationBuilder)aggregationBuilder).getValues());
        if(orderOption != null) this.queryBuilder.append(" ").append(orderOption);
        return (T) this;
    }

    @Override
    public T groupBy(String... fields) {
        Validate.notNull(fields, "Fields can't be null.");
        if(this.groupBy) {
            this.queryBuilder.append(" GROUP BY ");
            this.groupBy = false;
        }
        else this.queryBuilder.append(" AND ");
        boolean first = true;
        for(String field : fields) {
            if(!first) this.queryBuilder.append(",");
            else first = false;
            this.queryBuilder.append("`").append(field).append("`");
        }
        return (T) this;
    }

    @Override
    public T groupBy(AggregationBuilder... aggregationBuilders) {
        Validate.notNull(aggregationBuilders, "AggregationBuilders can't be null.");
        if(this.groupBy) {
            this.queryBuilder.append(" GROUP BY ");
            this.groupBy = false;
        }
        else this.queryBuilder.append(" AND ");
        boolean first = true;
        for (AggregationBuilder aggregationBuilder : aggregationBuilders) {
            if(!first) this.queryBuilder.append(",");
            else first = false;
            this.queryBuilder.append(((MySqlAggregationBuilder)aggregationBuilder).getAggregationBuilder());
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

    protected T aggregation(String aggregation, Object first, String operator, Object second) {
        Validate.notNull(aggregation, "Aggregation can't be null.");
        Validate.notNull(first, "First can't be null.");
        Validate.notNull(operator, "Operator can't be null.");
        Validate.notNull(second, "Second can't be null.");
        this.queryBuilder.append(aggregation).append("(");
        buildAggregationPart(first);
        this.queryBuilder.append(" ").append(operator).append(" ");
        buildAggregationPart(second);
        return (T) this;
    }

    protected void buildAggregationPart(Object first) {
        if(first instanceof MySqlAggregationBuilder) {
            this.queryBuilder.append(((MySqlAggregationBuilder)first).getAggregationBuilder());
            this.values.addAll(((MySqlAggregationBuilder)first).getValues());
        } else if(first instanceof String) {
            this.queryBuilder.append("`").append(first).append("`");
        } else {
            this.queryBuilder.append("?");
            this.values.add(first);
        }
    }

    @Override
    public QueryResult execute(boolean commit, Object... values) {
        String query = buildExecuteString(values);
        this.databaseCollection.getDatabase().executeUpdateQuery(query, commit, preparedStatement -> {
            int index = 1;
            int valueGet = 0;
            for (Object value : this.values) {
                if(value == null) {
                    value = values[valueGet];
                    valueGet++;
                } else if(value == Option.NULL) {
                    value = null;
                } else {
                    DataTypeAdapter adapter = this.databaseCollection.getDatabase().getDriver().getDataTypeAdapterByWriteClass(value.getClass());
                    if(adapter != null) value = adapter.write(value);
                }
                preparedStatement.setObject(index, value);
                index++;
            }
        });
        return new SimpleQueryResult(new ArrayList<>());
    }
}