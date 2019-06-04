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
import java.util.List;
import java.util.function.Consumer;

public interface FindQuery extends SearchQuery<FindQuery> {

    FindQuery get(String... fields);

    FindQuery get(GetBuilder... getBuilders);

    FindQuery get(GetBuilderConsumer... getBuilders);

    GetBuilder getGetBuilder();

    default FindQuery get(Object... fieldsAndGetBuilders) {
        for (Object fieldsAndGetBuilder : fieldsAndGetBuilders) {
            if(fieldsAndGetBuilder instanceof String) get((String)fieldsAndGetBuilder);
            else if(fieldsAndGetBuilder instanceof GetBuilder) get((GetBuilder)fieldsAndGetBuilder);
        }
        return this;
    }

    interface GetBuilder {

        List<Entry> getEntries();

        GetBuilder withField(String field);

        GetBuilder withOperator(String operator);

        GetBuilder withAggregation(Aggregation aggregation, String field);

        GetBuilder withReturnAlias(String alias);

        GetBuilder withGetBuilder(GetBuilder getBuilder);

        interface Entry {

            Type getType();

            Object getValue();

            enum Type {

                FIELD,
                OPERATOR,
                AGGREGATION,
                ALIAS,
                GET_BUILDER

            }
        }
    }

    interface GetBuilderConsumer extends Consumer<GetBuilder> {}
}