/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.12.19, 17:01
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

package net.prematic.databasequery.api.query.type;

import net.prematic.databasequery.api.query.Query;
import net.prematic.databasequery.api.query.result.QueryResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link InsertQuery} is used for inserting new data records in a collection.
 * The insert query is a port of the data manipulation language (DML).
 *
 * <p>Before you can insert any kind of data, you have to defined the fields with {@link #fields(String...)},
 * after that you are able to directly declare the values {@link #values(Object...)}, using an external
 * select query {@link #query(FindQuery)} or set the values later at the execution{@link #execute()}.</p>
 *
 * <p>Examples:</p>
 *
 * <pre>
 *   insert.fields("Name","Age")
 *         .values("John Doe",54)
 *         .values("Jane Doe",36)
 *         .execute();
 *
 *   insert.set("Name","John Doe","Jane Doe")
 *         .set("Age",54,36)
 *         .execute();
 *
 *   insert.fields("Name","Age")
 *         .execute("John Doe",54,"Jane Doe",36);
 * </pre>
 */
public interface InsertQuery extends Query {

    InsertQuery set(String field);

    InsertQuery set(String field, Object... values);

    InsertQuery set(String field, List<Object> values);

    /**
     * Define one or multiple values.
     *
     * @param fields The fields to define
     * @return The current query
     */
    InsertQuery fields(String... fields);

    InsertQuery values(Object... values);

    InsertQuery query(FindQuery query);


    QueryResult executeAndGetGeneratedKeys(String[] keyColumns, Object... values);

    QueryResult executeAndGetGeneratedKeys(String... keyColumns);

    int executeAndGetGeneratedKeyAsInt(String keyColumn, Object... values);

    long executeAndGetGeneratedKeyAsLong(String keyColumn, Object... values);

    CompletableFuture<QueryResult> executeAsyncAndGetGeneratedKeys(String[] keyColumns, Object... values);

    CompletableFuture<QueryResult> executeAsyncAndGetGeneratedKeys(String... keyColumns);

    CompletableFuture<Integer> executeAsyncAndGetGeneratedKeyAsInt(String keyColumn, Object... values);

    CompletableFuture<Long> executeAsyncAndGetGeneratedKeyAsLong(String keyColumn, Object... values);
}
