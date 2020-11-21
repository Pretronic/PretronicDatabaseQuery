/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.05.20, 15:17
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

package net.pretronic.databasequery.sql.dialect.defaults.postgres;

import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.exceptions.DatabaseQueryException;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.common.query.EntryOption;
import net.pretronic.databasequery.common.query.type.AbstractCreateQuery;
import net.pretronic.databasequery.sql.DataTypeInformation;
import net.pretronic.databasequery.sql.dialect.DialectDefaultSettings;
import net.pretronic.databasequery.sql.dialect.context.CreateQueryContext;
import net.pretronic.databasequery.sql.dialect.defaults.AbstractDialect;
import net.pretronic.libraries.utility.map.Pair;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class PostgreSQLDialect extends AbstractDialect {

    public PostgreSQLDialect() {
        super("PostgreSQL", "org.postgresql.Driver", "postgresql",
                new DialectDefaultSettings(5432, TimeUnit.MINUTES.toMillis(15)), DatabaseDriverEnvironment.REMOTE,
                true, "\"", "\"");
    }

    @Override
    public String createConnectionString(String connectionString, Object host) {
        if(connectionString != null) {
            return connectionString;
        } else if(host instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) host;
            int port = address.getPort();
            if(port == 0) {
                port = getDefaultSettings().getDefaultPort();
            }
            return String.format("jdbc:postgresql://%s:%s", address.getHostName(), port)+"/";
        }
        throw new DatabaseQueryException("Can't match jdbc url for dialect " + getName());
    }

    @Override
    protected void buildCreateQueryCreateEntry(CreateQueryContext context, AbstractCreateQuery.CreateEntry entry) {
        if(entry.hasFieldOption(FieldOption.AUTO_INCREMENT)) {
            context.getQueryBuilder().append(firstBackTick).append(entry.getField()).append(secondBackTick).append(" ").append("SERIAL");
            buildCreateQueryFieldOptions(context, entry);
        } else {
            super.buildCreateQueryCreateEntry(context, entry);
        }
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

    @Override
    protected void buildCreateQueryFieldOptions(CreateQueryContext context, AbstractCreateQuery.CreateEntry entry) {
        if(entry.getFieldOptions() != null && entry.getFieldOptions().length != 0) {
            Pair<String, String> queryParts = new Pair<>(null, null);

            boolean primaryKey = false;
            boolean unique = false;
            for (FieldOption fieldOption : entry.getFieldOptions()) {
                if(fieldOption == FieldOption.PRIMARY_KEY) primaryKey = true;
                if(fieldOption == FieldOption.UNIQUE) unique = true;
                if(fieldOption != FieldOption.AUTO_INCREMENT) {
                    buildCreateQueryFieldOption(context, entry, fieldOption, queryParts);
                }
            }
            if(primaryKey && !unique) {
                buildCreateQueryFieldOption(context, entry, FieldOption.UNIQUE, queryParts);
            }
            if(queryParts.getKey() != null) context.getQueryBuilder().append(queryParts.getKey());
            if(queryParts.getValue() != null) context.getQueryBuilder().append(queryParts.getValue());
        }
    }

    @Override
    protected String createFieldIndex(CreateQueryContext context, AbstractCreateQuery.CreateEntry entry) {
        String indexName = context.getDatabase().getName()+context.getCollectionName()+entry.getField();
        if(indexName.length() > 64) indexName = indexName.substring(0, 64);
        StringBuilder indexQuery = new StringBuilder();
        indexQuery.append("CREATE INDEX IF NOT EXISTS ")
                .append(firstBackTick)
                .append(indexName)
                .append(secondBackTick)
                .append(" ON ")
                .append(firstBackTick);
        if(getEnvironment() == DatabaseDriverEnvironment.REMOTE) {
            indexQuery.append(context.getDatabase().getName()).append(secondBackTick).append(".").append(firstBackTick);
        }
        indexQuery.append(context.getCollectionName()).append(secondBackTick)
                .append("(").append(firstBackTick).append(entry.getField()).append(secondBackTick).append(");");
        context.getAdditionalExecutedQueries().add(indexQuery.toString());
        return null;
    }

    @Override
    protected void registerDataTypeInformation() {
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DOUBLE).names("DOUBLE PRECISION"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.LONG).names("BIGINT").sizeAble(false));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.LONG_TEXT).names("TEXT").sizeAble(false));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DATETIME).names("TIMESTAMP"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.BINARY).names("BYTEA").sizeAble(false));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.UUID).names("BYTEA").sizeAble(false));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.BOOLEAN).names("BOOLEAN").sizeAble(false));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.FLOAT).names("FLOAT"));

        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DECIMAL).names("DECIMAL"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.INTEGER).names("INTEGER", "INT"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.CHAR).names("CHAR").defaultSize(1));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.STRING).names("VARCHAR").defaultSize(255));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DATE).names("DATE"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.TIMESTAMP).names("TIMESTAMP"));
    }
}
