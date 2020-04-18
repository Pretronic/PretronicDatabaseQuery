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

package net.pretronic.databasequery.mongodb.collection;

import com.mongodb.client.MongoCollection;
import net.pretronic.databasequery.api.collection.AliasDatabaseCollection;
import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.field.CollectionField;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.QueryTransaction;
import net.pretronic.databasequery.api.query.type.*;
import net.pretronic.databasequery.common.collection.AbstractDatabaseCollection;
import net.pretronic.databasequery.mongodb.MongoDBDatabase;
import net.pretronic.databasequery.mongodb.query.*;
import org.bson.Document;

import java.util.Collection;

public class MongoDBDatabaseCollection extends AbstractDatabaseCollection<MongoDBDatabase> {

    private final MongoCollection<Document> collection;

    public MongoDBDatabaseCollection(String name, MongoDBDatabase database, DatabaseCollectionType type) {
        super(name, database, type);
        this.collection = database.getDatabase().getCollection(name);
    }

    @Override
    public long getSize() {
        return this.collection.countDocuments();
    }

    @Override
    public InsertQuery insert() {
        return new MongoDBInsertQuery(this);
    }

    @Override
    public FindQuery find() {
        return new MongoDBFindQuery(this);
    }

    @Override
    public UpdateQuery update() {
        return new MongoDBUpdateQuery(this);
    }

    @Override
    public ReplaceQuery replace() {
        return new MongoDBReplaceQuery(this);
    }

    @Override
    public DeleteQuery delete() {
        return new MongoDBDeleteQuery(this);
    }

    @Override
    public void drop() {
        this.collection.drop();
    }

    @Override
    public void clear() {

    }

    @Override
    public QueryTransaction transact() {
        return null;
    }

    @Override
    public QueryGroup group() {
        return null;
    }

    @Override
    public Collection<CollectionField> getFields() {
        return null;
    }

    @Override
    public CollectionField getField(String name) {
        return null;
    }

    @Override
    public boolean hasField(String name) {
        return false;
    }

    @Override
    public CollectionField addField(String name) {
        return null;
    }

    @Override
    public AliasDatabaseCollection as(String alias) {
        return null;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }
}
