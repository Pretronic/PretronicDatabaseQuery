/*
 * (C) Copyright 2020 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 06.01.20, 21:16
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

package net.pretronic.databasequery.sql.dialect.defaults;

import net.prematic.databasequery.api.collection.DatabaseCollectionType;
import net.prematic.databasequery.api.collection.field.FieldOption;
import net.prematic.databasequery.api.exceptions.DatabaseQueryException;
import net.prematic.databasequery.api.query.Aggregation;
import net.prematic.databasequery.api.query.ForeignKey;
import net.prematic.databasequery.api.query.PreparedValue;
import net.prematic.databasequery.api.query.type.FindQuery;
import net.prematic.libraries.utility.map.Pair;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.common.query.EntryOption;
import net.pretronic.databasequery.common.query.type.*;
import net.pretronic.databasequery.sql.DataTypeInfo;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.collection.SQLDatabaseCollection;
import net.pretronic.databasequery.sql.dialect.Dialect;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractDialect implements Dialect {

    private final String name;
    private final String driverName;
    private Class<? extends Driver> driver;
    private final String protocol;
    private final DatabaseDriverEnvironment environment;

    public AbstractDialect(String name, String driverName, String protocol, DatabaseDriverEnvironment environment) {
        this.name = name;
        this.driverName = driverName;
        this.protocol = protocol;
        this.environment = environment;
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

        queryBuilder.append("CREATE TABLE IF NOT EXISTS `");
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(database.getName()).append("`.`");
        }
        queryBuilder.append(name).append("`(");

        StringBuilder foreignKeyBuilder = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            if(i != 0) {
                queryBuilder.append(",");
            }

            AbstractCreateQuery.Entry entry = entries.get(i);
            if(entry instanceof AbstractCreateQuery.CreateEntry) {
                buildCreateQueryCreateEntry(database, queryBuilder, preparedValues,(AbstractCreateQuery.CreateEntry) entry);
            } else if(entry instanceof AbstractCreateQuery.ForeignKeyEntry) {
                buildForeignKey(database, foreignKeyBuilder, (AbstractCreateQuery.ForeignKeyEntry) entry);
            } else {
                throw new IllegalArgumentException(String.format("Entry %s is not supported for MySQL query", entry.getClass().getName()));
            }
        }
        return new Pair<>(queryBuilder.append(");").toString(), preparedValues);
    }

    private void buildCreateQueryCreateEntry(SQLDatabase database, StringBuilder queryBuilder, List<Object> preparedValues, AbstractCreateQuery.CreateEntry entry) {
        DataTypeInfo dataTypeInfo = database.getDriver().getDataTypeInfo(entry.getDataType());
        queryBuilder.append("`").append(entry.getField()).append("` ").append(dataTypeInfo.getName());

        if(dataTypeInfo.isSizeAble()) {
            if(entry.getSize() != 0) queryBuilder.append("(").append(entry.getSize()).append(")");
            else if(dataTypeInfo.getDefaultSize() != 0) queryBuilder.append("(").append(dataTypeInfo.getDefaultSize()).append(")");
        }
        if(entry.getDefaultValue() != null && entry.getDefaultValue() != EntryOption.NOT_DEFINED) {
            preparedValues.add(entry.getDefaultValue());
            queryBuilder.append(" DEFAULT ?");

        }
        if(entry.getFieldOptions() != null && entry.getFieldOptions().length != 0) {
            Pair<String, String> queryParts = buildCreateQueryFieldOptions(database, queryBuilder, entry);
            if(queryParts.getKey() != null) queryBuilder.append(queryParts.getKey());
            if(queryParts.getValue() != null) queryBuilder.append(queryParts.getValue());
        }
    }

    private Pair<String, String> buildCreateQueryFieldOptions(SQLDatabase database, StringBuilder queryBuilder, AbstractCreateQuery.CreateEntry entry) {
        Pair<String, String> queryParts = new Pair<>(null, null);
        for (FieldOption fieldOption : entry.getFieldOptions()) {
            switch (fieldOption) {
                case INDEX: {
                    queryParts.setKey(",INDEX `"+database.getName()+ this.name + entry.getField() + "`(`" + entry.getField() + "`)");
                    break;
                }
                case UNIQUE_INDEX: {
                    queryParts.setValue(",UNIQUE INDEX `"+database.getName()+ this.name + entry.getField() + "`(`" + entry.getField() + "`)");
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
        return queryParts;
    }

    private void buildForeignKey(SQLDatabase database, StringBuilder queryBuilder, AbstractCreateQuery.ForeignKeyEntry entry) {
        queryBuilder.append("CONSTRAINT `")
                .append(database.getName())
                .append(this.name)
                .append(entry.getField())
                .append("` FOREIGN KEY(`")
                .append(entry.getField())
                .append("`) REFERENCES `");
        queryBuilder.append(entry.getForeignKey().getCollection()).append("`(`").append(entry.getForeignKey().getField()).append("`)");
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

        StringBuilder queryBuilder = new StringBuilder().append("INSERT INTO `");
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(collection.getDatabase().getName()).append("`.`");
        }
        queryBuilder.append(collection.getName()).append("` ");

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

    private void buildInsertQueryFieldsPart(List<AbstractInsertQuery.Entry> entries, StringBuilder queryBuilder, AtomicInteger preparedValuesCount, AtomicInteger valueCount, List<Object> preparedValues, Object[] values) {
        for (int i = 0; i < entries.size(); i++) {
            AbstractInsertQuery.Entry entry = entries.get(i);
            if(i == 0) {
                queryBuilder.append("(");
            } else {
                queryBuilder.append(",");
            }
            queryBuilder.append("`").append(entry.getField()).append("`");
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
            if(entry instanceof AbstractUpdateQuery.SetEntry) {
                buildUpdateQueryEntry((AbstractUpdateQuery.SetEntry) entry, state);
            } else {
                buildSearchQueryEntry(entry, state);
            }
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE `");
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(collection.getDatabase().getName()).append("`.`");
        }
        queryBuilder.append(collection.getName()).append("` ").append(state.setBuilder).append(state.buildSearchQuery());
        return new Pair<>(queryBuilder.toString(), state.preparedValues);
    }

    private void buildUpdateQueryEntry(AbstractUpdateQuery.SetEntry entry, UpdateQueryBuilderState state) {
        if(state.setBuilder.length() == 0) {
            state.setBuilder.append("SET ");
        } else {
            state.setBuilder.append(",");
        }
        state.setBuilder.append("`").append(entry.getField()).append("`").append("=?");
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
            buildSearchQueryEntry(entry, state);
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ").append(buildFindQueryGetBuilder(state)).append(" FROM `");
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(collection.getDatabase().getName()).append("`.`");
        }
        queryBuilder.append(collection.getName()).append("` ").append(state.buildSearchQuery());

        return new Pair<>(queryBuilder.toString(), state.preparedValues);
    }

    private String buildFindQueryGetBuilder(FindQueryBuilderState state) {
        if(state.getBuilder.length() == 0) return "*";
        else return state.getBuilder.toString();
    }

    private void buildFindQueryEntry(AbstractFindQuery.GetEntry entry, FindQueryBuilderState state) {
        if(state.getBuilder.length() != 0) {
            state.getBuilder.append(",");
        }
        if(entry.getAggregation() != null) {
            state.getBuilder.append(entry.getAggregation()).append("(`").append(entry.getField()).append("`)");
        }else {
            state.getBuilder.append("`").append(entry.getField()).append("`");
        }
    }



    @Override
    public Pair<String, List<Object>> newDeleteQuery(SQLDatabaseCollection collection, List<AbstractDeleteQuery.Entry> entries, Object[] values) {
        SearchQueryBuilderState state = new SearchQueryBuilderState(values);
        for (AbstractSearchQuery.Entry entry : entries) {
            buildSearchQueryEntry(entry, state);
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM `");
        if(this.environment == DatabaseDriverEnvironment.REMOTE) {
            queryBuilder.append(collection.getDatabase().getName()).append("`.`");
        }
        queryBuilder.append(collection.getName()).append("` ").append(state.buildSearchQuery());
        return new Pair<>(queryBuilder.toString(), state.preparedValues);
    }



    private void buildSearchQueryEntry(AbstractSearchQuery.Entry entry, SearchQueryBuilderState state) {
        if(entry instanceof AbstractSearchQuery.ConditionEntry) {
            buildSearchQueryConditionEntry((AbstractSearchQuery.ConditionEntry) entry, state);
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

    private void buildSearchQueryConditionEntry(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
        if(state.operator) {
            if(state.where) {
                state.clauseBuilder.append(" WHERE ");
                state.where = false;
            } else {
                state.clauseBuilder.append(" AND ");
            }
        }
        buildSearchQueryConditionEntryType(entry, state);
    }

    private void buildSearchQueryConditionEntryType(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
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

    private void buildSearchQueryWhereConditionEntry(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
        if(state.negate) {
            state.clauseBuilder.append("NOT ");
        }
        if(entry.getExtra() != null) {
            Aggregation aggregation = (Aggregation) entry.getExtra();
            state.clauseBuilder.append(aggregation.toString()).append("(").append("`").append(entry.getField()).append("`").append(")");
        } else {
            state.clauseBuilder.append("`").append(entry.getField()).append("`");
        }
        addEntry(entry.getValue1(), state);
        state.clauseBuilder.append(getWhereCompareSymbol(entry.getType())).append("?");
    }

    private void buildSearchQueryWhereNullConditionEntry(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
        state.clauseBuilder.append("`").append(entry.getField()).append("`");
        state.clauseBuilder.append(" IS ");
        if(state.negate) {
            state.clauseBuilder.append("NOT ");
        }
        state.clauseBuilder.append("NULL");
    }

    private void buildSearchQueryWhereInConditionEntry(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
        if(state.negate) {
            state.clauseBuilder.append("NOT ");
        }
        state.clauseBuilder.append("`").append(entry.getField()).append("` IN (");

        @SuppressWarnings("unchecked")
        List<Object> values = (List<Object>) addAndGetEntry(entry.getValue1(), state);

        for (int i = 0; i < values.size(); i++) {
            if(i > 0) state.clauseBuilder.append(",");
            state.clauseBuilder.append("?");
        }
        state.clauseBuilder.append(")");
    }

    private void buildSearchQueryWhereBetweenConditionEntry(AbstractSearchQuery.ConditionEntry entry, SearchQueryBuilderState state) {
        if(state.negate) {
            state.clauseBuilder.append("NOT ");
        }
        state.clauseBuilder.append("`").append(entry.getField()).append("`");
        state.clauseBuilder.append(" BETWEEN ? AND ?");
        addEntry(entry.getValue1(), state);
        addEntry(entry.getExtra(), state);
    }

    private String getWhereCompareSymbol(AbstractSearchQuery.ConditionEntry.Type type) {
        switch (type) {
            case WHERE: return "=";
            case WHERE_LIKE: return " LIKE ";
            case WHERE_LOWER: return "<";
            case WHERE_HIGHER: return ">";
        }
        throw new IllegalArgumentException("Can't match compare symbol for " + type.toString());
    }

    private void buildSearchQueryOperationEntry(AbstractSearchQuery.OperationEntry entry, SearchQueryBuilderState state) {
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
                    buildSearchQueryEntry(child, state);
                }
                state.negate = false;
            }
        }
    }

    private void andOr(AbstractSearchQuery.OperationEntry entry, String symbol, SearchQueryBuilderState state) {
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

        for (AbstractSearchQuery.Entry child : entry.getEntries()) {
            buildSearchQueryEntry(child, state);
        }

        state.clauseBuilder.append(")");
    }

    private void buildSearchQueryJoinEntry(AbstractSearchQuery.JoinEntry entry, SearchQueryBuilderState state) {
        state.joinBuilder.append(entry.getType().toString()).append(" JOIN `")
                .append(entry.getCollection().getDatabase().getName()).append("`.`")
                .append(entry.getCollection().getName()).append("` ");

        for (int i = 0; i < entry.getOnEntries().size(); i++) {
            AbstractSearchQuery.JoinOnEntry onEntry = entry.getOnEntries().get(i);
            if(i == 0) {
                state.joinBuilder.append("ON ");
            } else {
                state.joinBuilder.append("AND ");
            }
            state.joinBuilder.append("`").append(onEntry.getCollection1().getDatabase().getName()).append("`.`")
                    .append(onEntry.getCollection1().getName()).append("`")
                    .append("=")
                    .append("`").append(onEntry.getCollection2().getDatabase().getName()).append("`.`")
                    .append(onEntry.getCollection2().getName()).append("`");
        }
    }

    private void buildSearchQueryOrderByEntry(AbstractSearchQuery.OrderByEntry entry, SearchQueryBuilderState state) {
        if(state.orderByBuilder.length() == 0) {
            state.orderByBuilder.append(" ORDER BY ");
        } else {
            state.orderByBuilder.append(",");
        }

        if(entry.getAggregation() != null) {
            state.orderByBuilder.append(entry.getAggregation()).append("(`").append(entry.getField()).append("`)");
        }else {
            state.orderByBuilder.append("`").append(entry.getField()).append("`");
        }

        state.orderByBuilder.append(" ").append(entry.getOrder());
    }

    private void buildSearchQueryGroupByEntry(AbstractSearchQuery.GroupByEntry entry, SearchQueryBuilderState state) {
        if(state.groupByBuilder.length() == 0) {
            state.groupByBuilder.append(" GROUP BY ");
        } else {
            state.groupByBuilder.append(" AND ");
        }

        if(entry.getAggregation() != null) {
            state.groupByBuilder.append(entry.getAggregation()).append("(`").append(entry.getField()).append("`)");
        } else {
            state.groupByBuilder.append("`").append(entry.getField()).append("`");
        }
    }

    private void buildSearchQueryLimitEntry(AbstractSearchQuery.LimitEntry entry, SearchQueryBuilderState state) {
        if(state.limitBuilder.length() == 0) {
            addEntry(entry.getLimit(), state);
            addEntry(entry.getOffset(), state);
            state.limitBuilder.append("LIMIT ? OFFSET ?");
        } else {
            throw new IllegalArgumentException("Query can't have more than one limit and offset");
        }
    }



    private Object getEntry(Object value, AtomicInteger preparedValuesCount, Object[] values) {
        if(EntryOption.PREPARED != value) {
            return value;
        } else if(values.length > preparedValuesCount.get()) {
            return values[preparedValuesCount.getAndIncrement()];
        }
        throw new IllegalArgumentException("No prepared value in Query#execute");
    }

    private Object addAndGetEntry(Object value, SearchQueryBuilderState state) {
        if(EntryOption.PREPARED != value) {
            state.preparedValues.add(value);
            return value;
        } else if(state.values.length > state.preparedValuesCount) {
            Object preparedValue = state.values[state.preparedValuesCount++];
            if(preparedValue instanceof PreparedValue) {
                state.preparedValues.addAll(((PreparedValue) preparedValue).getValues());
                return ((PreparedValue)preparedValue).getValues();
            } else {
                state.preparedValues.add(preparedValue);
                return preparedValue;
            }
        }
        throw new IllegalArgumentException("No prepared value in Query#execute");
    }

    private void addEntry(Object value, SearchQueryBuilderState state) {
        addAndGetEntry(value, state);
    }



    private static class SearchQueryBuilderState {

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

    private static class FindQueryBuilderState extends SearchQueryBuilderState {

        final StringBuilder getBuilder;

        public FindQueryBuilderState(Object[] values) {
            super(values);
            this.getBuilder = new StringBuilder();
        }
    }

    private static class UpdateQueryBuilderState extends SearchQueryBuilderState {

        final StringBuilder setBuilder;


        public UpdateQueryBuilderState(Object[] values) {
            super(values);
            this.setBuilder = new StringBuilder();
        }
    }
}
