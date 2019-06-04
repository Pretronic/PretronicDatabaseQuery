/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 24.05.19, 21:41
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

package net.prematic.databasequery.core.impl.query.helper;

import net.prematic.databasequery.core.Aggregation;
import net.prematic.databasequery.core.DatabaseCollection;
import net.prematic.databasequery.core.QueryOperator;
import net.prematic.databasequery.core.impl.query.QueryEntry;
import net.prematic.databasequery.core.impl.query.AbstractFindQuery;
import net.prematic.databasequery.core.query.SearchQuery;
import net.prematic.databasequery.core.query.option.OrderOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public abstract class SearchQueryHelper<T extends SearchQuery> extends QueryHelper implements SearchQuery<T> {

    private final DatabaseCollection collection;

    public SearchQueryHelper(DatabaseCollection collection) {
        this.collection = collection;
    }

    public DatabaseCollection getCollection() {
        return collection;
    }

    @Override
    public T where(String field, Object value) {
        addEntry(new QueryEntry(QueryOperator.WHERE)
                .addData("field", field)
                .addDataIfNotNull("value", value));
        return (T) this;
    }

    @Override
    public T where(String field, String pattern) {
        addEntry(new QueryEntry(QueryOperator.WHERE_PATTERN)
                .addData("field", field)
                .addDataIfNotNull("pattern", pattern));
        return (T) this;
    }

    @Override
    public T where(String field, String operator, Object value) {
        addEntry(new QueryEntry(QueryOperator.WHERE_AGGREGATION)
                .addData("field", field)
                .addDataIfNotNull("operator", operator)
                .addDataIfNotNull("value", value));
        return (T) this;
    }

    @Override
    public T not(SearchQuery searchQuery) {
        addEntry(new QueryEntry(QueryOperator.NOT).addData("searchQuery", searchQuery));
        return (T) this;
    }

    @Override
    public T not(SearchQueryConsumer searchQuery) {
        searchQuery.accept(this.collection.find());
        return (T) this;
    }

    @Override
    public T and(SearchQuery... searchQueries) {
        System.out.println("AND");
        List<QueryEntry> entries = new ArrayList<>();
        for (SearchQuery searchQuery : searchQueries) {
            for (QueryEntry entry : ((AbstractFindQuery) searchQuery).getEntries()) {
                entries.add(entry.setHasParentEntry(true));
            }
        }
        addEntry(new QueryEntry(QueryOperator.AND, entries));
        return (T) this;
    }

    @Override
    public T and(SearchQueryConsumer... searchQueries) {
        System.out.println("AND CONSUMER");
        List<SearchQuery> resultQueries = new ArrayList<>();
        for (Consumer<SearchQuery> searchQueryConsumer : searchQueries) {
            SearchQuery searchQuery = this.collection.find();
            searchQueryConsumer.accept(searchQuery);
        }
        System.out.println(resultQueries);
        for (SearchQuery resultQuery : resultQueries) {
            ((AbstractFindQuery)resultQuery).getEntries().forEach(queryEntry -> System.out.println(queryEntry.getOperator()));
        }
        return (T) this;
    }

    @Override
    public T or(SearchQuery... searchQueries) {
        List<QueryEntry> entries = new ArrayList<>();
        for (SearchQuery searchQuery : searchQueries) {
            for (QueryEntry entry : ((AbstractFindQuery) searchQuery).getEntries()) {
                entries.add(entry.setHasParentEntry(true));
            }
        }
        addEntry(new QueryEntry(QueryOperator.OR, entries));
        return (T) this;
    }

    @Override
    public T or(SearchQueryConsumer... searchQueries) {
        for (Consumer<SearchQuery> searchQuery : searchQueries) {
            searchQuery.accept(this.collection.find());
        }
        return (T) this;
    }

    @Override
    public T between(String field, Object value1, Object value2) {
        addEntry(new QueryEntry(QueryOperator.BETWEEN)
                .addData("field", field)
                .addDataIfNotNull("value1", value1)
                .addDataIfNotNull("value2", value2));
        return (T) this;
    }

    @Override
    public T limit(int limit) {
        addEntry(new QueryEntry(QueryOperator.LIMIT).addData("limit", limit, (value)-> value instanceof Integer && (int) value != -1));
        return (T) this;
    }

    @Override
    public T orderBy(String field, OrderOption orderOption) {
        addEntry(new QueryEntry(QueryOperator.ORDER_BY).addData("field", field).addData("orderOption", orderOption));
        return (T) this;
    }

    @Override
    public T groupBy(String... fields) {
        for (String field : fields) {
            groupBy(field, null);
        }
        return (T) this;
    }

    @Override
    public T groupBy(String field, Aggregation aggregation) {
        addEntry(new QueryEntry(QueryOperator.GROUP_BY).addDataIfNotNull("field", field).addDataIfNotNull("aggregation", aggregation));
        return (T) this;
    }

    @Override
    public T having(Object first, String operator, Object second) {
        addEntry(new QueryEntry(QueryOperator.HAVING)
                .addDataIfNotNull("findQuery", first)
                .addDataIfNotNull("operator", operator)
                .addDataIfNotNull("second", second));
        return (T) this;
    }

    @Override
    public T min(String field) {
        addEntry(new QueryEntry(QueryOperator.MIN).addDataIfNotNull("field", field));
        return (T) this;
    }

    @Override
    public T max(String field) {
        addEntry(new QueryEntry(QueryOperator.MAX).addDataIfNotNull("field", field));
        return (T) this;
    }

    @Override
    public T count(String field) {
        addEntry(new QueryEntry(QueryOperator.COUNT).addDataIfNotNull("field", field));
        return (T) this;
    }

    @Override
    public T avg(String field) {
        addEntry(new QueryEntry(QueryOperator.AVG).addDataIfNotNull("field", field));
        return (T) this;
    }

    @Override
    public T sum(String field) {
        addEntry(new QueryEntry(QueryOperator.SUM).addDataIfNotNull("field", field));
        return (T) this;
    }
}