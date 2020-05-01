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
import net.pretronic.databasequery.common.query.type.AbstractFindQuery;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import net.pretronic.databasequery.mongodb.query.utils.BuildContext;
import net.pretronic.databasequery.mongodb.query.utils.MongoDBQueryUtil;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDBFindQuery extends AbstractFindQuery<MongoDBDatabaseCollection> {

    public MongoDBFindQuery(MongoDBDatabaseCollection collection) {
        super(collection);
    }

    @Override
    public QueryResult execute(Object... values) {
        BuildContext context = BuildContext.newContext(values, this.collection);
        MongoDBQueryUtil.buildEntries(context, this.entries);

        MongoDBQueryUtil.printQuery(context);

        DefaultQueryResult result = new DefaultQueryResult();
        MongoCursor<Document> cursor = collection.getCollection().aggregate(context.getFindQuery()).cursor();
        while(cursor.hasNext()) {

            Document document = cursor.next();
            DefaultQueryResultEntry resultEntry = new DefaultQueryResultEntry(collection.getDatabase().getDriver());
            document.forEach((key, value)-> {

            });
            if(getEntries.isEmpty()) {
                document.forEach((key, value)-> {
                    if(value instanceof ArrayList<?>) {
                        List<Document> subResult = (List<Document>) value;
                        if(!subResult.isEmpty()) {
                            subResult.get(0).forEach((key0, value0)-> resultEntry.addEntry(key.substring(6)+"."+key0, value0));
                        }
                    } else {
                        resultEntry.addEntry(key, value);
                    }
                });
            } else {
                for (GetEntry getEntry : getEntries) {
                    if(getEntry.getDatabase() != null) throw new UnsupportedOperationException("MongoDB cross database entry getting is not possible");

                    if(getEntry.getDatabaseCollection() != null) {
                        Document subDocument = document.get("result"+getEntry.getDatabaseCollection(), Document.class);
                        Object value = subDocument.get(getEntry.getField());
                        resultEntry.addEntry(getEntry.getDatabaseCollection()+"."+getEntry.getField(), value);
                    } else {
                        Object value = document.get(getEntry.getField());
                        resultEntry.addEntry(getEntry.getField(), value);
                    }

                }
            }
            result.addEntry(resultEntry);
        }
        return result;
    }
}
