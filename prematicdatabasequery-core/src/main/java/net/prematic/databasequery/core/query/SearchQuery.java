/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 03.05.19, 23:50
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

package net.prematic.databasequery.core.query;

import net.prematic.databasequery.core.Aggregation;
import net.prematic.databasequery.core.Pattern;
import net.prematic.databasequery.core.query.option.OrderOption;
import net.prematic.libraries.utility.map.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface SearchQuery<T extends SearchQuery> extends Query {

    T where(String field, Object value);

    default T where(String field) {
        return where(field, (Object) null);
    }

    default T where(String field, Pattern pattern) {
        return where(field, pattern.build());
    }

    T where(String field, String pattern);

    T where(String field, String operator, Object value);

    T not(SearchQuery searchQuery);

    T not(SearchQueryConsumer searchQuery);

    T and(SearchQuery... searchQueries);

    T and(SearchQueryConsumer... searchQueries);

    T or(SearchQuery... searchQueries);

    T or(SearchQueryConsumer... searchQueries);

    T between(String field, Object value1, Object value2);

    default T between(String field) {
        return between(field, null, null);
    }

    T limit(int limit, int offset);

    default T limit(int limit) {
        return limit(limit, 0);
    }

    default T limit() {
        return limit(-1, -1);
    }

    default T first() {
        return limit(1, 0);
    }

    T orderBy(String field, OrderOption orderOption);

    T groupBy(String... fields);

    T groupBy(String field, Aggregation aggregation);

    default T groupBy(Pair<String, Aggregation>... fields) {
        for (Pair<String, Aggregation> field : fields) {
            groupBy(field.getKey(), field.getValue());
        }
        return (T) this;
    }

    default T having(FindQuery first, String operator, FindQuery second) {
        return having((Object)first, operator, (Object)second);
    }

    default T having(Object first, String operator, FindQuery second) {
        return having(first, operator, (Object)second);
    }

    default T having(FindQuery first, String operator, Object second) {
        return having((Object)first, operator, second);
    }

    T having(Object first, String operator, Object second);

    T min(String field);

    T max(String field);

    T count(String field);

    T avg(String field);

    T sum(String field);

    interface SearchQueryConsumer extends Consumer<SearchQuery> {}
}