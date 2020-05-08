/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 06.01.20, 21:16
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

package net.pretronic.databasequery.sql.dialect.defaults;

import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.api.exceptions.DatabaseQueryException;
import net.pretronic.databasequery.api.query.Aggregation;
import net.pretronic.databasequery.api.query.ForeignKey;
import net.pretronic.databasequery.api.query.PreparedValue;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.common.query.EntryOption;
import net.pretronic.databasequery.common.query.type.*;
import net.pretronic.databasequery.sql.DataTypeInfo;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;
import net.pretronic.databasequery.sql.dialect.Dialect;
import net.pretronic.databasequery.sql.driver.SQLDatabaseDriver;
import net.pretronic.libraries.utility.map.Pair;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractDialect implements Dialect {

    private final String name;
    private final String driverName;
    private Class<? extends Driver> driver;
    private final String protocol;
    private final DatabaseDriverEnvironment environment;

    private final boolean dynamicDependencies;

    protected final String firstBackTick;
    protected final String secondBackTick;

    public AbstractDialect(String name, String driverName, String protocol, DatabaseDriverEnvironment environment, boolean dynamicDependencies, String firstBackTick, String secondBackTick) {
        this.name = name;
        this.driverName = driverName;
        this.protocol = protocol;
        this.environment = environment;
        this.dynamicDependencies = dynamicDependencies;
        this.firstBackTick = firstBackTick;
        this.secondBackTick = secondBackTick;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDriverName() {
        return this.driverName;
    }

    @Override
    public Class<? extends Driver> getDriver() {
        loadDriver();
        return this.driver;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadDriver() {
        if(driver == null){
            try {
                this.driver = (Class<? extends Driver>) Class.forName(this.driverName);
            } catch (ClassNotFoundException ignored) {
                if(dynamicDependencies && DatabaseDriverFactory.getDriverLoader() != null){
                    DatabaseDriverFactory.getDriverLoader().loadOptionalDriverDependencies(SQLDatabaseDriver.class,name);
                    try{
                        this.driver = (Class<? extends Driver>) Class.forName(this.driverName);
                        return;
                    }catch (ClassNotFoundException ignored2){}
                }
                throw new DatabaseQueryException("Database driver " + this.driverName+" is not available");
            }
        }
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public DatabaseDriverEnvironment getEnvironment() {
        return this.environment;
    }

    @Override
    public Pair<String, List<Object>> newCreateQuery(SQLDatabase database, List<AbstractCreateQuery.Entry> entries, String name, String engine, DatabaseCollectionType collectionType, FindQuery includingQuery, Object[] values) {
        List<Object> preparedValues = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("CREATE TABLE IF NOT EXISTS ").append(firstBackTick);
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(database.getName()).append(secondBackTick).append(".").append(firstBackTick);
        }
        queryBuilder.append(name).append(secondBackTick).append("(");

        for (int i = 0; i < entries.size(); i++) {
            if(i != 0) {
                queryBuilder.append(",");
            }

            AbstractCreateQuery.Entry entry = entries.get(i);
            if(entry instanceof AbstractCreateQuery.CreateEntry) {
                buildCreateQueryCreateEntry(database, queryBuilder, preparedValues,(AbstractCreateQuery.CreateEntry) entry);
            } else if(entry instanceof AbstractCreateQuery.ForeignKeyEntry) {
                buildForeignKey(database, queryBuilder, (AbstractCreateQuery.ForeignKeyEntry) entry);
            } else {
                throw new IllegalArgumentException(String.format("Entry %s is not supported for MySQL query", entry.getClass().getName()));
            }
        }
        return new Pair<>(queryBuilder.append(");").toString(), preparedValues);
    }

    protected void buildCreateQueryCreateEntry(SQLDatabase database, StringBuilder queryBuilder, List<Object> preparedValues, AbstractCreateQuery.CreateEntry entry) {
        DataTypeInfo dataTypeInfo = database.getDriver().getDataTypeInfo(entry.getDataType());
        queryBuilder.append(firstBackTick).append(entry.getField()).append(secondBackTick).append(" ").append(dataTypeInfo.getName());

        buildCreateQuerySize(queryBuilder, entry, dataTypeInfo);
        buildCreateQueryDefaultValue(queryBuilder, entry, preparedValues);
        buildCreateQueryFieldOptions(queryBuilder, entry);
    }

    protected void buildCreateQuerySize(StringBuilder queryBuilder, AbstractCreateQuery.CreateEntry entry, DataTypeInfo dataTypeInfo) {
        if(dataTypeInfo.isSizeAble()) {
            if(entry.getSize() != 0) queryBuilder.append("(").append(entry.getSize()).append(")");
            else if(dataTypeInfo.getDefaultSize() != 0) queryBuilder.append("(").append(dataTypeInfo.getDefaultSize()).append(")");
        }
    }

    protected void buildCreateQueryDefaultValue(StringBuilder queryBuilder, AbstractCreateQuery.CreateEntry entry, List<Object> preparedValues) {
        if(entry.getDefaultValue() != null && entry.getDefaultValue() != EntryOption.NOT_DEFINED) {
            preparedValues.add(entry.getDefaultValue());
            queryBuilder.append(" DEFAULT ?");
        }
    }

    protected void buildCreateQueryFieldOptions(StringBuilder queryBuilder, AbstractCreateQuery.CreateEntry entry) {
        if(entry.getFieldOptions() != null && entry.getFieldOptions().length != 0) {
            Pair<String, String> queryParts = new Pair<>(null, null);
            for (FieldOption fieldOption : entry.getFieldOptions()) {
                buildCreateQueryFieldOption(queryBuilder, entry, fieldOption, queryParts);
            }
            if(queryParts.getKey() != null) queryBuilder.append(queryParts.getKey());
            if(queryParts.getValue() != null) queryBuilder.append(queryParts.getValue());
        }
    }

    protected void buildCreateQueryFieldOption(StringBuilder queryBuilder, AbstractCreateQuery.CreateEntry entry, FieldOption fieldOption, Pair<String, String> queryParts) {
        switch (fieldOption) {
            case INDEX: {
                queryParts.setKey(",INDEX "+firstBackTick+ UUID.randomUUID().toString() +secondBackTick+"("+firstBackTick + entry.getField() +secondBackTick+")");
                break;
            }
            case UNIQUE_INDEX: {
                queryParts.setValue(",UNIQUE INDEX "+firstBackTick+UUID.randomUUID().toString() +secondBackTick+ "("+firstBackTick + entry.getField()+secondBackTick + ")");
                break;
            }
            case PRIMARY_KEY: {
                queryBuilder.append(" PRIMARY KEY");
                break;
            }
            case NOT_NULL: {
                queryBuilder.append(" NOT NULL");
                break;
            }
            default: {
                queryBuilder.append(" ").append(fieldOption.toString());
                break;
            }
        }
    }

    protected void buildForeignKey(SQLDatabase database, StringBuilder queryBuilder, AbstractCreateQuery.ForeignKeyEntry entry) {
        queryBuilder.append("CONSTRAINT ").append(firstBackTick)
                .append(UUID.randomUUID().toString())
                .append(secondBackTick).append(" FOREIGN KEY(").append(firstBackTick)
                .append(entry.getField())
                .append(secondBackTick).append(") REFERENCES ").append(firstBackTick);
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(database.getName()).append(secondBackTick).append(".").append(firstBackTick);
        }
        queryBuilder.append(entry.getForeignKey().getCollection()).append(secondBackTick).append("(").append(firstBackTick)
                .append(entry.getForeignKey().getField()).append(secondBackTick).append(")");
        if(entry.getForeignKey().getDeleteOption() != null && entry.getForeignKey().getDeleteOption() != ForeignKey.Option.DEFAULT) {
            queryBuilder.append(" ON DELETE ").append(entry.getForeignKey().getDeleteOption().toString().replace("_", " "));
        }
        if(entry.getForeignKey().getUpdateOption() != null && entry.getForeignKey().getUpdateOption() != ForeignKey.Option.DEFAULT) {
            queryBuilder.append(" ON UPDATE ").append(entry.getForeignKey().getDeleteOption().toString().replace("_", " "));
        }
    }



    @Override
    public Pair<String, List<Object>> newInsertQuery(SQLDatabaseCollection collection, List<AbstractInsertQuery.Entry> entries, Object[] values) {
        List<Object> preparedValues = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder().append("INSERT INTO ").append(firstBackTick);
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(collection.getDatabase().getName()).append(secondBackTick).append(".").append(firstBackTick);
        }
        queryBuilder.append(collection.getName()).append(secondBackTick).append(" ");

        AtomicInteger preparedValuesCount = new AtomicInteger();

        int fieldCount = entries.size();
        AtomicInteger valueCount = new AtomicInteger(values.length);

        buildInsertQueryFieldsPart(entries, queryBuilder, preparedValuesCount, valueCount, preparedValues, values);

        int valuesPerField = valueCount.get()/fieldCount;

        queryBuilder.append(")");

        for (int i = 0; i < valuesPerField; i++) {
            if(i == 0) queryBuilder.append(" VALUES (");
            else queryBuilder.append(",(");
            for (int i1 = 0; i1 < fieldCount; i1++) {
                if(i1 != 0) {
                    queryBuilder.append(",");
                }
                queryBuilder.append("?");
            }
            queryBuilder.append(")");
        }
        return new Pair<>(queryBuilder.append(";").toString(), preparedValues);
    }

    protected void buildInsertQueryFieldsPart(List<AbstractInsertQuery.Entry> entries, StringBuilder queryBuilder, AtomicInteger preparedValuesCount, AtomicInteger valueCount, List<Object> preparedValues, Object[] values) {
        for (int i = 0; i < entries.size(); i++) {
            AbstractInsertQuery.Entry entry = entries.get(i);
            if(i == 0) {
                queryBuilder.append("(");
            } else {
                queryBuilder.append(",");
            }
            queryBuilder.append(firstBackTick).append(entry.getField()).append(secondBackTick);
            valueCount.addAndGet(entry.getValues().size());
            for (Object value : entry.getValues()) {
                preparedValues.add(getEntry(value, preparedValuesCount, values));
            }
        }
    }

    @Override
    public Pair<String, List<Object>> newUpdateQuery(SQLDatabaseCollection collection, List<AbstractUpdateQuery.Entry> entries, Object[] values) {
        UpdateQueryBuilderState state = new UpdateQueryBuilderState(values);
        for (AbstractSearchQuery.Entry entry : entries) {
            if(entry instanceof AbstractChangeAndSearchQuery.ChangeAndSearchEntry) {
                buildUpdateQueryEntry((AbstractChangeAndSearchQuery.ChangeAndSearchEntry) entry, state);
            } else {
                buildSearchQueryEntry(entry, state, "AND", false);
            }
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(firstBackTick);
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(collection.getDatabase().getName()).append(secondBackTick).append(".").append(firstBackTick);
        }
        queryBuilder.append(collection.getName()).append(secondBackTick).append(" ").append(state.setBuilder).append(state.buildSearchQuery());
        return new Pair<>(queryBuilder.toString(), state.preparedValues);
    }

    protected void buildUpdateQueryEntry(AbstractChangeAndSearchQuery.ChangeAndSearchEntry entry, UpdateQueryBuilderState state) {
        if(state.setBuilder.length() == 0) {
            state.setBuilder.append("SET ");
        } else {
            state.setBuilder.append(",");
        }
        String field = buildField(entry);
        state.setBuilder.append(firstBackTick).append(field).append(secondBackTick).append("=");
        if(entry.getOperator() != null) {
            state.setBuilder.append(firstBackTick).append(field).append(secondBackTick).append(entry.getOperator().getSymbol());
        }
        state.setBuilder.append("?");
        addEntry(entry.getValue(), state);
    }

    @Override
    public Pair<String, List<Object>> newReplaceQuery(SQLDatabaseCollection collection, List<AbstractReplaceQuery.Entry> entries, Object[] values) {
        return null;
    }

    @Override
    public Pair<String, List<Object>> newFindQuery(SQLDatabaseCollection collection, List<AbstractFindQuery.GetEntry> getEntries, List<AbstractFindQuery.Entry> entries, Object[] values) {
        FindQueryBuilderState state = new FindQueryBuilderState(values);

        for (AbstractFindQuery.GetEntry getEntry : getEntries) {
            buildFindQueryEntry(getEntry, state);
        }
        for (AbstractSearchQuery.Entry entry : entries) {
            buildSearchQueryEntry(entry, state, "AND", false);
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ").append(buildFindQueryGetBuilder(state)).append(" FROM ").append(firstBackTick);
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(collection.getDatabase().getName()).append(secondBackTick).append(".").append(firstBackTick);
        }
        queryBuilder.append(collection.getName()).append(secondBackTick).append(" ").append(state.buildSearchQuery());

        return new Pair<>(queryBuilder.toString(), state.preparedValues);
    }

    protected String buildFindQueryGetBuilder(FindQueryBuilderState state) {
        if(state.getBuilder.length() == 0) return "*";
        else return state.getBuilder.toString();
    }

    protected void buildFindQueryEntry(AbstractFindQuery.GetEntry entry, FindQueryBuilderState state) {
        if(state.getBuilder.length() != 0) {
            state.getBuilder.append(",");
        }
        if(entry.getAggregation() != null) {
            state.getBuilder.append(entry.getAggregation()).append("(").append(firstBackTick).append(buildField(entry)).append(secondBackTick).append(")");
        }else {
            state.getBuilder.append(firstBackTick).append(buildField(entry)).append(secondBackTick);
        }
        if(entry.getAlias() != null) {
            state.getBuilder.append(" AS ").append(entry.getAlias());
        }
    }



    @Override
    public Pair<String, List<Object>> newDeleteQuery(SQLDatabaseCollection collection, List<AbstractDeleteQuery.Entry> entries, Object[] values) {
        SearchQueryBuilderState state = new SearchQueryBuilderState(values);
        for (AbstractSearchQuery.Entry entry : entries) {
            buildSearchQueryEntry(entry, state, "AND", false);
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(firstBackTick);
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(collection.getDatabase().getName()).append(secondBackTick).append(".").append(firstBackTick);
        }
        queryBuilder.append(collection.getName()).append(secondBackTick).append(" ").append(state.buildSearchQuery());
        return new Pair<>(queryBuilder.toString(), state.preparedValues);
    }



    protected void buildSearchQueryEntry(AbstractSearchQuery.Entry entry, SearchQueryBuilderState state, String entryConnector, boolean bracketFirst) {
        if(entry instanceof AbstractSearchQuery.ConditionEntry) {
            buildSearchQueryConditionEntry((AbstractSearchQuery.ConditionEntry) entry, state, entryConnector, bracketFirst);
        } else if(entry instanceof AbstractSearchQuery.OperationEntry) {
            buildSearchQueryOperationEntry((AbstractSearchQuery.OperationEntry) entry, state);
        } else if(entry instanceof AbstractSearchQuery.JoinEntry) {
            buildSearchQueryJoinEntry((AbstractSearchQuery.JoinEntry) entry, state);
        } else if(entry instanceof AbstractSearchQuery.LimitEntry) {
            buildSearchQueryLimitEntry((AbstractSearchQuery.LimitEntry) entry, state);
        } else if(entry instanceof AbstractSearchQuery.OrderByEntry) {
            buildSearchQueryOrderByEntry((AbstractSearchQuery.OrderByEntry) entry, state);
        } else if(entry instanceof AbstractSearchQuery.GroupByEntry) {
            buildSearchQueryGroupByEntry((AbstractSearchQuery.GroupByEntry) entry, state);
        }
    }

    protected void buildSearchQueryConditionEntry(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state, String entryConnector, boolean bracketFirst) {
        if(state.operator) {
            if(!bracketFirst) {
                if(state.where) {
                    state.clauseBuilder.append(" WHERE ");
                    state.where = false;
                } else {
                    state.clauseBuilder.append(" ").append(entryConnector).append(" ");
                }
            }
        }
        buildSearchQueryConditionEntryType(entry, state);
    }

    protected void buildSearchQueryConditionEntryType(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
        switch (entry.getType()) {
            case WHERE:
            case WHERE_LIKE:
            case WHERE_LOWER:
            case WHERE_HIGHER: {
                buildSearchQueryWhereConditionEntry(entry, state);
                break;
            }
            case WHERE_NULL: {
                buildSearchQueryWhereNullConditionEntry(entry, state);
                break;
            }
            case WHERE_IN: {
                buildSearchQueryWhereInConditionEntry(entry, state);
                break;
            }
            case WHERE_BETWEEN: {
                buildSearchQueryWhereBetweenConditionEntry(entry, state);
                break;
            }
        }
    }

    protected void buildSearchQueryWhereConditionEntry(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
        if(state.negate) {
            state.clauseBuilder.append("NOT ");
        }
        if(entry.getExtra() != null) {
            Aggregation aggregation = (Aggregation) entry.getExtra();
            state.clauseBuilder.append(aggregation.toString()).append("(").append(firstBackTick).append(buildField(entry)).append(secondBackTick).append(")");
        } else {
            state.clauseBuilder.append(firstBackTick).append(buildField(entry)).append(secondBackTick);
        }
        addEntry(entry.getValue1(), state);
        state.clauseBuilder.append(getWhereCompareSymbol(entry.getType())).append("?");
    }

    protected void buildSearchQueryWhereNullConditionEntry(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
        state.clauseBuilder.append(firstBackTick).append(buildField(entry)).append(secondBackTick);
        state.clauseBuilder.append(" IS ");
        if(state.negate) {
            state.clauseBuilder.append("NOT ");
        }
        state.clauseBuilder.append("NULL");
    }

    protected void buildSearchQueryWhereInConditionEntry(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
        if(state.negate) {
            state.clauseBuilder.append("NOT ");
        }
        state.clauseBuilder.append(firstBackTick).append(buildField(entry)).append(secondBackTick).append(" IN (");

        @SuppressWarnings("unchecked")
        List<Object> values = (List<Object>) addAndGetEntry(entry.getValue1(), state);

        for (int i = 0; i < values.size(); i++) {
            if(i > 0) state.clauseBuilder.append(",");
            state.clauseBuilder.append("?");
        }
        state.clauseBuilder.append(")");
    }

    protected void buildSearchQueryWhereBetweenConditionEntry(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
        if(state.negate) {
            state.clauseBuilder.append("NOT ");
        }
        state.clauseBuilder.append(firstBackTick).append(buildField(entry)).append(secondBackTick);
        state.clauseBuilder.append(" BETWEEN ? AND ?");
        addEntry(entry.getValue1(), state);
        addEntry(entry.getExtra(), state);
    }

    protected String getWhereCompareSymbol(AbstractSearchQuery.ConditionEntry.Type type) {
        switch (type) {
            case WHERE: return "=";
            case WHERE_LIKE: return " LIKE ";
            case WHERE_LOWER: return "<";
            case WHERE_HIGHER: return ">";
        }
        throw new IllegalArgumentException("Can't match compare symbol for " + type.toString());
    }

    protected void buildSearchQueryOperationEntry(AbstractSearchQuery.OperationEntry entry, SearchQueryBuilderState state) {
        switch (entry.getType()) {
            case OR: {
                andOr(entry,"OR", state);
                break;
            }
            case AND: {
                andOr(entry,"AND", state);
            }
            case NOT : {
                state.negate = true;
                for (AbstractSearchQuery.Entry child : entry.getEntries()) {
                    buildSearchQueryEntry(child, state, "AND", false);
                }
                state.negate = false;
            }
        }
    }

    protected void andOr(AbstractSearchQuery.OperationEntry entry, String symbol, SearchQueryBuilderState state) {
        if(state.operator) {
            if(state.where) {
                state.clauseBuilder.append(" WHERE ");
                state.where = false;
            } else {
                state.clauseBuilder.append(" ").append(symbol).append(" ");
            }
        }

        if(state.negate) {
            state.clauseBuilder.append("NOT ");
        }

        state.clauseBuilder.append("(");
        boolean first = true;
        for (AbstractSearchQuery.Entry child : entry.getEntries()) {
            buildSearchQueryEntry(child, state, symbol, first);
            first = false;
        }

        state.clauseBuilder.append(")");
    }

    protected void buildSearchQueryJoinEntry(AbstractSearchQuery.JoinEntry entry, SearchQueryBuilderState state) {
        state.joinBuilder.append(entry.getType().toString()).append(" JOIN ").append(firstBackTick);
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            state.joinBuilder.append(entry.getCollection().getDatabase().getName()).append(secondBackTick).append(".").append(firstBackTick);
        }
        state.joinBuilder.append(entry.getCollection().getName()).append(secondBackTick).append(" ");

        for (int i = 0; i < entry.getOnEntries().size(); i++) {
            AbstractSearchQuery.JoinOnEntry onEntry = entry.getOnEntries().get(i);
            if(i == 0) {
                state.joinBuilder.append("ON ").append(firstBackTick);
            } else {
                state.joinBuilder.append("AND ").append(firstBackTick);
            }
            if(this.environment == DatabaseDriverEnvironment.REMOTE) {
                state.joinBuilder.append(onEntry.getCollection1().getDatabase().getName()).append(secondBackTick).append(".").append(firstBackTick);
            }
            state.joinBuilder.append(onEntry.getCollection1().getName()).append(secondBackTick).append(".").append(firstBackTick)
                    .append(onEntry.getColumn1()).append(secondBackTick)
                    .append("=").append(firstBackTick);

            if(this.environment == DatabaseDriverEnvironment.REMOTE) {
                if(onEntry.getCollection2() != null) {
                    state.joinBuilder.append(onEntry.getCollection2().getDatabase().getName()).append(secondBackTick).append(".").append(firstBackTick);
                }
            }
            if(onEntry.getCollection2() != null) {
                state.joinBuilder.append(onEntry.getCollection2().getName()).append(secondBackTick).append(".").append(firstBackTick);
            }
            state.joinBuilder.append(onEntry.getColumn2()).append(secondBackTick);
        }
    }

    protected void buildSearchQueryOrderByEntry(AbstractSearchQuery.OrderByEntry entry, SearchQueryBuilderState state) {
        if(state.orderByBuilder.length() == 0) {
            state.orderByBuilder.append(" ORDER BY ");
        } else {
            state.orderByBuilder.append(",");
        }

        if(entry.getAggregation() != null) {
            state.orderByBuilder.append(entry.getAggregation()).append("(").append(firstBackTick).append(buildField(entry)).append(secondBackTick).append(")");
        }else {
            state.orderByBuilder.append(firstBackTick).append(buildField(entry)).append(secondBackTick);
        }

        state.orderByBuilder.append(" ").append(entry.getOrder());
    }

    protected void buildSearchQueryGroupByEntry(AbstractSearchQuery.GroupByEntry entry, SearchQueryBuilderState state) {
        if(state.groupByBuilder.length() == 0) {
            state.groupByBuilder.append(" GROUP BY ");
        } else {
            state.groupByBuilder.append(" AND ");
        }

        if(entry.getAggregation() != null) {
            state.groupByBuilder.append(entry.getAggregation()).append("(").append(firstBackTick).append(buildField(entry)).append(secondBackTick).append(")");
        } else {
            state.groupByBuilder.append(firstBackTick).append(buildField(entry)).append(secondBackTick);
        }
    }

    protected void buildSearchQueryLimitEntry(AbstractSearchQuery.LimitEntry entry, SearchQueryBuilderState state) {
        if(state.limitBuilder.length() == 0) {
            addEntry(entry.getLimit(), state);
            addEntry(entry.getOffset(), state);
            state.limitBuilder.append(" LIMIT ? OFFSET ?");
        } else {
            throw new IllegalArgumentException("Query can't have more than one limit and offset");
        }
    }

    protected String buildField(AbstractSearchQuery.GroupByEntry entry) {
        return buildField(entry.getDatabase(), entry.getDatabaseCollection(), entry.getField());
    }

    protected String buildField(AbstractFindQuery.GetEntry entry) {
        return buildField(entry.getDatabase(), entry.getDatabaseCollection(), entry.getField());
    }

    protected String buildField(AbstractChangeAndSearchQuery.ChangeAndSearchEntry entry) {
        return buildField(entry.getDatabase(), entry.getDatabaseCollection(), entry.getField());
    }

    protected String buildField(AbstractSearchQuery.OrderByEntry entry) {
        return buildField(entry.getDatabase(), entry.getDatabaseCollection(), entry.getField());
    }

    protected String buildField(AbstractSearchQuery.ConditionEntry entry) {
        return buildField(entry.getDatabase(), entry.getDatabaseCollection(), entry.getField());
    }

    protected String buildField(String database, String databaseCollection, String field) {
        StringBuilder builder = new StringBuilder();
        if(database != null) {
            builder.append(database).append(secondBackTick).append(".").append(firstBackTick);
        }
        if(databaseCollection != null) {
            builder.append(databaseCollection).append(secondBackTick).append(".").append(firstBackTick);
        }
        builder.append(field);
        return builder.toString();
    }

    protected Object getEntry(Object value, AtomicInteger preparedValuesCount, Object[] values) {
        if(EntryOption.PREPARED != value) {
            return value;
        } else if(values.length > preparedValuesCount.get()) {
            return values[preparedValuesCount.getAndIncrement()];
        }
        throw new IllegalArgumentException("No prepared value in Query#execute");
    }

    protected Object addAndGetEntry(Object value, SearchQueryBuilderState state) {
        if(EntryOption.PREPARED != value) {
            if(value instanceof Collection<?>) state.preparedValues.addAll((Collection<?>) value);
            else state.preparedValues.add(value);
            return value;
        } else if(state.values.length > state.preparedValuesCount) {
            Object preparedValue = state.values[state.preparedValuesCount++];
            if(preparedValue instanceof PreparedValue) {
                state.preparedValues.addAll(((PreparedValue) preparedValue).getValues());
                return ((PreparedValue)preparedValue).getValues();
            } else {
                if(preparedValue instanceof Collection<?>) state.preparedValues.addAll((Collection<?>) preparedValue);
                else state.preparedValues.add(preparedValue);
                return preparedValue;
            }
        }
        throw new IllegalArgumentException("No prepared value in Query#execute");
    }

    protected void addEntry(Object value, SearchQueryBuilderState state) {
        addAndGetEntry(value, state);
    }



    protected static class SearchQueryBuilderState {

        final StringBuilder clauseBuilder;
        final StringBuilder joinBuilder;
        final StringBuilder limitBuilder;
        final StringBuilder orderByBuilder;
        final StringBuilder groupByBuilder;
        final Object[] values;
        final List<Object> preparedValues;
        int preparedValuesCount;
        boolean where;
        boolean operator;
        boolean negate;

        public SearchQueryBuilderState(Object[] values) {
            this.clauseBuilder = new StringBuilder();
            this.joinBuilder = new StringBuilder();
            this.limitBuilder = new StringBuilder();
            this.orderByBuilder = new StringBuilder();
            this.groupByBuilder = new StringBuilder();
            this.values = values;
            this.preparedValues = new ArrayList<>();
            preparedValuesCount = 0;
            this.where = true;
            this.operator = true;
            this.negate = false;
        }

        public String buildSearchQuery() {
            return joinBuilder.toString() + clauseBuilder.toString() + groupByBuilder.toString()
                    + orderByBuilder.toString() + limitBuilder.toString();
        }
    }

    protected static class FindQueryBuilderState extends SearchQueryBuilderState {

        final StringBuilder getBuilder;

        public FindQueryBuilderState(Object[] values) {
            super(values);
            this.getBuilder = new StringBuilder();
        }
    }

    protected static class UpdateQueryBuilderState extends SearchQueryBuilderState {

        final StringBuilder setBuilder;


        public UpdateQueryBuilderState(Object[] values) {
            super(values);
            this.setBuilder = new StringBuilder();
        }
    }
}
