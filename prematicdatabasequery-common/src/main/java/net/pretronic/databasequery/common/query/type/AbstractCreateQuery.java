/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.12.19, 21:26
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

import net.prematic.databasequery.api.Database;
import net.prematic.databasequery.api.collection.DatabaseCollectionType;
import net.prematic.databasequery.api.collection.field.FieldBuilder;
import net.prematic.databasequery.api.collection.field.FieldOption;
import net.prematic.databasequery.api.datatype.DataType;
import net.prematic.databasequery.api.query.ForeignKey;
import net.prematic.databasequery.api.query.type.CreateQuery;
import net.prematic.databasequery.api.query.type.FindQuery;
import net.prematic.libraries.utility.Validate;
import net.pretronic.databasequery.common.collection.field.DefaultFieldBuilder;
import net.pretronic.databasequery.common.query.EntryOption;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractCreateQuery extends AbstractQuery implements CreateQuery {

    protected final Database database;
    protected final List<Entry> entries;
    protected final List<ForeignKeyEntry> foreignKeyEntries;
    protected String engine;
    protected DatabaseCollectionType type;
    protected FindQuery includingQuery;

    public AbstractCreateQuery(Database database) {
        super(database.getDriver());
        this.database = database;
        this.entries = new ArrayList<>();
        this.foreignKeyEntries = new ArrayList<>();
    }

    @Override
    public CreateQuery field(String field, DataType type, int size, Object defaultValue, ForeignKey foreignKey, FieldOption... options) {
        Validate.notNull(field, type, size, defaultValue, foreignKey);
        this.entries.add(new Entry(field, type, size, defaultValue, options));
        this.foreignKeyEntries.add(new ForeignKeyEntry(field, foreignKey));
        return this;
    }

    @Override
    public CreateQuery field(String field, DataType type, int size, Object defaultValue, FieldOption... options) {
        Validate.notNull(field, type, size);
        this.entries.add(new Entry(field, type, size, defaultValue, options));
        return this;
    }

    @Override
    public CreateQuery field(String field, DataType type, int size, FieldOption... options) {
        Validate.notNull(field, type, size);
        this.entries.add(new Entry(field, type, size, EntryOption.NOT_DEFINED, options));
        return this;
    }

    @Override
    public CreateQuery field(String field, DataType type, ForeignKey foreignKey, FieldOption... options) {
        Validate.notNull(field, type, foreignKey);
        this.entries.add(new Entry(field, type, 0, EntryOption.NOT_DEFINED, options));
        return this;
    }

    @Override
    public CreateQuery field(String field, DataType type, FieldOption... options) {
        Validate.notNull(field, type);
        this.entries.add(new Entry(field, type, 0, EntryOption.NOT_DEFINED, options));
        return this;
    }

    @Override
    public CreateQuery field(Consumer<FieldBuilder> builder) {
        DefaultFieldBuilder fieldBuilder = new DefaultFieldBuilder();
        builder.accept(fieldBuilder);
        Validate.notNull(fieldBuilder.getName(), fieldBuilder.getType());
        this.entries.add(new Entry(fieldBuilder.getName(), fieldBuilder.getType(), fieldBuilder.getSize(), fieldBuilder.getDefaultValue(), fieldBuilder.getOptions()));
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
        this.foreignKeyEntries.add(new ForeignKeyEntry(field, foreignKey));
        return this;
    }

    @Override
    public CreateQuery include(FindQuery query) {
        if(this.includingQuery != null) throw new IllegalArgumentException("Including query already set");
        this.includingQuery = query;
        return this;
    }

    protected static class Entry {

        private final String field;
        private final DataType type;
        private final int size;
        private final Object defaultValue;
        private final FieldOption[] options;

        Entry(String field, DataType type, int size, Object defaultValue, FieldOption[] options) {
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

    protected static class ForeignKeyEntry {

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