/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 07.05.19, 13:54
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
import net.prematic.libraries.utility.map.Pair;

import java.util.HashMap;
import java.util.Map;

public interface FindQuery extends SearchQuery<FindQuery> {

    FindQuery get(String... fields);

    FindQuery get(GetBuilder... getBuilders);

    class GetBuilder {

        private Map<EntryType, Object> entries;

        public GetBuilder() {
            this.entries = new HashMap<>();
        }

        public Map<EntryType, Object> getEntries() {
            return entries;
        }

        public GetBuilder withField(String field) {
            this.entries.put(EntryType.FIELD, field);
            return this;
        }

        public GetBuilder withOperator(String operator) {
            this.entries.put(EntryType.OPERATOR, operator);
            return this;
        }

        public GetBuilder withAggregation(Aggregation aggregation, String field) {
            this.entries.put(EntryType.AGGREGATION, new Pair<>(aggregation, field));
            return this;
        }

        enum EntryType {

            FIELD,
            OPERATOR,
            AGGREGATION

        }
    }
}