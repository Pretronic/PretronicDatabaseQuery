/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.10.19, 20:45
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

package net.prematic.databasequery.common.query;

import net.prematic.databasequery.api.collection.DatabaseCollection;
import net.prematic.databasequery.api.query.ForeignKey;
import net.prematic.databasequery.api.datatype.DataType;
import net.prematic.databasequery.api.query.type.CreateQuery;
import net.prematic.databasequery.api.collection.field.FieldOption;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCreateQuery implements CreateQuery {

    private final String collectionName;
    private final List<Entry> entries;

    public AbstractCreateQuery(String collectionName) {
        this.collectionName = collectionName;
        this.entries = new ArrayList<>();
    }

    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public CreateQuery attribute(String field, DataType dataType, int fieldSize, Object defaultValue, ForeignKey foreignKey, FieldOption... createOptions) {
        this.entries.add(new Entry(field, dataType, fieldSize, defaultValue, foreignKey, createOptions));
        return this;
    }

    @Override
    public CreateQuery engine(String engine) {
        this.entries.add(new Entry(engine));
        return this;
    }

    @Override
    public CreateQuery collectionType(DatabaseCollection.Type collectionType) {
        this.entries.add(new Entry(collectionType));
        return this;
    }

    protected class Entry {

        private final String field, engine;
        private final DataType dataType;
        private final int fieldSize;
        private final Object defaultValue;
        private final ForeignKey foreignKey;
        private final FieldOption[] createOptions;
        private final DatabaseCollection.Type collectionType;

        private Entry(String field, String engine, DataType dataType, int fieldSize, Object defaultValue, ForeignKey foreignKey, FieldOption[] createOptions, DatabaseCollection.Type collectionType) {
            this.field = field;
            this.engine = engine;
            this.dataType = dataType;
            this.fieldSize = fieldSize;
            this.defaultValue = defaultValue;
            this.foreignKey = foreignKey;
            this.createOptions = createOptions;
            this.collectionType = collectionType;
        }

        protected Entry(String field, DataType dataType, int fieldSize, Object defaultValue, ForeignKey foreignKey, FieldOption[] createOptions) {
            this(field, null, dataType, fieldSize, defaultValue, foreignKey, createOptions, null);
        }

        protected Entry(String engine) {
            this(null, engine, null, -1, null, null, null, null);
        }

        protected Entry(DatabaseCollection.Type collectionType) {
            this(null, null, null, -1, null, null, null, collectionType);
        }

        public String getField() {
            return field;
        }

        public String getEngine() {
            return engine;
        }

        public DataType getDataType() {
            return dataType;
        }

        public int getFieldSize() {
            return fieldSize;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public ForeignKey getForeignKey() {
            return foreignKey;
        }

        public FieldOption[] getCreateOptions() {
            return createOptions;
        }

        public DatabaseCollection.Type getCollectionType() {
            return collectionType;
        }
    }
}