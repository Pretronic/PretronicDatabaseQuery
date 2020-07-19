/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.07.20, 12:36
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

package net.pretronic.databasequery.sql.collection;

import net.pretronic.databasequery.api.collection.AliasDatabaseCollection;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.InnerQueryDatabaseCollection;
import net.pretronic.databasequery.api.collection.field.CollectionField;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.QueryTransaction;
import net.pretronic.databasequery.api.query.type.*;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.query.type.SQLFindQuery;

import java.util.Collection;
import java.util.function.Consumer;

public class SQLInnerQueryDatabaseCollection extends SQLDatabaseCollection implements InnerQueryDatabaseCollection {

    private final FindQuery query;
    private final DatabaseCollection collection;

    public SQLInnerQueryDatabaseCollection(String name, SQLDatabase database, DatabaseCollectionType type, DatabaseCollection collection,
                                           Consumer<FindQuery> queryConsumer) {
        super(name, database, type);
        FindQuery query = find();
        queryConsumer.accept(query);
        this.query = query;
        this.collection = collection;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public FindQuery find() {
        return new SQLFindQuery(this);
    }

    @Override
    public DatabaseCollection getCollection() {
        return this.collection;
    }

    @Override
    public FindQuery getQuery() {
        return this.query;
    }

    @Override
    public InsertQuery insert() {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public UpdateQuery update() {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public ReplaceQuery replace() {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public DeleteQuery delete() {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public void drop() {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public QueryTransaction transact() {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public QueryGroup group() {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public Collection<CollectionField> getFields() {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public CollectionField getField(String name) {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public boolean hasField(String name) {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public CollectionField addField(String name) {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }

    @Override
    public AliasDatabaseCollection as(String alias) {
        throw new UnsupportedOperationException("Not supported in InnerQueryDatabaseCollection");
    }
}
