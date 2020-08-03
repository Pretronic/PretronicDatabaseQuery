/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 16.12.19, 15:12
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

package net.pretronic.databasequery.sql;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.InnerQueryDatabaseCollection;
import net.pretronic.databasequery.api.exceptions.DatabaseQueryException;
import net.pretronic.databasequery.api.exceptions.DatabaseQueryExecuteFailedException;
import net.pretronic.databasequery.api.query.Query;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.QueryTransaction;
import net.pretronic.databasequery.api.query.function.RowNumberQueryFunction;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.type.CreateQuery;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.databasequery.common.AbstractDatabase;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;
import net.pretronic.databasequery.sql.collection.SQLInnerQueryDatabaseCollection;
import net.pretronic.databasequery.sql.driver.SQLDatabaseDriver;
import net.pretronic.databasequery.sql.query.SQLQueryGroup;
import net.pretronic.databasequery.sql.query.SQLQueryTransaction;
import net.pretronic.databasequery.sql.query.type.SQLCreateQuery;
import net.pretronic.libraries.utility.annonations.Internal;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class SQLDatabase extends AbstractDatabase<SQLDatabaseDriver> {

    private final DataSource dataSource;

    public SQLDatabase(String name, SQLDatabaseDriver driver, DataSource dataSource) {
        super(name, driver);
        this.dataSource = dataSource;
    }

    @Override
    public DatabaseCollection getCollection(String name) {
        return new SQLDatabaseCollection(name, this, DatabaseCollectionType.NORMAL);
    }

    @Override
    public InnerQueryDatabaseCollection getInnerQueryCollection(DatabaseCollection from, String aliasName, Consumer<FindQuery> queryConsumer) {
        return new SQLInnerQueryDatabaseCollection(aliasName, this, DatabaseCollectionType.NORMAL, from, queryConsumer);
    }

    @Override
    public InnerQueryDatabaseCollection getRowNumberInnerQueryCollection(DatabaseCollection from, String aliasName, RowNumberQueryFunction function) {
        return getInnerQueryCollection(from, aliasName, query -> query.get("*").getFunction(function, "RowNumber"));
    }

    @Override
    public CreateQuery createCollection(String name) {
        return new SQLCreateQuery(name, this);
    }

    @Override
    public void dropCollection(String name) {
        getCollection(name).drop();
    }

    @Override
    public void drop() {

    }

    @Override
    public QueryTransaction transact() {
        return new SQLQueryTransaction(this);
    }

    @Override
    public QueryGroup group() {
        return new SQLQueryGroup(this);
    }

    @Override
    public QueryResult execute(Query... queries) {
        return null;
    }

    public boolean isLocalConnected() {
        if(getDriver().getDialect().getEnvironment() != DatabaseDriverEnvironment.LOCAL) {
            throw new DatabaseQueryException("Only available for database driver with local enviroment");
        }
        try(Connection ignored = this.dataSource.getConnection()) {
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }

    @Internal
    public DataSource getDataSource() {
        return dataSource;
    }

    @Internal
    public <R> R executeResultQuery(String query, boolean commit, PreparedStatementConsumer preparedStatementConsumer, ResultSetFunction<R> resultSetFunction, Consumer<SQLException> exceptionConsumer) {
        try(Connection connection = this.dataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatementConsumer.accept(preparedStatement);
                try(ResultSet resultSet = preparedStatement.executeQuery();) {
                    R result = resultSetFunction.apply(resultSet);
                    if(commit) connection.commit();
                    if(getLogger().isDebugging()) getLogger().debug("{} - Executed sql query: {}", getDriver().getName(), query);
                    return result;
                }
            }
        } catch (SQLException exception) {
            exceptionConsumer.accept(exception);
        }
        return null;
    }

    @Internal
    public <R> R executeResultQuery(String query, boolean commit, PreparedStatementConsumer preparedStatementConsumer, ResultSetFunction<R> resultSetFunction) {
        return executeResultQuery(query, commit, preparedStatementConsumer, resultSetFunction,
                exception -> handleDatabaseQueryExecuteFailedException(exception, query));
    }

    @Internal
    public Number[] executeUpdateQuery(String query, boolean commit, PreparedStatementConsumer preparedStatementConsumer, String[] keyColumns, Consumer<SQLException> exceptionConsumer) {
        try(Connection connection = this.dataSource.getConnection()) {
            try(PreparedStatement preparedStatement = setPrepareStatement(connection, query, keyColumns)) {
                preparedStatementConsumer.accept(preparedStatement);
                int affectedRows = preparedStatement.executeUpdate();
                if(affectedRows != 0) {
                    if(keyColumns != null && keyColumns.length > 0) {
                        Number[] generatedKeys = new Number[keyColumns.length];
                        try(ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                            if(resultSet.next()) {
                                for (int i = 1; i <= keyColumns.length; i++) {
                                    generatedKeys[i-1] = (Number) resultSet.getObject(i);
                                }
                            }
                        }
                        if(commit) connection.commit();
                        return generatedKeys;
                    }
                }
                if(commit) connection.commit();
                if(getLogger().isDebugging()) getLogger().debug("{} - Executed sql query: {}", this.getDriver().getName(), query);
            }
        } catch (SQLException exception) {
            exceptionConsumer.accept(exception);
        }
        return new Number[0];
    }

    private PreparedStatement setPrepareStatement(Connection connection, String query, String[] keyColumns) throws SQLException {
        if(keyColumns != null) return connection.prepareStatement(query, keyColumns);
        else return connection.prepareStatement(query);
    }

    @Internal
    public Number[] executeUpdateQuery(String query, boolean commit, PreparedStatementConsumer preparedStatementConsumer, String[] keyColumns) {
        return executeUpdateQuery(query, commit, preparedStatementConsumer, keyColumns, exception -> handleDatabaseQueryExecuteFailedException(exception, query));
    }

    @Internal
    public void executeUpdateQuery(String query, boolean commit, PreparedStatementConsumer preparedStatementConsumer) {
        executeUpdateQuery(query, commit, preparedStatementConsumer, null);
    }

    @Internal
    public void executeUpdateQuery(String sql, boolean commit) {
        executeUpdateQuery(sql, commit, ignored -> {});
    }

    @Internal
    public void handleDatabaseQueryExecuteFailedException(SQLException exception, String query) {
        throw new DatabaseQueryExecuteFailedException(String.format("%s - Error executing sql query: %s", getDriver().getName(), query)
                , exception);
    }
}
