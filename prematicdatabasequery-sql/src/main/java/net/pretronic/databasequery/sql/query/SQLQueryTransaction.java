/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 20:54
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

package net.pretronic.databasequery.sql.query;

import net.prematic.databasequery.api.query.Query;
import net.prematic.databasequery.api.query.QueryGroup;
import net.prematic.databasequery.api.query.QueryTransaction;
import net.prematic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.sql.SQLDatabase;

public class SQLQueryTransaction implements QueryTransaction {

    private final SQLDatabase database;

    public SQLQueryTransaction(SQLDatabase database) {
        this.database = database;
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public QueryResult execute(Query query, Object... values) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public QueryGroup add(Query query, Object... values) {
        return null;
    }

    @Override
    public QueryResult execute() {
        return null;
    }

    @Override
    public void clear() {

    }
}
