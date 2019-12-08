/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.12.19, 20:44
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

package net.pretronic.databasequery.common.query.result;

import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.databasequery.api.query.result.QueryResultEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class DefaultQueryResult implements QueryResult {

    public static final QueryResult EMPTY = new DefaultQueryResult(new ArrayList<>());

    private final List<QueryResultEntry> entries;

    public DefaultQueryResult(List<QueryResultEntry> entries) {
        this.entries = entries;
    }

    @Override
    public QueryResultEntry first() {
        return get(0);
    }

    @Override
    public QueryResultEntry firstOrNull() {
        return getOrNull(0);
    }

    @Override
    public QueryResultEntry last() {
        return get(this.entries.size()-1);
    }

    @Override
    public QueryResultEntry lastOrNull() {
        return getOrNull(this.entries.size()-1);
    }

    @Override
    public QueryResultEntry get(int index) {
        return this.entries.get(index);
    }

    @Override
    public QueryResultEntry getOrNull(int index) {
        return this.entries.size() > index ? get(index) : null;
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public List<QueryResultEntry> asList() {
        return this.entries;
    }

    @Override
    public <T> void loadIn(Collection<T> collection, Function<QueryResultEntry, T> loader) {
        for (QueryResultEntry entry : entries) {
            collection.add(loader.apply(entry));
        }
    }
}