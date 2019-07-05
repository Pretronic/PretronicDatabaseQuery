/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.06.19, 15:40
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

package net.prematic.databasequery.core.impl;

import net.prematic.databasequery.core.Database;
import net.prematic.databasequery.core.aggregation.Aggregation;
import net.prematic.databasequery.core.aggregation.AggregationBuilder;

public class SimpleAggregationBuilder implements AggregationBuilder {

    private final Database database;
    //private final List<Entry> entries;

    public SimpleAggregationBuilder(Database database) {
        this.database = database;
        //this.entries = new ArrayList<>();
    }

    @Override
    public AggregationBuilder field(String field) {
        return null;
    }

    @Override
    public AggregationBuilder operator(String operator) {
        return null;
    }

    @Override
    public AggregationBuilder aggregation(Aggregation aggregation, String field) {
        return null;
    }

    @Override
    public AggregationBuilder builder(AggregationBuilder builder) {
        return null;
    }

    @Override
    public AggregationBuilder builder(Consumer consumer) {
        return null;
    }

    @Override
    public AggregationBuilder value(Object value) {
        return null;
    }

    @Override
    public AggregationBuilder alias(String alias) {
        return null;
    }

    /*@Override
    public List<Entry> getEntries() {
        return this.entries;
    }

    @Override
    public AggregationBuilder field(String field) {
        return addEntry(new SimpleEntry(Entry.Type.FIELD, field));
    }

    @Override
    public AggregationBuilder operator(String operator) {
        return addEntry(new SimpleEntry(Entry.Type.OPERATOR, operator));
    }

    @Override
    public AggregationBuilder aggregation(Aggregation aggregation, String field) {
        return addEntry(new SimpleEntry(Entry.Type.AGGREGATION, new Pair<>(aggregation, field)));
    }

    @Override
    public AggregationBuilder builder(AggregationBuilder builder) {
        return addEntry(new SimpleEntry(Entry.Type.BUILDER, builder));
    }

    @Override
    public AggregationBuilder builder(Consumer consumer) {
        AggregationBuilder aggregationBuilder = this.database.newAggregationBuilder(false);
        consumer.accept(aggregationBuilder);
        return builder(aggregationBuilder);
    }

    @Override
    public AggregationBuilder value(Object value) {
        return addEntry(new SimpleEntry(Entry.Type.VALUE, value));
    }

    @Override
    public AggregationBuilder alias(String alias) {
        return addEntry(new SimpleEntry(Entry.Type.ALIAS, alias));
    }

    private AggregationBuilder addEntry(Entry entry) {
        this.entries.add(entry);
        return this;
    }

    private class SimpleEntry implements Entry {

        private final Type type;
        private final Object value;

        public SimpleEntry(Type type, Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public Type getType() {
            return this.type;
        }

        @Override
        public Object getValue() {
            return this.value;
        }
    }*/
}