/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.06.19, 23:33
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

import net.prematic.databasequery.core.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.core.impl.query.AbstractInsertQuery;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.mysql.CommitOnExecute;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlInsertQuery extends AbstractInsertQuery implements QueryStringBuildAble, CommitOnExecute {

    private final MySqlDatabaseCollection databaseCollection;
    private int valuesPerField;

    public MySqlInsertQuery(MySqlDatabaseCollection databaseCollection) {
        this.databaseCollection = databaseCollection;
        this.valuesPerField = 0;
    }

    public int getValuesPerField() {
        return valuesPerField;
    }

    @Override
    public QueryResult execute(boolean commit, Object... values) {
        try(Connection connection = this.databaseCollection.getDatabase().getDriver().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(buildExecuteString(values));
            int index = 1;
            int valueGet = 0;
            for (int i = 0; i < this.valuesPerField; i++) {
                for (Entry entry : getEntries()) {
                    Object value;
                    if(entry.getValues().size() > i) {
                        value = entry.getValues().get(i);
                    } else {
                        value = values[valueGet];
                        valueGet++;
                    }
                    DataTypeAdapter adapter = this.databaseCollection.getDatabase().getDriver().getDataTypeAdapterByWriteClass(value.getClass());
                    if(adapter != null) value = adapter.write(value);
                    preparedStatement.setObject(index, value);
                    index++;
                }
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

    @Override
    public String buildExecuteString(Object... values) {
        StringBuilder fieldQueryBuilder = new StringBuilder();
        StringBuilder valueQueryBuilder = new StringBuilder();

        int fieldCount = getEntries().size();
        int valueCount = values.length;

        for (Entry entry : getEntries()) {
            if(fieldQueryBuilder.length() == 0) fieldQueryBuilder.append("(");
            else fieldQueryBuilder.append(",");
            fieldQueryBuilder.append("`").append(entry.getField()).append("`");
            valueCount+=entry.getValues().size();
        }

        this.valuesPerField = valueCount/fieldCount;

        for (int i = 0; i < valuesPerField; i++) {
            if(valueQueryBuilder.length() != 0) valueQueryBuilder.append(",(");
            else valueQueryBuilder.append(" VALUES (");
            for (int i1 = 0; i1 < fieldCount; i1++) {
                if(i1 != 0) valueQueryBuilder.append(",");
                valueQueryBuilder.append("?");
            }
            valueQueryBuilder.append(")");
        }

        return "INSERT INTO `" +
                this.databaseCollection.getDatabase().getName() +
                "`.`" +
                this.databaseCollection.getName() +
                "` " +
                fieldQueryBuilder +
                ")" +
                valueQueryBuilder +
                ";";
    }
}