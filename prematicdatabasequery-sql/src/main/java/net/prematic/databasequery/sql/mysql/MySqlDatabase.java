/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 26.05.19, 15:34
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

import net.prematic.databasequery.core.Database;
import net.prematic.databasequery.core.DatabaseCollection;
import net.prematic.databasequery.core.aggregation.AggregationBuilder;
import net.prematic.databasequery.core.exceptions.DatabaseQueryExecuteFailedException;
import net.prematic.databasequery.core.query.CreateQuery;
import net.prematic.databasequery.core.query.Query;
import net.prematic.databasequery.core.query.QueryTransaction;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.mysql.query.MySqlCreateQuery;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.utility.exceptions.NotImplementedException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MySqlDatabase implements Database {

    private final String name;
    private final MySqlDatabaseDriver driver;

    public MySqlDatabase(String name, MySqlDatabaseDriver driver) {
        this.name = name;
        this.driver = driver;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public MySqlDatabaseDriver getDriver() {
        return this.driver;
    }

    @Override
    public DatabaseCollection getCollection(String name) {
        return new MySqlDatabaseCollection(name, DatabaseCollection.Type.NORMAL, this);
    }

    @Override
    public CreateQuery createCollection(String name) {
        return new MySqlCreateQuery(name, this);
    }

    @Override
    public DatabaseCollection createCollection(Class<?> clazz) {
        throw new NotImplementedException();
    }

    @Override
    public DatabaseCollection updateCollectionStructure(String collection, Class<?> clazz) {
        throw new NotImplementedException();
    }

    @Override
    public void dropCollection(String name) {
        try(Connection connection = getDriver().getConnection()) {
            String query = "DROP TABLE IF EXISTS `" + this.name + "`.`" + name + "`";
            connection.prepareStatement(query);
            if(getLogger().isDebugging()) getLogger().debug("Executed sql query: ", query);
        } catch (SQLException exception) {
            throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
        }
    }

    @Override
    public void drop() {
        try(Connection connection = getDriver().getConnection()) {
            String query = "DROP DATABASE IF EXISTS `" + getName() + "`";
            connection.prepareStatement(query);
            if(getLogger().isDebugging()) getLogger().debug("Executed sql query: {}", query);
        } catch (SQLException exception) {
            throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<QueryResult> execute(Query... queries) {
        throw new NotImplementedException();
    }

    @Override
    public QueryTransaction transact() {
        MySqlQueryTransaction transaction = new MySqlQueryTransaction(this);
        if(getLogger().isDebugging()) getLogger().debug("Created sql transaction: {}", transaction);
        return transaction;
    }

    @Override
    public AggregationBuilder newAggregationBuilder(boolean aliasAble) {
        return new MySqlAggregationBuilder(this, aliasAble);
    }

    public PrematicLogger getLogger() {
        return getDriver().getLogger();
    }
}