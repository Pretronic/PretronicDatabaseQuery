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
import net.prematic.databasequery.core.query.CreateQuery;
import net.prematic.databasequery.core.query.Query;
import net.prematic.databasequery.core.query.QueryTransaction;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.SqlDatabaseConnectionHolder;
import net.prematic.databasequery.sql.mysql.query.MySqlCreateQuery;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.utility.annonations.Internal;
import net.prematic.libraries.utility.exceptions.NotImplementedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class MySqlDatabase implements Database {

    private final String name;
    protected final MySqlDatabaseDriver driver;

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
        String query = "DROP TABLE IF EXISTS `" + this.name + "`.`" + name + "`";
        executeSimpleUpdateQuery(query, true);
    }

    @Override
    public void drop() {
        String query = "DROP DATABASE IF EXISTS `" + getName() + "`";
        executeSimpleUpdateQuery(query, true);
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

    @Internal
    public void executeResultQuery(String query, boolean commit, Consumer<PreparedStatement> preparedStatementConsumer, Consumer<ResultSet> resultSetConsumer, Consumer<SQLException> exceptionConsumer) {
        getDriver().getExecutorService().execute(()-> {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                connection = getConnectionHolder().getConnection();
                preparedStatement = connection.prepareStatement(query);
                preparedStatementConsumer.accept(preparedStatement);
                resultSet = preparedStatement.executeQuery();
                resultSetConsumer.accept(resultSet);
                if(commit) connection.commit();
                if(getLogger().isDebugging()) getLogger().debug("Executed sql query: {}", query);
            } catch (SQLException exception) {
                exceptionConsumer.accept(exception);
            } finally {
                try {
                    if(connection != null && getDriver().useDataSource()) connection.close();
                    if(preparedStatement != null) preparedStatement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException exception) {
                    exceptionConsumer.accept(exception);
                }
            }
        });
    }

    @Internal
    public void executeResultQuery(String query, boolean commit, Consumer<PreparedStatement> preparedStatementConsumer, Consumer<ResultSet> resultSetConsumer) {
        executeResultQuery(query, commit, preparedStatementConsumer, resultSetConsumer, exception -> getDriver().handleDatabaseQueryExecuteFailedException(exception, query));
    }

    @Internal
    public CompletableFuture<Number[]> executeUpdateQuery(String query, boolean commit, Consumer<PreparedStatement> preparedStatementConsumer, String[] returnIdColumns, Consumer<SQLException> exceptionConsumer) {
        CompletableFuture<Number[]> future = new CompletableFuture<>();
        getDriver().getExecutorService().execute(()-> {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                connection = getConnectionHolder().getConnection();
                if(returnIdColumns != null) preparedStatement = connection.prepareStatement(query, returnIdColumns);
                else preparedStatement = connection.prepareStatement(query);
                preparedStatementConsumer.accept(preparedStatement);
                int affectedRows = preparedStatement.executeUpdate();
                if(affectedRows != 0) {
                    if(returnIdColumns != null && returnIdColumns.length > 0) {
                        Number[] generatedKeys = new Number[returnIdColumns.length];
                        try(ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                            if(resultSet.next()) {
                                for (int i = 1; i <= returnIdColumns.length; i++) {
                                    generatedKeys[i-1] = (Number) resultSet.getObject(i);
                                }
                            }
                        }
                        future.complete(generatedKeys);
                    }
                }
                if(commit) connection.commit();
                if(getLogger().isDebugging()) getLogger().debug("Executed sql query: {}", query);
            } catch (SQLException exception) {
                exceptionConsumer.accept(exception);
            } finally {
                try {
                    if(connection != null && getDriver().useDataSource()) connection.close();
                    if(preparedStatement != null) preparedStatement.close();
                } catch (SQLException exception) {
                    exceptionConsumer.accept(exception);
                }
            }
            future.complete(new Number[0]);
        });
        return future;
    }

    @Internal
    public CompletableFuture<Number[]> executeUpdateQuery(String query, boolean commit, Consumer<PreparedStatement> preparedStatementConsumer, String[] returnIdColumns) {
        return executeUpdateQuery(query, commit, preparedStatementConsumer, returnIdColumns, exception -> getDriver().handleDatabaseQueryExecuteFailedException(exception, query));
    }

    @Internal
    public void executeUpdateQuery(String query, boolean commit, Consumer<PreparedStatement> preparedStatementConsumer) {
        executeUpdateQuery(query, commit, preparedStatementConsumer, null);
    }

    @Internal
    public void executeSimpleUpdateQuery(String sql, boolean commit) {
        executeUpdateQuery(sql, commit, ignored -> {});
    }

    public abstract SqlDatabaseConnectionHolder getConnectionHolder();
}