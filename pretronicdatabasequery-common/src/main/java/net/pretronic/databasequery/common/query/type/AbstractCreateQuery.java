/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.12.19, 21:26
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

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.field.FieldBuilder;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.ForeignKey;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.type.CreateQuery;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.databasequery.common.collection.field.DefaultFieldBuilder;
import net.pretronic.databasequery.common.query.EntryOption;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AbstractCreateQuery<T extends Database> extends AbstractQuery implements CreateQuery {

    protected final String name;
    protected final T database;
    protected final List<Entry> entries;
    protected String engine;
    protected DatabaseCollectionType type;
    protected FindQuery includingQuery;

    public AbstractCreateQuery(String name, T database) {
        super(database.getDriver());
        this.name = name;
        this.database = database;
        this.entries = new ArrayList<>();
    }

    @Override
    public CreateQuery field(String field, DataType type, int size, Object defaultValue, ForeignKey foreignKey, FieldOption... options) {
        Validate.notNull(field, type, size, defaultValue, foreignKey);
        this.entries.add(new CreateEntry(field, type, size, defaultValue, options));
        this.entries.add(new ForeignKeyEntry(field, foreignKey));
        return this;
    }

    @Override
    public CreateQuery field(String field, DataType type, int size, Object defaultValue, FieldOption... options) {
        Validate.notNull(field, type, size);
        this.entries.add(new CreateEntry(field, type, size, defaultValue, options));
        return this;
    }

    @Override
    public CreateQuery field(String field, DataType type, int size, FieldOption... options) {
        Validate.notNull(field, type, size);
        this.entries.add(new CreateEntry(field, type, size, EntryOption.NOT_DEFINED, options));
        return this;
    }

    @Override
    public CreateQuery field(String field, DataType type, ForeignKey foreignKey, FieldOption... options) {
        Validate.notNull(field, type, foreignKey);
        this.entries.add(new CreateEntry(field, type, 0, EntryOption.NOT_DEFINED, options));
        this.entries.add(new ForeignKeyEntry(field, foreignKey));
        return this;
    }

    @Override
    public CreateQuery field(String field, DataType type, FieldOption... options) {
        Validate.notNull(field, type);
        this.entries.add(new CreateEntry(field, type, 0, EntryOption.NOT_DEFINED, options));
        return this;
    }

    @Override
    public CreateQuery field(Consumer<FieldBuilder> builder) {
        DefaultFieldBuilder fieldBuilder = new DefaultFieldBuilder();
        builder.accept(fieldBuilder);
        Validate.notNull(fieldBuilder.getName(), fieldBuilder.getType());
        this.entries.add(new CreateEntry(fieldBuilder.getName(), fieldBuilder.getType(), fieldBuilder.getSize(), fieldBuilder.getDefaultValue(), fieldBuilder.getOptions()));
        return this;
    }

    @Override
    public CreateQuery engine(String engine) {
        this.engine = engine;
        return this;
    }

    @Override
    public CreateQuery type(DatabaseCollectionType type) {
        this.type = type;
        return this;
    }

    @Override
    public CreateQuery foreignKey(String field, ForeignKey foreignKey) {
        this.entries.add(new ForeignKeyEntry(field, foreignKey));
        return this;
    }

    @Override
    public CreateQuery include(FindQuery query) {
        if(this.includingQuery != null) throw new IllegalArgumentException("Including query already set");
        this.includingQuery = query;
        return this;
    }

    @Override
    public CompletableFuture<DatabaseCollection> createAsync() {
        CompletableFuture<DatabaseCollection> future = new CompletableFuture<>();
        future.complete(create());
        return future;
    }

    @Override
    public QueryResult execute(Object... values) {
        return new DefaultQueryResult().addEntry(new DefaultQueryResultEntry(this.database.getDriver()).addEntry("collection", create()));
    }

    public static class Entry {}

    public static class CreateEntry extends Entry {

        private final String field;
        private final DataType type;
        private final int size;
        private final Object defaultValue;
        private final FieldOption[] options;

        CreateEntry(String field, DataType type, int size, Object defaultValue, FieldOption[] options) {
            this.field = field;
            this.type = type;
            this.size = size;
            this.defaultValue = defaultValue;
            this.options = options;
        }

        public String getField() {
            return field;
        }

        public DataType getDataType() {
            return type;
        }

        public int getSize() {
            return size;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public FieldOption[] getFieldOptions() {
            return options;
        }
    }

    public static class ForeignKeyEntry extends Entry {

        private final String field;
        private final ForeignKey foreignKey;

        ForeignKeyEntry(String field, ForeignKey foreignKey) {
            this.field = field;
            this.foreignKey = foreignKey;
        }

        public String getField() {
            return field;
        }

        public ForeignKey getForeignKey() {
            return foreignKey;
        }
    }
}
