/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.12.19, 20:44
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

package net.pretronic.databasequery.common.query.result;

import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.map.caseintensive.CaseIntensiveHashMap;
import net.pretronic.libraries.utility.map.caseintensive.CaseIntensiveMap;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The {@link DefaultQueryResult} is the default implementation of {@link QueryResult}. It can be used for returning the result in {@link net.pretronic.databasequery.api.query.Query#execute(Object...)}.
 */
public class DefaultQueryResult implements QueryResult {

    public static final QueryResult EMPTY = new DefaultQueryResult(new ArrayList<>());

    private final List<QueryResultEntry> entries;
    private final CaseIntensiveMap<Object> properties;

    public DefaultQueryResult() {
        this(new ArrayList<>());
    }

    public DefaultQueryResult(List<QueryResultEntry> entries) {
        this.entries = entries;
        this.properties = new CaseIntensiveHashMap<>();
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
    public int size() {
        return this.entries.size();
    }

    @Override
    public CaseIntensiveMap<Object> getProperties() {
        return this.properties;
    }

    @Override
    public Stream<QueryResultEntry> stream() {
        return this.entries.stream();
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

    /**
     * Adds an entry to the entries list.
     * @param entry to add not null
     * @return the current result instance
     */
    @Internal
    public DefaultQueryResult addEntry(QueryResultEntry entry) {
        Validate.notNull(entry);
        this.entries.add(entry);
        return this;
    }

    @Internal
    public DefaultQueryResult addProperty(String key, Object value) {
        Validate.notNull(key);
        this.properties.put(key, value);
        return this;
    }
}
