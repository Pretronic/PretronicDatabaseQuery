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

import java.util.concurrent.CompletableFuture;

public interface InsertQuery extends Query {

    default InsertQuery set(String field) {
        return set(field, EMPTY_OBJECT_ARRAY);
    }

    InsertQuery set(String field, Object... values);


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