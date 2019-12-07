/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.10.19, 20:45
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

package net.prematic.databasequery.common.query.result;

import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.databasequery.api.query.result.QueryResultEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class SimpleQueryResult implements QueryResult {

    public static final QueryResult EMPTY = new SimpleQueryResult(new ArrayList<>());

    private final List<QueryResultEntry> entries;

    public SimpleQueryResult(List<QueryResultEntry> entries) {
        this.entries = entries;
    }

    @Override
    public QueryResultEntry first() {
        return entries.get(0);
    }

    @Override
    public QueryResultEntry firstOrNull() {
        return isEmpty() ? null : first();
    }

    @Override
    public QueryResultEntry last() {
        return entries.get(entries.size()-1);
    }

    @Override
    public QueryResultEntry get(int index) {
        return entries.get(index);
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
