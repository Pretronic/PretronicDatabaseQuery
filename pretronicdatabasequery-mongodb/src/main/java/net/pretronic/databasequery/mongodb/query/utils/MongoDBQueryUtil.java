/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 10.04.20, 19:05
 * @web %web%
 *
 * The PretronicDatabaseQuery Project is under the Apache License, version 2.0 (the "License");
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

package net.pretronic.databasequery.mongodb.query.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.Aggregation;
import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.common.query.type.AbstractChangeAndSearchQuery;
import net.pretronic.databasequery.common.query.type.AbstractSearchQuery;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import net.pretronic.libraries.utility.Convert;
import org.bson.*;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.function.BiConsumer;

public final class MongoDBQueryUtil {

    public static void buildEntries(BuildContext context, List<AbstractSearchQuery.Entry> entries) {
        for (AbstractSearchQuery.Entry entry : entries) {
            buildEntry(context, entry);
        }
    }

    public static void buildEntry(BuildContext context, AbstractSearchQuery.Entry entry) {
        if(entry instanceof AbstractSearchQuery.ConditionEntry) {
            buildConditionEntry(context, (AbstractSearchQuery.ConditionEntry) entry);
        } else if(entry instanceof AbstractSearchQuery.OperationEntry) {
            buildOperationEntry(context, (AbstractSearchQuery.OperationEntry) entry);
        } else if(entry instanceof AbstractSearchQuery.JoinEntry) {
            buildJoinEntry(context, (AbstractSearchQuery.JoinEntry) entry);
        } else if(entry instanceof AbstractSearchQuery.LimitEntry) {
            buildLimitEntry(context, (AbstractSearchQuery.LimitEntry) entry);
        } else if(entry instanceof AbstractSearchQuery.OrderByEntry) {
            buildOrderByEntry(context, (AbstractSearchQuery.OrderByEntry) entry);
        } else if(entry instanceof AbstractSearchQuery.GroupByEntry) {
            buildGroupByEntry(context, (AbstractSearchQuery.GroupByEntry) entry);
        }
    }

    public static void buildConditionEntry(BuildContext context, AbstractSearchQuery.ConditionEntry entry) {
        switch (entry.getType()) {
            case WHERE_NULL:
            case WHERE: {
                buildConditionAggregationGroup(context, entry);
                context.add(Aggregates.match(buildNegateConditionEntry(context.negate,
                        Filters.eq(entry.getField(), context.getValue(entry.getValue1())))));
                break;
            }
            case WHERE_LIKE: {
                buildConditionAggregationGroup(context, entry);
                context.add(Aggregates.match(buildNegateConditionEntry(context.negate,
                        Filters.regex(entry.getField(), (String) context.getValue(entry.getValue1())))));
                break;
            }
            case WHERE_IN: {
                context.add(Aggregates.match(buildNegateConditionEntry(context.negate,
                        Filters.in(entry.getField(), (List<Object>) context.getValue(entry.getValue1())))));
                break;
            }
            case WHERE_BETWEEN: {
                context.add(Aggregates.match(buildNegateConditionEntry(context.negate,
                        Filters.or(Filters.gte(entry.getField(), context.getValue(entry.getValue1())),
                                Filters.lte(entry.getField(), context.getValue(entry.getExtra()))))));
                break;
            }
            case WHERE_LOWER: {
                buildConditionAggregationGroup(context, entry);
                context.add(Aggregates.match(buildNegateConditionEntry(context.negate,
                        Filters.lt(entry.getField(), context.getValue(entry.getValue1())))));
                break;
            }
            case WHERE_HIGHER: {
                buildConditionAggregationGroup(context, entry);
                context.add(Aggregates.match(buildNegateConditionEntry(context.negate,
                        Filters.gt(entry.getField(), context.getValue(entry.getValue1())))));
                break;
            }
        }
    }

    public static Bson buildNegateConditionEntry(boolean negate, Bson bson) {
        return negate ? Filters.not(bson) : bson;
    }

