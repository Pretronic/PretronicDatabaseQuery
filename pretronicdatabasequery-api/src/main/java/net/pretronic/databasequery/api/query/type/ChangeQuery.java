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

import net.pretronic.databasequery.api.query.Query;

/**
 * The change query is implemented in the {@link ReplaceQuery} and the {@link UpdateQuery}.
 * It represents the changed data of fields in a {@link net.pretronic.databasequery.api.collection.DatabaseCollection}.
 *
 * @param <T>
 */
public interface ChangeQuery<T extends Query> extends Query {

    T add(String field, Number value);

    T add(String field);

    T subtract(String field, Number value);

    T subtract(String field);

    T multiply(String field, Number value);

    T multiply(String field);

    T divide(String field, Number value);

    T divide(String field);

    /**
     * Sets the value for the {@code field}.
     *
     * @param field which should be updated
     * @param value to update
     * @return this query instance
     */
    T set(String field, Object value);

    /**
     * Sets the value for the {@code field}. The value is prepared and must be pass in the {@link Query#execute(Object...)} method.
     *
     * @param field which should be updated
     * @return this query instance
     */
    T set(String field);
}
