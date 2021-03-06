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

package net.pretronic.databasequery.api.query;

import net.pretronic.databasequery.api.query.result.QueryResult;

import java.util.concurrent.CompletableFuture;

/**
 * The {@link Query} represents the base query for all queries.
 */
public interface Query {

    Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * Executes a query with the values, which are prepared in the query.
     *
     * @param values to set for prepared values
     * @return result
     */
    QueryResult execute(Object... values);

    default QueryResult execute() {
        return execute(EMPTY_OBJECT_ARRAY);
    }

    CompletableFuture<QueryResult> executeAsync(Object... values);

    default CompletableFuture<QueryResult> executeAsync() {
        return executeAsync(EMPTY_OBJECT_ARRAY);
    }

    @Deprecated
    enum Option {
        NULL
    }
}
