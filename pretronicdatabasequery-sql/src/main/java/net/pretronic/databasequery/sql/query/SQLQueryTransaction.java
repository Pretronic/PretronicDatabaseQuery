/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 20:54
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

package net.pretronic.databasequery.sql.query;

import net.pretronic.databasequery.api.exceptions.DatabaseQueryTransactionException;
import net.pretronic.databasequery.api.query.Query;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.QueryTransaction;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.query.type.SQLCreateQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public class SQLQueryTransaction implements QueryTransaction {

    private final SQLDatabase database;
    private final Connection connection;

    public SQLQueryTransaction(SQLDatabase database) {
        this.database = database;
        try {
            connection = database.getDataSource().getConnection();
        } catch (SQLException exception) {
            throw new DatabaseQueryTransactionException("Can't create transaction", exception);
        }
    }


    @Override
    public void commit() {
        try {
            this.connection.commit();
        } catch (SQLException exception) {
            throw new DatabaseQueryTransactionException("Can't commit sql transaction.", exception);
        }
    }

    @Override
    public void rollback() {
        try {
            this.connection.rollback();
        } catch (SQLException exception) {
            throw new DatabaseQueryTransactionException("Can't rollback sql transaction.", exception);
        }
    }

    @Override
    public QueryResult execute(Query query, Object... values) {
        if(query instanceof SQLCreateQuery) {
            return new DefaultQueryResult().addEntry(new DefaultQueryResultEntry(this.database.getDriver()).addEntry("collection", ((SQLCreateQuery)query).create(false)));
        } else if(query instanceof CommitOnExecute) {
            return ((CommitOnExecute)query).execute(false, values);
        }
        throw new IllegalArgumentException("Can't execute sql transaction for query " + query.getClass());
    }

    @Override
    public QueryResult execute(QueryGroup queryGroup, Object... values) {
        return null;
    }

    @Override
    public QueryResult execute(Consumer<QueryGroup> queryGroupConsumer, Object... values) {
        return null;
    }
}
