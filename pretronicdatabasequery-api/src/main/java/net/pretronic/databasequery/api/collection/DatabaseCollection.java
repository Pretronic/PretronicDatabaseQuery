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

package net.pretronic.databasequery.api.collection;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.field.CollectionField;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.QueryTransaction;
import net.pretronic.databasequery.api.query.type.*;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link DatabaseCollection}
 */
public interface DatabaseCollection {

    /**
     * Returns the name of this database collection.
     *
     * @return name
     */
    String getName();

    /**
     * Returns the {@link Database} on which this {@link DatabaseCollection} is hold.
     *
     * @return database
     */
    Database getDatabase();

    /**
     * Returns the {@link DatabaseCollectionType} of this database collection type.
     * The type may be incorrect when retrieving and not creating this {@link DatabaseCollection}
     * and then it's {@link DatabaseCollectionType#NORMAL}.
     *
     * @return type of collection
     */
    DatabaseCollectionType getType();

    /**
     * Returns the size of data in this {@link DatabaseCollection}.
     *
     * @return size of database collection
     */
    long getSize();

    /**
     * Returns the size of the {@link DatabaseCollection} async.
     *
     * @return completable future of size
     */
    CompletableFuture<Long> getSizeAsync();


    /**
     * Returns a {@link InsertQuery} to insert new data in this {@link DatabaseCollection}.
     *
     * @return insert query
     */
    InsertQuery insert();

    /**
     * Returns a {@link FindQuery} to specify conditions to find data on this {@link DatabaseCollection}
     *
     * @return find query
     */
    FindQuery find();

    /**
     * Returns a {@link UpdateQuery} to update data in this {@link DatabaseCollection} with a specified conditions.
     *
     * @return update query
     */
    UpdateQuery update();

    /**
     * Returns a {@link ReplaceQuery} to replace a data in this {@link DatabaseCollection}.
     *
     * @return replace query
     */
    ReplaceQuery replace();

    /**
     * Returns a {@link DeleteQuery} to delete data in this {@link DatabaseCollection}.
     *
     * @return delete query
     */
    DeleteQuery delete();


    /**
     * Drops this {@link DatabaseCollection}.
     */
    void drop();

    /**
     * Drops this {@link DatabaseCollection} async.
     *
     * @return completable future if dropped
     */
    CompletableFuture<Void> dropAsync();

    /**
     * Clear all data in this {@link DatabaseCollection}.
     */
    void clear();

    /**
     * Clear all data in this {@link DatabaseCollection} async.
     * @return completable future if cleared
     */
    CompletableFuture<Void> clearAsync();


    /**
     * Creates a new {@link QueryTransaction} on this database collection. For more information, see {@link QueryTransaction}.
     *
     * @return a new query transaction
     */
    QueryTransaction transact();

    /**
     * Returns a new {@link QueryGroup}.
     *
     * @return query group
     */
    QueryGroup group();


    /**
     * Returns all database collection fields with his information.
     * In some implementations, it may not work, for example on NoSQL based databases.
     *
     * @return collection of all database collection fields
     */
    Collection<CollectionField> getFields();

    /**
     * Returns {@link #getFields()} async.
     *
     * @return future of fields
     */
    CompletableFuture<Collection<CollectionField>> getFieldsAsync();

    /**
     * Get a specific field by {@code name}.
     *
     * @param name of field
     * @return the field in the database collection
     */
    CollectionField getField(String name);

    /**
     * Returns {@link #getField(String)} async.
     *
     * @param name of field
     * @return the field in the database collection async
     */
    CompletableFuture<CollectionField> getFieldAsync(String name);

    /**
     * Check if a field with the name {@code name} exists in this {@link DatabaseCollection}.
     *
     * @param name of field
     * @return if field exist or not
     */
    boolean hasField(String name);

    /**
     * Returns {@link #hasField(String)} async.
     *
     * @param name of field
     * @return if field exist or not
     */
    CompletableFuture<Boolean> hasFieldAsync(String name);

    /**
     * Adds a new field to this {@link DatabaseCollection} with the name {@code name}.
     *
     * @param name of field
     * @return new created collection field
     */
    CollectionField addField(String name);


    /**
     * Gets this {@link DatabaseCollection} with an alias name. For more information, see {@link AliasDatabaseCollection}.
     *
     * @param alias of this collection
     * @return a aliases database collection
     */
    AliasDatabaseCollection as(String alias);
}
