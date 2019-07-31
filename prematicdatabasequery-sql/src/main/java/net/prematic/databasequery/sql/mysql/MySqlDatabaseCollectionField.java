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

import net.prematic.databasequery.core.DatabaseCollectionField;
import net.prematic.databasequery.core.ForeignKey;
import net.prematic.databasequery.core.datatype.DataType;
import net.prematic.databasequery.core.exceptions.DatabaseQueryExecuteFailedException;
import net.prematic.databasequery.core.query.option.CreateOption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public class MySqlDatabaseCollectionField implements DatabaseCollectionField {

    private final MySqlDatabaseCollection databaseCollection;
    private String name;
    private DataType dataType;
    private int fieldSize;
    private Object defaultValue;
    private final Collection<CreateOption> createOptions;

    public MySqlDatabaseCollectionField(MySqlDatabaseCollection databaseCollection, String name) {
        this.databaseCollection = databaseCollection;
        this.name = name;
        this.createOptions = new HashSet<>();
        try(Connection connection = databaseCollection.getDatabase().getDriver().getConnection()) {
            String sql = "DESCRIBE `" + databaseCollection.getDatabase().getName() + "`.`" + databaseCollection.getName() + "`";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String dataTypeName = resultSet.getString("Type");
                this.dataType = databaseCollection.getDatabase().getDriver()
                        .getDataTypeInformationByName(dataTypeName.contains("(") ? dataTypeName.split("\\(")[0] : dataTypeName)
                        .getDataType();
                if(dataTypeName.contains("(")) {
                    this.fieldSize = Integer.valueOf(dataTypeName.substring(dataTypeName.indexOf("("), dataTypeName.length()-1));
                } else {
                    this.fieldSize = -1;
                }
                this.defaultValue = resultSet.getObject("Default");
            } else {

            }
            connection.commit();
        } catch (SQLException exception) {
            throw new DatabaseQueryExecuteFailedException(exception.getMessage(), exception);
        }
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
    public Collection<CreateOption> getCreateOptions() {
        return this.createOptions;
    }

    @Override
    public void setName(String name) {
        String sql  = "ALTER TABLE `" + this.databaseCollection.getDatabase().getName() + "`.`" + this.databaseCollection.getName()
                + "` CHANGE COLUMN `" + this.name + "` `" + name + "` " +
                this.databaseCollection.getDatabase().getDriver().getDataTypeInformationByDataType(this.dataType).getName();
        if(this.fieldSize != -1) sql+= "(" + this.fieldSize + ");";
        this.databaseCollection.getDatabase().getDriver().executeSimpleUpdateQuery(sql);
    }

    @Override
    public void setFieldSize(int size) {
        String sql  = "ALTER TABLE `" + this.databaseCollection.getDatabase().getName() + "`.`" + this.databaseCollection.getName() +
                "` MODIFY `" +  this.name + "` " + this.databaseCollection.getDatabase().getDriver().getDataTypeInformationByDataType(dataType).getName()
                + "(" + size + ");";
        this.databaseCollection.getDatabase().getDriver().executeSimpleUpdateQuery(sql);
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        String sql ="ALTER TABLE `" + this.databaseCollection.getDatabase().getName() + "`.`" + this.databaseCollection.getName() +
                "` ALTER COLUMN `" + this.name + "` SET DEFAULT ?";
        this.databaseCollection.getDatabase().getDriver().executeUpdateQuery(sql, preparedStatement -> {
            try {
                preparedStatement.setObject(1, defaultValue);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void addCreateOption(CreateOption createOption) {

    }

    @Override
    public void removeCreateOption(CreateOption createOption) {

    }

    @Override
    public void addForeignKey(ForeignKey foreignKey) {
        StringBuilder sql = new StringBuilder()
                .append("ALTER TABLE `")
                .append(this.databaseCollection.getDatabase().getName())
                .append("`.`")
                .append(this.databaseCollection.getName())
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
            sql.append(" ON DELETE ").append(foreignKey.getDeleteOption().toString().replace("_", " "));
        }

        if(foreignKey.getUpdateOption() != null && foreignKey.getUpdateOption() != ForeignKey.Option.DEFAULT) {
            sql.append(" ON UPDATE ").append(foreignKey.getDeleteOption().toString().replace("_", " "));
        }
        this.databaseCollection.getDatabase().getDriver().executeSimpleUpdateQuery(sql.append(";").toString());
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
        this.databaseCollection.getDatabase().getDriver().executeSimpleUpdateQuery(sql);
    }
}