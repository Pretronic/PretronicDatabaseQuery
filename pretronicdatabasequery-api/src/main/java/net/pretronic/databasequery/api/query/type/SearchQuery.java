/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.03.20, 20:28
 * @website %web%
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

package net.pretronic.databasequery.api.query.type;

import net.pretronic.databasequery.api.query.Aggregation;
import net.pretronic.databasequery.api.query.Pattern;
import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.type.join.JoinQuery;

import java.util.Collection;
import java.util.function.Function;

/**
 * The {@link SearchQuery} represents the base query for filtering data out of a {@link net.pretronic.databasequery.api.collection.DatabaseCollection}.
 * It is implemented in {@link FindQuery}, {@link DeleteQuery}, {@link ReplaceQuery} and {@link UpdateQuery}.
 * @param <T>
 */
public interface SearchQuery<T extends SearchQuery<T>> extends JoinQuery<T> {

    /**
     * Specify the field, which the collection field have to have.
     * If {@link #and(SearchConsumer...)} or {@link #or(SearchQuery)} is not used in this query, then between this operators
     * are and.
     *
     * @param field name
     * @param value of the field
     * @return the query instance
     */
    T where(String field, Object value);

    /**
     * Specify the search for a field with a aggregation. It must have the {@code value}.
     *
     * @param aggregation to use
     * @param field name
     * @param value to compare
     * @return the query instance
     */
    T where(Aggregation aggregation,String field, Object value);

    /**
     * Filter {@link #where(String, Object)} with a prepared value.
     *
     * @param field name
     * @return query instance
     */
    T where(String field);

    /**
     * Filter {@link #where(Aggregation, String, Object)} with a prepared value.
     * @param aggregation to use
     * @param field name
     * @return the query instance
     */
    T where(Aggregation aggregation,String field);


    /**
     * Filter for fields, which are not the {@code value}.
     *
     * @param field name
     * @param value to compare
     * @return the query instance
     */
    T whereNot(String field, Object value);

    /**
     * Filter for fields with an aggregtion, which are not the {@code value}.
     *
     * @param aggregation to use
     * @param field name
     * @param value to compare
     * @return the query instance
     */
    T whereNot(Aggregation aggregation,String field, Object value);

    T whereNot(String field);

    T whereNot(Aggregation aggregation,String field);


    /**
     * Filter for fields with a pattern.
     *
     * @param field name
     * @param pattern object
     * @return query instance
     */
    T whereLike(String field, Pattern pattern);

    T whereLike(Aggregation aggregation,String field, Pattern pattern);

    T whereLike(String field, String pattern);

    T whereLike(Aggregation aggregation,String field, String pattern);

    T whereLike(String field);

    T whereLike(Aggregation aggregation,String field);


    /**
     * Filter for values of the field, which are lower then the {@code value}.
     *
     * @param field name
     * @param value to compare
     * @return query instance
     */
    T whereLower(String field, Object value);

    T whereLower(Aggregation aggregation,String field, Object value);

    T whereLower(String field);

    T whereLower(Aggregation aggregation,String field);


    /**
     * Filter for values of the field, whch are higher then the {@code value}.
     *
     * @param field name
     * @param value to compare
     * @return query instance
     */
    T whereHigher(String field, Object value);

    T whereHigher(Aggregation aggregation,String field, Object value);

    T whereHigher(String field);

    T whereHigher(Aggregation aggregation,String field);


    /**
     * Filter, where the field value is null.
     *
     * @param field name
     * @return query instance
     */
    T whereIsNull(String field);

    /**
     * Filter, where the string value is empty.
     *
     * @param field name
     * @return query instance
     */
    T whereIsEmpty(String field);


    /**
     * Filter for field, where the value of the field contains in {@code values}.
     *
     * @param field name
     * @param values to compare
     * @return query instance
     */
    T whereIn(String field, Object... values);

    default T whereIn(String field, Collection<?> values){
        return whereIn(field,values.toArray());
    }

    @SuppressWarnings("unchecked")
    default <R> T whereIn(String field, Collection<R> values,Function<R,Object> mapper){
        if(values.isEmpty()) return (T) this;
        Object[] data = new Object[values.size()];
        int index = 0;
        for (R value : values) {
            data[index] = mapper.apply(value);
            index++;
        }
        return whereIn(field,data);
    }

    T whereIn(String field);

    T whereIn(String field, FindQuery query);


    /**
     * Filter for fields, where the value is between {@code value1} and {@code value2}.
     *
     * @param field name
     * @param value1 to compare
     * @param value2 to compare
     * @return query instance
     */
    T whereBetween(String field, Object value1, Object value2);

    T whereBetween(String field);


    SearchQuery<?> newSearchQuery();


    /**
     * Filter for fields, where not the included query equal.
     *
     * @param searchQuery consumer for including query
     * @return query instance
     */
    T not(SearchConsumer searchQuery);

    T not(SearchQuery<?> query);


    /**
     * Filter for fields, which equal all filters {@code searchQueries}.
     *
     * @param searchQueries to equal.
     * @return query instance
     */
    T and(SearchConsumer... searchQueries);

    T and(SearchQuery<?> query);


    /**
     * Filter for fields, which equal one filters {@code searchQueries}.
     *
     * @param searchQueries where one must equal
     * @return query instance
     */
    T or(SearchConsumer... searchQueries);

    T or(SearchQuery<?> query);


    //Offset 0, if not used

    /**
     * Limit the result with the limit and the given offset. It only can be used in the super query.
     *
     * @param limit for result
     * @param offset to skip in result
     * @return query instance
     */
    T limit(int limit, int offset);

    /**
     * Limit the result with the given limit. The offset is 0. It only can be used in the super query.
     *
     * @param limit to skip in result
     * @return query instance
     */
    T limit(int limit);

    /**
     * {@link #limit(int)} with 1. It only can be used in the super query.
     *
     * @return query instance
     */
    T onlyOne();


    /**
     * Returns all result entries with the start and end index. It only can be used in the super query.
     *
     * @param start index
     * @param end index
     * @return query instane
     */
    T index(int start, int end);

    /**
     * Returns all result entries for the given page with configured entries per page. It only can be used in the super query.
     *
     * @param page to return
     * @param entriesPerPage amount for each page
     * @return query instance
     */
    T page(int page, int entriesPerPage);


    /**
     * Order the result with the given field in the given order. It only can be used in the super query.
     *
     * @param field name
     * @param order to sort
     * @return query instance
     */
    T orderBy(String field, SearchOrder order);

    /**
     * Order the result with an additional aggregation. It only can be used in the super query.
     *
     * @param aggregation to order
     * @param field name
     * @param order to sort
     * @return query instance
     */
    T orderBy(Aggregation aggregation, String field, SearchOrder order);


    /**
     * Groups the result with the given fields.
     *
     * @param fields to group
     * @return query instance
     */
    T groupBy(String... fields);

    /**
     * Groups the result with the given field and aggregation.
     *
     * @param aggregation to use
     * @param field name
     * @return query instance
     */
    T groupBy(Aggregation aggregation, String field);


    T union(SearchQuery<?> query);

    /**
     * The search query consumer, which can be used in {@link #and(SearchConsumer...)}, {@link #or(SearchConsumer...)}
     * and {@link #not(SearchConsumer)}.
     */
    interface SearchConsumer extends java.util.function.Consumer<SearchQuery<?>> {}
}
