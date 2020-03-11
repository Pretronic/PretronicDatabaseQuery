/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 15.12.19, 20:22
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

package net.pretronic.databasequery.common.query;

import net.pretronic.databasequery.api.query.Query;
import net.pretronic.databasequery.api.query.QueryGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQueryGroup implements QueryGroup {

    final List<Entry> entries;

    public AbstractQueryGroup() {
        this.entries = new ArrayList<>();
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public QueryGroup add(Query query, Object... values) {
        this.entries.add(new Entry(query, values));
        return this;
    }

    @Override
    public void clear() {
        this.entries.clear();
    }

    protected static class Entry {

        final Query query;
        final Object[] values;

        public Entry(Query query, Object[] values) {
            this.query = query;
            this.values = values;
        }
    }
}
