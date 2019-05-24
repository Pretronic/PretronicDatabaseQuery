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

public interface SearchQuery<T extends SearchQuery> extends Query {

    T where(String field, Object value);

    default T where(String field) {
        return where(field, (Object) null);
    }

    T where(String field, Pattern pattern);

    T not(FindQuery searchQuery);

    T and(FindQuery... searchQueries);

    T or(FindQuery... searchQueries);

    T between(String field, Object value1, Object value2);

    default T between(String field) {
        return between(field, null, null);
    }

    T limit(int limit);

    default T limit() {
        return limit(-1);
    }

    default T first() {
        return limit(1);
    }

    T orderBy(String field, OrderOption orderOption);

    default T orderBy(String field) {
        return orderBy(field, null);
    }

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

    default T min() {
        return min(null);
    }

    T max(String field);

    default T max() {
        return max(null);
    }

    T count(String field);

    default T count() {
        return count(null);
    }

    T avg(String field);

    default T avg() {
        return avg(null);
    }

    T sum(String field);

    default T sum() {
        return sum(null);
    }
}