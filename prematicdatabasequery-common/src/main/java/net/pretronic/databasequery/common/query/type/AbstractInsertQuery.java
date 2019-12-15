/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.12.19, 18:55
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
import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.databasequery.api.query.type.FindQuery;
import net.prematic.databasequery.api.query.type.InsertQuery;
import net.prematic.libraries.utility.Iterators;
import net.prematic.libraries.utility.annonations.Internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractInsertQuery extends AbstractQuery implements InsertQuery {

    private final DatabaseCollection collection;
    private final List<Entry> entries;
    private final List<FindQuery> queries;

    protected AbstractInsertQuery(DatabaseCollection collection) {
        super(collection.getDatabase().getDriver());
        this.collection = collection;
        this.entries = new ArrayList<>();
        this.queries = new ArrayList<>();
    }

    @Override
    public InsertQuery set(String field, Object... values) {
        Entry entry = getEntry(field);
        if(entry == null){
            entry = new Entry(field, Arrays.asList(values));
            this.entries.add(entry);
        }else entry.values.addAll(Arrays.asList(values));
        return this;
    }

    @Override
    public InsertQuery fields(String... fields) {
        for(String field : fields){
            if(getEntry(field) == null) this.entries.add(new Entry(field));
        }
        return this;
    }

    @Override
    public InsertQuery values(Object... values) {
        if(values.length != entries.size()) throw new IllegalArgumentException("Invalid values length.");
        for(int i = 0;i<values.length;i++) entries.get(i).values.add(values[i]);
        return this;
    }

    @Override
    public InsertQuery query(FindQuery query) {
        this.queries.add(query);
        return this;
    }

    @Override
    public CompletableFuture<QueryResult> executeAsyncAndGetGeneratedKeys(String[] keyColumns, Object... values) {
        CompletableFuture<QueryResult> future = new CompletableFuture<>();
        this.collection.getDatabase().getDriver().getExecutorService().execute(()->
                future.complete(executeAndGetGeneratedKeys(keyColumns, values)));
        return future;
    }

    @Override
    public CompletableFuture<QueryResult> executeAsyncAndGetGeneratedKeys(String... keyColumns) {
        CompletableFuture<QueryResult> future = new CompletableFuture<>();
        this.collection.getDatabase().getDriver().getExecutorService().execute(()->
                future.complete(executeAndGetGeneratedKeys(keyColumns)));
        return future;
    }

    @Override
    public CompletableFuture<Integer> executeAsyncAndGetGeneratedKeyAsInt(String keyColumn, Object... values) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        this.collection.getDatabase().getDriver().getExecutorService().execute(()->
                future.complete(executeAndGetGeneratedKeyAsInt(keyColumn, values)));
        return future;
    }

    @Override
    public CompletableFuture<Long> executeAsyncAndGetGeneratedKeyAsLong(String keyColumn, Object... values) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        this.collection.getDatabase().getDriver().getExecutorService().execute(()->
                future.complete(executeAndGetGeneratedKeyAsLong(keyColumn, values)));
        return future;
    }

    @Internal
    public List<Entry> getEntries() {
        return this.entries;
    }

    @Internal
    public List<FindQuery> getQueries() {
        return this.queries;
    }

    @Internal
    public Entry getEntry(String name){
        return Iterators.findOne(this.entries, entry -> entry.field.equalsIgnoreCase(name));
    }

    @Internal
    protected static class Entry {
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
