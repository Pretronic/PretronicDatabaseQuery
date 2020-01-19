/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 20:20
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

package net.pretronic.databasequery.sql.dialect;

import net.prematic.databasequery.api.collection.DatabaseCollectionType;
import net.prematic.databasequery.api.query.type.FindQuery;
import net.prematic.libraries.utility.Iterators;
import net.prematic.libraries.utility.map.Pair;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.common.query.type.*;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;
import net.pretronic.databasequery.sql.dialect.defaults.H2PortableDialect;
import net.pretronic.databasequery.sql.dialect.defaults.MySQLDialect;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Dialect extends ConnectionStringCreator {

    Collection<Dialect> DIALECTS = new ArrayList<>();

    Dialect MYSQL = registerDialect(new MySQLDialect());
    Dialect H2Portable = registerDialect(new H2PortableDialect());


    String getName();

    String getDriverName();

    Class<? extends Driver> getDriver();

    String getProtocol();

    DatabaseDriverEnvironment getEnvironment();

    Pair<String, List<Object>> newCreateQuery(SQLDatabase database, List<AbstractCreateQuery.Entry> entries, String name, String engine, DatabaseCollectionType collectionType, FindQuery includingQuery, Object[] values);

    Pair<String, List<Object>> newDeleteQuery(SQLDatabaseCollection collection, List<AbstractDeleteQuery.Entry> entries, Object[] values);

    Pair<String, List<Object>> newFindQuery(SQLDatabaseCollection collection, List<AbstractFindQuery.GetEntry> getEntries, List<AbstractFindQuery.Entry> entries, Object[] values);

    Pair<String, List<Object>> newInsertQuery(SQLDatabaseCollection collection, List<AbstractInsertQuery.Entry> entries, Object[] values);

    Pair<String, List<Object>> newReplaceQuery(SQLDatabaseCollection collection, List<AbstractReplaceQuery.Entry> entries, Object[] values);

    Pair<String, List<Object>> newUpdateQuery(SQLDatabaseCollection collection, List<AbstractUpdateQuery.Entry> entries, Object[] values);



    static Collection<Dialect> getDialects() {
        return DIALECTS;
    }

    static Dialect byName(String name) {
        return Iterators.findOne(DIALECTS, dialect -> dialect.getName().equalsIgnoreCase(name));
    }

    static Dialect registerDialect(Dialect dialect) {
        DIALECTS.add(dialect);
        return dialect;
    }

    static void unregisterDialect(Dialect dialect) {
        DIALECTS.remove(dialect);
    }

    static void unregisterDialect(String dialectName) {
        Iterators.remove(DIALECTS, dialect -> dialect.getName().equalsIgnoreCase(dialectName));
    }
}