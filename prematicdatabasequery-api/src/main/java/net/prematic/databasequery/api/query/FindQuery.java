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

import net.prematic.databasequery.api.aggregation.AggregationBuilder;

/**
 * Query order:
 * - all methods from this class
 * - order of {@link SearchQuery}
 */
public interface FindQuery extends SearchQuery<FindQuery> {

    FindQuery get(String... fields);

    FindQuery get(AggregationBuilder... aggregationBuilders);

    FindQuery get(AggregationBuilder.Consumer... aggregationBuilders);

    default FindQuery get(Object... fieldsAndAggregationBuilders) {
        for (Object fieldsAndAggregationBuilder : fieldsAndAggregationBuilders) {
            if(fieldsAndAggregationBuilder instanceof String) get((String)fieldsAndAggregationBuilder);
            else if(fieldsAndAggregationBuilder instanceof AggregationBuilder.Consumer) get((AggregationBuilder) fieldsAndAggregationBuilder);
        }
        return this;
    }
}