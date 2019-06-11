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
import net.prematic.databasequery.core.DatabaseCollectionType;
import net.prematic.databasequery.core.aggregation.AggregationBuilder;
import net.prematic.databasequery.core.query.CreateQuery;
import net.prematic.databasequery.core.query.Query;
import net.prematic.databasequery.core.query.QueryTransaction;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.mysql.query.MySqlCreateQuery;

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
        return driver;
    }

    @Override
    public DatabaseCollection getCollection(String name) {
        return new MySqlDatabaseCollection(name, DatabaseCollectionType.NORMAL, this);
    }

    @Override
    public CreateQuery createCollection(String name) {
        return new MySqlCreateQuery(name, this);
    }

    @Override
    public DatabaseCollection createCollection(Class<?> clazz) {
        return null;
    }

    @Override
    public DatabaseCollection updateCollectionStructure(String collection, Class<?> clazz) {
        return null;
    }

    @Override
    public void deleteCollection(String name) {

    }

    @Override
    public void dropCollection(String name) {

    }

    @Override
    public void drop() {

    }

    @Override
    public List<QueryResult> execute(Query... queries) {
        return null;
    }

    @Override
    public QueryTransaction transact() {
        return new MySqlQueryTransaction(getDriver().getConnection());
    }

    @Override
    public AggregationBuilder newAggregationBuilder(boolean aliasAble) {
        return new MySqlAggregationBuilder(this, aliasAble);
    }
}