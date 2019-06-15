/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.05.19, 21:51
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

package net.prematic.databasequery.core.impl.query;

import net.prematic.databasequery.core.query.InsertQuery;
import net.prematic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractInsertQuery implements InsertQuery {

    private final List<Entry> entries;

    public AbstractInsertQuery() {
        this.entries = new ArrayList<>();
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public Entry getEntry(String name){
        return Iterators.findOne(this.entries, entry -> entry.field.equalsIgnoreCase(name));
    }

    public InsertQuery set(String field, Object... values) {
        Entry entry = getEntry(field);
        if(entry == null){
            entry = new Entry(field, Arrays.asList(values));
            this.entries.add(entry);
        }else entry.values.addAll(Arrays.asList(values));
        return this;
    }

    public InsertQuery attribute(String... fields) {
        for(String field : fields){
            if(getEntry(field) == null) this.entries.add(new Entry(field));
        }
        return this;
    }

    public InsertQuery value(Object... values) {
        if(values.length != entries.size()) throw new IllegalArgumentException("Invalid values length.");
        for(int i = 0;i<values.length;i++) entries.get(i).values.add(values[i]);
        return this;
    }

    protected class Entry {

        private final String field;
        private final List<Object> values;

        Entry(String field) {
            this(field,new ArrayList<>());
        }

        Entry(String field, List<Object> values) {
            this.field = field;
            this.values = values;
        }

        public String getField() {
            return field;
        }

        public List<Object> getValues() {
            return values;
        }
    }
}