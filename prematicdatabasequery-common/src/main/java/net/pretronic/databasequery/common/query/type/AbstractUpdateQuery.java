/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 15.12.19, 20:08
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
import net.prematic.databasequery.api.query.type.UpdateQuery;
import net.pretronic.databasequery.common.query.EntryOption;

public abstract class AbstractUpdateQuery<C extends DatabaseCollection> extends AbstractSearchQuery<UpdateQuery, C> implements UpdateQuery {

    public AbstractUpdateQuery(C collection) {
        super(collection);
    }

    @Override
    public UpdateQuery set(String field, Object value) {
        return addEntry(new SetEntry(field, value));
    }

    @Override
    public UpdateQuery set(String field) {
        return addEntry(new SetEntry(field, EntryOption.PREPARED));
    }

    public static class SetEntry extends Entry {

        final String field;
        final Object value;

        public SetEntry(String field, Object value) {
            this.field = field;
            this.value = value;
        }
    }
}
