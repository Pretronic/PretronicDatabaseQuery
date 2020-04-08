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

public interface SearchQuery<T extends SearchQuery<T>> extends JoinQuery<T> {

    T where(String field, Object value);

    T where(Aggregation aggregation,String field, Object value);

    T where(String field);

    T where(Aggregation aggregation,String field);


    T whereNot(String field, Object value);

    T whereNot(Aggregation aggregation,String field, Object value);

    T whereNot(String field);

    T whereNot(Aggregation aggregation,String field);


    T whereLike(String field, Pattern pattern);

    T whereLike(Aggregation aggregation,String field, Pattern pattern);

    T whereLike(String field, String pattern);

    T whereLike(Aggregation aggregation,String field, String pattern);

    T whereLike(String field);

    T whereLike(Aggregation aggregation,String field);


    T whereLower(String field, Object value);

    T whereLower(Aggregation aggregation,String field, Object value);

    T whereLower(String field);

    T whereLower(Aggregation aggregation,String field);


    T whereHigher(String field, Object value);

    T whereHigher(Aggregation aggregation,String field, Object value);

    T whereHigher(String field);

    T whereHigher(Aggregation aggregation,String field);


    T whereIsNull(String field);

    T whereIsEmpty(String field);


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


    T whereBetween(String field, Object value1, Object value2);

    T whereBetween(String field);


    SearchQuery<?> newSearchQuery();


    T not(SearchConsumer searchQuery);

    T not(SearchQuery<?> query);


    T and(SearchConsumer... searchQueries);

    T and(SearchQuery<?> query);


    T or(SearchConsumer... searchQueries);

    T or(SearchQuery<?> query);


    //Offset 0, if not used
    T limit(int limit, int offset);

    T limit(int limit);

    T onlyOne();


    T index(int start, int end);

    T page(int page, int entriesPerPage);


    T orderBy(String field, SearchOrder order);

    T orderBy(Aggregation aggregation, String field, SearchOrder order);


    T groupBy(String... fields);

    T groupBy(Aggregation aggregation, String field);


    T union(SearchQuery<?> query);

    interface SearchConsumer extends java.util.function.Consumer<SearchQuery<?>> {}
}
