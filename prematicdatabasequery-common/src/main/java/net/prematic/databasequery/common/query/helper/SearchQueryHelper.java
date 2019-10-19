/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.10.19, 20:45
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

package net.prematic.databasequery.common.query.helper;

import net.prematic.databasequery.api.DatabaseCollection;
import net.prematic.databasequery.api.aggregation.AggregationBuilder;
import net.prematic.databasequery.api.query.SearchQuery;
import net.prematic.databasequery.api.query.option.OrderOption;
import net.prematic.databasequery.common.QueryOperator;
import net.prematic.databasequery.common.query.AbstractFindQuery;
import net.prematic.databasequery.common.query.QueryEntry;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchQueryHelper<T extends SearchQuery> extends EntryHelper<T> implements SearchQuery<T> {

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
    public T whereLike(String field, String pattern) {
        addEntry(new QueryEntry(QueryOperator.WHERE_PATTERN)
                .addData("field", field)
                .addDataIfNotNull("value", pattern));
        return (T) this;
    }

    @Override
    public T where(String field, String operator, Object value) {
        addEntry(new QueryEntry(QueryOperator.WHERE_COMPARE)
                .addData("field", field)
                .addData("operator", operator)
                .addDataIfNotNull("value", value));
        return (T) this;
    }

    @Override
    public T not(SearchQuery.Consumer searchQuery) {
        SearchQuery resultQuery = this.collection.find();
        searchQuery.accept(resultQuery);
        addEntry(new QueryEntry(QueryOperator.NOT, ((AbstractFindQuery)searchQuery).getEntries()));
        return (T) this;
    }

    @Override
    public T and(SearchQuery.Consumer... searchQueries) {
        List<SearchQuery> resultQueries = new ArrayList<>();
        for (Consumer searchQueryConsumer : searchQueries) {
            SearchQuery searchQuery = this.collection.find();
            searchQueryConsumer.accept(searchQuery);
            resultQueries.add(searchQuery);
        }
        List<QueryEntry> entries = new ArrayList<>();
        for (SearchQuery searchQuery : resultQueries) {
            for (QueryEntry entry : ((AbstractFindQuery) searchQuery).getEntries()) {
                entries.add(entry.setHasParentEntry(true));
            }
        }
        addEntry(new QueryEntry(QueryOperator.AND, entries));
        return (T) this;
    }

    @Override
    public T or(SearchQuery.Consumer... searchQueries) {
        List<SearchQuery> resultQueries = new ArrayList<>();
        for (Consumer searchQueryConsumer : searchQueries) {
            SearchQuery searchQuery = this.collection.find();
            searchQueryConsumer.accept(searchQuery);
            resultQueries.add(searchQuery);
        }
        List<QueryEntry> entries = new ArrayList<>();
        for (SearchQuery searchQuery : resultQueries) {
            for (QueryEntry entry : ((AbstractFindQuery) searchQuery).getEntries()) {
                entries.add(entry.setHasParentEntry(true));
            }
        }
        addEntry(new QueryEntry(QueryOperator.OR, entries));
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
    public T limit(int limit, int offset) {
        addEntry(new QueryEntry(QueryOperator.LIMIT)
                .addData("limit", limit)
                .addData("offset", offset));
        return (T) this;
    }

    @Override
    public T orderBy(String field, OrderOption orderOption) {
        addEntry(new QueryEntry(QueryOperator.ORDER_BY).addData("value", field)
                .addDataIfNotNull("orderOption", orderOption));
        return (T) this;
    }

    @Override
    public T orderBy(AggregationBuilder aggregationBuilder, OrderOption orderOption) {
        addEntry(new QueryEntry(QueryOperator.ORDER_BY).addData("value", aggregationBuilder)
                .addDataIfNotNull("orderOption", orderOption));
        return (T) this;
    }

    @Override
    public T groupBy(String... fields) {
        addEntry(new QueryEntry(QueryOperator.GROUP_BY).addData("value", fields));
        return (T) this;
    }

    @Override
    public T groupBy(AggregationBuilder... aggregationBuilders) {
        addEntry(new QueryEntry(QueryOperator.GROUP_BY).addData("value", aggregationBuilders));
        return (T) this;
    }

    @Override
    public T where(Object first, String operator, Object second) {
        if(first instanceof AggregationBuilder.Consumer) {
            AggregationBuilder aggregationBuilder = this.collection.newAggregationBuilder(false);
            ((AggregationBuilder.Consumer) first).accept(aggregationBuilder);
            first = aggregationBuilder;
        }
        if(second instanceof AggregationBuilder.Consumer) {
            AggregationBuilder aggregationBuilder = this.collection.newAggregationBuilder(false);
            ((AggregationBuilder.Consumer) second).accept(aggregationBuilder);
            second = aggregationBuilder;
        }
        addEntry(new QueryEntry(QueryOperator.HAVING)
                .addDataIfNotNull("first", first)
                .addData("operator", operator)
                .addDataIfNotNull("second", second));
        return (T) this;
    }

    @Override
    public T min(Object first, String operator, Object second) {
        addEntry(new QueryEntry(QueryOperator.MIN)
                .addData("first", first)
                .addDataIfNotNull("operator", operator)
                .addDataIfNotNull("second", second));
        return (T) this;
    }

    @Override
    public T max(Object first, String operator, Object second) {
        addEntry(new QueryEntry(QueryOperator.MAX)
                .addData("first", first)
                .addDataIfNotNull("operator", operator)
                .addDataIfNotNull("second", second));
        return (T) this;
    }

    @Override
    public T count(Object first, String operator, Object second) {
        addEntry(new QueryEntry(QueryOperator.COUNT)
                .addData("first", first)
                .addDataIfNotNull("operator", operator)
                .addDataIfNotNull("second", second));
        return (T) this;
    }

    @Override
    public T avg(Object first, String operator, Object second) {
        addEntry(new QueryEntry(QueryOperator.AVG)
                .addData("first", first)
                .addDataIfNotNull("operator", operator)
                .addDataIfNotNull("second", second));
        return (T) this;
    }

    @Override
    public T sum(Object first, String operator, Object second) {
        addEntry(new QueryEntry(QueryOperator.SUM)
                .addData("first", first)
                .addDataIfNotNull("operator", operator)
                .addDataIfNotNull("second", second));
        return (T) this;
    }
}