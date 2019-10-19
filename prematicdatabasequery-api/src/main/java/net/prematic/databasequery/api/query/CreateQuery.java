/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.10.19, 20:44
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

package net.prematic.databasequery.api.query;

import net.prematic.databasequery.api.DatabaseCollection;
import net.prematic.databasequery.api.ForeignKey;
import net.prematic.databasequery.api.datatype.DataType;
import net.prematic.databasequery.api.query.option.CreateOption;

import java.util.concurrent.CompletableFuture;

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

    default CreateQuery foreignKey(String field,DatabaseCollection collection, String otherField, ForeignKey.Option deleteOption, ForeignKey.Option updateOption){
        return foreignKey(field,ForeignKey.of(collection,otherField,deleteOption,updateOption));
    }

    default CreateQuery foreignKey(String field,DatabaseCollection collection, String otherField, ForeignKey.Option option){
        return foreignKey(field,ForeignKey.of(collection,otherField,option));
    }


    default CreateQuery foreignKey(String field, DatabaseCollection collection, String otherField){
        return foreignKey(field,ForeignKey.of(collection,otherField));
    }

    CreateQuery collectionName(String name);

    default DatabaseCollection create(Object... values) {
        return (DatabaseCollection) execute(values).first().getObject("databaseCollection");
    }

    CompletableFuture<DatabaseCollection> createAsync(Object... values);
}