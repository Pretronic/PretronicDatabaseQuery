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

import net.prematic.databasequery.core.query.UpdateQuery;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;

public class MySqlUpdateQuery extends MySqlSearchQueryHelper<UpdateQuery> implements UpdateQuery {

    private final StringBuilder setQueryBuilder;
    private final String mainQuery;

    public MySqlUpdateQuery(MySqlDatabaseCollection databaseCollection) {
        super(databaseCollection);
        this.setQueryBuilder = new StringBuilder();
        this.mainQuery = "UPDATE `"
                + databaseCollection.getDatabase().getName()
                + "`.`"
                + databaseCollection.getName()
                + "` ";
    }

    @Override
    public UpdateQuery set(String field, Object value) {
        if(this.setQueryBuilder.length() == 0) {
            this.setQueryBuilder.append("SET ");
        } else {
            this.setQueryBuilder.append(",");
        }
        this.setQueryBuilder.append("`").append(field).append("`").append("=?");
        this.values.add(value);
        return this;
    }

    @Override
    public String buildExecuteString(Object... values) {
        StringBuilder queryString = new StringBuilder();
        queryString.append(this.mainQuery)
                .append(this.setQueryBuilder);
        if(this.searchQueryBuilder.length() != 0) queryString.append(this.searchQueryBuilder);
        if(this.whereAggregationQueryBuilder.length() != 0) queryString.append(this.whereAggregationQueryBuilder);
        if(this.groupByQueryBuilder.length() != 0) queryString.append(this.groupByQueryBuilder);
        if(this.orderByQueryBuilder.length() != 0) queryString.append(this.orderByQueryBuilder);
        if(this.limit != null) queryString.append(this.limit);
        return queryString.append(";").toString();
    }
}