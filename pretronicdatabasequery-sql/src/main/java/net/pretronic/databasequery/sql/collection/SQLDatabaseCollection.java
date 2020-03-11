/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 16:39
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
import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.field.CollectionField;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.QueryTransaction;
import net.pretronic.databasequery.api.query.type.*;
import net.pretronic.databasequery.common.collection.AbstractDatabaseCollection;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.query.SQLQueryGroup;
import net.pretronic.databasequery.sql.query.SQLQueryTransaction;
import net.pretronic.databasequery.sql.query.type.*;

import java.util.Collection;

public class SQLDatabaseCollection extends AbstractDatabaseCollection<SQLDatabase> {

    public SQLDatabaseCollection(String name, SQLDatabase database, DatabaseCollectionType type) {
        super(name, database, type);
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public InsertQuery insert() {
        return new SQLInsertQuery(this);
    }

    @Override
    public FindQuery find() {
        return new SQLFindQuery(this);
    }

    @Override
    public UpdateQuery update() {
        return new SQLUpdateQuery(this);
    }

    @Override
    public ReplaceQuery replace() {
        return new SQLReplaceQuery(this);
    }

    @Override
    public DeleteQuery delete() {
        return new SQLDeleteQuery(this);
    }

    @Override
    public void drop() {

    }

    @Override
    public void clear() {

    }

    @Override
    public QueryTransaction transact() {
        return new SQLQueryTransaction(getDatabase());
    }

    @Override
    public QueryGroup group() {
        return new SQLQueryGroup(getDatabase());
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
}
