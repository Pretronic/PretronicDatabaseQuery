/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 10.04.20, 19:18
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

package net.pretronic.databasequery.mongodb.query;

import com.mongodb.client.MongoCursor;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.common.query.type.AbstractChangeAndSearchQuery;
import net.pretronic.databasequery.common.query.type.AbstractUpdateQuery;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import net.pretronic.databasequery.mongodb.query.utils.BuildContext;
import net.pretronic.databasequery.mongodb.query.utils.MongoDBQueryUtil;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDBUpdateQuery extends AbstractUpdateQuery<MongoDBDatabaseCollection> {

    public MongoDBUpdateQuery(MongoDBDatabaseCollection collection) {
        super(collection);
    }

    @Override
    public QueryResult execute(Object... values) {
        BuildContext context = BuildContext.newContext(this.collection);
        List<ChangeAndSearchEntry> setEntries = new ArrayList<>();
        for (Entry entry : entries) {
            if(entry instanceof AbstractChangeAndSearchQuery.ChangeAndSearchEntry) {
                setEntries.add((ChangeAndSearchEntry) entry);
            } else {
                MongoDBQueryUtil.buildEntry(context, entry);
            }
        }

        MongoDBQueryUtil.printQuery(context);

        DefaultQueryResult result = new DefaultQueryResult();
        MongoCursor<Document> cursor = collection.getCollection().aggregate(context.getFindQuery()).cursor();
        while(cursor.hasNext()) {
            Document document = cursor.next();
            DefaultQueryResultEntry entry = new DefaultQueryResultEntry(collection.getDatabase().getDriver());
            document.forEach(entry::addEntry);
            result.addEntry(entry);
            Document update = new Document();
            Document set = new Document();
            Document inc = new Document();
            Document mul = new Document();
            for (ChangeAndSearchEntry changeAndSearchEntry : setEntries) {
                if(changeAndSearchEntry.getOperator() == null) {
                    set.append(changeAndSearchEntry.getField(), changeAndSearchEntry.getValue());
                } else {
                    ChangeAndSearchEntry.ArithmeticOperator operator = changeAndSearchEntry.getOperator();
                    switch (operator) {
                        case ADD: {
                            inc.append(changeAndSearchEntry.getField(), changeAndSearchEntry.getValue());
                            break;
                        }
                        case SUBTRACT: {
                            inc.append(changeAndSearchEntry.getField(), createCounterPart((Number) changeAndSearchEntry.getValue()));
                            break;
                        }
                        case MULTIPLY: {
                            mul.append(changeAndSearchEntry.getField(), changeAndSearchEntry.getValue());
                            break;
                        }
                        case DIVIDE: {
                            double divider = 1/((Number)changeAndSearchEntry.getValue()).doubleValue();
                            mul.append(changeAndSearchEntry.getField(), divider);
                            break;
                        }
                    }
                }
            }
            if(!set.isEmpty()) update.append("$set", set);
            if(!inc.isEmpty()) update.append("$inc", inc);
            if(!mul.isEmpty()) update.append("$mul", mul);

            this.collection.getCollection().updateOne(document, update);
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
}