    public static void buildConditionAggregationGroup(BuildContext context, AbstractSearchQuery.ConditionEntry entry) {
        if(entry.getExtra() instanceof Aggregation) {
            Aggregation aggregation = (Aggregation) entry.getExtra();
            context.add(Aggregates.group("$"+entry.getField(), new BsonField(entry.getField(),
                    new BsonDocument("$"+aggregation.toString().toLowerCase(), convertToBsonValue(context.getValue(entry.getValue1()))))));
        }
    }

    public static void buildOperationEntry(BuildContext context, AbstractSearchQuery.OperationEntry entry) {
        switch (entry.getType()) {
            case AND: {
                context.add(Filters.and(andOr(context, entry)));
                break;
            }
            case OR: {
                context.add(Filters.or(andOr(context, entry)));
                break;
            }
            case NOT: {
                for (AbstractSearchQuery.Entry child : entry.getEntries()) {
                    BuildContext childContext = BuildContext.newContext(context);
                    childContext.negate = true;
                    buildEntry(childContext, child);
                    context.addAll(childContext.findQuery);
                }
            }
        }
    }

    public static List<Bson> andOr(BuildContext context, AbstractSearchQuery.OperationEntry entry) {
        List<Bson> childFinds = new ArrayList<>();
        for (AbstractSearchQuery.Entry child : entry.getEntries()) {
            BuildContext childFind = BuildContext.newContext(context);
            buildEntry(childFind, child);
            childFinds.addAll(childFind.findQuery);
        }
        return childFinds;
    }

    public static void buildJoinEntry(BuildContext context, AbstractSearchQuery.JoinEntry entry) {
        List<Bson> matches = new ArrayList<>(entry.getOnEntries().size());
        List<Variable<String>> variables = new ArrayList<>();

        for (AbstractSearchQuery.JoinOnEntry onEntry : entry.getOnEntries()) {
            Variable<String> variable = new Variable<>(
                    onEntry.getColumn2(), "$"+onEntry.getColumn2());
            matches.add(new Document("$eq", new BsonArray(Arrays.asList(
                    new BsonString("$"+onEntry.getColumn1()),
                    new BsonString("$$"+variable.getName())))));
            variables.add(variable);
        }

        Bson pipeline = Aggregates.match(Filters.expr(Filters.and(matches)));



        Bson lookUp = Aggregates.lookup(entry.getCollection().getName(), variables, Collections.singletonList(pipeline),
                "result"+entry.getCollection().getName());

        context.add(lookUp);
    }

    public static void buildLimitEntry(BuildContext context, AbstractSearchQuery.LimitEntry entry) {
        context.add(Aggregates.limit(context.getValue(entry.getLimit())+context.getValue(entry.getOffset())));
        context.add(Aggregates.skip(context.getValue(entry.getOffset())));
    }

    public static void buildOrderByEntry(BuildContext context, AbstractSearchQuery.OrderByEntry entry) {
        if(entry.getAggregation() != null) {
            Bson sort = Aggregates.unwind("$"+entry.getField());
            Bson group = Aggregates.group("$_id", Accumulators.sum(entry.getField(), "$"+entry.getField()));
            context.add(sort);
            context.add(group);
        }
        if(entry.getOrder() == SearchOrder.ASC) {
            context.add(Aggregates.sort(Sorts.ascending(entry.getField())));
        } else {
            context.add(Aggregates.sort(Sorts.descending(entry.getField())));
        }
    }

    public static void buildGroupByEntry(BuildContext context, AbstractSearchQuery.GroupByEntry entry) {
        if(entry.getAggregation() == null) {
            context.add(Aggregates.group("$"+entry.getField()));
        } else {
            context.add(Aggregates.group("$"+entry.getField(), new BsonField(entry.getField(),
                    new BsonDocument("$"+entry.getAggregation().toString().toLowerCase(),
                    new BsonInt32(1)))));
        }
    }

