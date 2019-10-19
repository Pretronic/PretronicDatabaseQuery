/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.10.19, 17:38
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

package net.prematic.databasequery.sql;

import net.prematic.databasequery.api.query.Query;
import net.prematic.databasequery.api.query.result.QueryResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface SqlQuery extends Query {

    ExecutorService getExecutorService();

    QueryResult execute(boolean commit, Object... values);

    @Override
    default QueryResult execute(Object... values) {
        return execute(true, values);
    }

    @Override
    default CompletableFuture<QueryResult> executeAsync(Object... values) {
        return executeAsync(true, values);
    }

    default CompletableFuture<QueryResult> executeAsync(boolean commit, Object... values) {
        CompletableFuture<QueryResult> future = new CompletableFuture<>();
        getExecutorService().execute(()-> future.complete(execute(commit, values)));
        return future;
    }
}