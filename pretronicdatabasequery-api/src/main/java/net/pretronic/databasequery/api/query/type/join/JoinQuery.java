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

package net.pretronic.databasequery.api.query.type.join;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.Query;

/**
 * The {@link JoinQuery} represents a join between {@link DatabaseCollection}(s). You can configure a {@link JoinType} for the queries.
 *
 * @param <T>
 */
public interface JoinQuery<T extends JoinQuery<?>> extends Query {

    /**
     * Join whit {@link JoinType#INNER}. See {@link #join(DatabaseCollection, JoinType)}.
     *
     * @param collection
     * @return
     */
    T join(DatabaseCollection collection);

    /**
     * Joins the {@code collection} to the {@link DatabaseCollection} in which this query is executed.
     *
     * @param collection to join
     * @param type of join
     * @return this query instance
     */
    T join(DatabaseCollection collection,JoinType type);


    /**
     * Set the columns, which should be joined with the same value.
     *
     * @param column1 of collection 1
     * @param column2 of collection 2
     * @return this query instance
     */
    T on(String column1, String column2);

    /**
     * Set the columns, which should be joined with the same value and the joined collection. It should be used, if column 1 and 2 have the same name
     * to prevent errors in some implementations.
     *
     * @param column1 of collection 1
     * @param collection2 to join
     * @param column2 of collection2
     * @return this query instance
     */
    T on(String column1, DatabaseCollection collection2, String column2);

    /**
     * Set the columns, which should be joined with the same value and the joined collection and collection1. It should be also used, if column 1 and 2 have the same name
     * to prevent errors in some implementations.
     *
     * @param collection1 of this query
     * @param column1 of collection1
     * @param collection2 to join
     * @param column2 of collection2
     * @return this query instance
     */
    T on(DatabaseCollection collection1, String column1, DatabaseCollection collection2, String column2);


}
