/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 03.05.19, 23:38
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

package net.prematic.databasequery.core;

import net.prematic.databasequery.core.query.CreateQuery;
import net.prematic.databasequery.core.query.Query;
import net.prematic.databasequery.core.query.QueryTransaction;
import net.prematic.databasequery.core.query.result.QueryResult;
import java.util.List;

public interface Database {

    String getName();

    DatabaseCollection getCollection(String name);

    CreateQuery createCollection(String name);

    DatabaseCollection createCollection(Class<?> clazz);

    default DatabaseCollection createCollection(Object object) {
        return createCollection(object.getClass());
    }

    DatabaseCollection updateCollectionStructure(String collection, Class<?> clazz);

    default DatabaseCollection updateCollectionStructure(DatabaseCollection collection, Class<?> clazz) {
        return updateCollectionStructure(collection.getName(), clazz);
    }

    void deleteCollection(String name);

    void dropCollection(String name);

    default void dropCollection(DatabaseCollection collection) {
        dropCollection(collection.getName());
    }

    void drop();

    List<QueryResult> execute(Query... queries);

    QueryTransaction transact();
}