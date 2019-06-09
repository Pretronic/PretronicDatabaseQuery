/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 26.05.19, 15:37
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

package net.prematic.databasequery.sql.mysql;

import net.prematic.databasequery.core.DatabaseCollection;
import net.prematic.databasequery.core.DatabaseCollectionType;
import net.prematic.databasequery.core.query.*;
import net.prematic.databasequery.sql.mysql.query.MySqlFindQuery;
import net.prematic.databasequery.sql.mysql.query.MySqlInsertQuery;
import net.prematic.databasequery.sql.mysql.query.MySqlUpdateQuery;

public class MySqlDatabaseCollection implements DatabaseCollection {

    private final String name;
    private final DatabaseCollectionType type;
    private final MySqlDatabase database;

    public MySqlDatabaseCollection(String name, DatabaseCollectionType type, MySqlDatabase database) {
        this.name = name;
        this.type = type;
        this.database = database;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DatabaseCollectionType getType() {
        return this.type;
    }

    public MySqlDatabase getDatabase() {
        return database;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public InsertQuery insert() {
        return new MySqlInsertQuery(this);
    }

    @Override
    public FindQuery find() {
        return new MySqlFindQuery(this);
    }

    @Override
    public UpdateQuery update() {
        return new MySqlUpdateQuery(this);
    }

    @Override
    public ReplaceQuery replace() {
        return null;
    }

    @Override
    public DeleteQuery delete() {
        return null;
    }

    @Override
    public QueryTransaction transact() {
        return null;
    }
}
