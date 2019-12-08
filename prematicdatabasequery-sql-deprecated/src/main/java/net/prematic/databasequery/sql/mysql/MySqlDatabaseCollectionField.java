/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 14.07.19, 21:26
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

import net.prematic.databasequery.api.collection.field.CollectionField;
import net.prematic.databasequery.api.query.ForeignKey;
import net.prematic.databasequery.api.datatype.DataType;
import net.prematic.databasequery.api.exceptions.DatabaseQueryException;
import net.prematic.databasequery.api.collection.field.FieldOption;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public class MySqlDatabaseCollectionField implements CollectionField {

    private final MySqlDatabaseCollection databaseCollection;
    private String name;
    private DataType dataType;
    private int fieldSize;
    private Object defaultValue;
    private final Collection<FieldOption> createOptions;

    public MySqlDatabaseCollectionField(MySqlDatabaseCollection databaseCollection, String name) {
        this.databaseCollection = databaseCollection;
        this.name = name;
        this.createOptions = new HashSet<>();
        String query = "DESCRIBE `";
        if(databaseCollection.getDatabase().getDriver().getConfig().isMultipleDatabaseConnectionsAble()) {
            query+=databaseCollection.getDatabase().getName() + "`.`";
        }
        query+=databaseCollection.getName() + "`";
        String finalQuery = query;
        this.databaseCollection.getDatabase().executeResultQuery(query, true, preparedStatement -> {}
                , resultSet -> {
                    if (resultSet.next()) {
                        String dataTypeName = resultSet.getString("Type");
                        this.dataType = databaseCollection.getDatabase().getDriver()
                                .getDataTypeInformationByName(dataTypeName.contains("(") ? dataTypeName.split("\\(")[0] : dataTypeName)
                                .getDataType();
                        if(dataTypeName.contains("(")) {
                            this.fieldSize = Integer.parseInt(dataTypeName.substring(dataTypeName.indexOf("("), dataTypeName.length()-1));
                        } else {
                            this.fieldSize = -1;
                        }
                        this.defaultValue = resultSet.getObject("Default");
                    } else {
                        throw new DatabaseQueryException(String.format("Collection field %s was not found", name));
                    }
                    return null;
                });
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DataType getType() {
        return this.dataType;
    }

    @Override
    public int getFieldSize() {
        return this.fieldSize;
    }

    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public Collection<FieldOption> getCreateOptions() {
        return this.createOptions;
    }

    @Override
    public void setName(String name) {
        String query  = "ALTER TABLE `";
        if(this.databaseCollection.getDatabase().getDriver().getConfig().isMultipleDatabaseConnectionsAble()) {
            query+=this.databaseCollection.getDatabase().getName() + "`.`";
        }
        query+=this.databaseCollection.getName()
                + "` CHANGE COLUMN `" + this.name + "` `" + name + "` " +
                this.databaseCollection.getDatabase().getDriver().getDataTypeInformationByDataType(this.dataType).getName();
        if(this.fieldSize != -1) query+= "(" + this.fieldSize + ");";
        this.databaseCollection.getDatabase().executeSimpleUpdateQuery(query, true);
    }

    @Override
    public void setFieldSize(int size) {
        String query  = "ALTER TABLE `";
        if(this.databaseCollection.getDatabase().getDriver().getConfig().isMultipleDatabaseConnectionsAble()) {
            query+=this.databaseCollection.getDatabase().getName() + "`.`";
        }
        query+=this.databaseCollection.getName() +
                "` MODIFY `" +  this.name + "` " + this.databaseCollection.getDatabase().getDriver().getDataTypeInformationByDataType(dataType).getName()
                + "(" + size + ");";
        this.databaseCollection.getDatabase().executeSimpleUpdateQuery(query, true);
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        String query ="ALTER TABLE `";
        if(this.databaseCollection.getDatabase().getDriver().getConfig().isMultipleDatabaseConnectionsAble()) {
            query+=this.databaseCollection.getDatabase().getName() + "`.`";
        }
        query+= this.databaseCollection.getName() +
                "` ALTER COLUMN `" + this.name + "` SET DEFAULT ?";
        String finalQuery = query;
        this.databaseCollection.getDatabase().executeUpdateQuery(query, true, preparedStatement -> {
            try {
                preparedStatement.setObject(1, defaultValue);
            } catch (SQLException exception) {
                this.databaseCollection.getDatabase().getDriver().handleDatabaseQueryExecuteFailedException(exception, finalQuery);
            }
        });
    }

    @Override
    public void addCreateOption(FieldOption createOption) {

    }

    @Override
    public void removeCreateOption(FieldOption createOption) {

    }

    @Override
    public void addForeignKey(ForeignKey foreignKey) {
        StringBuilder query = new StringBuilder().append("ALTER TABLE `");
        if(this.databaseCollection.getDatabase().getDriver().getConfig().isMultipleDatabaseConnectionsAble()) {
            query.append(this.databaseCollection.getDatabase().getName())
                    .append("`.`");
        }
        query.append(this.databaseCollection.getName())
                .append(" ADD CONSTRAINT `")
                .append(this.databaseCollection.getDatabase().getName())
                .append(this.databaseCollection.getName())
                .append(this.name)
                .append("` FOREIGN KEY(`")
                .append(this.name)
                .append("`) REFERENCES `")
                .append(foreignKey.getDatabase())
                .append("`.`")
                .append(foreignKey.getCollection())
                .append("`(`")
                .append(foreignKey.getField()).append("`)");

        if(foreignKey.getDeleteOption() != null && foreignKey.getDeleteOption() != ForeignKey.Option.DEFAULT) {
            query.append(" ON DELETE ").append(foreignKey.getDeleteOption().toString().replace("_", " "));
        }

        if(foreignKey.getUpdateOption() != null && foreignKey.getUpdateOption() != ForeignKey.Option.DEFAULT) {
            query.append(" ON UPDATE ").append(foreignKey.getDeleteOption().toString().replace("_", " "));
        }
        this.databaseCollection.getDatabase().executeSimpleUpdateQuery(query.append(";").toString(), true);
    }

    @Override
    public void removeForeignKey() {
        String sql = "ALTER TABLE `" +
                this.databaseCollection.getDatabase().getName() + "`.`" +
                this.databaseCollection.getName() +
                "` DROP FOREIGN KEY `" +
                this.databaseCollection.getDatabase().getName()
                +this.databaseCollection.getName()
                +this.name + "`;";
        this.databaseCollection.getDatabase().executeSimpleUpdateQuery(sql, true);
    }
}