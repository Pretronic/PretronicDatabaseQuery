/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 16:32
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

package net.pretronic.databasequery.common.collection;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.field.CollectionField;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractDatabaseCollection<T extends Database> implements DatabaseCollection {

    private final String name;
    private final T database;
    private final DatabaseCollectionType type;

    public AbstractDatabaseCollection(String name, T database, DatabaseCollectionType type) {
        this.name = name;
        this.database = database;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T getDatabase() {
        return this.database;
    }

    @Override
    public DatabaseCollectionType getType() {
        return this.type;
    }

    @Override
    public CompletableFuture<Long> getSizeAsync() {
        CompletableFuture<Long> future = new CompletableFuture<>();
        this.database.getDriver().getExecutorService().execute(()-> future.complete(getSize()));
        return future;
    }

    @Override
    public CompletableFuture<Void> dropAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        this.database.getDriver().getExecutorService().execute(()-> {
            drop();
            future.complete(null);
        });
        return future;
    }

    @Override
    public CompletableFuture<Void> clearAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        this.database.getDriver().getExecutorService().execute(()-> {
            clear();
            future.complete(null);
        });
        return future;
    }

    @Override
    public CompletableFuture<Collection<CollectionField>> getFieldsAsync() {
        CompletableFuture<Collection<CollectionField>> future = new CompletableFuture<>();
        this.database.getDriver().getExecutorService().execute(()-> future.complete(getFields()));
        return future;
    }

    @Override
    public CompletableFuture<CollectionField> getFieldAsync(String name) {
        CompletableFuture<CollectionField> future = new CompletableFuture<>();
        this.database.getDriver().getExecutorService().execute(()-> future.complete(getField(name)));
        return future;
    }

    @Override
    public CompletableFuture<Boolean> hasFieldAsync(String name) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        this.database.getDriver().getExecutorService().execute(()-> future.complete(hasField(name)));
        return future;
    }
}
