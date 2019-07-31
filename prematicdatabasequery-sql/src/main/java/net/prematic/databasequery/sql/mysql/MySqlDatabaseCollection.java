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
import net.prematic.databasequery.core.DatabaseCollectionField;
import net.prematic.databasequery.core.aggregation.Aggregation;
import net.prematic.databasequery.core.aggregation.AggregationBuilder;
import net.prematic.databasequery.core.exceptions.DatabaseQueryExecuteFailedException;
import net.prematic.databasequery.core.query.*;
import net.prematic.databasequery.sql.mysql.query.*;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.utility.Iterators;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public class MySqlDatabaseCollection implements DatabaseCollection {

    private final String name;
    private final DatabaseCollection.Type type;
    private final MySqlDatabase database;
    private final Collection<DatabaseCollectionField> fields;

    public MySqlDatabaseCollection(String name, DatabaseCollection.Type type, MySqlDatabase database) {
        this.name = name;
        this.type = type;
        this.database = database;
        this.fields = new HashSet<>();
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
    public long getSize() {
        return find().get(aggregationBuilder -> aggregationBuilder.aggregation(Aggregation.COUNT, "*")).execute().first().getLong(0);
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
            String query = "DROP TABLE IF EXISTS `" + getDatabase().getName() + "`.`" + getName() + "`";
            connection.prepareStatement(query);
            if(getLogger().isDebugging()) getLogger().debug("Executed sql query: {}", query);
        } catch (SQLException exception) {
            throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
        }
    }

    @Override
    public void truncate() {
        try(Connection connection = getDatabase().getDriver().getConnection()) {
            String query = "TRUNCATE TABLE IF EXISTS `" + getDatabase().getName() + "`.`" + getName() + "`";
            connection.prepareStatement(query);
            if(getLogger().isDebugging()) getLogger().debug("Executed sql query: {}", query);
        } catch (SQLException exception) {
            throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
        }
    }

    @Override
    public QueryTransaction transact() {
        MySqlQueryTransaction transaction = new MySqlQueryTransaction(this.getDatabase());
        if(getLogger().isDebugging()) getLogger().debug("Created sql transaction: {}", transaction);
        return transaction;
    }

    @Override
    public AggregationBuilder newAggregationBuilder(boolean aliasAble) {
        return getDatabase().newAggregationBuilder(aliasAble);
    }

    @Override
    public Collection<DatabaseCollectionField> getFields() {
        return null;
    }

    @Override
    public boolean hasField(String name) {
        return false;
    }

    @Override
    public DatabaseCollectionField getField(String name) {
        DatabaseCollectionField collectionField = Iterators.findOne(this.fields, field -> field.getName().equalsIgnoreCase(name));
        return collectionField == null ? getField(name) : collectionField;
    }

    @Override
    public void addField(DatabaseCollectionField field) {

    }

    @Override
    public void removeField(String field) {

    }

    public PrematicLogger getLogger() {
        return getDatabase().getLogger();
    }
}