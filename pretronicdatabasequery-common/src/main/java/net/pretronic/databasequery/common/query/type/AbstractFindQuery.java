/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 15.12.19, 20:15
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
import net.pretronic.databasequery.api.query.function.QueryFunction;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.map.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link AbstractFindQuery} represents the base implementation of{@link FindQuery}. It only holds the query logic in form of entries.
 * @param <C>
 */
public abstract class AbstractFindQuery<C extends DatabaseCollection> extends AbstractSearchQuery<FindQuery, C> implements FindQuery {

    protected final List<Entry> getEntries;

    public AbstractFindQuery(C collection) {
        super(collection);
        this.getEntries = new ArrayList<>();
    }

    @Internal
    public List<Entry> getGetEntries() {
        return getEntries;
    }

    @Override
    public FindQuery get(String... fields) {
        for (String field : fields) {
            Triple<String, String, String> assignment = getAssignment(field);
            this.getEntries.add(new GetEntry(assignment.getFirst(), assignment.getSecond(), assignment.getThird(), null));
        }
        return this;
    }

    @Override
    public FindQuery get(String collection, String field) {
        Validate.notNull(collection, field);
        this.getEntries.add(new GetEntry(null, collection, field, null));
        return this;
    }

    @Override
    public FindQuery getAs(String field, String alias) {
        Validate.notNull(field, alias);
        Triple<String, String, String> assignment = getAssignment(field);
        this.getEntries.add(new GetEntry(assignment.getFirst(), assignment.getSecond(), assignment.getThird(), null, alias));
        return this;
    }

    @Override
    public FindQuery getAs(String collection, String field, String alias) {
        Validate.notNull(collection);
        Validate.notNull(field);
        Validate.notNull(alias);
        this.getEntries.add(new GetEntry(null, collection, field, null, alias));
        return this;
    }

    @Override
    public FindQuery get(Aggregation aggregation, String field) {
        Validate.notNull(aggregation, field);
        Triple<String, String, String> assignment = getAssignment(field);
        this.getEntries.add(new GetEntry(assignment.getFirst(), assignment.getSecond(), assignment.getThird(), aggregation));
        return this;
    }

    @Override
    public FindQuery get(Aggregation aggregation, String collection, String field) {
        Validate.notNull(aggregation, collection, field);
        this.getEntries.add(new GetEntry(null, collection, field, aggregation));
        return this;
    }

    @Override
    public FindQuery getAs(Aggregation aggregation, String field, String alias) {
        Validate.notNull(aggregation);
        Validate.notNull(field);
        Validate.notNull(alias);
        Triple<String, String, String> assignment = getAssignment(field);
        this.getEntries.add(new GetEntry(assignment.getFirst(), assignment.getSecond(), assignment.getThird(), aggregation, alias));
        return this;
    }

    @Override
    public FindQuery getAs(Aggregation aggregation, String collection, String field, String alias) {
        Validate.notNull(aggregation);
        Validate.notNull(collection);
        Validate.notNull(field);
        Validate.notNull(alias);
        this.getEntries.add(new GetEntry(null, collection, field, aggregation, alias));
        return this;
    }

    @Override
    public FindQuery getFunction(QueryFunction function, String getAliasName) {
        Validate.notNull(function, (Object) getAliasName);
        this.getEntries.add(new FunctionEntry(function, getAliasName));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o instanceof AbstractFindQuery) {
            AbstractFindQuery<?> other = ((AbstractFindQuery<?>) o);
            if(getEntries.size() != other.getEntries.size()) return false;
            for (int i = 0; i < getEntries.size(); i++) {
                if(!getEntries.get(i).equals(other.getEntries.get(i))) return false;
            }
            return super.equals(o);
        }
        return false;
    }

    public static class GetEntry extends Entry {

        private final String database;
        private final String databaseCollection;
        private final String field;
        private final Aggregation aggregation;
        private final String alias;

        public GetEntry(String database, String databaseCollection, String field, Aggregation aggregation) {
            this(database, databaseCollection, field, aggregation, null);
        }

        public GetEntry(String database, String databaseCollection, String field, Aggregation aggregation, String alias) {
            this.database = database;
            this.databaseCollection = databaseCollection;
            this.field = field;
            this.aggregation = aggregation;
            this.alias = alias;
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

        public String getAlias() {
            return alias;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o instanceof GetEntry) {
                GetEntry entry = ((GetEntry) o);
                if(database != null && !database.equals(entry.database)) return false;
                if(databaseCollection != null && !databaseCollection.equals(entry.databaseCollection)) return false;
                if(aggregation != null && aggregation != entry.aggregation) return false;
                if(alias != null && !alias.equals(entry.alias)) return false;
                return field.equals(entry.field);
            }
            return false;
        }
    }

    public static class FunctionEntry extends Entry {

        private final QueryFunction function;
        private final String getAliasName;

        public FunctionEntry(QueryFunction function, String getAliasName) {
            this.function = function;
            this.getAliasName = getAliasName;
        }

        public QueryFunction getFunction() {
            return function;
        }

        public String getGetAliasName() {
            return getAliasName;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o instanceof FunctionEntry) {
                FunctionEntry entry = ((FunctionEntry) o);
                return function.equals(entry.function) && getAliasName.equals(entry.getAliasName);
            }
            return false;
        }
    }
}
