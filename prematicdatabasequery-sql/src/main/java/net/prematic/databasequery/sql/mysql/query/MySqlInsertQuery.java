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

import net.prematic.databasequery.core.DatabaseCollection;
import net.prematic.databasequery.core.QueryOperator;
import net.prematic.databasequery.core.datatype.DataTypeAdapter;
import net.prematic.databasequery.core.impl.query.AbstractInsertQuery;
import net.prematic.databasequery.core.impl.query.QueryEntry;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlInsertQuery extends AbstractInsertQuery implements QueryStringBuildAble {

    private final DatabaseCollection collection;
    private String queryString;

    public MySqlInsertQuery(DatabaseCollection collection) {
        this.collection = collection;
    }

    @Override
    public QueryResult execute(Object... values) {
        try(Connection connection = ((MySqlDatabaseCollection)this.collection).getDatabase().getDriver().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(buildExecuteString());
            List<Integer> indexToPrepare = new ArrayList<>();
            int index = 1;
            for (QueryEntry queryEntry : getEntries()) {
                if(queryEntry.getOperator() == QueryOperator.SET) {
                    if(queryEntry.hasData("value")) {
                        Object value = queryEntry.getData("value");

                        DataTypeAdapter adapter = ((MySqlDatabaseCollection) this.collection).getDatabase().getDriver().getDataTypeAdapterByWriteClass(value.getClass());
                        preparedStatement.setObject(index, adapter != null ? adapter.write(value) : value);
                    }
                    else indexToPrepare.add(index);
                    index++;
                }
            }
            index = 0;
            for (Object value : values) {
                preparedStatement.setObject(indexToPrepare.get(index), value);
                index++;
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
        StringBuilder subQueryString = new StringBuilder().append("(");
        queryString.append("INSERT INTO `").append(((MySqlDatabaseCollection)this.collection).getDatabase().getName())
                .append("`.`").append(this.collection.getName()).append("` (");
        boolean first = true;
        for (QueryEntry queryEntry : getEntries()) {
            if(!first) {
                queryString.append(",");
                subQueryString.append(",");
            }
            else first = false;
            queryString.append("`").append(queryEntry.getData("field")).append("`");
            subQueryString.append("?");
        }
        this.queryString = queryString.append(") VALUES ").append(subQueryString).append(");").toString();
        return this.queryString;
    }
}