/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.10.19, 20:44
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

package net.prematic.databasequery.api.query;

import net.prematic.databasequery.api.NotPrepareAble;
import net.prematic.databasequery.api.Pattern;
import net.prematic.databasequery.api.aggregation.AggregationBuilder;
import net.prematic.databasequery.api.query.option.OrderOption;

/**
 * Query order:
 * - methods {@link #where(Object, String, Object)} and all extending methods
 * - methods {@link #groupBy(String...)} and {@link #groupBy(AggregationBuilder...)}
 * - methods {@link #orderBy(String, OrderOption)} and {@link #orderBy(AggregationBuilder, OrderOption)}
 * - methods {@link #limit(int, int)} and all extending methods
 * @param <T>
 */
public interface SearchQuery<T extends SearchQuery> extends Query {

    T where(String field, Object value);

    default T where(String field) {
        return where(field, null);
    }

    default T whereLike(String field, Pattern pattern) {
        return where(field, pattern.build());
    }

    T whereLike(String field, String pattern);

    T where(String field, String operator, Object value);

    T where(Object first, String operator, Object second);

    default T where(AggregationBuilder first, String operator, Object second) {
        return where((Object) first, operator, second);
    }

    default T where(AggregationBuilder.Consumer first, String operator, Object second) {
        return where((Object)first, operator, second);
    }

    default T where(Object first, String operator, AggregationBuilder second) {
        return where(first, operator, (Object) second);
    }

    default T where(Object first, String operator, AggregationBuilder.Consumer second) {
        return where(first, operator, (Object)second);
    }

    default T where(AggregationBuilder first, String operator, AggregationBuilder second) {
        return where((Object) first, operator, (Object)second);
    }

    default T where(AggregationBuilder.Consumer first, String operator, AggregationBuilder.Consumer second) {
        return where((Object) first, operator, (Object) second);
    }

    T whereNull(String field);

    @NotPrepareAble
    T whereIn(String field, Object... values);

    T not(Consumer searchQuery);

    T and(Consumer... searchQueries);

    T or(Consumer... searchQueries);

    T between(String field, Object value1, Object value2);

    default T between(String field, AggregationBuilder value1, AggregationBuilder value2) {
        return between(field, value1, (Object)value2);
    }

    default T between(String field, AggregationBuilder value1, Object value2) {
        return between(field, (Object)value1, value2);
    }

    default T between(String field, Object value1, AggregationBuilder value2) {
        return between(field, value1, (Object) value2);
    }

    default T between(String field) {
        return between(field, (Object)null, null);
    }

    //Offset 0, if not used
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

    T orderBy(AggregationBuilder aggregationBuilder, OrderOption orderOption);

    T groupBy(String... fields);

    T groupBy(AggregationBuilder... aggregationBuilders);

    T min(Object first, String operator, Object second);

    default T min(String field) {
        return min(field, null, null);
    }

    T max(Object first, String operator, Object second);

    default T max(String field) {
        return max(field, null, null);
    }

    T count(Object first, String operator, Object second);

    default T count(String field) {
        return count(field, null, null);
    }

    T avg(Object first, String operator, Object second);

    default T avg(String field) {
        return avg(field, null, null);
    }

    T sum(Object first, String operator, Object second);

    default T sum(String field) {
        return sum(field, null, null);
    }

    interface Consumer extends java.util.function.Consumer<SearchQuery> {}
}