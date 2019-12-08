/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.12.19, 16:26
 * @website %web%
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

package net.prematic.databasequery.api.collection;

import net.prematic.databasequery.api.Database;
import net.prematic.databasequery.api.collection.field.CollectionField;
import net.prematic.databasequery.api.query.QueryGroup;
import net.prematic.databasequery.api.query.QueryTransaction;
import net.prematic.databasequery.api.query.type.*;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface DatabaseCollection {

    String getName();

    Database getDatabase();

    Type getType();

    long getSize();

    CompletableFuture<Long> getSizeAsync();


    InsertQuery insert();

    FindQuery find();

    UpdateQuery update();

    ReplaceQuery replace();

    DeleteQuery delete();


    void drop();

    CompletableFuture<Void> dropAsync();

    void clear();

    CompletableFuture<Void> clearAsync();


    QueryTransaction transact();

    QueryGroup group();


    Collection<CollectionField> getFields();

    CompletableFuture<Collection<CollectionField>> getFieldsAsync();

    CollectionField getField(String name);

    CompletableFuture<CollectionField> getFieldAsync(String name);

    boolean hasField(String name);

    CompletableFuture<Boolean> hasFieldAsync(String name);

    CollectionField addField(String name);


    AliasDatabaseCollection as(String alias);


    enum Type {

        NORMAL,
        EDGE,
        VIEW
    }
}