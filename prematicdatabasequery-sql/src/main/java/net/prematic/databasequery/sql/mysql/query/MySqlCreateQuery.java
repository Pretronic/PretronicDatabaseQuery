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
import net.prematic.databasequery.core.impl.DataTypeInformation;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.query.CreateQuery;
import net.prematic.databasequery.core.query.option.CreateOption;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.mysql.CommitOnExecute;
import net.prematic.databasequery.sql.mysql.MySqlDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlCreateQuery implements CreateQuery, QueryStringBuildAble, CommitOnExecute {

    private final String name;
    private final MySqlDatabase database;
    private final StringBuilder createQueryBuilder;
    private final String mainQuery;
    private String engine;
    private boolean first;
    private final List<Object> values;

    public MySqlCreateQuery(String name, MySqlDatabase database) {
        this.name = name;
        this.database = database;
        this.createQueryBuilder = new StringBuilder();
        this.mainQuery = "CREATE TABLE IF NOT EXISTS `"
                + database.getName()
                + "`.`"
                + name
                + "`(";
        this.values = new ArrayList<>();
        this.first = true;
    }

    @Override
    public CreateQuery attribute(String field, DataType dataType, int fieldSize, Object defaultValue, ForeignKey foreignKey, CreateOption... createOptions) {
        if(!first) createQueryBuilder.append(",");
        else first = false;
        boolean uniqueIndex = false;
        boolean index = false;
        DataTypeInformation dataTypeInformation = this.database.getDriver().getDataTypeInformation(dataType);
        createQueryBuilder.append("`").append(field).append("` ")
                .append(dataTypeInformation.getName());
        if(dataTypeInformation.isSizeAble()) {
            if(fieldSize != -1) createQueryBuilder.append("(").append(fieldSize).append(")");
            else if(dataTypeInformation.getDefaultSize() != -1) createQueryBuilder.append("(").append(dataTypeInformation.getDefaultSize()).append(")");
        }
        if(defaultValue != null) {
            this.values.add(defaultValue);
            createQueryBuilder.append(" DEFAULT ?");
        }
        if(createOptions.length != 0) {
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
                        createQueryBuilder.append(" PRIMARY KEY");
                        break;
                    }
                    case NOT_NULL: {
                        createQueryBuilder.append(" NOT NULL");
                        break;
                    }
                    default: {
                        createQueryBuilder.append(" ").append(createOption.toString());
                    }
                }
            }
        }
        if(uniqueIndex) {
            createQueryBuilder.append(",UNIQUE INDEX `").append(this.name).append(field).append("`(`").append(field).append("`)");
        }else if(index) {
            createQueryBuilder.append(",INDEX `").append(this.name).append(field).append("`(`").append(field).append("`)");
        }
        if(foreignKey != null) buildForeignKey(field, foreignKey);
        return this;
    }

    @Override
    public CreateQuery engine(String engine) {
        this.engine = engine;
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
    public QueryResult execute(boolean commit, Object... values) {
        try(Connection connection = this.database.getDriver().getConnection()) {
            int index = 1;
            int valueGet = 0;
            PreparedStatement preparedStatement = connection.prepareStatement(buildExecuteString());
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
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public QueryResult execute(Object... values) {
        return execute(true, values);
    }

    private CreateQuery buildForeignKey(String field, ForeignKey foreignKey) {
        if(!first) createQueryBuilder.append(",");
        else first = false;
        createQueryBuilder.append("FOREIGN KEY(`")
                .append(field)
                .append("`) REFERENCES `")
                .append(foreignKey.getDatabase())
                .append("`.`")
                .append(foreignKey.getCollection())
                .append("`(`")
                .append(foreignKey.getField()).append("`)");
        if(foreignKey.getDeleteOption() != null && foreignKey.getDeleteOption() != ForeignKey.Option.DEFAULT) {
            createQueryBuilder.append(" ON DELETE ").append(foreignKey.getDeleteOption().toString().replace("_", " "));
        }
        if(foreignKey.getUpdateOption() != null && foreignKey.getUpdateOption() != ForeignKey.Option.DEFAULT) {
            createQueryBuilder.append(" ON UPDATE ").append(foreignKey.getDeleteOption().toString().replace("_", " "));
        }
        return this;
    }

    @Override
    public String buildExecuteString(Object... values) {
        return mainQuery +
                createQueryBuilder +
                ")" +
                (engine != null ? " ENGINE=" + this.engine : "") +
                ";";
    }
}