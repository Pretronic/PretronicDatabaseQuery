/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.12.19, 20:20
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

package net.pretronic.databasequery.sql.dialect;

import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.common.query.type.*;
import net.pretronic.databasequery.sql.DataTypeInformation;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;
import net.pretronic.databasequery.sql.dialect.context.CreateQueryContext;
import net.pretronic.databasequery.sql.dialect.defaults.mssql.MsSQLDialect;
import net.pretronic.databasequery.sql.dialect.defaults.mysql.H2PortableDialect;
import net.pretronic.databasequery.sql.dialect.defaults.mysql.MariaDBDialect;
import net.pretronic.databasequery.sql.dialect.defaults.mysql.MySQLDialect;
import net.pretronic.databasequery.sql.dialect.defaults.postgres.PostgreSQLDialect;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.map.Pair;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Dialect extends ConnectionStringCreator {

    Collection<Dialect> DIALECTS = new ArrayList<>();

    Dialect MYSQL = registerDialect(new MySQLDialect());
    Dialect MARIADB = registerDialect(new MariaDBDialect());
    Dialect H2_PORTABLE = registerDialect(new H2PortableDialect());
    Dialect POSTGRESQL = registerDialect(new PostgreSQLDialect());
    Dialect MSSQL = registerDialect(new MsSQLDialect());


    String getName();

    String getDriverName();

    Class<? extends Driver> getDriver();

    void loadDriver();

    String getProtocol();

    DatabaseDriverEnvironment getEnvironment();

    Collection<DataTypeInformation> getDataTypeInformation();

    DataTypeInformation getDataTypeInformation(DataType dataType);


    CreateQueryContext newCreateQuery(SQLDatabase database, List<AbstractCreateQuery.Entry> entries, String name, String engine, DatabaseCollectionType collectionType, FindQuery includingQuery, Object[] values);

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
