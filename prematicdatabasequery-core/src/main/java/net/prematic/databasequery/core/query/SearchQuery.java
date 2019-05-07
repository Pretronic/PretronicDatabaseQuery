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

public interface SearchQuery extends Query {

    SearchQuery where(String field, Object value);

    default SearchQuery where(String field) {
        return where(field, (Object) null);
    }

    SearchQuery where(String field, Pattern pattern);

    SearchQuery not(SearchQuery searchQuery);

    SearchQuery and(SearchQuery... searchQueries);

    SearchQuery or(SearchQuery... searchQueries);

    SearchQuery between(String field, Object value1, Object value2);

    default SearchQuery between(String field) {
        return between(field, null, null);
    }

    SearchQuery limit(int limit);

    default SearchQuery limit() {
        return limit(-1);
    }

    default SearchQuery first() {
        return limit(1);
    }

    SearchQuery orderBy(String field, OrderOption orderOption);

    default SearchQuery orderBy(String field) {
        return orderBy(field, null);
    }

    SearchQuery groupBy(String... fields);

    SearchQuery groupBy(String field, Aggregation aggregation);

    SearchQuery groupBy(Pair<String, Aggregation>... fields);

    SearchQuery having(SearchQuery first, String operator, SearchQuery second);

    SearchQuery having(Object first, String operator, SearchQuery second);

    SearchQuery having(SearchQuery first, String operator, Object second);

    SearchQuery min(String field);

    default SearchQuery min() {
        return min(null);
    }

    SearchQuery max(String field);

    default SearchQuery max() {
        return max(null);
    }

    SearchQuery count(String field);

    default SearchQuery count() {
        return count(null);
    }

    SearchQuery avg(String field);

    default SearchQuery avg() {
        return avg(null);
    }

    SearchQuery sum(String field);

    default SearchQuery sum() {
        return sum(null);
    }

    SearchQuery get(String... fields);

    SearchQuery get(SearchQueryGetBuilder... fields);

    class SearchQueryGetBuilder {

        private Map<SearchQueryGetBuilderEntryType, Object> entries;

        public SearchQueryGetBuilder() {
            this.entries = new HashMap<>();
        }

        public Map<SearchQueryGetBuilderEntryType, Object> getEntries() {
            return entries;
        }

        public SearchQueryGetBuilder withField(String field) {
            this.entries.put(SearchQueryGetBuilderEntryType.FIELD, field);
            return this;
        }

        public SearchQueryGetBuilder withOperator(String operator) {
            this.entries.put(SearchQueryGetBuilderEntryType.OPERATOR, operator);
            return this;
        }

        public SearchQueryGetBuilder withAggregation(Aggregation aggregation, String field) {
            this.entries.put(SearchQueryGetBuilderEntryType.AGGREGATION, new Pair<>(aggregation, field));
            return this;
        }

        enum SearchQueryGetBuilderEntryType {

            FIELD,
            OPERATOR,
            AGGREGATION

        }
    }
}