    public static void printQuery(BuildContext context) {
        for (Bson bson : context.getFindQuery()) {
            context.collection.getDatabase().getLogger().debug("["+bson.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry())+"]");
        }
    }

    public static Document prepareUpdate(BuildContext context, MongoDBDatabaseCollection collection, Collection<AbstractChangeAndSearchQuery.ChangeAndSearchEntry> setEntries) {
        DefaultQueryResultEntry entry = new DefaultQueryResultEntry(collection.getDatabase().getDriver());

        Document update = new Document();
        Document set = new Document();
        Document inc = new Document();
        Document mul = new Document();
        System.out.println("size"+setEntries.size());
        for (AbstractChangeAndSearchQuery.ChangeAndSearchEntry changeAndSearchEntry : setEntries) {
            System.out.println(changeAndSearchEntry.getField()+":"+changeAndSearchEntry.getOperator()+":"+changeAndSearchEntry.getValue());
            Object value = context.getValue(changeAndSearchEntry.getValue());
            if(changeAndSearchEntry.getOperator() == null) {
                set.append(changeAndSearchEntry.getField(), value);
            } else {
                AbstractChangeAndSearchQuery.ChangeAndSearchEntry.ArithmeticOperator operator = changeAndSearchEntry.getOperator();
                switch (operator) {
                    case ADD: {
                        inc.append(changeAndSearchEntry.getField(), value);
                        break;
                    }
                    case SUBTRACT: {
                        inc.append(changeAndSearchEntry.getField(), createCounterPart((Number) value));
                        break;
                    }
                    case MULTIPLY: {
                        mul.append(changeAndSearchEntry.getField(), value);
                        break;
                    }
                    case DIVIDE: {
                        double divider = 1/((Number) value).doubleValue();
                        mul.append(changeAndSearchEntry.getField(), divider);
                        break;
                    }
                }
            }
        }
        if(!set.isEmpty()) update.append("$set", set);
        if(!inc.isEmpty()) update.append("$inc", inc);
        if(!mul.isEmpty()) update.append("$mul", mul);
        return update;
    }

    public static QueryResult createUpdate(MongoDBDatabaseCollection collection, List<AbstractSearchQuery.Entry> entries, Object[] values, BiConsumer<Document, Document> consumer) {
        BuildContext context = BuildContext.newContext(values, collection);
        List<AbstractChangeAndSearchQuery.ChangeAndSearchEntry> setEntries = MongoDBQueryUtil.collectChangeAndSearchEntries(context, entries);

        MongoDBQueryUtil.printQuery(context);

        DefaultQueryResult result = new DefaultQueryResult();
        MongoCursor<Document> cursor = collection.getCollection().aggregate(context.getFindQuery()).cursor();
        Document update = MongoDBQueryUtil.prepareUpdate(context, collection, setEntries);
        while(cursor.hasNext()) {
            Document document = cursor.next();
            DefaultQueryResultEntry entry = new DefaultQueryResultEntry(collection.getDatabase().getDriver());
            document.forEach(entry::addEntry);
            result.addEntry(entry);
            //collection.getCollection().updateOne(document, update);
            consumer.accept(document, update);
        }
        return result;
    }

    private static Number createCounterPart(Number number) {
        if(number instanceof Integer) return -number.intValue();
        if(number instanceof Long) return -number.longValue();
        if(number instanceof Float) return -number.floatValue();
        if(number instanceof Byte) return -number.byteValue();
        if(number instanceof Short) return -number.shortValue();
        return -number.doubleValue();
    }

    public static List<AbstractChangeAndSearchQuery.ChangeAndSearchEntry> collectChangeAndSearchEntries(BuildContext context, List<AbstractSearchQuery.Entry> entries) {
        List<AbstractChangeAndSearchQuery.ChangeAndSearchEntry> setEntries = new ArrayList<>();
        for (AbstractSearchQuery.Entry entry : entries) {
            if(entry instanceof AbstractChangeAndSearchQuery.ChangeAndSearchEntry) {
                setEntries.add((AbstractChangeAndSearchQuery.ChangeAndSearchEntry) entry);
            } else {
                MongoDBQueryUtil.buildEntry(context, entry);
            }
        }
        return setEntries;
    }

    public static BsonValue convertToBsonValue(Object value) {
        if(value instanceof Double || value instanceof Float) return new BsonDouble((double) value);
        else if(value instanceof Boolean) return new BsonBoolean((boolean) value);
        else if(value instanceof Integer || value instanceof Byte || value instanceof Short) return new BsonInt32((int) value);
        else if(value instanceof Long) return new BsonInt64((long) value);
        else if(value instanceof Byte[]) return new BsonBinary((byte[]) value);
        return new BsonString((String) value);
    }
}
