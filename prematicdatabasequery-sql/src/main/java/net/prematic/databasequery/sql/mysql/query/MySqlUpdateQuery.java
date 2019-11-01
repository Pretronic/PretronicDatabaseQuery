/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 26.05.19, 16:01
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

import net.prematic.databasequery.api.query.UpdateQuery;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;
import net.prematic.libraries.utility.Validate;

public class MySqlUpdateQuery extends MySqlSearchQueryHelper<UpdateQuery> implements UpdateQuery {

    private boolean first;

    public MySqlUpdateQuery(MySqlDatabaseCollection databaseCollection) {
        super(databaseCollection);
        this.queryBuilder.append("UPDATE `");

        if(databaseCollection.getDatabase().getDriver().getConfig().isMultipleDatabaseConnectionsAble()) {
            this.queryBuilder.append(databaseCollection.getDatabase().getName())
                    .append("`.`");
        }

        this.queryBuilder.append(databaseCollection.getName())
                .append("` ");
        this.first = true;
    }

    @Override
    public UpdateQuery set(String field, Object value) {
        Validate.notNull(field, "Field can't be null.");
        if(this.first) {
            this.queryBuilder.append("SET ");
        } else {
            this.queryBuilder.append(",");
        }
        this.queryBuilder.append("`").append(field).append("`").append("=?");
        this.values.add(value);
        return this;
    }

    @Override
    public String buildExecuteString(Object... values) {
        return this.queryBuilder + ";";
    }
}