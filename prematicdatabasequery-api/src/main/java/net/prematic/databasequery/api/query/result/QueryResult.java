/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.10.19, 20:44
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

package net.prematic.databasequery.api.query.result;

import net.prematic.databasequery.api.query.Query;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The {@link QueryResult} is always returned by a query execution ({@link Query#execute()}).
 * The result usually contains all founded rows or information about the execution.
 *
 * <p>The {@link QueryResult} provides many methods for accessing the data in different ways.
 * You can use methods for getting the data by an index, filter rows directly in a stream
 * or transforming the data into custom java objects./p>
 */
public interface QueryResult extends Iterable<QueryResultEntry> {

    /**
     * Get the first result row.
     *
     * <p>If no result is available, an exception will be thrown.</p>
     *
     * @return The first entry
     */
    QueryResultEntry first();

    /**
     * Get the first result row.
     *
     * <p>If no result is available, null will be returned.</p>
     *
     * @return The first entry
     */
    QueryResultEntry firstOrNull();

    /**
     * Get the last result row.
     *
     * <p>If no result is available, an exception will be thrown.</p>
     *
     * @return The last entry
     */
    QueryResultEntry last();

    /**
     * Get the last result row.
     *
     * <p>If no result is available, null will be returned.</p>
     *
     * @return The first entry
     */
    QueryResultEntry lastOrNull();

    /**
     * Get a result row by index
     *
     * <p>If no result is available, an exception will be thrown.</p>
     *
     * @param index The index (Starts by 0)
     * @return The entry at the position of this index
     */
    QueryResultEntry get(int index);

    /**
     * Get a result row by index
     *
     * <p>If no result is available, null will be returned.</p>
     *
     * @param index The index (Starts by 0)
     * @return The entry at the position of this index
     */
    QueryResultEntry getOrNull(int index);

    /**
     * Check if the result is empty.
     *
     * @return True when the result is empty.
     */
    boolean isEmpty();

    /**
     * Get the amount of result rows.
     *
     * @return The amount of rows
     */
    int size();

    /**
     * Create a new stream with the result entries.
     *
     * @return A new stream for filtering and searching rows
     */
    Stream<QueryResultEntry> stream();

    /**
     * Get all result rows in a list.
     *
     * @return A list which contains all rows.
     */
    List<QueryResultEntry> asList();

    /**
     * Load all rows in a collection by mapping them with a {@link Function}
     *
     * @param collection The collection where the rows should be loaded in
     * @param loader The function for mapping the data from the entry to your java object
     * @param <T> The object into which the line should be transformed
     */
    <T> void loadIn(Collection<T> collection, Function<QueryResultEntry,T> loader);

    /**
     * Create a new iterator for iterating all entries.
     *
     * @return A new iterator which points to this result
     */
    @Override
    default Iterator<QueryResultEntry> iterator() {
        return asList().iterator();
    }
}
