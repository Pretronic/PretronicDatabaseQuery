/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 27.05.19, 18:33
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

import net.prematic.databasequery.core.Aggregation;
import net.prematic.databasequery.core.QueryOperator;
import net.prematic.databasequery.core.impl.query.AbstractFindQuery;
import net.prematic.databasequery.core.impl.query.QueryEntry;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseCollection;
import net.prematic.databasequery.sql.mysql.MySqlUtils;
import net.prematic.libraries.utility.map.Pair;
import java.util.Comparator;
import java.util.List;

public class MySqlFindQuery extends AbstractFindQuery implements QueryStringBuildAble {

    private String queryString;

    public MySqlFindQuery(MySqlDatabaseCollection collection) {
        super(collection);
    }

    @Override
    public QueryResult execute(Object... values) {
        System.out.println(buildExecuteString(false));
        return null;
    }

    @Override
    public String buildExecuteString(boolean rebuild) {
        if(!rebuild && this.queryString != null) return this.queryString;
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT ");

        List<QueryEntry> queryEntries = getEntries();
        queryEntries.sort(Comparator.comparingInt(queryEntry -> MySqlUtils.getQueryOperatorPriority(queryEntry.getOperator())));
        boolean first = true;
        for (QueryEntry queryEntry : queryEntries) {
            if(queryEntry.getOperator() == QueryOperator.GET) {
                if(queryEntry.containsData("fields")) {
                    for (String field : (String[]) queryEntry.getData("fields")) {
                        if(!first) queryString.append(",");
                        else first = false;
                        queryString.append("`").append(field).append("`");
                    }
                } else if(queryEntry.containsData("getBuilders")) {
                    for (GetBuilder getBuilder : (GetBuilder[]) queryEntry.getData("getBuilders")) {
                        if(!first) queryString.append(",");
                        else first = false;
                        queryString.append("(");
                        String alias = null;
                        for (GetBuilder.Entry entry : getBuilder.getEntries()) {
                            alias = buildGetBuilderEntry(entry, queryString);
                        }
                        queryString.append(")");
                        if(alias != null) queryString.append(" AS `").append(alias).append("`");
                    }
                }
            }
        }
        queryString.append(" FROM `").append(getCollection().getName()).append("`");
        MySqlUtils.buildSearchQuery(queryString, queryEntries);
        this.queryString = queryString.append(";").toString();
        return this.queryString;
    }

    private String buildGetBuilderEntry(GetBuilder.Entry entry, StringBuilder queryString) {
        System.out.println(entry);
        switch (entry.getType()) {
            case FIELD: {
                queryString.append("`").append(entry.getValue()).append("`");
                return null;
            }
            case OPERATOR: {
                queryString.append(" ").append(entry.getValue()).append(" ");
                return null;
            }
            case AGGREGATION: {
                Pair<Aggregation, String> value = (Pair<Aggregation, String>) entry.getValue();
                queryString.append(value.getKey()).append("(").append(value.getValue()).append(")");
                return null;
            }
            case ALIAS: {
                return (String) entry.getValue();
            }
            case GET_BUILDER: {
                queryString.append("(");
                for (GetBuilder.Entry childEntry : ((GetBuilder) entry.getValue()).getEntries()) {
                    buildGetBuilderEntry(childEntry, queryString);
                }
                queryString.append(")");
            }
        }
        return null;
    }

    @Override
    public GetBuilder getGetBuilder() {
        return null;
    }
}