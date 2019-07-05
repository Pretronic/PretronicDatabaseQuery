/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.06.19, 16:46
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

package net.prematic.databasequery.core.aggregation;

import net.prematic.libraries.utility.annonations.NotNull;
import net.prematic.libraries.utility.annonations.Nullable;

/**
 * A builder to build aggregations for methods in {@link net.prematic.databasequery.core.query.SearchQuery}
 * and in {@link net.prematic.databasequery.core.query.FindQuery}
 *
 * You get a new instance of this builder with {@link net.prematic.databasequery.core.Database#newAggregationBuilder(boolean)}
 * or with {@link net.prematic.databasequery.core.DatabaseCollection#newAggregationBuilder(boolean)}
 */
public interface AggregationBuilder {

    /**
     * Adds a field to the entries
     *
     * @param field
     * @return the AggregationBuilder instance
     */
    AggregationBuilder field(@NotNull String field);

    /**
     * Adds a operator to the entries
     * Available operators: +,-,*,/,^
     *
     * @param operator
     * @return the AggregationBuilder instance
     */
    AggregationBuilder operator(@NotNull String operator);

    /**
     * Adds a aggregation to the entries
     *
     * @param aggregation
     * @param field
     * @return the AggregationBuilder instance
     */
    AggregationBuilder aggregation(@NotNull Aggregation aggregation, @NotNull String field);

    /**
     * Adds a builder to the entries
     * @param builder
     * @return the AggregationBuilder instance
     */
    AggregationBuilder builder(@NotNull AggregationBuilder builder);

    /**
     * Adds the given AggregationBuilder to the entries
     *
     * @param consumer
     * @return the AggregationBuilder instance
     */
    AggregationBuilder builder(@NotNull Consumer consumer);

    /**
     * Adds a value, like a number to the entries
     *
     * @param value
     * @return the AggregationBuilder instance
     */
    AggregationBuilder value(@Nullable Object value);

    /**
     * Adds an empty value.
     * You have to set the value later in the {@link net.prematic.databasequery.core.query.Query#execute(Object...)} method
     *
     * @return the AggregationBuilder instance
     */
    default AggregationBuilder value() {
        return value(null);
    }

    /**
     * Adds an alias to the entries.
     * The alias is only considered, if the builder instance supports alias.
     * If you want a builder with alias support, you need to set the boolean of aliasAble to true
     * of the method {@link net.prematic.databasequery.core.Database#newAggregationBuilder(boolean)} or
     * of {@link net.prematic.databasequery.core.DatabaseCollection#newAggregationBuilder(boolean)}
     *
     * This method only works with a {@link net.prematic.databasequery.core.query.FindQuery}.
     *
     * @param alias
     * @return the AggregationBuilder instance
     */
    AggregationBuilder alias(@NotNull String alias);

    interface Consumer extends java.util.function.Consumer<AggregationBuilder> {}
}