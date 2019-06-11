/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.06.19, 17:01
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

import net.prematic.databasequery.core.Database;
import net.prematic.databasequery.core.aggregation.Aggregation;
import net.prematic.databasequery.core.aggregation.AggregationBuilder;
import net.prematic.databasequery.core.impl.SimpleAggregationBuilder;
import net.prematic.databasequery.core.impl.query.QueryStringBuildAble;
import net.prematic.libraries.utility.map.Pair;

import java.util.ArrayList;
import java.util.List;

public class MySqlAggregationBuilder extends SimpleAggregationBuilder implements QueryStringBuildAble {

    private String queryString, alias;
    private final boolean aliasAble;
    private final List<Object> values;

    public MySqlAggregationBuilder(Database database, boolean aliasAble) {
        super(database);
        this.alias = null;
        this.aliasAble = aliasAble;
        this.values = new ArrayList<>();
    }

    public String getAlias() {
        return alias == null ? buildExecuteString() : this.alias;
    }

    public List<Object> getValues() {
        return values;
    }

    public MySqlAggregationBuilder addValue(Object value) {
        this.values.add(value);
        return this;
    }

    @Override
    public String buildExecuteString(boolean rebuild) {
        if(!rebuild && this.queryString != null) return this.queryString;
        StringBuilder queryString = new StringBuilder();
        for (Entry entry : getEntries()) {
            this.alias = buildAggregationBuilderEntry(entry, queryString);
        }
        if(this.aliasAble && alias != null) {
            queryString.append(" AS `").append(alias).append("`");
        }
        this.queryString = queryString.toString();
        return this.queryString;
    }

    private String buildAggregationBuilderEntry(Entry entry, StringBuilder queryString) {
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
                if(entry.getValue() instanceof Pair) {
                    Pair<Aggregation, String> value = (Pair<Aggregation, String>) entry.getValue();
                    queryString.append(value.getKey()).append("(`").append(value.getValue()).append("`)");
                }
                return null;
            }
            case BUILDER: {
                queryString.append("(");
                for (Entry childEntry : ((AggregationBuilder) entry.getValue()).getEntries()) {
                    buildAggregationBuilderEntry(childEntry, queryString);
                }
                queryString.append(")");
                return null;
            }
            case VALUE: {
                queryString.append("?");
                return null;
            }
            case ALIAS: {
                return (String) entry.getValue();
            }
        }
        return null;
    }
}
