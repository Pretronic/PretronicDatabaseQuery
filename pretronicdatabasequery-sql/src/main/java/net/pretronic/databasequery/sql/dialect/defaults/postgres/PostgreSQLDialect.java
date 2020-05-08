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
import net.pretronic.databasequery.api.exceptions.DatabaseQueryException;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.common.query.EntryOption;
import net.pretronic.databasequery.common.query.type.AbstractCreateQuery;
import net.pretronic.databasequery.sql.dialect.context.CreateQueryContext;
import net.pretronic.databasequery.sql.dialect.defaults.AbstractDialect;
import net.pretronic.libraries.utility.map.Pair;

import java.net.InetSocketAddress;
import java.util.UUID;

public class PostgreSQLDialect extends AbstractDialect {

    public PostgreSQLDialect() {
        super("PostgreSQL", "org.postgresql.Driver", "postgresql", DatabaseDriverEnvironment.REMOTE,
                true, "\"", "\"");
    }

    @Override
    public String createConnectionString(String connectionString, Object host) {
        if(connectionString != null) {
            return connectionString;
        } else if(host instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) host;
            return String.format("jdbc:postgresql://%s:%s", address.getHostName(), address.getPort())+"/";
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
            if(primaryKey && !unique) buildCreateQueryFieldOption(context, entry, FieldOption.UNIQUE, queryParts);
            if(queryParts.getKey() != null) context.getQueryBuilder().append(queryParts.getKey());
            if(queryParts.getValue() != null) context.getQueryBuilder().append(queryParts.getValue());
        }
    }

    @Override
    protected void buildCreateQueryFieldOption(CreateQueryContext context, AbstractCreateQuery.CreateEntry entry, FieldOption fieldOption, Pair<String, String> queryParts) {

    }
}
