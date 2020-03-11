/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.03.20, 20:28
 * @website %web%
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

package net.pretronic.databasequery.api.query.type;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.field.FieldBuilder;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.ForeignKey;
import net.pretronic.databasequery.api.query.Query;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface CreateQuery extends Query {

    CreateQuery field(String field, DataType type, int size, Object defaultValue, ForeignKey foreignKey, FieldOption... options);

    CreateQuery field(String field, DataType type, int size, Object defaultValue, FieldOption... options);

    CreateQuery field(String field, DataType type, int size, FieldOption... options);

    CreateQuery field(String field, DataType type,ForeignKey foreignKey, FieldOption... options);

    CreateQuery field(String field, DataType type, FieldOption... options);

    CreateQuery field(Consumer<FieldBuilder> builder);



    CreateQuery engine(String engine);

    CreateQuery type(DatabaseCollectionType type);


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


    CreateQuery include(FindQuery query);


    DatabaseCollection create();

    CompletableFuture<DatabaseCollection> createAsync();
}