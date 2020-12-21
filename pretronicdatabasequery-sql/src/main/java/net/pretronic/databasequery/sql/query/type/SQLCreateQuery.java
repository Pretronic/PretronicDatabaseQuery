/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 20:52
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

package net.pretronic.databasequery.sql.query.type;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.common.query.type.AbstractCreateQuery;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;
import net.pretronic.databasequery.sql.dialect.context.CreateQueryContext;
import net.pretronic.libraries.utility.annonations.Internal;

public class SQLCreateQuery extends AbstractCreateQuery<SQLDatabase> {

    public SQLCreateQuery(String name, SQLDatabase database) {
        super(name, database);
    }

    @Override
    public DatabaseCollection create() {
        return create(true);
    }

    @Internal
    public DatabaseCollection create(boolean commit) {
        CreateQueryContext context = this.database.getDriver().getDialect().newCreateQuery(this.database, this.entries, this.name, this.engine, this.type, this.includingQuery, this.ifNotExist, EMPTY_OBJECT_ARRAY);
        this.database.executeUpdateQuery(context.getQueryBuilder().toString(), commit, preparedStatement -> {
            for (int i = 1; i <= context.getPreparedValues().size(); i++) {
                preparedStatement.setObject(i, context.getPreparedValues().get(i-1));
            }
        });
        for (String additionalExecutedQuery : context.getAdditionalExecutedQueries()) {
            this.database.executeUpdateQuery(additionalExecutedQuery, commit);
        }
        return new SQLDatabaseCollection(this.name, this.database, this.type);
    }
}
