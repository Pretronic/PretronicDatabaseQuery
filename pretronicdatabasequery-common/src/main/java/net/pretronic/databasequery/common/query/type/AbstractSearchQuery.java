/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.12.19, 17:31
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
import net.pretronic.databasequery.api.query.Aggregation;
import net.pretronic.databasequery.api.query.Pattern;
import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.databasequery.api.query.type.SearchQuery;
import net.pretronic.databasequery.api.query.type.join.JoinType;
import net.pretronic.databasequery.common.query.EntryOption;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.map.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@link AbstractSearchQuery} represents the base implementation of {@link SearchQuery}. It only holds the query logic in form of entries.
 * @param <T>
 * @param <C>
 */
public abstract class AbstractSearchQuery<T extends SearchQuery<T>, C extends DatabaseCollection> extends AbstractQuery implements SearchQuery<T> {

    protected final C collection;
    protected final List<Entry> entries;

    public AbstractSearchQuery(C collection) {
        super(collection.getDatabase().getDriver());
        this.collection = collection;
        this.entries = new ArrayList<>();
    }

    public C getCollection() {
        return collection;
    }

    @Internal
    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public T where(String field, Object value) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE, field, value);
    }

    @Override
    public T where(Aggregation aggregation, String field, Object value) {
        Validate.notNull(aggregation, field);
        return addConditionEntry(ConditionEntry.Type.WHERE, field, value, aggregation);
    }

    @Override
    public T where(String field) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE, field, EntryOption.PREPARED);
    }

    @Override
    public T where(Aggregation aggregation, String field) {
        Validate.notNull(aggregation, field);
        return addConditionEntry(ConditionEntry.Type.WHERE, field, EntryOption.PREPARED, aggregation);
    }

    @Override
    public T whereNot(String field, Object value) {
        return addEntry(new OperationEntry(OperationEntry.Type.NOT, buildConditionEntry(ConditionEntry.Type.WHERE, field, value, null)));
    }

    @Override
    public T whereNot(Aggregation aggregation, String field, Object value) {
        return addEntry(new OperationEntry(OperationEntry.Type.NOT, buildConditionEntry(ConditionEntry.Type.WHERE, field, value, aggregation)));
    }

    //@Todo sql union implementation
    @Override
    public T union(SearchQuery<?> query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T whereNot(String field) {
        return addEntry(new OperationEntry(OperationEntry.Type.NOT, buildConditionEntry(ConditionEntry.Type.WHERE, field, EntryOption.PREPARED, null)));
    }

    @Override
    public T whereNot(Aggregation aggregation, String field) {
        return addEntry(new OperationEntry(OperationEntry.Type.NOT, buildConditionEntry(ConditionEntry.Type.WHERE, field, EntryOption.PREPARED, aggregation)));
    }

    @Override
    public T whereLike(String field, Pattern pattern) {
        Validate.notNull(field, pattern);
        return addConditionEntry(ConditionEntry.Type.WHERE_LIKE, field, pattern.build());
    }

    @Override
    public T whereLike(Aggregation aggregation, String field, Pattern pattern) {
        Validate.notNull(aggregation, field, pattern);
        return addConditionEntry(ConditionEntry.Type.WHERE_LIKE, field, pattern.build(), aggregation);
    }

    @Override
    public T whereLike(String field, String pattern) {
        Validate.notNull(field, pattern);
        return addConditionEntry(ConditionEntry.Type.WHERE_LIKE, field, pattern);
    }

    @Override
    public T whereLike(Aggregation aggregation, String field, String pattern) {
        Validate.notNull(aggregation, field, pattern);
        return addConditionEntry(ConditionEntry.Type.WHERE_LIKE, field, pattern, aggregation);
    }

    @Override
    public T whereLike(String field) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE_LIKE, field, EntryOption.PREPARED);
    }

    @Override
    public T whereLike(Aggregation aggregation, String field) {
        Validate.notNull(aggregation, field);
        return addConditionEntry(ConditionEntry.Type.WHERE_LIKE, field, EntryOption.PREPARED, aggregation);
    }

    @Override
    public T whereLower(String field, Object value) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE_LOWER, field, value);
    }

    @Override
    public T whereLower(Aggregation aggregation, String field, Object value) {
        Validate.notNull(aggregation, field);
        return addConditionEntry(ConditionEntry.Type.WHERE_LOWER, field, value, aggregation);
    }

    @Override
    public T whereLower(String field) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE_LOWER, field, EntryOption.PREPARED);
    }

    @Override
    public T whereLower(Aggregation aggregation, String field) {
        Validate.notNull(aggregation, field);
        return addConditionEntry(ConditionEntry.Type.WHERE_LOWER, field, EntryOption.PREPARED, aggregation);
    }

    @Override
    public T whereHigher(String field, Object value) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE_HIGHER, field, value);
    }

    @Override
    public T whereHigher(Aggregation aggregation, String field, Object value) {
        Validate.notNull(aggregation, field);
        return addConditionEntry(ConditionEntry.Type.WHERE_HIGHER, field, value, aggregation);
    }

    @Override
    public T whereHigher(String field) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE_HIGHER, field, EntryOption.PREPARED);
    }

    @Override
    public T whereHigher(Aggregation aggregation, String field) {
        Validate.notNull(aggregation, field);
        return addConditionEntry(ConditionEntry.Type.WHERE_HIGHER, field, EntryOption.PREPARED, aggregation);
    }

    @Override
    public T whereIsNull(String field) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE_NULL, field, null);
    }

    @Override
    public T whereIsEmpty(String field) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE, field, "");
    }

    @Override
    public T whereIn(String field, Object... values) {
        Validate.notNull(field, values);
        return addConditionEntry(ConditionEntry.Type.WHERE_IN, field, Arrays.asList(values));
    }

    @Override
    public T whereIn(String field) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE_IN, field, EntryOption.PREPARED);
    }

    @Override
    public T whereIn(String field, FindQuery query) {
        Validate.notNull(field, query);
        return addConditionEntry(ConditionEntry.Type.WHERE_IN, field, query);
    }

    @Override
    public T whereBetween(String field, Object value1, Object value2) {
        Validate.notNull(field, value1, value2);
        return addConditionEntry(ConditionEntry.Type.WHERE_BETWEEN, field, value1, value2);
    }

    @Override
    public T whereBetween(String field) {
        Validate.notNull(field);
        return addConditionEntry(ConditionEntry.Type.WHERE_BETWEEN, field, EntryOption.PREPARED, EntryOption.PREPARED);
    }

    @Override
    public T where(String field, FindQuery innerQuery) {
        Validate.notNull(field, innerQuery);
        return addConditionEntry(ConditionEntry.Type.WHERE, field, innerQuery);
    }

    @Override
    public T where(Aggregation aggregation, String field, FindQuery innerQuery) {
        Validate.notNull(aggregation, field, innerQuery);
        return addConditionEntry(ConditionEntry.Type.WHERE, field, innerQuery, aggregation);
    }

    @Override
    public T whereNot(String field, FindQuery innerQuery) {
        Validate.notNull(field, innerQuery);
        return addEntry(new OperationEntry(OperationEntry.Type.NOT, buildConditionEntry(ConditionEntry.Type.WHERE, field, innerQuery, null)));
    }

    @Override
    public T whereNot(Aggregation aggregation, String field, FindQuery innerQuery) {
        Validate.notNull(aggregation, field, innerQuery);
        return addEntry(new OperationEntry(OperationEntry.Type.NOT, buildConditionEntry(ConditionEntry.Type.WHERE, field, innerQuery, aggregation)));
    }

    @Override
    public T whereLike(String field, FindQuery innerQuery) {
        Validate.notNull(field, innerQuery);
        return addConditionEntry(ConditionEntry.Type.WHERE_LIKE, field, innerQuery);
    }

    @Override
    public T whereLike(Aggregation aggregation, String field, FindQuery innerQuery) {
        Validate.notNull(aggregation, field, innerQuery);
        return addConditionEntry(ConditionEntry.Type.WHERE_LIKE, field, innerQuery, aggregation);
    }

    @Override
    public T whereLower(String field, FindQuery innerQuery) {
        Validate.notNull(field, innerQuery);
        return addConditionEntry(ConditionEntry.Type.WHERE_LOWER, field, innerQuery);
    }

    @Override
    public T whereLower(Aggregation aggregation, String field, FindQuery innerQuery) {
        Validate.notNull(aggregation, field, innerQuery);
        return addConditionEntry(ConditionEntry.Type.WHERE_LOWER, field, innerQuery, aggregation);
    }

    @Override
    public T whereHigher(String field, FindQuery innerQuery) {
        Validate.notNull(field, innerQuery);
        return addConditionEntry(ConditionEntry.Type.WHERE_HIGHER, field, innerQuery);
    }

    @Override
    public T whereHigher(Aggregation aggregation, String field, FindQuery innerQuery) {
        Validate.notNull(aggregation, field, innerQuery);
        return addConditionEntry(ConditionEntry.Type.WHERE_HIGHER, field, innerQuery);
    }

    @Override
    public SearchQuery<?> newSearchQuery() {
        return collection.find();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T not(SearchConsumer searchQuery) {
        Validate.notNull(searchQuery);
        SearchQuery<?> query = this.collection.find();
        searchQuery.accept(query);
        addOperatorEntry(OperationEntry.Type.NOT, query);
        return (T) this;
    }

    @Override
    public T not(SearchQuery<?> query) {
        Validate.notNull(query);
        return addOperatorEntry(OperationEntry.Type.NOT,query);
    }

    @Override
    public T and(SearchConsumer... searchQueries) {
        return andOr(OperationEntry.Type.AND, searchQueries);
    }

    @Override
    public T and(SearchQuery<?> query) {
        Validate.notNull(query);
        return addOperatorEntry(OperationEntry.Type.AND,query);
    }

    @Override
    public T or(SearchConsumer... searchQueries) {
        return andOr(OperationEntry.Type.OR, searchQueries);
    }

    @Override
    public T or(SearchQuery<?> query) {
        Validate.notNull(query);
        return addOperatorEntry(OperationEntry.Type.OR,query);
    }

    private T andOr(OperationEntry.Type type, SearchConsumer... searchQueries) {
        Validate.notNull(type);
        SearchQuery<?>[] queries = new SearchQuery<?>[searchQueries.length];
        for (int i = 0; i < searchQueries.length; i++) {
            SearchQuery<?> searchQuery = this.collection.find();
            searchQueries[i].accept(searchQuery);
            queries[i] = searchQuery;
        }
        return addOperatorEntry(type, queries);
    }

    @Override
    public T limit(int limit, int offset) {
        Validate.isTrue(limit > 0 && offset >= 0);
        return addEntry(new LimitEntry(limit, offset));
    }

    @Override
    public T limit(int limit) {
        return limit(limit, 0);
    }

    @Override
    public T onlyOne() {
        return limit(1);
    }

    @Override
    public T index(int start, int end) {
        int limit = end-start+1;
        int offset = start-1;
        if(offset < 0) {
            offset = 0;
        }
        return limit(limit, offset);
    }

    @Override
    public T page(int page, int entriesPerPage) {
        int start = entriesPerPage * (page - 1) + 1;
        int end = page * entriesPerPage;
        return index(start, end);
    }

    @Override
    public T orderBy(String field, SearchOrder order) {
        Validate.notNull(field, order);
        return addOrderByEntry(field, order, null);
    }

    @Override
    public T orderBy(Aggregation aggregation, String field, SearchOrder order) {
        Validate.notNull(aggregation, field, order);
        return addOrderByEntry(field, order, aggregation);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T groupBy(String... fields) {
        for (String field : fields) {
            Triple<String, String, String> assignment = getAssignment(field);
            addEntry(new GroupByEntry(assignment.getFirst(), assignment.getSecond(), assignment.getThird(), null));
        }
        return (T) this;
    }

    @Override
    public T groupBy(Aggregation aggregation, String field) {
        Validate.notNull(aggregation, field);
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new GroupByEntry(assignment.getFirst(), assignment.getSecond(), assignment.getThird(), aggregation));
    }

    @Override
    public T join(DatabaseCollection collection) {
        return join(collection, JoinType.INNER);
    }

    @Override
    public T join(DatabaseCollection collection, JoinType type) {
        Validate.notNull(collection, type);
        return addEntry(new JoinEntry(collection, type));
    }

    @Override //Current, von join
    public T on(String column1, String column2) {
        return on(column1, null, column2);
    }

    @Override //Current, collection2
    public T on(String column1, DatabaseCollection collection2, String column2) {
        return on(this.collection, column1, collection2, column2);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T on(DatabaseCollection collection1, String column1, DatabaseCollection collection2, String column2) {
        AtomicBoolean wrongJoinEntry = new AtomicBoolean(false);
        JoinEntry joinEntry = (JoinEntry) Iterators.findOneReversed(this.entries, entry -> {
            if(entry instanceof JoinEntry) return true;
            else if(!(entry instanceof JoinOnEntry)) wrongJoinEntry.set(true);
            return false;
        });
        Validate.notNull(joinEntry, "Wrong search query order for join");
        Validate.isTrue(!wrongJoinEntry.get(), "Wrong search query order for join");
        joinEntry.onEntries.add(new JoinOnEntry(collection1, column1, collection2, column2));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected T addEntry(Entry entry) {
        this.entries.add(entry);
        return (T) this;
    }

    protected ConditionEntry buildConditionEntry(ConditionEntry.Type type, String assignment0, Object value1, Object extra) {
        Triple<String, String, String> assignment = getAssignment(assignment0);
        return new ConditionEntry(type, assignment.getFirst(), assignment.getSecond(), assignment.getThird(), value1, extra);
    }

    protected T addConditionEntry(ConditionEntry.Type type, String assignment0, Object value1, Object extra) {
        return addEntry(buildConditionEntry(type, assignment0, value1, extra));
    }

    protected T addConditionEntry(ConditionEntry.Type type, String field, Object value) {
        return addConditionEntry(type, field, value, null);
    }

    protected T addOperatorEntry(OperationEntry.Type type, SearchQuery<?>... queries) {
        List<Entry> entries = new ArrayList<>();
        for (SearchQuery<?> query : queries) {
            entries.addAll(((AbstractSearchQuery<?, ?>) query).entries);
        }
        return addEntry(new OperationEntry(type, entries));
    }

    protected T addOrderByEntry(String assignment0, SearchOrder order, Aggregation aggregation) {
        Triple<String, String, String> assignment = getAssignment(assignment0);
        return addEntry(new OrderByEntry(assignment.getFirst(), assignment.getSecond(), assignment.getThird(), order, aggregation));
    }

    /**
     * Creates a triple assignment for input field by splitting the input at any point.
     * It splits the input into database, database collection and field, if given.
     * For example:
     * - Input: productive.customers.name
     * Then it returns the triple in following order:
     * - productive as database
     * - customers as database collection
     * - name as field
     *
     * @param assignment0 the input to split
     * @return the result
     */
    protected Triple<String, String, String> getAssignment(String assignment0) {
        String[] assignment = assignment0.split("\\.");
        String database = null;
        String databaseCollection = null;
        String field = null;
        for (int i = assignment.length - 1; i >= 0; i--) {
            if(field == null) {
                field = assignment[i];
            } else if(databaseCollection == null) {
                databaseCollection = assignment[i];
            } else if(database == null) {
                database = assignment[i];
            }
        }
        return new Triple<>(database, databaseCollection, field);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o instanceof AbstractSearchQuery) {
            AbstractSearchQuery<?,?> other = ((AbstractSearchQuery<?,?>)o);
            if(entries.size() != other.entries.size()) return false;
            /*for (int i = 0; i < entries.size(); i++) {
                if(!entries.get(i).equals(other.entries.get(i))) return false;
            }*/
            for (Entry entry : entries) {
                if(!other.entries.contains(entry)) return false;
            }
            return true;
        }
        return false;
    }

    public static class Entry {}

    /**
     * Represents the entry for condition for all where based methods.
     */
    public static class ConditionEntry extends Entry {

        /**
         * The condition type. In this case the where type.
         */
        private final Type type;
        private final String database;
        private final String databaseCollection;
        private final String field;
        /**
         * The value of the entry. If it equals {@link EntryOption#PREPARED}, you should replace it by query execution to the prepared values in {@link net.pretronic.databasequery.api.query.Query#execute(Object...)}.
         */
        private final Object value1;
        /**
         * This value can be the aggregation or the second value for between.
         */
        private final Object extra;

        public ConditionEntry(Type type, String database, String databaseCollection, String field, Object value1, Object extra) {
            this.type = type;
            this.database = database;
            this.databaseCollection = databaseCollection;
            this.field = field;
            this.value1 = value1;
            this.extra = extra;
        }

        public Type getType() {
            return type;
        }

        public String getDatabase() {
            return database;
        }

        public String getDatabaseCollection() {
            return databaseCollection;
        }

        public String getField() {
            return field;
        }

        public Object getValue1() {
            return value1;
        }

        public Object getExtra() {
            return extra;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o instanceof ConditionEntry) {
                ConditionEntry entry = ((ConditionEntry) o);
                if(type != entry.type) return false;
                if(database != null && !database.equals(entry.database)) return false;
                if(databaseCollection != null && !databaseCollection.equals(entry.databaseCollection)) return false;
                if(!field.equals(entry.field)) return false;
                if(type != Type.WHERE_NULL && !value1.equals(entry.value1)) return false;
                if(extra != null && !extra.equals(entry.getExtra())) return false;
                return true;
            }
            return false;
        }

        public enum Type {

            WHERE,
            WHERE_LIKE,
            WHERE_LOWER,
            WHERE_HIGHER,
            WHERE_NULL,
            WHERE_IN,
            WHERE_BETWEEN
        }
    }

    /**
     * It represents the operation entry.
     */
    public static class OperationEntry extends Entry {

        /**
         * The type of operation.
         */
        private final Type type;
        /**
         * All included entries in this operation.
         */
        private final List<Entry> entries;

        public OperationEntry(Type type, Entry entry) {
            this(type, Collections.singletonList(entry));
        }

        public OperationEntry(Type type, List<Entry> entries) {
            this.type = type;
            this.entries = entries;
        }


        public Type getType() {
            return type;
        }

        public List<Entry> getEntries() {
            return entries;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o instanceof OperationEntry) {
                OperationEntry entry = ((OperationEntry) o);
                if(type != entry.type) return false;
                if(entries.size() != entry.entries.size()) return false;
                /*for (int i = 0; i < entries.size(); i++) {
                    if(!entries.get(i).equals(entry.entries.get(i))) return false;
                }*/
                for (Entry entry1 : entries) {
                    if(!entry.entries.contains(entry1)) return false;
                }
                return true;
            }
            return false;
        }

        public enum Type {

            NOT,
            AND,
            OR
        }
    }

    /**
     * This represents the join entry.
     */
    public static class JoinEntry extends Entry {

        private final DatabaseCollection collection;
        private final JoinType type;
        private final List<JoinOnEntry> onEntries;

        public JoinEntry(DatabaseCollection collection, JoinType type) {
            this.collection = collection;
            this.type = type;
            this.onEntries = new ArrayList<>();
        }

        public DatabaseCollection getCollection() {
            return collection;
        }

        public JoinType getType() {
            return type;
        }

        public List<JoinOnEntry> getOnEntries() {
            return onEntries;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o instanceof JoinEntry) {
                JoinEntry entry = ((JoinEntry) o);
                if(collection != null && !collection.equals(entry.getCollection())) return false;
                if(type != entry.getType()) return false;
                if(onEntries.size() != entry.onEntries.size()) return false;
                /*for (int i = 0; i < onEntries.size(); i++) {
                    if(!onEntries.get(i).equals(entry.onEntries.get(i))) return false;
                }*/
                for (JoinOnEntry onEntry : onEntries) {
                    if(!entry.onEntries.contains(onEntry)) return false;
                }
                return true;
            }
            return false;
        }
    }

    /**
     * It represents the join on entry. It is used in join entry.
     */
    public static class JoinOnEntry extends Entry {

        private final DatabaseCollection collection1;
        private final String column1;
        private final DatabaseCollection collection2;
        private final String column2;

        public JoinOnEntry(DatabaseCollection collection1, String column1, DatabaseCollection collection2, String column2) {
            this.collection1 = collection1;
            this.column1 = column1;
            this.collection2 = collection2;
            this.column2 = column2;
        }

        public DatabaseCollection getCollection1() {
            return collection1;
        }

        public String getColumn1() {
            return column1;
        }

        public DatabaseCollection getCollection2() {
            return collection2;
        }

        public String getColumn2() {
            return column2;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o instanceof JoinOnEntry) {
                JoinOnEntry entry = ((JoinOnEntry) o);
                if(collection1 != null && !collection1.equals(entry.collection1)) return false;
                if(collection2 != null && !collection2.equals(entry.collection2)) return false;
                return column1.equals(entry.column1) && column2.equals(entry.column2);
            }
            return false;
        }
    }

    /**
     * It represents the limit entry.
     */
    public static class LimitEntry extends Entry {

        private final int limit;
        private final int offset;

        public LimitEntry(int limit, int offset) {
            this.limit = limit;
            this.offset = offset;
        }

        public int getLimit() {
            return limit;
        }

        public int getOffset() {
            return offset;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o instanceof LimitEntry) {
                LimitEntry entry = ((LimitEntry) o);
                return limit == entry.limit && offset == entry.offset;
            }
            return false;
        }
    }

    /**
     * It represents the order by entry.
     */
    public static class OrderByEntry extends Entry {

        private final String database;
        private final String databaseCollection;
        private final String field;
        private final SearchOrder order;
        private final Aggregation aggregation;

        public OrderByEntry(String database, String databaseCollection, String field, SearchOrder order, Aggregation aggregation) {
            this.database = database;
            this.databaseCollection = databaseCollection;
            this.field = field;
            this.order = order;
            this.aggregation = aggregation;
        }

        public String getDatabase() {
            return database;
        }

        public String getDatabaseCollection() {
            return databaseCollection;
        }

        public String getField() {
            return field;
        }

        public SearchOrder getOrder() {
            return order;
        }

        public Aggregation getAggregation() {
            return aggregation;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o instanceof OrderByEntry) {
                OrderByEntry entry = ((OrderByEntry) o);
                if(database != null && !database.equals(entry.getDatabase())) return false;
                if(databaseCollection != null && !databaseCollection.equals(entry.databaseCollection)) return false;
                if(aggregation != null && !aggregation.equals(entry.aggregation)) return false;
                return field.equals(entry.getField()) && order == entry.getOrder();
            }
            return false;
        }
    }

    /**
     * It represents the group by entry.
     */
    public static class GroupByEntry extends Entry {

        private final String database;
        private final String databaseCollection;
        private final String field;
        private final Aggregation aggregation;

        public GroupByEntry(String database, String databaseCollection, String field, Aggregation aggregation) {
            this.database = database;
            this.databaseCollection = databaseCollection;
            this.field = field;
            this.aggregation = aggregation;
        }

        public String getDatabase() {
            return database;
        }

        public String getDatabaseCollection() {
            return databaseCollection;
        }

        public String getField() {
            return field;
        }

        public Aggregation getAggregation() {
            return aggregation;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o instanceof GroupByEntry) {
                GroupByEntry entry = ((GroupByEntry) o);
                if(database != null && !database.equals(entry.database)) return false;
                if(databaseCollection != null && !databaseCollection.equals(entry.databaseCollection)) return false;
                if(aggregation != null && aggregation != entry.aggregation) return false;
                return field.equals(entry.field);
            }
            return false;
        }
    }
}
