/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.05.19, 21:48
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

package net.prematic.databasequery.core.impl;

import net.prematic.databasequery.core.QueryOperator;

import java.util.*;
import java.util.function.Predicate;

public class QueryEntry {

    private boolean hasParentEntry;
    private final QueryOperator operator;
    private final List<QueryEntry> entries;
    private final Map<String, Object> data;

    public QueryEntry(QueryOperator operator, List<QueryEntry> entries) {
        this.operator = operator;
        this.entries = entries;
        this.data = new HashMap<>();
    }

    public QueryEntry(QueryOperator operator) {
        this.operator = operator;
        this.entries = new ArrayList<>();
        this.data = new HashMap<>();
    }

    public boolean hasParentEntry() {
        return hasParentEntry;
    }

    public QueryOperator getOperator() {
        return operator;
    }

    public List<QueryEntry> getEntries() {
        return entries;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public boolean containsData(String key) {
        return getData().containsKey(key);
    }

    public Object getData(String key) {
        return getData().get(key);
    }

    public boolean hasData(String... keys) {
        boolean contains = false;
        for (String key : keys) {
            if(getData().containsKey(key)) contains = true;
            else return false;
        }
        return contains;
    }

    public QueryEntry addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public QueryEntry addDataIfNotNull(String key, Object value) {
        if(value != null) this.data.put(key, value);
        return this;
    }

    public QueryEntry addData(String key, Object value, Predicate<Object> predicate) {
        if(predicate.test(value)) addData(key, value);
        return this;
    }

    public QueryEntry setHasParentEntry(boolean hasParentEntry) {
        this.hasParentEntry = hasParentEntry;
        return this;
    }

    public List<Object> getDataDeep(String... keys) {
        List<Object> objects = new LinkedList<>();
        for (String key : keys) {
            if(hasData(key)) objects.add(getData(key));
        }
        for (QueryEntry entry : this.entries) {
            objects.addAll(entry.getDataDeep(keys));
        }
        return objects;
    }
}