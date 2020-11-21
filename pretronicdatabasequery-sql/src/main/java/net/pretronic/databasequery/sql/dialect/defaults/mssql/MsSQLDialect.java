/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.05.20, 16:49
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

package net.pretronic.databasequery.sql.dialect.defaults.mssql;

import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.exceptions.DatabaseQueryException;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.common.query.EntryOption;
import net.pretronic.databasequery.common.query.type.AbstractCreateQuery;
import net.pretronic.databasequery.sql.DataTypeInformation;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.dialect.DialectDefaultSettings;
import net.pretronic.databasequery.sql.dialect.context.CreateQueryContext;
import net.pretronic.databasequery.sql.dialect.defaults.AbstractDialect;
import net.pretronic.libraries.utility.map.Pair;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MsSQLDialect extends AbstractDialect {

    public MsSQLDialect() {
        super("MsSQL", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sqlserver",
                new DialectDefaultSettings(1433, TimeUnit.MINUTES.toMillis(15)),
                DatabaseDriverEnvironment.REMOTE, true, "[", "]");
    }

    /*
    IF NOT EXISTS ; IDENTITY(1,1)

     */

    @Override
    public String createConnectionString(String connectionString, Object host) {
        //jdbc:sqlserver://<server>:<port>;databaseName=AdventureWorks;user=<user>;password=<password>
        if(connectionString != null) {
            return connectionString;
            /*
            IF OBJECT_ID(N'dbo.Cars', N'U') IS NULL BEGIN CREATE TABLE dbo.Cars (Name varchar(64) not null); END;
             */
        } else if(host instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) host;
            return String.format("jdbc:sqlserver://%s:%s", address.getHostName(), address.getPort());
        }
        throw new DatabaseQueryException("Can't match jdbc url for dialect " + getName());
    }

    @Override
    public CreateQueryContext newCreateQuery(SQLDatabase database, List<AbstractCreateQuery.Entry> entries, String name, String engine, DatabaseCollectionType collectionType, FindQuery includingQuery, Object[] values) {
        return super.newCreateQuery(database, entries, name, engine, collectionType, includingQuery, values);
    }

    @Override
    protected void buildCreateQueryFieldOption(CreateQueryContext context, AbstractCreateQuery.CreateEntry entry, FieldOption fieldOption, Pair<String, String> queryParts) {
        if(fieldOption == FieldOption.AUTO_INCREMENT) {
            context.getQueryBuilder().append(" IDENTITY(1,1)");
        } else {
            super.buildCreateQueryFieldOption(context, entry, fieldOption, queryParts);
        }
    }

    @Override
    protected void registerDataTypeInformation() {
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DOUBLE).names("DOUBLE"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.FLOAT).names("REAL"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.LONG).names("BIGINT").defaultSize(8));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.LONG_TEXT).names("LONGTEXT").sizeAble(false));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DATETIME).names("DATETIME"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.BINARY).names("BINARY"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.UUID).names("BINARY").defaultSize(16));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.BOOLEAN).names("BIT").sizeAble(false));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DECIMAL).names("DECIMAL"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.INTEGER).names("INTEGER", "INT"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.CHAR).names("CHAR").defaultSize(1));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.STRING).names("VARCHAR").defaultSize(255));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DATE).names("DATE"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.TIMESTAMP).names("TIMESTAMP"));
    }

    @Override
    protected void buildCreateQueryDefaultValue(CreateQueryContext context, AbstractCreateQuery.CreateEntry entry) {
        if(entry.getDefaultValue() != null && entry.getDefaultValue() != EntryOption.NOT_DEFINED) {
            context.getQueryBuilder().append(" DEFAULT ");
            switch (entry.getDataType()) {
                case LONG_TEXT:
                case STRING: {
                    context.getQueryBuilder().append("'").append(entry.getDefaultValue()).append("'");
                    break;
                }
                default: {
                    context.getQueryBuilder().append(entry.getDefaultValue());
                }
            }
        }
    }
}
