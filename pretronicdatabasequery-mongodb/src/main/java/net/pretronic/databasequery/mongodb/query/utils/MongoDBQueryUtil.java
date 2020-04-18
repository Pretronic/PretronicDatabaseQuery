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
import com.mongodb.client.model.*;
import net.pretronic.databasequery.api.query.Aggregation;
import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.common.query.type.AbstractSearchQuery;
import net.pretronic.libraries.utility.StringUtil;
import org.bson.*;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            case WHERE: {
                buildConditionAggregationGroup(context, entry);
                context.add(Aggregates.match(buildNegateConditionEntry(context.negate,
                        Filters.eq(entry.getField(), entry.getValue1()))));
                break;
            }
            case WHERE_IN: {
                context.add(Aggregates.match(buildNegateConditionEntry(context.negate,
                        Filters.in(entry.getField(), (List<Object>) entry.getValue1()))));
                break;
            }
            case WHERE_LIKE: {
                //context.add(Aggregates.match(buildNegateConditionEntry(context.negate,
                //        buildConditionAggregationEntry())));
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
            context.add(Aggregates.group("$_id", Accumulators.sum("sum", "$" + entry.getField()),
                    Accumulators.push("entries", "$$ROOT")));
            //context.add(Aggregates.match(Accumulators.sum("", new Document())));
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
                    BuildContext childContext = BuildContext.newContext(context.collection);
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
            BuildContext childFind = BuildContext.newContext(context.collection);
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

        /*context.add(Aggregates.replaceRoot(new Document("$mergeObjects", new BsonArray(
                Arrays.asList(
                        new BsonDocument("$arrayElemAt", new BsonArray(Arrays.asList(
                                new BsonString("$result"), new BsonInt32(0)
                        ))),
                        new BsonString("$$ROOT")
                )
        ))));
        context.add(Aggregates.project(new Document("result", 0)));*/
    }

    public static void buildLimitEntry(BuildContext context, AbstractSearchQuery.LimitEntry entry) {
        context.add(Aggregates.limit(entry.getLimit()+entry.getOffset()));
        context.add(Aggregates.skip(entry.getOffset()));
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

    }


    public static void printQuery(BuildContext context) {
        for (Bson bson : context.getFindQuery()) {
            System.out.println(bson.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        }
    }
}
