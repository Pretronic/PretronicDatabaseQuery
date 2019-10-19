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

package net.prematic.databasequery.api.query;

import net.prematic.databasequery.api.query.result.QueryResult;

import java.util.concurrent.CompletableFuture;

/**
 * No query order
 */
public interface InsertQuery extends Query {

    default InsertQuery set(String field){
        return set(field, EMPTY_OBJECT_ARRAY);
    }

    InsertQuery set(String field, Object... values);

    InsertQuery attribute(String... fields);

    InsertQuery value(Object... values);

    QueryResult executeAndGetGeneratedKeys(String[] keyColumns, Object... values);

    default QueryResult executeAndGetGeneratedKeys(String... keyColumns) {
        return executeAndGetGeneratedKeys(keyColumns, EMPTY_OBJECT_ARRAY);
    }

    CompletableFuture<QueryResult> executeAsyncAndGetGeneratedKeys(String[] keyColumns, Object... values);

    default CompletableFuture<QueryResult> executeAsyncAndGetGeneratedKeys(String... keyColumns) {
        return executeAsyncAndGetGeneratedKeys(keyColumns, EMPTY_OBJECT_ARRAY);
    }
}