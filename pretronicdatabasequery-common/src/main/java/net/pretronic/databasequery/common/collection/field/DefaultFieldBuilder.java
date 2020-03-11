/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.12.19, 21:44
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

package net.pretronic.databasequery.common.collection.field;

import net.pretronic.databasequery.api.collection.field.FieldBuilder;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.ForeignKey;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.databasequery.common.query.EntryOption;

public class DefaultFieldBuilder implements FieldBuilder {

    private String name;
    private DataType type;
    private int size;
    private Object defaultValue;
    private ForeignKey foreignKey;
    private FieldOption[] options;

    public DefaultFieldBuilder() {
        this.defaultValue = EntryOption.NOT_DEFINED;
    }

    @Override
    public FieldBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public FieldBuilder type(DataType type) {
        this.type = type;
        return this;
    }

    @Override
    public FieldBuilder size(int size) {
        this.size = size;
        return this;
    }

    @Override
    public FieldBuilder defaultValue(Object value) {
        this.defaultValue = value;
        return this;
    }

    @Override
    public FieldBuilder foreignKey(ForeignKey foreignKey) {
        this.foreignKey = foreignKey;
        return this;
    }

    @Override
    public FieldBuilder options(FieldOption... options) {
        this.options = options;
        return this;
    }

    @Internal
    public String getName() {
        return name;
    }

    @Internal
    public DataType getType() {
        return type;
    }

    @Internal
    public int getSize() {
        return size;
    }

    @Internal
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Internal
    public ForeignKey getForeignKey() {
        return foreignKey;
    }

    @Internal
    public FieldOption[] getOptions() {
        return options;
    }
}
