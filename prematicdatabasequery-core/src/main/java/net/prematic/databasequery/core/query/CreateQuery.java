/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 03.05.19, 23:56
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

package net.prematic.databasequery.core.query;

import net.prematic.databasequery.core.DatabaseCollection;
import net.prematic.databasequery.core.ForeignKey;
import net.prematic.databasequery.core.datatype.DataType;
import net.prematic.databasequery.core.query.option.CreateOption;

/**
 * Query order:
 * - method {@link #engine(String)} at the end
 */
public interface CreateQuery extends Query {

    CreateQuery attribute(String field, DataType dataType, int fieldSize, Object defaultValue, ForeignKey foreignKey, CreateOption... createOptions);

    default CreateQuery attribute(String field, DataType dataType, CreateOption... createOptions) {
        return attribute(field, dataType, -1, null, null, createOptions);
    }

    default CreateQuery attribute(String field, DataType dataType, int fieldSize, CreateOption... createOptions) {
        return attribute(field, dataType, fieldSize, null, null, createOptions);
    }

    default CreateQuery attribute(String field, DataType dataType, int fieldSize, Object defaultValue, CreateOption... createOptions) {
        return attribute(field, dataType, fieldSize, defaultValue, null, createOptions);
    }

    CreateQuery engine(String engine);

    CreateQuery collectionType(DatabaseCollection.Type collectionType);

    CreateQuery foreignKey(String field, ForeignKey foreignKey);

    CreateQuery collectionName(String name);

    default DatabaseCollection create(Object... values) {
        return (DatabaseCollection) execute(values).first().getObject("databaseCollection");
    }
}