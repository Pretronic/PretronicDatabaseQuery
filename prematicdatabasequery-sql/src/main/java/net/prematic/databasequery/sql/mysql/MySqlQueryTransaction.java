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

import net.prematic.databasequery.core.query.Query;
import net.prematic.databasequery.core.query.QueryTransaction;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlQueryTransaction implements QueryTransaction {

    private final Connection connection;

    public MySqlQueryTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void commit() {
        try {
            this.connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollback() {
        try {
            this.connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Query query, Object... values) {
        if(query instanceof CommitOnExecute) ((CommitOnExecute)query).execute(false, values);
        else query.execute(values);
    }
}