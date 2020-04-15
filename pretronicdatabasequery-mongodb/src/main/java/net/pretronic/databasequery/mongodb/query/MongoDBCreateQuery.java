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

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.common.query.type.AbstractCreateQuery;
import net.pretronic.databasequery.mongodb.MongoDBDatabase;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import org.bson.Document;

public class MongoDBCreateQuery extends AbstractCreateQuery<MongoDBDatabase> {

    public MongoDBCreateQuery(String name, MongoDBDatabase database) {
        super(name, database);
    }

    @Override
    public DatabaseCollection create() {
        if(!database.existCollection(name)) this.database.getDatabase().createCollection(this.name);

        for (Entry entry : entries) {
            if(entry instanceof CreateEntry) {
                CreateEntry createEntry = (CreateEntry) entry;
                for (FieldOption fieldOption : createEntry.getFieldOptions()) {
                    if(fieldOption == FieldOption.AUTO_INCREMENT) {
                        if(!counterExist(createEntry.getField())) {
                            database.addCounter(this.name, createEntry.getField());
                        }
                    }
                }
            }
        }
        return new MongoDBDatabaseCollection(this.name, this.database, DatabaseCollectionType.NORMAL);
    }

    private boolean counterExist(String field) {
        for (Document ignored : this.database.getCounters().find(new Document("collectionName", this.name).append("field", field))) {
            return true;
        }
        return false;
    }
}
