/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 03.07.19, 18:14
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

import net.prematic.databasequery.core.query.DeleteQuery;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;

public class MySqlDeleteQuery extends MySqlSearchQueryHelper<DeleteQuery> implements DeleteQuery {

    private final String mainQuery;

    public MySqlDeleteQuery(MySqlDatabaseCollection databaseCollection) {
        super(databaseCollection);
        String mainQuery = "DELETE FROM `";

        if(databaseCollection.getDatabase().getDriver().getConfig().isMultipleDatabaseConnectionsAble()) {
            mainQuery+= databaseCollection.getDatabase().getName()
                    + "`.`";
        }


        mainQuery+= databaseCollection.getName() + "` ";
        this.mainQuery = mainQuery;
    }

    @Override
    public String buildExecuteString(Object... values) {
        return this.mainQuery + this.queryBuilder.toString() + ";";
    }
}
