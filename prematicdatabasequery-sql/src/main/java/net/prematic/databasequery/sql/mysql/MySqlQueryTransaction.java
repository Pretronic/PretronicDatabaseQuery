/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 26.05.19, 15:47
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

package net.prematic.databasequery.sql.mysql;

import net.prematic.databasequery.core.exceptions.transaction.DatabaseQueryCommitTransactionException;
import net.prematic.databasequery.core.exceptions.transaction.DatabaseQueryCreateTransactionException;
import net.prematic.databasequery.core.exceptions.transaction.DatabaseQueryRollbackTransactionException;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.query.Query;
import net.prematic.databasequery.core.query.QueryTransaction;
import net.prematic.databasequery.sql.CommitOnExecute;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlQueryTransaction implements QueryTransaction {

    private final MySqlDatabase database;
    private final Connection connection;

    public MySqlQueryTransaction(MySqlDatabase database) {
        this.database = database;
        try {
            this.connection = database.getDriver().getConnection();
        } catch (SQLException exception) {
            throw new DatabaseQueryCreateTransactionException(exception.getMessage(), exception);
        }
    }

    @Override
    public void commit() {
        try {
            this.connection.commit();
            if(this.database.getLogger().isDebugging()) this.database.getLogger().debug("Committed in transaction {}", this);
        } catch (SQLException exception) {
            throw new DatabaseQueryCommitTransactionException(exception.getMessage(), exception);
        }
    }

    @Override
    public void rollback() {
        try {
            this.connection.rollback();
            if(this.database.getLogger().isDebugging()) this.database.getLogger().debug("Roll backed in transaction {}", this);
        } catch (SQLException exception) {
            throw new DatabaseQueryRollbackTransactionException(exception.getMessage(), exception);
        }
    }

    @Override
    public void execute(Query query, Object... values) {
        String queryString = ((QueryStringBuildAble)query).buildExecuteString(values);
        if(query instanceof CommitOnExecute) ((CommitOnExecute)query).execute(false, values);
        else query.execute(values);
        if(this.database.getLogger().isDebugging()) this.database.getLogger().debug("Executed sql query ({}) in transaction {}", queryString, this);
    }
}