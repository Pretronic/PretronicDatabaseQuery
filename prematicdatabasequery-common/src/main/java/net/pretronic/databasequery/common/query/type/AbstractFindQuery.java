/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 15.12.19, 20:15
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

package net.pretronic.databasequery.common.query.type;

import net.prematic.databasequery.api.collection.DatabaseCollection;
import net.prematic.databasequery.api.query.Aggregation;
import net.prematic.databasequery.api.query.type.FindQuery;

public abstract class AbstractFindQuery<C extends DatabaseCollection> extends AbstractSearchQuery<FindQuery, C> implements FindQuery {

    public AbstractFindQuery(C collection) {
        super(collection);
    }

    @Override
    public FindQuery get(String... fields) {
        for (String field : fields) {
            addEntry(new GetEntry(field, null));
        }
        return this;
    }

    @Override
    public FindQuery get(Aggregation aggregation, String field) {
        return addEntry(new GetEntry(field, aggregation));
    }

    public static class GetEntry extends Entry {

        final String field;
        final Aggregation aggregation;

        public GetEntry(String field, Aggregation aggregation) {
            this.field = field;
            this.aggregation = aggregation;
        }
    }
}
