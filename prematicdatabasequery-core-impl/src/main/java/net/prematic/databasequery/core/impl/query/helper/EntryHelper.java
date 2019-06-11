/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 24.05.19, 21:40
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

package net.prematic.databasequery.core.impl.query.helper;

import net.prematic.databasequery.core.impl.query.QueryEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EntryHelper<T> {

    private final List<QueryEntry> entries;

    public EntryHelper() {
        this.entries = new ArrayList<>();
    }

    public List<QueryEntry> getEntries() {
        return entries;
    }

    public T addEntry(QueryEntry entry) {
        this.entries.add(entry);
        return (T) this;
    }

    public List<Object> getDataDeep(String... keys) {
        List<Object> objects = new LinkedList<>();
        for (QueryEntry entry : this.entries) objects.addAll(entry.getDataDeep(keys));
        return objects;
    }
}