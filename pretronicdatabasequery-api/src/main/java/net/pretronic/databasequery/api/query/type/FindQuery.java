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
import net.pretronic.databasequery.api.query.Aggregation;

/**
 * The {@link FindQuery} represents the query to search data in the executed {@link net.pretronic.databasequery.api.collection.DatabaseCollection}.
 */
public interface FindQuery extends SearchQuery<FindQuery> {

    /**
     * @param fields to get
     * @return the query instance
     */
    FindQuery get(String... fields);

    default FindQuery get(DatabaseCollection collection, String field) {
        return get(collection.getName(), field);
    }

    /**
     * @param collection of field
     * @param field to get
     * @return the query instance
     */
    FindQuery get(String collection, String field);

    /**
     * @param field to get
     * @param alias for getting in query result
     * @return the query instance
     */
    FindQuery getAs(String field, String alias);

    default FindQuery getAs(DatabaseCollection collection, String field, String alias) {
        return getAs(collection.getName(), field, alias);
    }

    /**
     * @param collection of field
     * @param field to get
     * @param alias for getting in query result
     * @return the query instance
     */
    FindQuery getAs(String collection, String field, String alias);


    /**
     * @param aggregation to get
     * @param field to get with the aggregation
     * @return the query instance
     */
    FindQuery get(Aggregation aggregation,String field);

    default FindQuery get(Aggregation aggregation, DatabaseCollection collection, String field) {
        return getAs(aggregation, collection.getName(), field);
    }

    /**
     * @param aggregation to get
     * @param collection of field
     * @param field
     * @return the query instance
     */
    FindQuery get(Aggregation aggregation, String collection, String field);

    /**
     * @param aggregation to get
     * @param field to get with aggregation
     * @param alias for getting in query result
     * @return the query instance
     */
    FindQuery getAs(Aggregation aggregation, String field, String alias);

    default FindQuery getAs(Aggregation aggregation, DatabaseCollection collection, String field, String alias) {
        return getAs(aggregation, collection.getName(), field, alias);
    }

    /**
     * @param aggregation to get
     * @param collection of field
     * @param field to get with the aggregation
     * @param alias for getting in query result
     * @return the query instance
     */
    FindQuery getAs(Aggregation aggregation, String collection, String field, String alias);
}
