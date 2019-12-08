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

import net.prematic.databasequery.api.Database;
import net.prematic.databasequery.api.query.Aggregation;

import java.util.ArrayList;
import java.util.List;

public class MySqlAggregationBuilder implements AggregationBuilder {

    private final Database database;
    private String alias;
    private final boolean aliasAble;
    private final List<Object> values;
    private final StringBuilder aggregationBuilder;

    public MySqlAggregationBuilder(Database database, boolean aliasAble) {
        this.database = database;
        this.alias = null;
        this.aliasAble = aliasAble;
        this.values = new ArrayList<>();
        this.aggregationBuilder = new StringBuilder();
    }

    public String getAlias() {
        return this.alias == null ? this.aggregationBuilder.toString() : this.alias;
    }

    public StringBuilder getAggregationBuilder() {
        return aggregationBuilder;
    }

    public List<Object> getValues() {
        return values;
    }

    @Override
    public AggregationBuilder field(String field) {
        this.aggregationBuilder.append("`").append(field).append("`");
        return this;
    }

    @Override
    public AggregationBuilder operator(String operator) {
        this.aggregationBuilder.append(" ").append(operator).append(" ");
        return this;
    }

    @Override
    public AggregationBuilder aggregation(Aggregation aggregation, String field) {
        this.aggregationBuilder.append(aggregation).append("(");
        if(field.equals("*")) this.aggregationBuilder.append(field).append(")");
        else this.aggregationBuilder.append("`").append(field).append("`)");
        return this;
    }

    @Override
    public AggregationBuilder builder(AggregationBuilder builder) {
        this.aggregationBuilder.append("(")
                .append(((MySqlAggregationBuilder)builder).aggregationBuilder)
                .append(")");
        return this;
    }

    @Override
    public AggregationBuilder builder(Consumer consumer) {
        AggregationBuilder aggregationBuilder = this.database.newAggregationBuilder(false);
        consumer.accept(aggregationBuilder);
        return builder(aggregationBuilder);
    }

    @Override
    public AggregationBuilder value(Object value) {
        this.values.add(value);
        this.aggregationBuilder.append("?");
        return this;
    }

    @Override
    public AggregationBuilder alias(String alias) {
        if(this.aliasAble) {
            this.aggregationBuilder.append(" AS `").append(alias).append("`");
        }
        return this;
    }
}
