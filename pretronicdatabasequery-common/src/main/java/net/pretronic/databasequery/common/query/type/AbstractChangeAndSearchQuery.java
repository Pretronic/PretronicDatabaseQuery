package net.pretronic.databasequery.common.query.type;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.type.ChangeQuery;
import net.pretronic.databasequery.api.query.type.SearchQuery;
import net.pretronic.databasequery.common.query.EntryOption;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Nullable;
import net.pretronic.libraries.utility.map.Triple;

/**
 * The {@link AbstractChangeAndSearchQuery} is the base implementation with entry storing of {@link AbstractSearchQuery} and {@link ChangeQuery}.
 * It stores the query logic in form of entries.
 * @param <T> search query implementation type
 * @param <C> collection implementation type
 */
public abstract class AbstractChangeAndSearchQuery<T extends SearchQuery<T>, C extends DatabaseCollection> extends AbstractSearchQuery<T, C> implements ChangeQuery<T> {

    public AbstractChangeAndSearchQuery(C collection) {
        super(collection);
    }

    @Override
    public T add(String field, Number value) {
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new ChangeAndSearchEntry(assignment.getFirst(), assignment.getSecond(),
                assignment.getThird(), value, ChangeAndSearchEntry.ArithmeticOperator.ADD));
    }

    @Override
    public T subtract(String field, Number value) {
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new ChangeAndSearchEntry(assignment.getFirst(), assignment.getSecond(),
                assignment.getThird(), value, ChangeAndSearchEntry.ArithmeticOperator.SUBTRACT));
    }

    @Override
    public T multiply(String field, Number value) {
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new ChangeAndSearchEntry(assignment.getFirst(), assignment.getSecond(),
                assignment.getThird(), value, ChangeAndSearchEntry.ArithmeticOperator.MULTIPLY));
    }

    @Override
    public T divide(String field, Number value) {
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new ChangeAndSearchEntry(assignment.getFirst(), assignment.getSecond(),
                assignment.getThird(), value, ChangeAndSearchEntry.ArithmeticOperator.DIVIDE));
    }

    @Override
    public T add(String field) {
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new ChangeAndSearchEntry(assignment.getFirst(), assignment.getSecond(),
                assignment.getThird(), EntryOption.PREPARED, ChangeAndSearchEntry.ArithmeticOperator.ADD));
    }

    @Override
    public T subtract(String field) {
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new ChangeAndSearchEntry(assignment.getFirst(), assignment.getSecond(),
                assignment.getThird(), EntryOption.PREPARED, ChangeAndSearchEntry.ArithmeticOperator.SUBTRACT));
    }

    @Override
    public T multiply(String field) {
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new ChangeAndSearchEntry(assignment.getFirst(), assignment.getSecond(),
                assignment.getThird(), EntryOption.PREPARED, ChangeAndSearchEntry.ArithmeticOperator.MULTIPLY));
    }

    @Override
    public T divide(String field) {
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new ChangeAndSearchEntry(assignment.getFirst(), assignment.getSecond(),
                assignment.getThird(), EntryOption.PREPARED, ChangeAndSearchEntry.ArithmeticOperator.DIVIDE));
    }

    @Override
    public T set(String field, Object value) {
        Triple<String, String, String> assignment = getAssignment(field);
        return addEntry(new ChangeAndSearchEntry(assignment.getFirst(), assignment.getSecond(),
                assignment.getThird(), value, null));
    }

    @Override
    public T set(String field) {
        return set(field, EntryOption.PREPARED);
    }

    /**
     * It holds the entry data for this implemented methods in this class.
     */
    public static class ChangeAndSearchEntry extends AbstractSearchQuery.Entry {

        private final String database;
        private final String databaseCollection;
        private final String field;
        private final Object value;
        @Nullable
        private final ArithmeticOperator operator;

        public ChangeAndSearchEntry(String database, String databaseCollection, String field, Object value, ArithmeticOperator operator) {
            Validate.notNull(field);
            this.database = database;
            this.databaseCollection = databaseCollection;
            this.field = field;
            this.value = value;
            this.operator = operator;
        }

        public String getDatabase() {
            return database;
        }

        public String getDatabaseCollection() {
            return databaseCollection;
        }

        public String getField() {
            return field;
        }

        public Object getValue() {
            return value;
        }

        public ArithmeticOperator getOperator() {
            return operator;
        }

        public enum ArithmeticOperator {

            ADD("+"),
            SUBTRACT("-"),
            MULTIPLY("*"),
            DIVIDE("/");

            private final String symbol;

            ArithmeticOperator(String symbol) {
                this.symbol = symbol;
            }

            public String getSymbol() {
                return symbol;
            }
        }
    }
}
