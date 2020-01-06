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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFindQuery<C extends DatabaseCollection> extends AbstractSearchQuery<FindQuery, C> implements FindQuery {

    protected final List<GetEntry> getEntries;

    public AbstractFindQuery(C collection) {
        super(collection);
        this.getEntries = new ArrayList<>();
    }

    @Override
    public FindQuery get(String... fields) {
        for (String field : fields) {
            this.getEntries.add(new GetEntry(field, null));
        }
        return this;
    }

    @Override
    public FindQuery get(Aggregation aggregation, String field) {
        this.getEntries.add(new GetEntry(field, aggregation));
        return this;
    }

    public List<GetEntry> getGetEntries() {
        return getEntries;
    }

    public static class GetEntry extends Entry {

        private final String field;
        private final Aggregation aggregation;

        public GetEntry(String field, Aggregation aggregation) {
            this.field = field;
            this.aggregation = aggregation;
        }

        public String getField() {
            return field;
        }

        public Aggregation getAggregation() {
            return aggregation;
        }
    }
}
