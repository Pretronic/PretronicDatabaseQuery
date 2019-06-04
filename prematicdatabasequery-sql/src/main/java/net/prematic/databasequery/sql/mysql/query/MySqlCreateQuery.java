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

import net.prematic.databasequery.core.DataType;
import net.prematic.databasequery.core.ForeignKey;
import net.prematic.databasequery.core.ForeignKeyOption;
import net.prematic.databasequery.core.QueryOperator;
import net.prematic.databasequery.core.impl.DataTypeInformation;
import net.prematic.databasequery.core.impl.query.AbstractCreateQuery;
import net.prematic.databasequery.core.impl.query.QueryEntry;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.query.option.CreateOption;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.mysql.MySqlDatabase;
import net.prematic.databasequery.sql.mysql.MySqlUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class MySqlCreateQuery extends AbstractCreateQuery implements QueryStringBuildAble {

    private final MySqlDatabase database;
    private String queryString;

    public MySqlCreateQuery(String collectionName, MySqlDatabase database) {
        super(collectionName);
        this.database = database;
    }

    @Override
    public QueryResult execute(Object... values) {
        try(Connection connection = database.getDriver().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(buildExecuteString());
            System.out.println(buildExecuteString());
            int index = 1;
            for (QueryEntry entry : getEntries()) {
                if(entry.getOperator() == QueryOperator.CREATE) {
                    if(entry.hasData("defaultValue")) {
                        preparedStatement.setObject(index, entry.getData("defaultValue"));
                        index++;
                    }
                }
            }
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public String buildExecuteString(boolean rebuild) {
        if(!rebuild && this.queryString != null) return this.queryString;
        StringBuilder queryString = new StringBuilder();
        queryString.append("CREATE TABLE IF NOT EXISTS `").append(this.database.getName()).append("`.`").append(getCollectionName()).append("`(");

        List<QueryEntry> queryEntries = getEntries();
        queryEntries.sort(Comparator.comparingInt(queryEntry -> MySqlUtils.getQueryOperatorPriority(queryEntry.getOperator())));
        String engine = null;
        boolean first = true;
        for (QueryEntry queryEntry : queryEntries) {
            switch (queryEntry.getOperator()) {
                case CREATE: {
                    if(!first) queryString.append(",");
                    else first = false;
                    String field = (String) queryEntry.getData("field");
                    boolean uniqueIndex = false;
                    boolean index = false;
                    DataTypeInformation dataTypeInformation = MySqlUtils.getDataTypeInformation((DataType) queryEntry.getData("dataType"));
                    queryString.append("`").append(field).append("` ")
                            .append(dataTypeInformation.getName());
                    if(dataTypeInformation.isSizeAble()) {
                        int fieldSize = (int) queryEntry.getData("fieldSize");
                        if(fieldSize != -1) queryString.append("(").append(fieldSize).append(")");
                    }
                    if(queryEntry.containsData("defaultValue")) {
                        queryString.append(" DEFAULT ?");
                    }
                    if(queryEntry.containsData("createOptions")) {
                        CreateOption[] createOptions = (CreateOption[]) queryEntry.getData("createOptions");
                        for (CreateOption createOption : createOptions) {
                            switch (createOption) {
                                case INDEX: {
                                    index = true;
                                    continue;
                                }
                                case UNIQUE_INDEX: {
                                    uniqueIndex = true;
                                    continue;
                                }
                                case PRIMARY_KEY: {
                                    queryString.append(" PRIMARY KEY");
                                    continue;
                                }
                                case NOT_NULL: {
                                    queryString.append(" NOT NULL");
                                    continue;
                                }
                                default: {
                                    queryString.append(" ").append(createOption.toString());
                                }
                            }
                        }
                    }
                    if(uniqueIndex) {
                        queryString.append(",UNIQUE INDEX `").append(getCollectionName()).append(field).append("`(`").append(field).append("`)");
                    }else if(index) {
                        queryString.append(",INDEX `").append(getCollectionName()).append(field).append("`(`").append(field).append("`)");
                    }
                    if(queryEntry.containsData("foreignKey")) {
                        ForeignKey foreignKey = (ForeignKey) queryEntry.getData("foreignKey");
                        queryString.append(", FOREIGN KEY(`")
                                .append(field)
                                .append("`) REFERENCES `")
                                .append(foreignKey.getDatabase())
                                .append("`.`")
                                .append(foreignKey.getCollection())
                                .append("`(`")
                                .append(foreignKey.getField()).append("`)");
                        if(foreignKey.getDeleteOption() != null && foreignKey.getDeleteOption() != ForeignKeyOption.DEFAULT) {
                            queryString.append(" ON DELETE ").append(foreignKey.getDeleteOption().toString().replace("_", " "));
                        }
                        if(foreignKey.getUpdateOption() != null && foreignKey.getUpdateOption() != ForeignKeyOption.DEFAULT) {
                            queryString.append(" ON UPDATE ").append(foreignKey.getDeleteOption().toString().replace("_", " "));
                        }
                    }
                    continue;
                }
                case ENGINE: {
                    engine = (String) queryEntry.getData("engine");
                    continue;
                }
                case COLLECTION_TYPE: {

                    continue;
                }
            }
        }
        this.queryString = queryString.append(")").append(engine != null ? " ENGINE="+engine : "").append(";").toString();
        return this.queryString;
    }
}