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
import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.common.query.type.AbstractChangeAndSearchQuery;
import net.pretronic.databasequery.common.query.type.AbstractSearchQuery;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import org.bson.*;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
                String operator = context.negate ? "ne" : "eq";
                Document where = new Document(entry.getField(), new Document("$"+operator, context.getValue(entry.getValue1())));

                if(context.parent == null) {
                    context.add(new Document("$match", where));
                } else {
                    context.add(where);
                }
                break;
            }
            case WHERE_LIKE: {
                Document like = new Document()
                        .append("$regex", context.getValue(entry.getValue1()));
                if(context.negate) {
                    like = new Document("$not", like);
                }
                if(context.parent == null) {
                    context.add(new Document("$match", new Document(entry.getField(), like)));
                } else {
                    context.add(new Document(entry.getField(), like));
                }
                break;
            }
            case WHERE_IN: {
                BsonArray values = new BsonArray();
                context.getValue((List<Object>)entry.getValue1()).forEach(value -> values.add(convertToBsonValue(value)));
                String operator = context.negate ? "nin" : "in";
                Document whereIn = new Document(entry.getField(), new Document(operator, values));
                if(context.parent == null) {
                    context.add(new Document("$match", whereIn));
                } else {
                    context.add(whereIn);
                }
                break;
            }
            case WHERE_BETWEEN: {
                BsonDocument gte = new BsonDocument(entry.getField(), new BsonDocument("$gte", convertToBsonValue(context.getValue(entry.getValue1()))));
                BsonDocument lte = new BsonDocument(entry.getField(), new BsonDocument("$lte", convertToBsonValue(context.getValue(entry.getExtra()))));

                BsonArray between = new BsonArray();
                between.add(gte);
                between.add(lte);

                Document and = new Document("$and", between);

                if(context.parent == null) {
                    context.add(new Document("$match", and));
                } else {
                    context.add(and);
                }
                break;
            }
            case WHERE_LOWER: {
                BsonDocument lte = new BsonDocument(entry.getField(), new BsonDocument("$lte", convertToBsonValue(context.getValue(entry.getValue1()))));
                if(context.parent == null) {
                    context.add(new Document("$match", lte));
                } else {
                    context.add(lte);
                }
                break;
            }
            case WHERE_HIGHER: {
                BsonDocument gte = new BsonDocument(entry.getField(), new BsonDocument("$gte", convertToBsonValue(context.getValue(entry.getValue1()))));
                if(context.parent == null) {
                    context.add(new Document("$match", gte));
                } else {
                    context.add(gte);
                }
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + entry.getType());
        }
    }

    public static void buildOperationEntry(BuildContext context, AbstractSearchQuery.OperationEntry entry) {
        switch (entry.getType()) {
            case AND: {
                Document and = new Document("$and", andOr(context, entry));
                if(context.parent == null) context.add(new Document("$match", and));
                else context.add(and);
                break;
            }
            case OR: {
                Document and = new Document("$or", andOr(context, entry));
                if(context.parent == null) context.add(new Document("$match", and));
                else context.add(and);
                break;
            }
            case NOT: {
                for (AbstractSearchQuery.Entry child : entry.getEntries()) {
                    BuildContext childContext = BuildContext.newContext(context);
                    childContext.negate = true;
                    buildEntry(childContext, child);
                    context.addAll(childContext.findQuery);
                }
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + entry.getType());
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
        BsonArray matches = new BsonArray();
        Document variables = new Document();

        for (AbstractSearchQuery.JoinOnEntry onEntry : entry.getOnEntries()) {
            variables.append(onEntry.getColumn2(), "$"+onEntry.getColumn2());
            matches.add(new BsonDocument("$eq", new BsonArray(Arrays.asList(
                    new BsonString("$"+onEntry.getColumn1()),
                    new BsonString("$$"+onEntry.getColumn2())
            ))));
        }

        BsonArray pipeline = new BsonArray();
        pipeline.add(new BsonDocument("$match", new BsonDocument("$expr", new BsonDocument("$and",matches))));

        Bson lookup = new Document("$lookup", new Document()
                .append("from", entry.getCollection().getName())
                .append("let", variables)
                .append("pipeline", pipeline)
                .append("as", "result"+entry.getCollection().getName()));
        context.add(lookup);
    }

    public static void buildLimitEntry(BuildContext context, AbstractSearchQuery.LimitEntry entry) {
        context.add(new Document("$limit", context.getValue(entry.getLimit())+context.getValue(entry.getOffset())));
        context.add(new Document("$skip", context.getValue(entry.getOffset())));
    }

    public static void buildOrderByEntry(BuildContext context, AbstractSearchQuery.OrderByEntry entry) {
        if(entry.getAggregation() != null) {
            /*Bson sort = Aggregates.unwind("$"^^+entry.getField());
            Bson group = Aggregates.group("$_id", Accumulators.sum(entry.getField(), "$"+entry.getField()));
            context.add(sort);
            context.add(group);*/
            context.add(new Document("$group", new Document()
                    .append("_id", "$"+entry.getField())
                    .append(entry.getField(), new Document("$"+entry.getAggregation().toString(), 1))));
        }
        int order = entry.getOrder() == SearchOrder.DESC ? -1 : 1;
        context.add(new Document("$sort", new Document(entry.getField(), order)));
    }

    public static void buildGroupByEntry(BuildContext context, AbstractSearchQuery.GroupByEntry entry) {
        Document groupBy = new Document();
        groupBy.append("_id", "$"+entry.getField());
        if(entry.getAggregation() != null) {
            groupBy.append(entry.getField(), new Document("$"+entry.getAggregation().toString().toLowerCase(), "1"));
        }
        context.add(new Document("$group", groupBy));
        /*if(entry.getAggregation() == null) {
            context.add(Aggregates.group("$"+entry.getField()));
        } else {
            context.add(Aggregates.group("$"+entry.getField(), new BsonField(entry.getField(),
                    new BsonDocument("$"+entry.getAggregation().toString().toLowerCase(),
                    new BsonInt32(1)))));
        }*/
    }

    public static void printQuery(BuildContext context) {
        StringBuilder query = new StringBuilder();
        for (Bson bson : context.getFindQuery()) {
            if(query.length() != 0) query.append(",");
            query.append(bson.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        }
        context.collection.getDatabase().getLogger().debug("["+query+"]");
    }

    public static Document prepareUpdate(BuildContext context, Collection<AbstractChangeAndSearchQuery.ChangeAndSearchEntry> setEntries) {
        Document update = new Document();
        Document set = new Document();
        Document inc = new Document();
        Document mul = new Document();
        for (AbstractChangeAndSearchQuery.ChangeAndSearchEntry changeAndSearchEntry : setEntries) {
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
        Document update = MongoDBQueryUtil.prepareUpdate(context, setEntries);
        while(cursor.hasNext()) {
            Document document = cursor.next();
            DefaultQueryResultEntry entry = new DefaultQueryResultEntry(collection.getDatabase().getDriver());
            document.forEach(entry::addEntry);
            result.addEntry(entry);
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
        else if(value instanceof byte[]) return new BsonBinary((byte[]) value);
        return new BsonString((String) value);
    }
}
