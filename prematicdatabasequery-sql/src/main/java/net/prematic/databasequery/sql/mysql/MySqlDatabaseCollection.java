/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 26.05.19, 15:37
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

import net.prematic.databasequery.core.DatabaseCollection;
import net.prematic.databasequery.core.aggregation.AggregationBuilder;
import net.prematic.databasequery.core.query.*;
import net.prematic.databasequery.sql.mysql.query.*;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlDatabaseCollection implements DatabaseCollection {

    private final String name;
    private final DatabaseCollection.Type type;
    private final MySqlDatabase database;

    public MySqlDatabaseCollection(String name, DatabaseCollection.Type type, MySqlDatabase database) {
        this.name = name;
        this.type = type;
        this.database = database;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DatabaseCollection.Type getType() {
        return this.type;
    }

    public MySqlDatabase getDatabase() {
        return this.database;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public InsertQuery insert() {
        return new MySqlInsertQuery(this);
    }

    @Override
    public FindQuery find() {
        return new MySqlFindQuery(this);
    }

    @Override
    public UpdateQuery update() {
        return new MySqlUpdateQuery(this);
    }

    @Override
    public ReplaceQuery replace() {
        return new MySqlReplaceQuery(this);
    }

    @Override
    public DeleteQuery delete() {
        return new MySqlDeleteQuery(this);
    }

    @Override
    public void drop() {
        try(Connection connection = getDatabase().getDriver().getConnection()) {
            connection.prepareStatement("DROP TABLE IF EXISTS `" + getDatabase().getName() + "`.`" + getName() + "`");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void truncate() {
        try(Connection connection = getDatabase().getDriver().getConnection()) {
            connection.prepareStatement("TRUNCATE TABLE IF EXISTS `" + getDatabase().getName() + "`.`" + getName() + "`");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public QueryTransaction transact() {
        try {
            return new MySqlQueryTransaction(getDatabase().getDriver().getConnection());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public AggregationBuilder newAggregationBuilder(boolean aliasAble) {
        return getDatabase().newAggregationBuilder(aliasAble);
    }
}
