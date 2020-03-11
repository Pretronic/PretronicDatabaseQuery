/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.12.19, 18:55
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
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.databasequery.api.query.type.InsertQuery;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.databasequery.common.query.EntryOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractInsertQuery<C extends DatabaseCollection> extends AbstractQuery implements InsertQuery {

    protected final C collection;
    protected final List<Entry> entries;
    protected final List<FindQuery> queries;

    public AbstractInsertQuery(C collection) {
        super(collection.getDatabase().getDriver());
        this.collection = collection;
        this.entries = new ArrayList<>();
        this.queries = new ArrayList<>();
    }

    @Override
    public InsertQuery set(String field, Object... values) {
        return set(field, Arrays.asList(values));
    }

    @Override
    public InsertQuery set(String field, List<Object> values) {
        Entry entry = getEntry(field);
        if(entry == null){
            entry = new Entry(field, values);
            this.entries.add(entry);
        }else entry.values.addAll(values);
        return this;
    }

    @Override
    public InsertQuery set(String field) {
        return set(field, EntryOption.PREPARED);
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
    public QueryResult executeAndGetGeneratedKeys(String... keyColumns) {
        return executeAndGetGeneratedKeys(keyColumns, EMPTY_OBJECT_ARRAY);
    }

    @Override
    public int executeAndGetGeneratedKeyAsInt(String keyColumn, Object... values) {
        QueryResultEntry resultEntry = executeAndGetGeneratedKeys(keyColumn).firstOrNull();
        Validate.notNull(resultEntry);
        return resultEntry.getInt(keyColumn);
    }

    @Override
    public long executeAndGetGeneratedKeyAsLong(String keyColumn, Object... values) {
        QueryResultEntry resultEntry = executeAndGetGeneratedKeys(keyColumn).firstOrNull();
        Validate.notNull(resultEntry);
        return resultEntry.getLong(keyColumn);
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

    @Override
    public QueryResult execute(Object... values) {
        return executeAndGetGeneratedKeys(EMPTY_STRING_ARRAY, values);
    }



    @Internal
    public Entry getEntry(String name){
        return Iterators.findOne(this.entries, entry -> entry.field.equalsIgnoreCase(name));
    }

    @Internal
    public static class Entry {

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
