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

package net.prematic.databasequery.api;

import net.prematic.databasequery.api.collection.CollectionCreator;
import net.prematic.databasequery.api.collection.DatabaseCollection;
import net.prematic.databasequery.api.driver.DatabaseDriver;
import net.prematic.databasequery.api.query.Query;
import net.prematic.databasequery.api.query.QueryGroup;
import net.prematic.databasequery.api.query.QueryTransaction;
import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.databasequery.api.query.type.CreateQuery;
import net.prematic.libraries.document.Document;

import java.io.File;

public interface Database {

    String getName();

    DatabaseDriver getDriver();


    DatabaseCollection getCollection(String name);

    CreateQuery createCollection(String name);

    default void createCollection(Document document) {
        CollectionCreator.create(this,document);
    }

    default void createCollection(File location) {
        CollectionCreator.create(this,location);
    }

    void dropCollection(String name);

    default void dropCollection(DatabaseCollection collection) {
        dropCollection(collection.getName());
    }


    void drop();

    QueryTransaction transact();

    QueryGroup group();

    QueryResult execute(Query... queries);

}