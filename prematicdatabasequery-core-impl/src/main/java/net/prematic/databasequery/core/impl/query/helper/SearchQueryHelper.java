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
import net.prematic.databasequery.core.Pattern;
import net.prematic.databasequery.core.QueryOperator;
import net.prematic.databasequery.core.impl.QueryEntry;
import net.prematic.databasequery.core.impl.query.AbstractFindQuery;
import net.prematic.databasequery.core.query.DeleteQuery;
import net.prematic.databasequery.core.query.FindQuery;
import net.prematic.databasequery.core.query.SearchQuery;
import net.prematic.databasequery.core.query.option.OrderOption;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchQueryHelper<T extends SearchQuery> extends QueryHelper implements SearchQuery<T> {

    @Override
    public T where(String field, Object value) {
        addEntry(new QueryEntry(QueryOperator.WHERE)
                .addData("field", field)
                .addDataIfNotNull("value", value));
        return (T) this;
    }

    @Override
    public T where(String field, Pattern pattern) {
        addEntry(new QueryEntry(QueryOperator.WHERE)
                .addData("field", field)
                .addDataIfNotNull("pattern", pattern.build()));
        return (T) this;
    }

    @Override
    public T not(FindQuery findQuery) {
        addEntry(new QueryEntry(QueryOperator.NOT).addData("findQuery", findQuery));
        return (T) this;
    }

    @Override
    public T and(FindQuery... findQueries) {
        List<QueryEntry> entries = new ArrayList<>();
        for (FindQuery findQuery : findQueries) {
            for (QueryEntry entry : ((AbstractFindQuery) findQuery).getEntries()) {
                entries.add(entry.setHasParentEntry(true));
            }
        }
        addEntry(new QueryEntry(QueryOperator.AND, entries));
        return (T) this;
    }

    @Override
    public T or(FindQuery... findQueries) {
        List<QueryEntry> entries = new ArrayList<>();
        for (FindQuery findQuery : findQueries) {
            for (QueryEntry entry : ((AbstractFindQuery) findQuery).getEntries()) {
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
    public T limit(int limit) {
        addEntry(new QueryEntry(QueryOperator.LIMIT).addData("limit", limit, (value)-> value instanceof Integer && (int) value != -1));
        return (T) this;
    }

    @Override
    public T orderBy(String field, OrderOption orderOption) {
        addEntry(new QueryEntry(QueryOperator.ORDER_BY).addData("field", field).addDataIfNotNull("orderOption", orderOption));
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