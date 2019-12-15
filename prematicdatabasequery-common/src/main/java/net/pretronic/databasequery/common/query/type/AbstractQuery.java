/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.12.19, 18:57
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

package net.pretronic.databasequery.common.query.type;

import net.prematic.databasequery.api.driver.DatabaseDriver;
import net.prematic.databasequery.api.query.Query;
import net.prematic.databasequery.api.query.result.QueryResult;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractQuery implements Query {

    private final DatabaseDriver driver;

    protected AbstractQuery(DatabaseDriver driver) {
        this.driver = driver;
    }

    @Override
    public CompletableFuture<QueryResult> executeAsync(Object... values) {
        CompletableFuture<QueryResult> future = new CompletableFuture<>();
        this.driver.getExecutorService().execute(()-> future.complete(execute(values)));
        return future;
    }
}
