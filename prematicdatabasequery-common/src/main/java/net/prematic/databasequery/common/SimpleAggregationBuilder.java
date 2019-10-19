/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.10.19, 20:45
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

package net.prematic.databasequery.common;

import net.prematic.databasequery.api.Database;
import net.prematic.databasequery.api.aggregation.Aggregation;
import net.prematic.databasequery.api.aggregation.AggregationBuilder;
import net.prematic.libraries.utility.map.Pair;

import java.util.ArrayList;
import java.util.List;

public class SimpleAggregationBuilder implements AggregationBuilder {

    private final Database database;
    private final List<Entry> entries;

    public SimpleAggregationBuilder(Database database) {
        this.database = database;
        this.entries = new ArrayList<>();
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    @Override
    public AggregationBuilder field(String field) {
        return addEntry(new Entry(Entry.Type.FIELD, field));
    }

    @Override
    public AggregationBuilder operator(String operator) {
        return addEntry(new Entry(Entry.Type.OPERATOR, operator));
    }

    @Override
    public AggregationBuilder aggregation(Aggregation aggregation, String field) {
        return addEntry(new Entry(Entry.Type.AGGREGATION, new Pair<>(aggregation, field)));
    }

    @Override
    public AggregationBuilder builder(AggregationBuilder builder) {
        return addEntry(new Entry(Entry.Type.BUILDER, builder));
    }

    @Override
    public AggregationBuilder builder(Consumer consumer) {
        AggregationBuilder aggregationBuilder = this.database.newAggregationBuilder(false);
        consumer.accept(aggregationBuilder);
        return builder(aggregationBuilder);
    }

    @Override
    public AggregationBuilder value(Object value) {
        return addEntry(new Entry(Entry.Type.VALUE, value));
    }

    @Override
    public AggregationBuilder alias(String alias) {
        return addEntry(new Entry(Entry.Type.ALIAS, alias));
    }

    private AggregationBuilder addEntry(Entry entry) {
        this.entries.add(entry);
        return this;
    }

    public static class Entry {

        private final Type type;
        private final Object value;

        public Entry(Type type, Object value) {
            this.type = type;
            this.value = value;
        }

        public Type getType() {
            return this.type;
        }

        public Object getValue() {
            return this.value;
        }

        public enum Type {

            FIELD,
            OPERATOR,
            AGGREGATION,
            BUILDER,
            VALUE,
            ALIAS

        }
    }
}