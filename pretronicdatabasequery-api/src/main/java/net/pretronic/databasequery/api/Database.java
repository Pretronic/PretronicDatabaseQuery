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

package net.pretronic.databasequery.api;

import net.pretronic.databasequery.api.collection.CollectionCreator;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.query.Query;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.QueryTransaction;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.type.CreateQuery;
import net.pretronic.libraries.document.Document;

import java.io.File;

/**
 * The {@link Database} object represents a database on a remote server or a file of a local database.
 * A database contains different collections ({@link DatabaseCollection}), it makes
 * no difference which type of database.
 *
 * <p>The {@link Database} provides difference methods for managing and creating collections on
 * a remote server or a local file database. This interface is an abstraction between
 * different types of databases. This interface also provides methods for executing
 * queries in different ways, see ({@link #transact()}, {@link #group()} ()} {@link #execute(Query...)} ()}).</p>
 *
 */
public interface Database {

    /**
     * Get the name of this database.
     *
     * <p>The name is the same as the name on the remote service or the file name for local databases.</p>
     *
     * @return The name as string
     */
    String getName();

    /**
     * The driver which is used for all queries to this database.
     * <p>Queries to collections are also executed over the database and the assigned driver.</p>
     *
     * @return The driver for the current database and collections
     */
    DatabaseDriver getDriver();

    /**
     * Get a collection form this database.
     *
     * <p>Important = A new collection object is also returned, when the collection doesn't exist on the remote server.</p>
     *
     * @param name The name of the collection on the remote server
     * @return A new collection object for accessing the data
     */
    DatabaseCollection getCollection(String name);

    /**
     * Create a new collection on a remote server or on a local file database.
     *
     * @param name The name of the collection
     * @return A new collection object for accessing the data
     */
    CreateQuery createCollection(String name);

    /**
     * Create multiple collections based on a document.
     *
     * <p>For more information see {@link CollectionCreator}</p>
     *
     * @param document The document
     */
    default void createCollection(Document document) {
        CollectionCreator.create(this,document);
    }

    /**
     * Create multiple collections based on a document.
     *
     * <p>This method automatically reads the document from a file.</p>
     *
     * <p>For more information see {@link CollectionCreator}</p>
     *
     * @param location The location of the document
     */
    default void createCollection(File location) {
        CollectionCreator.create(this,location);
    }

    /**
     * Drop a collection on a remote server or a local file database.
     *
     * @param name The name of the collection
     */
    void dropCollection(String name);

    /**
     * Drop a collection on a remote server or a local file database.
     *
     * @param collection The collection object
     */
    default void dropCollection(DatabaseCollection collection) {
        dropCollection(collection.getName());
    }

    /**
     * Delete this database and all contained collections on a remote server or a local file database.
     */
    void drop();

    /**
     * Create a new transaction for executing different queries over the same connection.
     *
     * <p>With a transaction you are able to undo your queries in case if an error.</p>
     *
     * <p>For more information see {@link QueryTransaction}</p>
     *
     * @return The new transaction object
     */
    QueryTransaction transact();

    /**
     * Create a query group for executing multiple queries at the same time.
     *
     * @return The new query group
     */
    QueryGroup group();

    /**
     * Execute a query over this database.
     *
     * @param queries All queries for executing
     * @return The result of the execution
     */
    QueryResult execute(Query... queries);

}
