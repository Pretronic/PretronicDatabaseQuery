/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 15.12.19, 20:18
 *
 * The PretronicDatabaseQuery Project is under the Apache License, version 2.0 (the "License");
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

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.type.ReplaceQuery;
import net.pretronic.libraries.utility.map.Triple;
import net.pretronic.databasequery.common.query.EntryOption;

public abstract class AbstractReplaceQuery<C extends DatabaseCollection> extends AbstractSearchQuery<ReplaceQuery, C> implements ReplaceQuery {

    public AbstractReplaceQuery(C collection) {
        super(collection);
    }

    @Override
    public ReplaceQuery set(String field, Object value) {
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new AbstractUpdateQuery.SetEntry(assignment.getFirst(), assignment.getSecond(), assignment.getThird(), value));
    }

    @Override
    public ReplaceQuery set(String field) {
        return set(field, EntryOption.PREPARED);
    }
}
