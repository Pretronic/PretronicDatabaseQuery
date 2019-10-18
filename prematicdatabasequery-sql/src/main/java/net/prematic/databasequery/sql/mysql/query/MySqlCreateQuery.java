/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 25.05.19, 23:10
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

package net.prematic.databasequery.sql.mysql.query;

import net.prematic.databasequery.core.DatabaseCollection;
import net.prematic.databasequery.core.ForeignKey;
import net.prematic.databasequery.core.datatype.DataType;
import net.prematic.databasequery.core.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.core.exceptions.DatabaseQueryExecuteFailedException;
import net.prematic.databasequery.core.impl.DataTypeInformation;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.impl.query.result.SimpleQueryResult;
import net.prematic.databasequery.core.impl.query.result.SimpleQueryResultEntry;
import net.prematic.databasequery.core.query.CreateQuery;
import net.prematic.databasequery.core.query.option.CreateOption;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.CommitOnExecute;
import net.prematic.databasequery.sql.mysql.MySqlDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class MySqlCreateQuery implements CreateQuery, QueryStringBuildAble, CommitOnExecute {

    private String name;
    private final MySqlDatabase database;
    private final StringBuilder queryBuilder;
    private boolean first;
    private final List<Object> values;

    public MySqlCreateQuery(String name, MySqlDatabase database) {
        this.name = name;
        this.database = database;
        this.queryBuilder = new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS `")
                .append(database.getName())
                .append("`.`")
                .append(name)
                .append("`(");
        this.values = new ArrayList<>();
        this.first = true;
    }

    @Override
    public CreateQuery attribute(String field, DataType dataType, int fieldSize, Object defaultValue, ForeignKey foreignKey, CreateOption... createOptions) {
        if(!first) queryBuilder.append(",");
        else first = false;
        boolean uniqueIndex = false;
        boolean index = false;
        DataTypeInformation dataTypeInformation = this.database.getDriver().getDataTypeInformationByDataType(dataType);
        queryBuilder.append("`").append(field).append("` ")
                .append(dataTypeInformation.getName());
        if(dataTypeInformation.isSizeAble()) {
            if(fieldSize != -1) queryBuilder.append("(").append(fieldSize).append(")");
            else if(dataTypeInformation.getDefaultSize() != -1) queryBuilder.append("(").append(dataTypeInformation.getDefaultSize()).append(")");
        }
        if(defaultValue != null) {
            this.values.add(defaultValue);
            queryBuilder.append(" DEFAULT ?");
        }
        if(createOptions!= null && createOptions.length != 0) {
            for (CreateOption createOption : createOptions) {
                switch (createOption) {
                    case INDEX: {
                        index = true;
                        break;
                    }
                    case UNIQUE_INDEX: {
                        uniqueIndex = true;
                        break;
                    }
                    case PRIMARY_KEY: {
                        queryBuilder.append(" PRIMARY KEY");
                        break;
                    }
                    case NOT_NULL: {
                        queryBuilder.append(" NOT NULL");
                        break;
                    }
                    default: {
                        queryBuilder.append(" ").append(createOption.toString());
                        break;
                    }
                }
            }
        }
        if(uniqueIndex) {
            queryBuilder.append(",UNIQUE INDEX `").append(this.database.getName()).append(this.name).append(field).append("`(`").append(field).append("`)");
        }else if(index) {
            queryBuilder.append(",INDEX `").append(this.database.getName()).append(this.name).append(field).append("`(`").append(field).append("`)");
        }
        if(foreignKey != null) buildForeignKey(field, foreignKey);
        return this;
    }

    @Override
    public CreateQuery engine(String engine) {
        this.queryBuilder.append(") ENGINE=").append(engine).append(";");
        return this;
    }

    @Override
    public CreateQuery collectionType(DatabaseCollection.Type collectionType) {
        return this;
    }

    @Override
    public CreateQuery foreignKey(String field, ForeignKey foreignKey) {
        return buildForeignKey(field, foreignKey);
    }

    @Override
    public CreateQuery collectionName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public QueryResult execute(boolean commit, Object... values) {
        String query = buildExecuteString(values);
        try(Connection connection = this.database.getDriver().getConnection()) {
            int index = 1;
            int valueGet = 0;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (Object value : this.values) {
                if(value == null) {
                    value = values[valueGet];
                    valueGet++;
                }
                DataTypeAdapter adapter = this.database.getDriver().getDataTypeAdapterByWriteClass(value.getClass());
                if(adapter != null) value = adapter.write(value);
                preparedStatement.setObject(index, value);
                index++;
            }
            preparedStatement.executeUpdate();
            if(commit) connection.commit();
            if(this.database.getLogger().isDebugging()) this.database.getLogger().debug("Executed sql query: {}", query);
        } catch (SQLException exception) {
            if(this.database.getLogger().isDebugging()) {
                this.database.getLogger().debug("Error executing sql query: {}", query);
                throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
            }else throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
        }
        Map<String, Object> results = new HashMap<>();
        results.put("databaseCollection", this.database.getCollection(this.name));
        SimpleQueryResultEntry queryResultEntry = new SimpleQueryResultEntry(results);
        return new SimpleQueryResult(Collections.singletonList(queryResultEntry));
    }

    @Override
    public QueryResult execute(Object... values) {
        return execute(true, values);
    }

    private CreateQuery buildForeignKey(String field, ForeignKey foreignKey) {
        if(!first) queryBuilder.append(",");
        else first = false;
        this.queryBuilder.append("CONSTRAINT `")
                .append(this.database.getName())
                .append(this.name)
                .append(field)
                .append("` FOREIGN KEY(`")
                .append(field)
                .append("`) REFERENCES `")
                .append(foreignKey.getDatabase())
                .append("`.`")
                .append(foreignKey.getCollection())
                .append("`(`")
                .append(foreignKey.getField()).append("`)");
        if(foreignKey.getDeleteOption() != null && foreignKey.getDeleteOption() != ForeignKey.Option.DEFAULT) {
            this.queryBuilder.append(" ON DELETE ").append(foreignKey.getDeleteOption().toString().replace("_", " "));
        }
        if(foreignKey.getUpdateOption() != null && foreignKey.getUpdateOption() != ForeignKey.Option.DEFAULT) {
            this.queryBuilder.append(" ON UPDATE ").append(foreignKey.getDeleteOption().toString().replace("_", " "));
        }
        return this;
    }

    @Override
    public String buildExecuteString(Object... values) {
        return this.queryBuilder.append(");").toString();
    }
}